import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.lang.Comparable;
import java.util.regex.Pattern;

class Solution {
  public static void main(String[] args) {
    try {
      BufferedReader bufreader = new BufferedReader(new FileReader(args[0]));
      String line = bufreader.readLine();
      List<String> locations = new ArrayList<>();
      while(line != null) {
	locations.add(line);
        line = bufreader.readLine();
      }
      bufreader.close();

      Day2 day = new Day2(locations);
      long total = day.part1();
      System.out.println(total);
      total = day.part2();
      System.out.println(total);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}


public class Day2 {
    private List<List<Integer>> levels;
    Day2(List<String> inputs) {
        levels = parse(inputs);
    }

    private List<List<Integer>> parse(List<String> inputs) {
       List<List<Integer>> result = new ArrayList<>();
       int n = inputs.size();
       Pattern p = Pattern.compile("\\s+");
       for (int i = 0; i < n; i++) {
           List<Integer> next = new ArrayList<>();
           String[] s = p.split(inputs.get(i));
           for (int j = 0; j < s.length; j++) {
               next.add(Integer.parseInt(s[j]));
           }
           result.add(next);
       }
       return result;
    }

    public long part1() { 
       long safe = 0;
       for (int i = 0; i < levels.size(); i++) {
           if (isSafe(levels.get(i))) safe++;
       }
       return safe; 
    }

    private boolean isSafe(List<Integer> arr) {
        if (arr.size() < 2) return true;
        boolean increasing = false;
        if (arr.get(0) < arr.get(1)) increasing = true;

        int n = arr.size();
        int a, b;
        for (int i = 0; i < n-1; i++) {
           a = arr.get(i);
           b = arr.get(i+1);
           int diff = Math.abs(a-b);
           if (diff < 1 || 
               diff > 3 || 
               (increasing && a > b) || 
               (!increasing && a < b)) {
               return false;
           }
        }

        return true;
    }
    public long part2() { return 0; }
}
