/**
 * @author UCSD MOOC development team
 * 
 * Grader for the checking of the Dijkstra algorithm. Runs implementation against
 * ten nodes from the Facebook dataset. 
 *
 */

package graph.grader;

import java.util.List;

import util.GraphLoader;
import graph.CapGraph;


public class DijkstraGrader extends Grader {
    private static final int TESTS = 4;

    public static void main(String[] args) {
        Grader grader = new DijkstraGrader();
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
            runTest(1, "dij/test_1.txt", "Small test graph 1", 2, 6);
            runTest(2, "dij/test_2.txt", "Small test graph 2", 2, 9);
            runTest(3, "dij/test_3.txt", "Small test graph 3", 1, 14);
            runTest(4, "dij/test_4.txt", "Small test graph 4", 4, 11);

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
    public void runTest(int i, String file, String desc, Integer start, Integer end) {
    	CapGraph graph = new CapGraph();

        feedback += "\n\n" + desc;
        GraphLoader.loadGraph(graph, "data/" + file);
        CorrectAnswer corr = new CorrectAnswer("data/dij_answers/dij_" + i + ".txt");

        judge(i, graph, corr, start, end);
    }
    
    /** Compare the user's result with the right answer.
     * @param i The graph number
     * @param graph The user's graph
     * @param corr The correct answer
     * @param start The point to start from
     * @param end The point to end at
     */
    public void judge(int i, CapGraph graph, CorrectAnswer corr, Integer start, Integer end) {
        // Correct if paths are same length and have the same elements
        feedback += appendFeedback(i, "Running Dijkstra's algorithm from (" + start + ") to (" + end + ")");
        List<Integer> path = graph.dijkstra(start, end);
        if (path == null) {
            if (corr.path == null) {
                feedback += "PASSED.";
                correct++;
            } else {
                feedback += "FAILED. Your implementation returned null; expected \n" + printPath(corr.path) + ".";
            }
        } else if (path.size() != corr.path.size() || !corr.path.containsAll(path)) {
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
    public String printPath(List<Integer> path) {
        String ret = "";
        for (Integer point : path) {
            ret += point + "\n";
        }
        return ret;
    }
}
