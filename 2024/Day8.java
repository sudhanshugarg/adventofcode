import java.io.BufferedReader;
import java.io.FileReader;
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

      Day8 day = new Day8(locations);
      long total = day.part1();
      System.out.println(total);
      total = day.part2();
      System.out.println(total);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}


public class Day8 {
    private List<String> inputs;
    private Map<Character, List<int[]>> antennas;
    private int m, n;
    Day8(List<String> arr) {
        inputs = arr;
        antennas = new HashMap<>();

        m = arr.size();
        n = arr.get(0).length();
        char c;

        for (int i = 0; i < m; i++) {
          for (int j = 0; j < n; j++) {
            c = inputs.get(i).charAt(j);
            if (c == '.') continue;
            if (!antennas.containsKey(c)) antennas.put(c, new ArrayList<>());

            int[] pos = new int[2];
            pos[0] = i; pos[1] = j;
            antennas.get(c).add(pos);
          }
        }
    }

    private long findAntiNodes(int[] p1, int[] p2, boolean[][] visited, boolean continuous) {
      isValid(p1);
      isValid(p2);
      long count = 0;

      int xdiff = p2[0] - p1[0];
      int ydiff = p2[1] - p1[1];

      int[] point = new int[2];
      boolean validFlag = true;

      point[0] = p1[0];
      point[1] = p1[1];
      while(validFlag) {
        point[0] -= xdiff;
        point[1] -= ydiff;
        validFlag = isValid(point);
        if (validFlag && !visited[point[0]][point[1]]) {
          count++;
          visited[point[0]][point[1]] = true;
        }
        validFlag = validFlag && continuous;
      }

      point[0] = p2[0];
      point[1] = p2[1];
      validFlag = true;
      while(validFlag) {
        point[0] += xdiff;
        point[1] += ydiff;
        validFlag = isValid(point);
        if (validFlag && !visited[point[0]][point[1]]) {
          count++;
          visited[point[0]][point[1]] = true;
        }
        validFlag = validFlag && continuous;
      }

      //System.out.println(count);
      if (continuous && !visited[p1[0]][p1[1]]) {
        count++;
        visited[p1[0]][p1[1]] = true;
      }
      if (continuous && !visited[p2[0]][p2[1]]) {
        count++;
        visited[p2[0]][p2[1]] = true;
      }

      return count;
    }

    private boolean isValid(int[] p) {
      boolean invalid = (p[0] < 0 || p[1] < 0 || p[0] >= m || p[1] >= n);
      //System.out.println(String.format("testing: [%d,%d]: %s, in [%d, %d]", p[0], p[1], !invalid, m, n));

      return !invalid;
    }

    public long common(boolean continuous) {
      long total = 0;
      boolean[][] visited = new boolean[m][n];
      int s;
      int[] p1, p2;

      for (Character c : antennas.keySet()) {
        List<int[]> positions = antennas.get(c);
        //printPos(c, positions);
        s = positions.size();
        for (int i = 0; i < s; i++) {
          p1 = positions.get(i);
          for (int j = i+1; j < s; j++) {
            p2 = positions.get(j);
            total += findAntiNodes(p1, p2, visited, continuous);
          }
        }
      }
      return total;
    }

    private void printPos(char c, List<int[]> posns) {
      System.out.print(String.format("%c: ", c));

      for (int i = 0; i < posns.size(); i++) {
        int[] p = posns.get(i);
        System.out.print(String.format("[%d,%d], ", p[0], p[1]));
      }
      System.out.println();
    }

    public long part1() { return common(false); }
    public long part2() { return common(true); }
}
