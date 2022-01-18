package jgrapht.grader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class CorrectAnswer {
    public List<List<Integer>> path;
    public CorrectAnswer(String file) {	
        try {
        	path = new ArrayList<List<Integer>>();
            Scanner scanner = new Scanner(new File(file));
            scanner.useLocale(Locale.ENGLISH);
            scanner.useDelimiter(System.getProperty("line.separator"));
            while(scanner.hasNext()){
            	List<Integer> si = new ArrayList<Integer>();
            	String str = scanner.next();
            	Scanner s = new Scanner(str);
                //System.out.println("s="+ str + "\n");
                while (s.hasNextInt()) {
                    Integer x = s.nextInt();
                    si.add(x);
                }
            	path.add(si);
            	s.close();
            }
            scanner.close();
            //System.out.println("path="+ path.toString() + "\n");
        } catch (Exception e) {
            System.err.println("Error reading correct answer!");
            e.printStackTrace();
        }
    }
}
