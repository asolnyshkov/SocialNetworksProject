/**
 * @author Aleksandr Solnyshkov.
 * 
 * Implementation of Community in a class
 * named CommunityNode.  
 *
 */
package graph;

import java.util.HashMap;
import java.util.HashSet;

public class CommunityNode {
	private int point;
	private GraphNode singleNode;
	private HashMap<Integer, GraphNode> nodesMap;
	private HashSet<Integer> allNodesSet;
	private HashSet<GraphEdge> internalLinks;
	private HashSet<GraphEdge> externalLinks;
	/**
	 * Create a new GraphNode
	 * @param the point of the node.
	 */	
	public CommunityNode(int p) {
		this.setPoint(p);
		this.singleNode = new GraphNode(point);
		this.singleNode.setCommunity(this);
		this.nodesMap = new HashMap<Integer, GraphNode>();
		this.allNodesSet = new HashSet<Integer>();
		this.internalLinks = new HashSet<GraphEdge>();
		this.externalLinks = new HashSet<GraphEdge>();
	}
	/**
	 * Get the point
	 * @return The point of the community. 	 
	 */	
	public int getPoint() {
		return point;
	}
	/**
	 * Set the point
	 * @param the point of the community. 	 
	 */	
	public void setPoint(int point) {
		this.point = point;
	}
	/**
	 * Get the point
	 * @return The GraphNode. 	 
	 */	
	public GraphNode getSingleNode() {
		return singleNode;
	}
	/**
	 * Set the point
	 * @param the GraphNode. 	 
	 */	
	public void setSingleNode(GraphNode sn) {
		this.singleNode = sn;
		sn.setCommunity(this);
	}
	
	/**
	 * Add node to the community.
	 * @param the node.
	 */	
	public void addNodeFirst(GraphNode node) {
		nodesMap.put(node.getPoint(), node);
		node.setCommunity(this);
		allNodesSet.add(node.getPoint());
		for(GraphEdge e: node.getEdges()) {				
			getExternalEdges().add(e);
		}
	}
	/**
	 * Add node to the community.
	 * @param the node.
	 */	
	public void addNode(GraphNode node) {
		nodesMap.put(node.getPoint(), node);
		getAllNodesSet().addAll(node.getAllNodesSet());
		node.setCommunity(this);
		for(GraphEdge e: node.getEdges()) {
			GraphNode nb = e.getTo();
			GraphEdge back = nb.getEdgeObject(node);
			CommunityNode nbCn = nb.getCommunity();
			
			if(nbCn != null && nbCn.equals(this)) {
				getInternalEdges().add(e);
				if(nbCn.getExternalEdges().contains(back)) {
					nbCn.getExternalEdges().remove(back);
				}	
				nbCn.getInternalEdges().add(back);
			} else {				
				getExternalEdges().add(e);
				if(nbCn != null) {
					nbCn.getExternalEdges().add(back);					
					if(nbCn.getInternalEdges().contains(back)) {
						nbCn.getInternalEdges().remove(back);
					}
					nbCn.getExternalEdges().add(back);
				}				
			}
		}//for
	}	
	
	/**
	 * Delete node from the community.
	 * @param the node.
	 */		
	public CommunityNode deleteNode(GraphNode node) {
		nodesMap.remove(node.getPoint());
		CommunityNode cn = new CommunityNode(node.getPoint());
		node.setCommunity(cn);
		getAllNodesSet().remove(node.getPoint());
		cn.getAllNodesSet().add(node.getPoint());
		for(GraphEdge e: node.getEdges()) {
			if(getInternalEdges().contains(e)) {
				getInternalEdges().remove(e);
			} 			
			if(getExternalEdges().contains(e)) {
				getExternalEdges().remove(e);
			} 
			GraphNode nb = e.getTo();
			CommunityNode nbCn = nb.getCommunity();
			if(nbCn != null) {
				GraphEdge back = nb.getEdgeObject(node);
				if(nbCn.getExternalEdges().contains(e)) {
					nbCn.getExternalEdges().remove(e);
				} 				
				if(nbCn.getInternalEdges().contains(back)) {
					nbCn.getInternalEdges().remove(back);
				} 
			}
		}//for
		return cn;
	}
	
	public HashSet<GraphNode> getNodes() {
		return new HashSet<GraphNode>(nodesMap.values());
	}
	
	public HashSet<Integer> getNodesPoints() {
		return new HashSet<Integer>(nodesMap.keySet());
	}
	
	public void setNodes(HashMap<Integer, GraphNode> nm) {
		nodesMap = nm;
	}
	
	public HashSet<Integer> getAllNodesSet() {
		return allNodesSet;
	}
	
	public void setAllNodesSet(HashSet<Integer> anm) {
		allNodesSet = anm;
	}	
	
	public int getInternalLinksNumber() {
		int weight = 0;
		for(GraphEdge g : internalLinks) {
			weight += g.getWeight();
		}
		return weight/2;
	}	
	
	public int getExternalLinksNumber() {
		int weight = 0;
		for(GraphEdge g : externalLinks) {
			weight += g.getWeight();
		}
		return weight;
	}	

	public HashSet<GraphEdge> getInternalEdges() {
		return internalLinks;
	}
	public HashSet<GraphEdge> getExternalEdges() {
		return externalLinks;
	}
	public void removeInternalEdge(GraphEdge e) {
		internalLinks.remove(e);
	}
	public void removeExternalEdge(GraphEdge e) {
		externalLinks.remove(e);
	}	
	public int getIncidentLinks(GraphNode gn) {
		return gn.getVertexDegree();
	}
	/**
	 * kiIn n is the sum of the weights of the links from i to nodes in C
	 * @param i is the node that we remove from its community and by
	 * placing it in the community of C
	 */	
	public int getLinksToCommunity(GraphNode i) {
		int kIIn = 0;
		for(GraphNode n : i.getNeighbors()) {
			if(nodesMap.containsValue(n)) {
				kIIn += i.getEdgeObject(n).getWeight();
			}
		}
		return kIIn;
	}
	/**
	 * eIn is the sum of the weights of the links inside C
	 * eTot is the sum of the weights of the links incident to nodes in C
	 * ki is the sum of the weights of the links incident to node i,
	 * kiIn n is the sum of the weights of the links from i to nodes in C
	 * @param m is the sum of the weights of all the links in the network. 
	 * @param i is the node that we remove from its community and by
	 * placing it in the community of j
	 */
	public double deltaModularity(int m, GraphNode i) {
		double dmod = 0;
		double eIn = getInternalLinksNumber();
		double eTot = eIn + getExternalLinksNumber();
		double ki = getIncidentLinks(i);
		double kiIn = getLinksToCommunity(i);
		dmod = ((eIn + 2*kiIn)/(2*m))- Math.pow((eTot+ki)/(2*m), 2);
		dmod = dmod - (eIn/(2*m)- Math.pow(eTot/(2*m), 2)- Math.pow(ki/(2*m), 2));
		return dmod;
	}
	
	public GraphNode communityAggregation() {
		GraphNode node = this.getSingleNode();
		HashSet<GraphEdge> intEdges = new HashSet<GraphEdge>();
		HashSet<GraphEdge> extEdges = new HashSet<GraphEdge>();

		for(GraphNode g : nodesMap.values()) {		
			for(GraphEdge e : g.getEdges()) {

				if(getInternalEdges().contains(e)) {
					GraphEdge intEdge  = node.getEdgeObject(node);
					if(intEdge != null) {
						intEdge.setWeight(intEdge.getWeight() + e.getWeight());
					} else {
						intEdge = node.addEdge(node);
						intEdge.setWeight(e.getWeight());
						intEdges.add(intEdge);
					}	
				} else if(getExternalEdges().contains(e)) {
					GraphNode neighbor = e.getTo();
					CommunityNode neighborCommunity = neighbor.getCommunity();
					GraphNode neighborNode = neighborCommunity.getSingleNode();
					GraphEdge extEdge  = node.getEdgeObject(neighborNode);
					if(extEdge != null) {
						extEdge.setWeight(extEdge.getWeight() + e.getWeight());
					} else {
						extEdge = node.addEdge(neighborNode);
						extEdge.setWeight(e.getWeight());
						extEdges.add(extEdge);
					}					
				}
				
			}
		}
		this.internalLinks = intEdges;
		this.externalLinks = extEdges;

		return node;
	}
	
	public String toString() {
		String s = "C" + getPoint() + " [";
		for(Integer g : nodesMap.keySet()) {
			s += g + " ";
		}
		s = s.trim() + "]";
		s += " allnodes=[";
		for(Integer g : allNodesSet) {
			s += g + " ";
		}
		s = s.trim() + "]";
		s += " in_edges={";
		for(GraphEdge g : internalLinks) {
			s += "(" + g.getFrom().getPoint() + ", " + g.getTo().getPoint() + ")" + g.getWeight() + " ";
		}
		s = s.trim() + "}";
		s += " ex_edges={";
		for(GraphEdge g : externalLinks) {
			s += "(" + g.getFrom().getPoint() + ", " + g.getTo().getPoint() + ")" + g.getWeight() + " ";
		}
		s = s.trim() + "}";
		s += " in_num=" + getInternalLinksNumber() + " ex_num=" + getExternalLinksNumber();
		return s;
	}
}
