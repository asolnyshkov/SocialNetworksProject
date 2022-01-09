/**
 * @author Aleksandr Solnyshkov.
 * 
 * Implementation of  Graph in a class
 * named CapGraph.  
 *
 */
package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.TreeMap;

import util.GraphLoader;

public class CapGraph implements Graph {
	
	private int numEdges;
	private HashMap<Integer, GraphNode> vertexMap;
	private List<Graph> scc;
	private boolean multiclustering;
	private List <HashSet<Integer>> clusters;
	
	/**
	 * Create a new empty CapGraph
	 */
	public CapGraph() {
		numEdges = 0;
		vertexMap = new HashMap<Integer, GraphNode>();
		scc = new ArrayList<Graph>();
		multiclustering = false;
		clusters = new ArrayList<HashSet<Integer>>();
	}

	/**
	 * Get vertex
	 * @param num the index of the vertex.
	 * @return The vertex in the graph.
	 */	
	public GraphNode getVertex(int num) {
		if(!this.vertexMap.keySet().contains(num)) {
			return null;
		}
		GraphNode node = this.vertexMap.get(num);
		return node;

	}	
	
	/**
	 * Report vertexes
	 * @return The vertexes in the graph.
	 */	
	public HashSet<Integer> getVertexes() {
		HashSet<Integer> vs = new HashSet<Integer>(vertexMap.keySet());
		return vs;
	}	

	/** 
	 * Creates a vertex with the given number.
	 * @param num the index of the vertex.  
	 */
	@Override
	public void addVertex(int num) {
		if(this.getVertex(num) == null) {
			GraphNode node = new GraphNode(num);
			vertexMap.put(num, node);
		}	
	}
	
	/**
	 * Report size of vertex set
	 * @return The number of vertices in the graph.
	 */
	public int getNumVertices() {
		return this.vertexMap.size();
	}
	
	/** 
	 * Creates an edge from the start vertex to the end.
	 * @param from the index of the start point for the edge.
	 * @param to the index of the end point for the edge.  
	 */
	@Override
	public void addEdge(int from, int to) {
		this.addVertex(from);		
		this.addVertex(to);
		GraphNode start = this.getVertex(from);
		GraphNode end = this.getVertex(to);
		start.addEdge(end);
		numEdges++;
	}
	
	/**
	 * Report size of edge set
	 * @return The number of edges in the graph.
	 */	
	public int getNumEdges() {
		return numEdges/2;
	}
	
	/**
	 * Report flag of multiclustering
	 * @return The flag of multiclustering.
	 */	
	public boolean getMulticlustering() {
		return multiclustering;
	}	
	
	/**
	 * Set flag of multiclustering.
	 */	
	public void setMulticlustering(boolean f) {
		multiclustering = f;
	}
	
	/** 
	 * Finds the egonet centered at a given node.
	 * @param from the index of the start point for the egonet.
	 * @return The graph on the egonet.
	 */
	@Override
	public Graph getEgonet(int center) {
		// add center point
		CapGraph en = new CapGraph();
		en.addVertex(center);
		GraphNode c = this.getVertex(center);
		// get neighbors
	    Iterator<Integer> np = (c.getNeighborPoints()).iterator();
	    while (np.hasNext()) {
	    	en.addVertex(np.next());
	    }
	    // get edges having from vertices
	    Iterator<Integer> v = (en.getVertexes()).iterator();
	    while (v.hasNext()) {
	    	Integer next = v.next();
	    	GraphNode start = this.getVertex(next);
	    	GraphNode startEn = en.getVertex(next);
		    Iterator<Integer> e = (start.getNeighborPoints()).iterator();
		    while (e.hasNext()) {
		    	Integer to = e.next();
				if(en.getVertex(to) == null) {
					continue;
				}	
				GraphNode endEn = en.getVertex(to);
				startEn.addEdge(endEn);
		    }	    	
	    }   
		return en;
	}

	/**
	 *  Returns all SCCs in a directed graph. Recall that the warm up
     * assignment assumes all Graphs are directed, and we will only 
     * test on directed graphs.
     * @return The list of the graphs of the SCCs. 
     */
	@Override
	public List<Graph> getSCCs() {
		Stack<Integer> vertices = new Stack<Integer>();
		vertices.addAll(this.getVertexes());
		
		vertices = dfs(this, vertices, false);
		CapGraph out = transpose(this);
		vertices = dfs(out, vertices, true);
		
		return this.scc;
	}
	
	/**
	 * Returns stack of vertixes after DFS.  
	 * @param currect graph.
	 * @param stack of vertixes.
	 * @param boolean flag of the finish stage.
     * @return The stack of vertixes after DFS. 
     */	
	public Stack<Integer> dfs(CapGraph g, Stack<Integer> vertices, boolean finishStage) {
		HashSet<Integer> visited = new HashSet<Integer>();	
		Stack<Integer> finished = new Stack<Integer>();
		while(!vertices.empty()) {
			Integer v = vertices.pop();
			CapGraph component = new CapGraph();

			if (!visited.contains(v)) {
				component = dfsVisit(g, v, visited, finished, component, finishStage);
				if (finishStage) {
					scc.add(component);
				}
			}		
		}
		return finished;
	}
	
	/**
	 * Returns SCC.  
	 * @param currect graph.
	 * @param currect vertix.
	 * @param stack of visited vertixes.
	 * @param stack of finished vertixes.
	 * @param currect component.
	 * @param boolean flag of the finish stage.
     * @return The stack of vertixes after DFS. 
     */		
	public CapGraph dfsVisit(CapGraph g, int v, HashSet<Integer> visited, Stack<Integer> finished, 
			CapGraph component, boolean finishStage) {
		visited.add(v);
		GraphNode node = g.getVertex(v);
	    Iterator<Integer> e = (node.getNeighborPoints()).iterator();
	    while (e.hasNext()) {
	    	Integer n = e.next();
			if(!visited.contains(n)) {
				component = dfsVisit(g, n, visited, finished, component, finishStage);
			}
	    }
		if (finishStage) {
			component.addVertex(v);
		}	    
	    finished.push(v);
	    
	    return component;
	}

	/**
	 * Returns transposed graph.  
	 * @param currect graph.
     * @return The transposed graph. 
     */	
	public CapGraph transpose(CapGraph in) {
		CapGraph out = new CapGraph();
		
	    Iterator<Integer> v = (in.getVertexes()).iterator();
	    while (v.hasNext()) {
	    	Integer next = v.next();
	    	out.addVertex(next);
	    	GraphNode startIn = in.getVertex(next);
	    	GraphNode startOut = out.getVertex(next);
		    Iterator<Integer> e = (startIn.getNeighborPoints()).iterator();
		    while (e.hasNext()) {
		    	Integer to = e.next();
		    	out.addVertex(to);
				GraphNode endOut = out.getVertex(to);
				endOut.addEdge(startOut);
		    }	    	
	    }
		return out;
	}	
	
    /** 
     * Return the graph's connections in a readable format. 
     * The keys in this HashMap are the vertices in the graph.
     * The values are the nodes that are reachable via a directed
     * edge from the corresponding key. 
	 * The returned representation ignores edge weights and 
	 * multi-edges. 
	 * @return The map of graph. The graph's connections in a readable format. 
	 */
	@Override
	public HashMap<Integer, HashSet<Integer>> exportGraph() {
		HashMap<Integer, HashSet<Integer>> eg = new HashMap<Integer, HashSet<Integer>>();
	    Iterator<GraphNode> i = (this.vertexMap.values()).iterator();
		while (i.hasNext()) {
			GraphNode node = i.next();
			eg.put(node.getPoint(), node.getNeighborPoints());		
		}			
		return eg;
	}
	
	/** 
	 *  Set default distances for all nodes
	 */
	public void setDefaultDistances() {
	    Iterator<GraphNode> i = (this.vertexMap.values()).iterator();
		while (i.hasNext()) {
			GraphNode node = i.next();
			node.setDefaultDistance();		
		}
	}
	
	/** Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<Integer> dijkstra(int start, int goal)
	{
		setDefaultDistances();
		
		HashMap<Integer, Integer> parentMap = new HashMap<Integer, Integer>();
		
		boolean found = dijkstraSearch(start, goal, parentMap);
		
		if (!found) {
			System.out.println("No path exists");
			setMulticlustering(true);
			return new LinkedList<Integer>();
		} 
		// reconstruct the path
		return constractPath(start, goal, parentMap);
	}
	
	private boolean dijkstraSearch(int start, int goal, HashMap<Integer, Integer> parentMap)
	{
		HashSet<Integer> visited = new HashSet<Integer>();
	    Comparator<GraphNode> comparator = new GraphNodeComparator();
		PriorityQueue<GraphNode> queue = new PriorityQueue<GraphNode>(getNumVertices(), comparator);
	
		GraphNode startNode = this.getVertex(start);
		startNode.setDistance(0);
		queue.add(startNode);
		boolean found = false;
		while (!queue.isEmpty()) {
			GraphNode curr = queue.remove();
			Integer currPoint = curr.getPoint();
			
			if (!visited.contains(currPoint)) {
				visited.add(currPoint);
				
				if (curr.equals(this.getVertex(goal))) {
					found = true;
					break;
				}
				HashSet<Integer> neighbors = curr.getNeighborPoints();
				for(Integer next: neighbors) {
					GraphNode nextNode = this.getVertex(next);
					if (!visited.contains(next)) {
						double lweight = curr.getDistance(nextNode);

						if(lweight < nextNode.getDistance()) {
							nextNode.setDistance(lweight);
	
							parentMap.put(next, currPoint);
							queue.add(nextNode);
						}
					}
				}
			}
		}
		
		//this.printPoints(visited);
		return found;
	}

	private void checkClusters(List<GraphEdge> list) {
		this.clusters = new ArrayList<HashSet<Integer>>();
		for(GraphEdge e: list) {
			int start = e.getFrom().getPoint();
			int goal = e.getTo().getPoint();
			boolean s = false;
			int curr = 0;
			int i = 0;
			for (HashSet<Integer> c : this.clusters) {
			    if((c.contains(start) || c.contains(goal) && !s)) {
			    	c.add(start);
			    	c.add(goal);			    	
			    	s = true;
			    	curr = i;
			    } else if((c.contains(start) || c.contains(goal) && s))  {
			    	HashSet<Integer> c0 = this.clusters.get(curr);
			    	Iterator<Integer> it = c.iterator();
			    	while (it.hasNext()) {
			    		Integer k = it.next();
			    		c0.add(k);
			    	    it.remove();
			    	}
			    }
			    i++;
			}	
			if (!s) {
				HashSet<Integer> hs = new HashSet<Integer>();
				hs.add(start);
				hs.add(goal);
				this.clusters.add(hs);
			}
		}
		for(int i = this.clusters.size()-1; i >= 0; i--) {
			if(this.clusters.get(i).isEmpty()) {
				this.clusters.remove(i);
			}
		}
	}
	
	/** Reconstruct the path
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param parentMap .
	 * @return The list of vertexes that there are the the shortest path from  
	 *   start to goal (including both start and goal).
	 */	
	private LinkedList <Integer> constractPath (int start, int goal, HashMap<Integer, Integer> parentMap)
	{
		// reconstruct the path
		LinkedList<Integer> path = new LinkedList<Integer>();
		Integer curr = goal;
		while (!getVertex(curr).equals(getVertex(start))) {
			path.addFirst(curr);
			curr = parentMap.get(curr);
		}
		path.addFirst(start);
		return path;
	}

	/**
	 * Print the LinkedList to the screen.
	 */
	public void printPoints(HashSet<Integer> route) {
		System.out.println("===============================================");
		for(Integer gp: route) {
			System.out.println(gp.toString());
			GraphNode gn = this.getVertex(gp);
			for(GraphEdge ge: gn.getEdges()) {
				System.out.println(ge.toString());	
			}
		}
	}
	
	/**
	 * Get all paths of the graph between all pairs of vertexes.
	 * @return The list of all paths.    
     */	
	public List<GraphEdge> getStraightPaths() {

		TreeMap<Integer, GraphNode> treeMap = new TreeMap<>(this.vertexMap);
		Integer max =  treeMap.lastKey();
		boolean[][] visited = new boolean [max+1][max+1];
		
		/*
			Comparator<GraphEdge> comparator = new Comparator<GraphEdge>() {
	    		public int compare(GraphEdge o1, GraphEdge o2) {
	    			return o2.getWeight().compareTo(o1.getWeight());
	    	}
	    };	
	    */	
	    Comparator<GraphEdge> comparator = (o1, o2) -> o2.getWeight().compareTo(o1.getWeight());
	    
	    List<GraphEdge> queue = new ArrayList<GraphEdge>(getNumEdges());
		
	    Iterator<Integer> v = (this.getVertexes()).iterator();
	    while (v.hasNext()) {
	    	Integer from = v.next();

		    Iterator<Integer> w = (this.getVertexes()).iterator();
		    while (w.hasNext()) {
		    	Integer to = w.next();
		    	if(from.equals(to) || visited[from][to] || visited[to][from]) {
		    		continue;
		    	} else {
		    		List<Integer> list = dijkstra(from, to);
		    		System.out.println(list.toString());
		    		Integer temp = from;
		    		for (Integer e : list) {
		    		    if(e.equals(temp)) {
		    		    	continue;
		    		    }
		    		    GraphEdge ed;
		    		    if(temp < e) {
		    		    	ed = this.getVertex(temp).getEdge(e);
		    		    } else {
		    		    	ed = this.getVertex(e).getEdge(temp);
		    		    }
		    		    if(ed != null) {
		    		    	if(!queue.contains(ed)) {
		    		    		ed.setWeight(0);
		    		    		queue.add(ed);
		    		    	}
		    		    	ed.addWeight();
		    		    } 
		    		    temp = e;
		    		}

		    		visited[from][to] = true;
		    		visited[to][from] = true;
		    	}

		    }	    	
	    }
	    Collections.sort(queue, comparator);
	    return queue;
	}
	
	/**
	 * Cut edge.
	 * @param int k.
	 */	
	public void cutEdge(int k, int w) {
		int i = 0;
		List<GraphEdge> queue = this.getStraightPaths();
		while (!this.getMulticlustering() && (k > i)) {
			while (!queue.isEmpty()) {	
				GraphEdge curr = queue.remove(0);
				GraphNode from = curr.getFrom();
				GraphNode to = curr.getTo();
				from.removeEdge(to);
				to.removeEdge(from);
				numEdges--;
				numEdges--;
				if(curr.getWeight() < w) {
					break;
				}
			}
			queue = this.getStraightPaths();
			i++;
		}
		if (this.getMulticlustering()) {
			System.out.println("There is multiclustering");
			checkClusters(queue);
			int j = 0;
			for(HashSet <Integer> hs: this.clusters) {
				System.out.println("" + j +" - "+ hs.size()+": "+ hs.toString());
				j++;
			}	
		}
	}	
	
	/**
	 * Report expected number of edges
	 * between two edges.
	 * @param vertex from.
	 * @param vertex to.
	 * @return The expected number of edges.
	 */	
	public int expectedNumberOfEdges(int from, int to) {
		GraphNode start = this.getVertex(from);
		GraphNode end = this.getVertex(to);

		return start.getVertexDegree() * end.getVertexDegree()/(2 * this.getNumEdges());
	}
	
	public static void main(String[] args) {
		String file = "facebook_1000.txt";
		//String file = "small_test_graph2.txt";
		CapGraph graph = new CapGraph();		
		
		GraphLoader.loadGraph(graph, "data/" + file);
	    
		graph.cutEdge(1, 60000);
	}

}
