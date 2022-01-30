package graph.grader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;

import graph.CapGraph;
import jgrapht.grader.CorrectAnswer;
import util.GraphLoader;

public class LouvainGrader extends Grader {
    private static final int TESTS = 16;

    public static void main(String[] args) {
        Grader grader = new LouvainGrader();
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

    	CapGraph graph = new CapGraph();

        feedback += "\n\n" + desc;
        GraphLoader.loadGraph(graph, "data/dij/test_" + i + ".txt");
        
        CorrectAnswer corr = new CorrectAnswer("data/lou_answers/lou_" + i + ".txt");
        judgeLouvain(i, graph, corr);
    }
    
    /** Compare the user's result with the right answer.
     * @param i The graph number
     * @param graph The user's graph
     * @param corr The correct answer
     * @param start The point to start from
     * @param end The point to end at
     */
    public void judgeLouvain(int i, CapGraph graph, CorrectAnswer corr) {

        // Prints the shortest path from vertex "start" to vertex "end". 
        feedback += appendFeedback(i, "Running Louvain algorithm");
        Set<List<Integer>> path = graph.getCommunities();

    	printResult(path, corr);
    }
    
    /** Print a search path in readable form */
    public void printResult(Set<List<Integer>> path, CorrectAnswer c) {
    	HashSet<List<Integer>> corr = new HashSet<List<Integer>>(c.path);
        if (path == null) {
            if (corr == null) {
                feedback += "PASSED.";
                correct++;
            } else {
                feedback += "FAILED. Your implementation returned null; expected \n" + printPath(corr) + ".";
            }
        } else if (path.size() != corr.size() || !corr.equals(path)) {
            feedback += "FAILED. Expected: \n" + printPath(corr) + "Got: \n" + printPath(path);
            if (path.size() != corr.size()) {
                feedback += "Your result has size " + path.size() + "; expected " + corr.size() + ".";
            } else {
                feedback += "Correct size, but incorrect path.";
            }
        } else {
            feedback += "PASSED.";
            correct++;
        }
    }    
    
    /** Print a search path in readable form */
    public String printPath(Set<List<Integer>> path) {
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
