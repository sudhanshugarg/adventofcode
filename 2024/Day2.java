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

    private boolean isSafeBrute(List<Integer> arr) {
      int n = arr.size();
      for (int i = 0; i < n; i++) {
        List<Integer> newArr = new ArrayList<>();
        for (int j = 0; j < n; j++) {
          if (i == j) continue;
          newArr.add(arr.get(j));
        }
        if (isSafe(newArr)) return true;
      }
      return false;
    }

    private boolean isSafe2(List<Integer> arr) {
        //System.out.println(String.format("testing %s", arr.toString()));
        int n = arr.size();
        if (n < 3) return true;

        boolean ok = false;

        int[] faultyIndex = new int[1];
        int fault;
        //increasing
        ok = safeHelper(true, 0, 1, arr, faultyIndex);
        if (ok) return true;
        fault = faultyIndex[0];

        //1, 3, 2, 6: leave 2
        //f = 2, f-1, f+1
        ok = safeHelper(true, fault - 1, fault + 1, arr, faultyIndex);
        if (ok) return true;

        //1, 3, 2, 3: leave 3
        //f = 2, f-2, f
        ok = safeHelper(true, fault - 2, fault, arr, faultyIndex);
        if (ok) return true;

        //decreasing
        ok = safeHelper(false, 0, 1, arr, faultyIndex);
        if (ok) return true;
        fault = faultyIndex[0];

        ok = safeHelper(false, fault - 1, fault + 1, arr, faultyIndex);
        if (ok) return true;

        ok = safeHelper(false, fault - 2, fault, arr, faultyIndex);
        if (ok) return true;

        return false;
    }

    private boolean safeHelper(boolean increasing, int currIndex, int startIndex, List<Integer> arr, int[] faultyIndex) {
        //System.out.println(String.format("in incr: %s, currIndex: %d, startIndex: %d", increasing, currIndex, startIndex));
        int n = arr.size();
        if (startIndex >= n) return true;
        if (currIndex < 0) {
            currIndex = startIndex;
            startIndex++;
        }

        int a, b;
        a = arr.get(currIndex);
        for (int i = startIndex; i < n; i++) {
            b = arr.get(i);
            if (increasing) {
                if (b <= a || ((b - a) > 3)) {
                    faultyIndex[0] = i;
                    return false;
                }
            } else {
                if (b >= a || ((a - b) > 3)) {
                    faultyIndex[0] = i;
                    return false;
                }
            }
            a = b;
        }

        return true;
    }

    public long part2() {
        //check with the following conditions
        //decreasing
        //increasing
        //given current num, index from where to look, and integer
        

        long safe = 0;
        boolean safe1, safe2;
        for (int i = 0; i < levels.size(); i++) {
            safe1 = isSafeBrute(levels.get(i));
            safe2 = isSafe2(levels.get(i));
            if (safe2) {
                safe++;
            }
            if (safe1 != safe2) {
                System.out.println(String.format("safe1: %s, safe2: %s, level: %s", safe1, safe2, levels.get(i)));
            }
        }
        return safe;
    }
}
