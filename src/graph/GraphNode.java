/**
 * @author Aleksandr Solnyshkov.
 * 
 * Implementation of Node in a class
 * named GraphNode.  
 *
 */
package graph;

import java.util.HashMap;
import java.util.HashSet;

public class GraphNode {
	private int point;
	private HashMap<Integer, GraphEdge> edgesMap;
	private Double distance;

	static final double DEFAULT_DISTANCE = Double.POSITIVE_INFINITY;
	
	/**
	 * Create a new GraphNode
	 * @param the point of the node.
	 */	
	public GraphNode(int p) {
		this.setPoint(p);
		this.edgesMap = new HashMap<Integer, GraphEdge>();
		this.distance = DEFAULT_DISTANCE;
	}
	
	/**
	 * Get the point
	 * @return The point of the node. 	 
	 */	
	public int getPoint() {
		return point;
	}
	
	/**
	 * Set the point
	 * @param the point of the node. 	 
	 */	
	public void setPoint(int point) {
		this.point = point;
	}

	/**
	 * Report neighbors of the vertex
	 * @return The neighbors of the vertex in the graph.
	 */	
	public HashSet<Integer> getNeighborPoints() {
		HashSet<Integer> vs = new HashSet<Integer>(edgesMap.keySet());
		return vs;
	}
	
	/**
	 * Add the edge to neighbor from the current node
	 * @param end node for the edge.
	 */	
	protected void addEdge(GraphNode neighbor) 	{
		GraphEdge e = new GraphEdge(this, neighbor);
		edgesMap.put(neighbor.getPoint(), e);
	}
	
	/**
	 * Remove the edge to neighbor from the current node
	 * @param end node for the edge.
	 */	
	protected void removeEdge(GraphNode neighbor) 	{
		edgesMap.remove(neighbor.getPoint());
	}	
	/** Find the edges of the current node
	 * 
	 * @return The list of edges 
	 */
	protected HashSet<GraphEdge> getEdges() {
		HashSet<GraphEdge> ed = new HashSet<GraphEdge>(edgesMap.values());
		return ed;
	}
	
	/**
	 * Vertex neighborhood check.
	 * @return 1 if true or 0 if false. 	 
	 */	
	public GraphEdge getEdge(int point) {
		if(edgesMap.containsKey(point)) {
			return edgesMap.get(point);
		} else {
			return null;
		}
	}	
	/**
	 * Vertex neighborhood check.
	 * @return 1 if true or 0 if false. 	 
	 */	
	public int vertexNeighborhoodCheck(int point) {
		if(edgesMap.containsKey(point)) {
			return 1;
		} else {
			return 0;
		}
	}
	
	/**
	 * Report degree of vertex
	 * between two edges
	 * @param vertex v
	 * @return The degree of vertex.
	 */	
	public int getVertexDegree() {
		return edgesMap.size();
	}
	
	/** Get the weight of current node on the path to the goal.
	 * 
	 * @return The weight.
	 */		
	public Double getDistance() {
		return distance;
	}	
	
	/** Get the distance or, other words, weight from the current node to the neighbor node
	 * 
	 * @param neighbor  The neighbor node
	 * @return The weight or infinity, if edge wasn't found.
	 */
	public Double getDistance(GraphNode neighbor) {
		for(GraphEdge edge : getEdges()) {
			if(edge.getTo().equals(neighbor)) {
				return distance + edge.getLength();
			}	
		}
		return Double.POSITIVE_INFINITY;
	}
	/** Set the weight of current node on the path to the goal.
	 * @param The weight.
	 */		
	public void setDistance(double d) {
		distance = d;
	}
	
	/** 
	 *  Set default distances for the node
	 */	
	public void setDefaultDistance() {
		distance = DEFAULT_DISTANCE;
	}
	

}
