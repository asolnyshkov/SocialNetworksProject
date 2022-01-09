package graph.grader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class CorrectAnswer {
    public List<Integer> path;
    public CorrectAnswer(String file) {
        try {
            Scanner s = new Scanner(new File(file));
            s.useLocale(Locale.ENGLISH);
            path = new ArrayList<Integer>();
            while (s.hasNextInt()) {
                Integer x = s.nextInt();
                path.add(x);
            }
            s.close();
        } catch (Exception e) {
            System.err.println("Error reading correct answer!");
            e.printStackTrace();
        }
    }
}
