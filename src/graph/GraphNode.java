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
	private HashMap<Integer, GraphNode> neighborsMap;
	private Double distance;
	private CommunityNode community;
	private HashSet<Integer> allNodesSet;

	static final double DEFAULT_DISTANCE = Double.POSITIVE_INFINITY;
	
	/**
	 * Create a new GraphNode
	 * @param the point of the node.
	 */	
	public GraphNode(int p) {
		this.setPoint(p);
		this.edgesMap = new HashMap<Integer, GraphEdge>();
		this.neighborsMap = new HashMap<Integer, GraphNode>();
		this.distance = DEFAULT_DISTANCE;
		this.community = null;
		this.allNodesSet = new HashSet<Integer>();
		allNodesSet.add(p);
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
	
	public HashSet<Integer> getAllNodesSet() {
		return allNodesSet;
	}
	
	public void setAllNodesSet(HashSet<Integer> anm) {
		allNodesSet = anm;
	}
	
	/**
	 * Report neighbor points of the vertex
	 * @return The neighbor points of the vertex in the graph.
	 */	
	public HashSet<Integer> getNeighborPoints() {
		return new HashSet<Integer>(edgesMap.keySet());
	}
	
	/**
	 * Report neighbors of the vertex
	 * @return The neighbors of the vertex in the graph.
	 */	
	public HashSet<GraphNode> getNeighbors() {		
		return new HashSet<GraphNode>(neighborsMap.values());
	}
	
	/**
	 * Add the edge to neighbor from the current node
	 * @param end node for the edge.
	 */	
	protected GraphEdge addEdge(GraphNode neighbor) 	{
		GraphEdge e = this.getEdge(neighbor.getPoint());
		if(e == null) {
			e = new GraphEdge(this, neighbor);

			edgesMap.put(neighbor.getPoint(), e);
			neighborsMap.put(neighbor.getPoint(), neighbor);
		}
		return e;
	}
	
	/**
	 * Remove the edge to neighbor from the current node
	 * @param end node for the edge.
	 */	
	protected void removeEdge(GraphNode neighbor) 	{
		edgesMap.remove(neighbor.getPoint());
		neighborsMap.remove(neighbor.getPoint());
	}
	
	protected void removeTwoEdges(GraphNode neighbor) 	{
		removeEdge(neighbor);
		neighbor.removeEdge(this);
	}	
	/** Find the edges of the current node
	 * 
	 * @return The list of edges 
	 */
	protected HashSet<GraphEdge> getEdges() {
		return new HashSet<GraphEdge>(edgesMap.values());	
	}
	
	/**
	 * Get GraphEdge.
	 * @param point GraphNode to.
	 * @return GraphEdge. 	 
	 */	
	public GraphEdge getEdge(int point) {
		if(edgesMap.containsKey(point)) {
			return edgesMap.get(point);
		} else {
			return null;
		}
	}
	public GraphEdge getEdgeObject(GraphNode neighbor) {
		for(GraphEdge edge : edgesMap.values()) {
			if(edge.getTo().equals(neighbor)) {
				return edge;
			} 
		}
		return null;
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
	 * Get the point
	 * @return The point of the node. 	 
	 */	
	public CommunityNode getCommunity() {
		return community;
	}	
	/**
	 * Get the point
	 * @return The point of the node. 	 
	 */	
	public void setCommunity(CommunityNode c) {
		community = c;
	}	
	/**
	 * Report degree of vertex
	 * between two edges
	 * @param vertex v
	 * @return The degree of vertex.
	 */	
	public int getVertexDegree() {
		int weight = 0;
		for(GraphEdge g : getEdges()) {
			weight += g.getWeight();
		}
		return weight;
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
	
	public String toString() {
		String s = "N" + getPoint() + " nbrs=[";
		for(GraphNode n : getNeighbors()) {
			s += n.getPoint() + " ";
		}
		s = s.trim() + "]";
		s += " allnodes=[";
		for(Integer n : getAllNodesSet()) {
			s += n + " ";
		}
		s = s.trim() + "]";
		s += " {";
		for(GraphEdge g : getEdges()) {
			s += g.toString();
		}
		s = s.trim() + "]";

		return s;
	}
}
