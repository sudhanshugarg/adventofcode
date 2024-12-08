import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
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

      Day7 day = new Day7(locations);
      long total = day.part1();
      System.out.println(total);
      total = day.part2();
      System.out.println(total);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}


public class Day7 {
    private List<Long> answers;
    private List<Long> maxPossible;
    private List<List<Long>> equations;
    Day7(List<String> arr) {
      answers = new ArrayList<>();
      equations = new ArrayList<>();
      maxPossible = new ArrayList<>();

      for (int i = 0; i < arr.size(); i++) {
        parse(arr.get(i));
      }
    }

    private void parse(String s) {
      int i;
      for (i = 0; i < s.length(); i++) {
        if (s.charAt(i) == ':') break;
      }
      long ans = Long.parseLong(s.substring(0, i));

      String[] numStr = s.substring(i+2).split("\\s+");
      List<Long> nums = new ArrayList<>();
      long maxAns = 1;
      for (i = 0; i < numStr.length; i++) {
        nums.add(Long.parseLong(numStr[i]));
        if (nums.get(i) == 1)
          maxAns++;
        else
          maxAns *= nums.get(i);
      }

      answers.add(ans);
      equations.add(nums);
      maxPossible.add(maxAns);
    }

    private boolean canConstruct(int equationId) {
      List<String> operators = new ArrayList<>();
      //System.out.println(String.format("testing [%d] == %s, max=%d", answers.get(equationId), equations.get(equationId), maxPossible.get(equationId)));
      return dfsHelper(equationId, 1, equations.get(equationId).get(0), operators);
    }

    private void printMethod(int equationId, List<String> operators) {
      long ans = answers.get(equationId);
      System.out.println(operators);
      //if (ans < 1000L || ans > 10000L) return;

      List<Long> nums = equations.get(equationId);
      System.out.println(String.format("[%d] == %s, max=%d", answers.get(equationId), equations.get(equationId), maxPossible.get(equationId)));
      String eval = "";
      for (int i = 0; i < operators.size(); i++) eval += "(";
      eval += String.valueOf(nums.get(0));
      for (int i = 1; i < nums.size(); i++) {
        eval += operators.get(i-1);
        eval += String.valueOf(nums.get(i));
        eval += ")";
      }
      String send = "echo \"" + eval + "\" | bc;" + String.valueOf(ans);
      System.out.println(send);
      //checkEquality(ans, eval);
      System.out.println();
    }

    private void checkEquality(long ans, String eval) {
      try {
        String send = "echo \"" + eval + "\" | bc";
        System.out.println(String.format("sending %s", send));
        ProcessBuilder pb = new ProcessBuilder(send); // Use "dir" for Windows
                                                      // Start the process
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "abc";
        while ((line = reader.readLine()) != null) {
          System.out.println(String.format("got result %s", line));
          long next = Long.parseLong(line);
          if (next != ans) {
            System.out.println("not equal");
          } else {
            System.out.println("yes");
          }
        }

        // Wait for the process to finish
        int exitCode = process.waitFor();
      } catch (IOException | InterruptedException e) {
      }
    }

    private boolean dfsHelper(int equationId, int start, long ansSoFar, List<String> operators) {
      long ans = answers.get(equationId);
      List<Long> nums = equations.get(equationId);

      //System.out.println(String.format("%d: %s, index: %d, ansSoFar: %d, ops: %s", ans, nums, start, ansSoFar, operators));

      //if (ans > maxPossible.get(equationId)) return false;
      if (ansSoFar > ans) return false;

      if (start >= nums.size()) {
        if (ansSoFar == ans) {
          printMethod(equationId, operators);
          return true;
        }
        return false;
      }

      boolean found = false;
      //try *
      operators.add(" * ");
      found = dfsHelper(equationId, start+1, ansSoFar * nums.get(start), operators);
      operators.remove(operators.size()-1);
      if (found) return true;
      //try +
      operators.add(" + ");
      found = dfsHelper(equationId, start+1, ansSoFar + nums.get(start), operators);
      operators.remove(operators.size()-1);
      if (found) return true;
      //try ||
      operators.add(" || ");
      found = dfsHelper(equationId, start+1, concat(ansSoFar, nums.get(start)), operators);
      operators.remove(operators.size()-1);
      if (found) return true;
      return false;
    }

    private long concat(long a, long b) {
      return Long.parseLong(String.valueOf(a) + String.valueOf(b));
    }

    public long part1() {
      long total = 0;
      for (int i = 0; i < answers.size(); i++) {
        if (canConstruct(i)) {
          total+=answers.get(i);
      //    if (answers.get(i) > 100000000)
          //System.out.println(String.format("[%d] == %s, max=%d", answers.get(i), equations.get(i), maxPossible.get(i)));
        } else {
          //System.out.println(String.format("[%d] != %s, max=%d", answers.get(i), equations.get(i), maxPossible.get(i)));
        }
      }
      return total;
    }

    public long part2() {
      return 0;
    }
}
