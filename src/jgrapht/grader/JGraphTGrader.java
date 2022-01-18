/**
 * @author Aleksandr Solnyshkov
 * 
 * Grader for the JGraphT library assignment. 
 *
 */
package jgrapht.grader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.clustering.GirvanNewmanClustering;
import org.jgrapht.alg.clustering.KSpanningTreeClustering;
import org.jgrapht.alg.clustering.LabelPropagationClustering;
import org.jgrapht.alg.interfaces.ClusteringAlgorithm.Clustering;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import jgrapht.grader.CorrectAnswer;
import graph.grader.Grader;
import util.GraphTLoader;

public class JGraphTGrader extends graph.grader.Grader {
    private static final int TESTS = 16;

    public static void main(String[] args) {
        Grader grader = new JGraphTGrader();
        Thread thread = new Thread(grader);
        thread.start();

        // Safeguard against infinite loops
        long endTime = System.currentTimeMillis() + 30000;
        boolean infinite = false;
        while (thread.isAlive()) {
            if (System.currentTimeMillis() > endTime) {
                thread.stop();
                infinite = true;
                break;
            }
        }
        //boolean infinite = false;
        if (grader.correct < TESTS) {
        	grader.feedback = "Some tests failed. Please check the following and try again:\nFailed tests will display the first mismatched lines of the output.\n" + grader.feedback;
        } else {
        	grader.feedback = "All tests passed. Congrats!\n" + grader.feedback;
        }
        if (infinite) {
            grader.feedback += "Your program entered an infinite loop or took longer than 30 seconds to finish.";
        }
        System.out.println(makeOutput((double)grader.correct / TESTS, grader.feedback));
    }

    /* Main grading method */
    public void run() {
        feedback = "";
        correct = 0;

        try {
            runTest(1, "Test graph 1", 2, 6, 2);
            runTest(2, "Test graph 2", 5, 8, 3);
            runTest(3, "Test graph 3", 2, 14, 4);
            runTest(4, "Test graph 4", 2, 14, 4);

            if (correct == TESTS)
                feedback = "All tests passed. Great job!" + feedback;
            else
                feedback = "Some tests failed. Check your code for errors, then try again:" + feedback;

        } catch (Exception e) {
            feedback += "\nError during runtime: " + e;
            e.printStackTrace();
        }
    }
    
    /** Run a test case on an adjacency list.
     * @param i The graph number
     * @param file The file to read from
     * @param desc A description of the graph
     * @param start The point to start from
     * @param end The point to end at
     */
    public void runTest(int i, String desc, Integer start, Integer end, int k) {

    	Graph<Integer, DefaultEdge> graph =
                new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        feedback += "\n\n" + desc;
        GraphTLoader.loadGraph(graph, "data/jgrapht/test_" + i + ".txt");
        
        CorrectAnswer corr = new CorrectAnswer("data/jgrapht_answers/dij_" + i + ".txt");
        judgeDijkstra(i, graph, corr, start, end);
        
        corr = new CorrectAnswer("data/jgrapht_answers/gn_" + i + ".txt");
        judgeGirvanNewman(i, graph, corr, k);
        corr = new CorrectAnswer("data/jgrapht_answers/st_" + i + ".txt");
        judgeKSpanningTree(i, graph, corr, k);
        corr = new CorrectAnswer("data/jgrapht_answers/lp_" + i + ".txt");
        judgeLabelPropagation(i, graph, corr, k);
    }
    
    /** Compare the user's result with the right answer.
     * @param i The graph number
     * @param graph The user's graph
     * @param corr The correct answer
     * @param start The point to start from
     * @param end The point to end at
     */
    public void judgeDijkstra(int i, Graph<Integer, DefaultEdge> graph, CorrectAnswer corr, Integer start, Integer end) {

        // Prints the shortest path from vertex "start" to vertex "end". 
        feedback += appendFeedback(i, "Running JGraphT's DijkstraShortestPath algorithm from (" + start + ") to (" + end + ")");
        DijkstraShortestPath<Integer, DefaultEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        List<Integer> list = dijkstraShortestPath.getPath(start,end).getVertexList();
        //System.out.println(list + "\n");
        List<Integer> si = new ArrayList<Integer>(list);
        List<List<Integer>> path = new ArrayList<List<Integer>>();
        path.add(si);
    	printResult(path, corr);
    }
    
    /** Compare the user's result with the right answer.
     * @param i The graph number
     * @param graph The user's graph
     * @param corr The correct answer
     * @param k The number of clusters 
     */
    public void judgeGirvanNewman(int i, Graph<Integer, DefaultEdge> graph, CorrectAnswer corr, Integer k) {
    	
        // Correct if paths are same length and have the same elements 
        feedback += appendFeedback(i, "Running JGraphT's GirvanNewmanClusterin algorithm for k = " + k);

		/*
         * GirvanNewmanClustering​(Graph<V,​E> graph,
         *                    int k)
         * Create a new clustering algorithm.
         * Parameters:
         * graph - the graph
         * k - the desired number of clusters
         * */
        Clustering<Integer> c1 = new GirvanNewmanClustering<>(graph, k).getClustering();
        List<List<Integer>> path = new ArrayList<List<Integer>>();
        for (Set<Integer> element : c1) {
        	path.add(new ArrayList<Integer>(element));
        }
        //System.out.println(c1 + "\n");
    		
    	printResult(path, corr);
    }
    
    /** Compare the user's result with the right answer.
     * @param i The graph number
     * @param graph The user's graph
     * @param corr The correct answer
     * @param k The number of clusters 
     */
    public void judgeKSpanningTree(int i, Graph<Integer, DefaultEdge> graph, CorrectAnswer corr, Integer k) {
    	
        // Correct if paths are same length and have the same elements 
        feedback += appendFeedback(i, "Running JGraphT's KSpanningTreeClusterin algorithm for k = " + k);

        /*
         * KSpanningTreeClustering​(Graph<V,​E> graph,
         *                     int k)
         * Create a new clustering algorithm.
         * Parameters:
         * graph - the graph (needs to be undirected)
         * k - the desired number of clusters
         * */
        KSpanningTreeClustering<Integer, DefaultEdge> alg = new KSpanningTreeClustering<>(graph, k);
        Clustering<Integer> c1 = alg.getClustering();
        List<List<Integer>> path = new ArrayList<List<Integer>>();
        for (Set<Integer> element : c1) {
        	path.add(new ArrayList<Integer>(element));
        }
        //System.out.println(c1 + "\n");
    		
    	printResult(path, corr);
    }    
    /** Compare the user's result with the right answer.
     * @param i The graph number
     * @param graph The user's graph
     * @param corr The correct answer
     * @param k The number of clusters 
     */
    public void judgeLabelPropagation(int i, Graph<Integer, DefaultEdge> graph, CorrectAnswer corr, Integer k) {
    	
        // Correct if paths are same length and have the same elements 
        feedback += appendFeedback(i, "Running JGraphT's LabelPropagationClusterin algorithm for k = " + k);

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
        LabelPropagationClustering<Integer, DefaultEdge> alg = new LabelPropagationClustering<>(graph, 0, new Random(13));
        Clustering<Integer> c1 = alg.getClustering();
        List<List<Integer>> path = new ArrayList<List<Integer>>();
        for (Set<Integer> element : c1) {
        	path.add(new ArrayList<Integer>(element));
        }
        //System.out.println(c1 + "\n");
    		
    	printResult(path, corr);
    }
    
    /** Print a search path in readable form */
    public void printResult(List<List<Integer>> path, CorrectAnswer corr) {
        if (path == null) {
            if (corr.path == null) {
                feedback += "PASSED.";
                correct++;
            } else {
                feedback += "FAILED. Your implementation returned null; expected \n" + printPath(corr.path) + ".";
            }
        } else if (path.size() != corr.path.size() || !corr.path.equals(path)) {
            feedback += "FAILED. Expected: \n" + printPath(corr.path) + "Got: \n" + printPath(path);
            if (path.size() != corr.path.size()) {
                feedback += "Your result has size " + path.size() + "; expected " + corr.path.size() + ".";
            } else {
                feedback += "Correct size, but incorrect path.";
            }
        } else {
            feedback += "PASSED.";
            correct++;
        }
    }    
    
    /** Print a search path in readable form */
    public String printPath(List<List<Integer>> path) {
        String ret = "";
        for (List<Integer> element : path) {
            for (Integer point : element) {
                ret += point + " ";
            }
            ret += "\n";
        }
        return ret;
    }
}
