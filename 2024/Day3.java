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
import java.util.regex.Matcher;

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

      Day3 day = new Day3(locations);
      long total = day.part1();
      System.out.println(total);
      total = day.part2();
      System.out.println(total);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}


public class Day3 {
    private List<String> inputs;
    Day3(List<String> arr) {
        inputs = arr;
    }

    public long part1() { 
      Pattern mul = Pattern.compile("mul\\(\\d{1,3}?,\\d{1,3}?\\)");
      long total = 0;
      for (int i = 0; i < inputs.size(); i++) {
        String input = inputs.get(i);
        Matcher match = mul.matcher(input);
        while(match.find()) {
          //System.out.println(match.group());
          total += mult(match.group());
        }
      }
      return total; 
    }

    private long mult(String multiply) {
      String[] nums = multiply.substring(4, multiply.length() - 1).split(",");
      return Long.parseLong(nums[0]) * Long.parseLong(nums[1]);
    }

    public long part2() { return 0; }
}
