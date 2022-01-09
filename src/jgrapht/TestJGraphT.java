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
import com.mxgraph.swing.mxGraphComponent;

import util.GraphLoader;
import util.GraphTLoader;

import java.awt.Dimension;
import java.util.*;

import javax.swing.JApplet;
import javax.swing.JFrame;

public class TestJGraphT extends JApplet {
	
    private static final long serialVersionUID = 2202072534703043194L;

    private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);

    private JGraphXAdapter<Integer, DefaultEdge> jgxAdapter;

    /**
     * Main demo entry point.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args)
    {
    	TestJGraphT applet = new TestJGraphT();
        applet.init();

        JFrame frame = new JFrame();
        frame.getContentPane().add(applet);
        frame.setTitle("JGraphT Adapter to JGraphX Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }
    @Override
    public void init()
    {
        // create a JGraphT graph
        Graph<Integer, DefaultEdge> graph =
                new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
        GraphTLoader.loadGraph(graph, "data/small_test_graph2.txt");

        // create a visualization using JGraph, via an adapter
        jgxAdapter = new JGraphXAdapter<>(graph);

        setPreferredSize(DEFAULT_SIZE);
        mxGraphComponent component = new mxGraphComponent(jgxAdapter);
        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(false);
        getContentPane().add(component);
        resize(DEFAULT_SIZE);
        

        
        // positioning via jgraphx layouts
        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);

        // center the circle
        int radius = 100;
        layout.setX0((DEFAULT_SIZE.width / 2.0) - radius);
        layout.setY0((DEFAULT_SIZE.height / 2.0) - radius);
        layout.setRadius(radius);
        layout.setMoveCircle(true);

        layout.execute(jgxAdapter.getDefaultParent());
        // that's all there is to it!...
        
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
        int k = 57;
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

    }
 
}
