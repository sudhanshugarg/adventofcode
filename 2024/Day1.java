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

class Day1 {
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
      List<Long> left = new ArrayList<>();
      List<Long> right = new ArrayList<>();
      parse(locations, left, right);
      Collections.sort(left);
      Collections.sort(right);

      long total = part1(left, right);
      System.out.println(total);
      total = part2(left, right);
      System.out.println(total);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private static int part1(List<Long> left, List<Long> right) {
    int total = 0;
    int n = left.size();
    for (int i = 0; i < n; i++)
      total += Math.abs(left.get(i) - right.get(i));
    return total;
  }
  
  private static long part2(List<Long> left, List<Long> right) {
    Map<Long, Integer> count = new HashMap<>();
    long key;
    for (int i = 0; i < right.size(); i++) {
       key = right.get(i);
       if (count.containsKey(key)) {
          count.put(key, count.get(key) + 1);
       } else {
          count.put(key, 1);
       }
    }


    long total = 0;
    for (int i = 0; i < left.size(); i++) {
      key = left.get(i);
      total += key * count.getOrDefault(key, 0);
    }
    return total;
  }


  private static void parse(List<String> locations, List<Long> left, List<Long> right) {
    int n = locations.size();
    Pattern p = Pattern.compile("\\s+");
    for (int i = 0; i < n; i++) {
      String[] arr = p.split(locations.get(i));
      left.add(Long.parseLong(arr[0]));
      right.add(Long.parseLong(arr[1]));
    }
  }
}
