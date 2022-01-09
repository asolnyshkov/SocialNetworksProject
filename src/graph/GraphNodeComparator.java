/**
 * @author Aleksandr Solnyshkov.
 * 
 * Comparator for Nodes.  
 *
 */
package graph;

import java.util.Comparator;

public class GraphNodeComparator implements Comparator<GraphNode> {

	@Override
	public int compare(GraphNode o1, GraphNode o2) {
		double o1Distance = o1.getDistance();
		double o2Distance = o2.getDistance();
        if (o1Distance < o2Distance) {
            return -1;
        }
        if (o1Distance > o2Distance) {
            return 1;
        }
		return 0;
	}

}
