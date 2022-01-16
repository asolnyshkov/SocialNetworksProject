package graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class Dfs {
	
	private List<Graph> scc;
	
	/**
	 * Create a new empty Dfs
	 */
	public Dfs() {		
		scc = new ArrayList<Graph>();
	}

	/**
	 * Returns stack of vertixes after DFS.  
	 * @param currect graph.
	 * @param stack of vertixes.
	 * @param boolean flag of the finish stage.
     * @return The stack of vertixes after DFS. 
     */	
	public Stack<Integer> deepFirstSearch(CapGraph g, Stack<Integer> vertices, boolean finishStage) {
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
	 *  Returns all SCCs in a directed graph. Recall that the warm up
     * assignment assumes all Graphs are directed, and we will only 
     * test on directed graphs.
     * @return The list of the graphs of the SCCs. 
     */
	public List<Graph> getSCCs(CapGraph in) {
		Stack<Integer> vertices = new Stack<Integer>();
		vertices.addAll(in.getVertexes());
		
		vertices = deepFirstSearch(in, vertices, false);
		CapGraph out = transpose(in);
		vertices = deepFirstSearch(out, vertices, true);
		
		return this.scc;
	}
}
