/**
 * @author Aleksandr Solnyshkov.
 * 
 * Comparator for Edges.  
 *
 */
package graph;

import java.util.Comparator;

public class GraphEdgeComparator implements Comparator<GraphEdge> {
	
	@Override
	public int compare(GraphEdge o1, GraphEdge o2) {

		double o1Distance = o1.getWeight();
		double o2Distance = o2.getWeight();
        if (o1Distance < o2Distance) {
            return -1;
        }
        if (o1Distance > o2Distance) {
            return 1;
        }
		return 0;
	}
}
