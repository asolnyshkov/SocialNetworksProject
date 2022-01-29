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
import java.util.PriorityQueue;
import java.util.TreeMap;

import util.GraphLoader;

public class CapGraph implements Graph {
	
	private int numEdges;
	private HashMap<Integer, GraphNode> vertexMap;
	private boolean multiclustering;
	private List <HashSet<Integer>> clusters;
	private HashSet<CommunityNode> communitySet;
	
	/**
	 * Create a new empty CapGraph
	 */
	public CapGraph() {		
		
		numEdges = 0;
		vertexMap = new HashMap<Integer, GraphNode>();

		multiclustering = false;
		clusters = new ArrayList<HashSet<Integer>>();
		communitySet = new HashSet<CommunityNode>();
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
		Dfs d = new Dfs();
		return d.getSCCs(this);
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

	    Comparator<GraphNode> comparator = (o1, o2) -> o1.getDistance().compareTo(o2.getDistance());
	    
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
						Double lweight = curr.getDistance(nextNode);

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
		    		    // we get the edge with increased vertexes
		    		    if(temp < e) {
		    		    	ed = this.getVertex(temp).getEdge(e);
		    		    } else {
		    		    	ed = this.getVertex(e).getEdge(temp);
		    		    }
		    		    // if edge exists
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
	 * @param int k How many edges do we have to remove? k.
	 * @param int w This is the min weight of edge to remove.
	 */	
	public void cutEdge(int k, int w) {
		int i = 0;
		List<GraphEdge> queue = this.getStraightPaths();
		while (!this.getMulticlustering() && (k > i) && (!queue.isEmpty())) {
//			if (!queue.isEmpty()) {	
				GraphEdge curr = queue.remove(0);
				GraphNode from = curr.getFrom();
				GraphNode to = curr.getTo();
				from.removeTwoEdges(to);
				numEdges--;
				numEdges--;
				if(curr.getWeight() < w) {
//					break;
				}
//			}
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
	
	/** 
	 * We assign a different community 
	 * to each node of the network.  
	 */
	public void assignCommunityToEachNode() {
		if (this.communitySet.size() == 0) {
			for(GraphNode g : vertexMap.values()) {
				CommunityNode cn = new CommunityNode(g.getPoint());
				cn.addNode(g);

				communitySet.add(cn);
			}
		}
	}

	/** 
	 * For each node i we consider 
	 * the neighbors j of i and 
	 * we evaluate the gain of modularity.  
	 */
	public void evaluateTheGainOfModularity() {
		int m = getNumEdges();
		HashSet<Integer> visited = new HashSet<Integer>();
		HashSet<CommunityNode> addMap = new HashSet<CommunityNode>();		

		for(CommunityNode cn : communitySet) {
			double maxMod = -1;
			GraphNode maxModNode = null;
			
			for(GraphNode gn : cn.getNodes()) {
				visited.add(gn.getPoint());
				for(GraphNode nb : gn.getNeighbors()) {
					if (!visited.contains(nb.getPoint())) {
						double deltaMod = cn.deltaModularity(m, nb);
						if(deltaMod > 0 && deltaMod > maxMod) {
							maxMod = deltaMod;
							maxModNode = nb;
						}
					}
				}
			}
			
			if(maxModNode != null) {
				CommunityNode nbcn = maxModNode.getCommunity();
				
				
				CommunityNode newCn = nbcn.deleteNode(maxModNode);
				
				addMap.add(newCn);
				cn.addNode(maxModNode);
				//addMap.add(nbcn);
				visited.add(maxModNode.getPoint());
			}
			//System.out.println(cn.getNodesPoints().toString());

		}
		communitySet.addAll(addMap);
		deleteEmptyCommunities();
	}
	
	public void deleteEmptyCommunities() {
		HashSet<CommunityNode> emptyCommunity = new HashSet<CommunityNode>();
		for(CommunityNode cn : communitySet) {
			if(cn.getNodesPoints().size()==0) {
				emptyCommunity.add(cn);
			}
		}
		for(CommunityNode cn : emptyCommunity) {
			communitySet.remove(cn);
		}
	}

	/**	 
	 * The aij represents the weight of the edge between i and j.
	 * @param vertex i. 
	 * @param vertex j. 
	 * @return aij.
	 */

	public int getAij(GraphNode i, GraphNode j) {
		GraphEdge e  = i.getEdgeObject(j);
		if(e != null) {
			return e.getWeight();
		} 

		return 0;
	}
	
	/**	 
	 * Delta is the function δ(u, v) is 1 if u = v and 0 otherwise.
	 * @param community ci. 
	 * @param community cj. 
	 * @return delta.
	 */	
	public int getDelta(CommunityNode ci, CommunityNode cj) {
		if(ci.equals(cj)) {
			return 0;
		}
		return 1;
	}
	
	/**
	 * The modularity of a partition is a scalar value between −1 and 1
	 * that measures the density of links inside communities 
	 * as compared to links between communities.
	 * 
	 * m is the sum of the weights of all the links in the network.	 
	 * aij represents the weight of the edge between i and j.
	 * ki is the sum of the weights of the edges attached to vertex i. 
	 * kj is the sum of the weights of the edges attached to vertex j. 
	 * delta is the function δ(u, v) is 1 if u = v and 0 otherwise.
	 * ci is the community to which vertex i is assigned. 
	 * cj is the community to which vertex j is assigned. 
	 */
	public double modularity() {
		int m = getNumEdges();		
		double mod = 0;
		for(GraphNode i : vertexMap.values()) {
			for(GraphNode j : vertexMap.values()) {
				CommunityNode ci = i.getCommunity();
				CommunityNode cj = j.getCommunity();				

				int ki = i.getVertexDegree();
				int kj = j.getVertexDegree();
				double temp = (getAij(i, j) - ki*kj/(2*m)) * getDelta(ci, cj);
		        //System.out.println("modularity =" + temp + " i=" + i.getPoint() + " j=" + j.getPoint());
				mod += temp;
			}
		}
		return mod/(2*m);
	}
	
	public String toString() {
		String s = "";
		int i = 1;
		for(CommunityNode cn : communitySet) {
			s += "CN" + i + " " + cn.toString() + "\n";
			i++;
		}
		return s;
	}
	
	public int getAllLinks() {
		int intWeight = 0;
		int extWeight = 0;
		HashSet<GraphEdge> internalLinksSet = new HashSet<GraphEdge>();
		HashSet<GraphEdge> externalLinksSet = new HashSet<GraphEdge>();
		for(CommunityNode cn : communitySet) {
			internalLinksSet.addAll(cn.getInternalEdges());
			externalLinksSet.addAll(cn.getExternalEdges());	
		}
		for(GraphEdge e : internalLinksSet) {
			intWeight += e.getWeight();
		}
		for(GraphEdge e : externalLinksSet) {
			extWeight += e.getWeight();
		}
		return intWeight + extWeight/2;
	}
	
	
	public void communityAggregation() {
		for(CommunityNode cn : communitySet) {
			GraphNode node = new GraphNode(cn.getPoint());
			cn.setSingleNode(node);
		}
		
		HashMap<Integer, GraphNode> vm = new HashMap<Integer, GraphNode>();
		
		for(CommunityNode cn : communitySet) {
			GraphNode snode = cn.communityAggregation();
			vm.put(cn.getPoint(), snode);
		}
		for(CommunityNode cn : communitySet) {
			GraphNode node = cn.getSingleNode();
			HashMap<Integer, GraphNode> nm = new HashMap<Integer, GraphNode>();
			nm.put(node.getPoint(), node);
			cn.setNodes(nm);
		}
		vertexMap = new HashMap<Integer, GraphNode>(vm);
	}
	
	public static void main(String[] args) {
		//String file = "facebook_1000.txt";
		String file = "dij/test_2.txt";
		CapGraph graph = new CapGraph();		
		
		GraphLoader.loadGraph(graph, "data/" + file);
	    
		//graph.cutEdge(1, 60000);
		graph.assignCommunityToEachNode();
		int k = 2;
		int size = graph.communitySet.size();
		while(size > k) {
			while(size > k) {
				graph.evaluateTheGainOfModularity();
				int temp = graph.communitySet.size();
				if(temp == size) {
					break;
				}
				size = temp;
			}
			double mod = graph.modularity();
			System.out.println(graph.toString());
			System.out.println("size=" + size + " edges=" + graph.getNumEdges() 
				+ " sum_edges=" + graph.getAllLinks()+ " modularity=" + mod + "\n");
			
			graph.communityAggregation();
			
			mod = graph.modularity();
			System.out.println(graph.toString());
			System.out.println("size=" + size + " edges=" + graph.getNumEdges() 
				+ " sum_edges=" + graph.getAllLinks()+ " modularity=" + mod + "\n");

		}
	}

}
