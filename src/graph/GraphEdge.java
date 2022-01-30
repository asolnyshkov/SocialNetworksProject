/**
 * @author Aleksandr Solnyshkov.
 * 
 * Implementation of Edge in a class
 * named GraphEdge.  
 *
 */
package graph;

public class GraphEdge {
	private GraphNode from;
	private GraphNode to;
	private double length;
	private Integer weight;
	/**
	 * Create a new GraphEdge
	 * @param the start node.
	 * @param the end node.
	 */	
	public GraphEdge(GraphNode f, GraphNode t) {
		
		this.from = f;
		this.to = t;
		this.length = 0;
		this.weight = 1;
	}

	/**
	 * Get the start node
	 * @return The node. 	 
	 */
	public GraphNode getFrom() {
		return this.from;
	}
	
	/**
	 * Get the end node
	 * @return The node. 	 
	 */
	public GraphNode getTo() {
		return this.to;
	}
	
	/**
	 * Get the length.
	 * @return The length.
	 */		
	public double getLength() {
		return length;
	}
	
	/**
	 * Set the length.
	 * @param The length.
	 */		
	public void setLength(double l) {
		length = l;
	}	
	
	/**
	 * Get the weight.
	 * @return The weight.
	 */		
	public Integer getWeight() {
		return weight;
	}
	
	/**
	 * Set the weight.
	 * @param The weight.
	 */		
	public void setWeight(int l) {
		weight = l;
	}
	
	/**
	 * Add the weight.
	 */		
	public void addWeight() {
		setWeight(getWeight() + 1);
	}
	
	public String toString() {
		return "(" + from.getPoint() + ", " + to.getPoint() + ")/" + weight + " ";
	}
}
