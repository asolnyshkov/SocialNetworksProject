/**
 * @author Aleksandr Solnyshkov.
 * 
 * Implementation of  Graph in a class
 * named TestJGraphT.  
 *
 */
package jgrapht;

import org.jgrapht.*;
import org.jgrapht.alg.clustering.*;
import org.jgrapht.alg.interfaces.ClusteringAlgorithm.Clustering;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.*;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.swing.mxGraphComponent;

import util.GraphLoader;
import util.GraphTLoader;

import java.awt.Component;
import java.awt.Dimension;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class TestJGraphT  {

    private static final Dimension DEFAULT_SIZE = new Dimension(800, 600);

    /**
     * Main demo entry point.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args)
    {
        // create a JGraphT graph
        Graph<Integer, DefaultEdge> graph =
                new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
        //GraphTLoader.loadGraph(graph, "data/jgrapht/test_3.txt");
        GraphTLoader.loadGraph(graph, "data/jgrapht/test_3.txt");
        if (graph.vertexSet().size() > 0) {
	        // create a visualization using JGraph, via an adapter
        	JGraphXAdapter<Integer, DefaultEdge> jgxAdapter = new JGraphXAdapter<>(graph);
        	
        	jgxAdapter.getModel().beginUpdate();
 
	        mxGraphComponent component = new mxGraphComponent(jgxAdapter);
	        
	        component.setConnectable(false);
	        component.getGraph().setAllowDanglingEdges(false);
        
	        // positioning via jgraphx layouts
            mxIGraphLayout layout = new mxFastOrganicLayout(jgxAdapter);	
	        layout.execute(jgxAdapter.getDefaultParent());
	        jgxAdapter.getModel().endUpdate();
	           
	        System.out.println(graph);
	        
	        // Prints the shortest path from vertex 2 to vertex 6. This certainly
	        // exists for our particular directed graph.
	        System.out.println("Shortest path from 2 to 6:");
	        DijkstraShortestPath<Integer, DefaultEdge> dijkstraAlg = new DijkstraShortestPath<>(graph);
	        SingleSourcePaths<Integer, DefaultEdge> iPaths = dijkstraAlg.getPaths(2);
	        System.out.println(iPaths.getPath(6) + "\n");
	        
	        DijkstraShortestPath<Integer, DefaultEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
	        List<Integer> shortestPath = dijkstraShortestPath.getPath(2,6).getVertexList();
	        System.out.println(shortestPath + "\n");
	        
	        /*
	         * GirvanNewmanClustering​(Graph<V,​E> graph,
	         *                    int k)
	         * Create a new clustering algorithm.
	         * Parameters:
	         * graph - the graph
	         * k - the desired number of clusters
	         * */
	        int k = 4;
	        Clustering<Integer> c1 = new GirvanNewmanClustering<>(graph, k).getClustering();
	        System.out.println(c1 + "\n");
	        
	        /*
	         * KSpanningTreeClustering​(Graph<V,​E> graph,
	         *                     int k)
	         * Create a new clustering algorithm.
	         * Parameters:
	         * graph - the graph (needs to be undirected)
	         * k - the desired number of clusters
	         * */
	        KSpanningTreeClustering<Integer, DefaultEdge> alg1 = new KSpanningTreeClustering<>(graph, k);
	        Clustering<Integer> c2 = alg1.getClustering();
	        System.out.println(c2 + "\n");
	        
	        /*
	         * LabelPropagationClustering​(Graph<V,​E> graph,
	         *                        int maxIterations,
	         *                        java.util.Random rng)
	         * Create a new clustering algorithm.
	         * Parameters:
	         * graph - the graph (needs to be undirected)
	         * maxIterations - maximum number of iterations (zero means no limit)
	         * rng - random number generator
	         * */
	        LabelPropagationClustering<Integer, DefaultEdge> alg2 = new LabelPropagationClustering<>(graph, 0, new Random(13));
	        Clustering<Integer> c3 = alg2.getClustering();
	        System.out.println(c3 + "\n");

	        String title = "JGraphT Demo";
	        JFrame frame = new JFrame();
	
	        frame.setTitle(String.format("%s (V = %d, E = %d)", title, graph.vertexSet().size(), graph.edgeSet().size()));
	        frame.setSize(DEFAULT_SIZE);
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.getContentPane().add(new JScrollPane(component));
	        frame.pack();
	        frame.setVisible(true);
        }
    }
 
}
