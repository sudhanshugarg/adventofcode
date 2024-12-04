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

      Day4 day = new Day4(locations);
      long total = day.part1();
      System.out.println(total);
      total = day.part2();
      System.out.println(total);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}


public class Day4 {
    private List<String> inputs;
    private int[][] dirs = {{0, -1}, {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}}; //L, UL, UP, UR, R, DR, D, DL
    Day4(List<String> arr) {
        inputs = arr;
    }

    public long part2() {
      int m = inputs.size();
      int n = inputs.get(0).length();

      int[][] counts = new int[m][n];
      //printGrid();
      char[] find = new char[4];
      find[0] = 'S'; find[1] = 'A'; find[2] = 'M'; find[3] = 'X';

      char c;
      int nr, nc;
      long total = 0;
      for (int letter = 0; letter < 4; letter++) {
        for (int i = 0; i < m; i++) {
          String row = inputs.get(i);
          for (int j = 0; j < n; j++) {
            c = row.charAt(j);
            if (c != find[letter]) continue;
            //System.out.println(String.format("testing char %c, r: %d, c: %d", inputs.get(i).charAt(j), i, j));

            if (letter == 0) {
              counts[i][j] = 1;
              continue;
            }

            for (int d = 0; d < 8; d++) {
              nr = i + dirs[d][0];
              nc = j + dirs[d][1];
              if (nr < 0 || nr >= m || nc < 0 || nc >= n) continue;
              if (inputs.get(nr).charAt(nc) == find[letter-1])
                counts[i][j] += counts[nr][nc];
            }

            if (letter == 3) total += counts[i][j];
          }
        }
      }
      //printCounts(counts);

      return total;
    }
    private void printGrid() {
      int m = inputs.size();
      int n = inputs.get(0).length();

      for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
          System.out.print(String.format("%3c,", inputs.get(i).charAt(j)));
        }
        System.out.println();
      }
    }

    private void printCounts(int [][] counts) {
      int m = counts.length;
      int n = counts[0].length;

      for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
          System.out.print(String.format("%3d,", counts[i][j]));
        }
        System.out.println();
      }
    }

    public long part1() {
      int m = inputs.size();
      int n = inputs.get(0).length();

      int[][] counts = new int[m][n];
      //printGrid();
      char[] find = new char[4];
      find[0] = 'S'; find[1] = 'A'; find[2] = 'M'; find[3] = 'X';

      char c;
      int nr, nc;
      long total = 0;
      int letter;
      for (int i = 0; i < m; i++) {
        String row = inputs.get(i);
        for (int j = 0; j < n; j++) {
          c = row.charAt(j);
          if (c != 'X') continue;
          //System.out.println(String.format("testing char %c, r: %d, c: %d", inputs.get(i).charAt(j), i, j));

          //test every direction
          for (int d = 0; d < 8; d++) {
            nr = i; nc = j;
            for (letter = 2; letter >= 0; letter--) {
              nr += dirs[d][0];
              nc += dirs[d][1];
              if (!ok(nr, nc, m, n)) break;
              if (inputs.get(nr).charAt(nc) != find[letter]) break;
            }
            if (letter < 0) total++;
          }
        }
      }

      return total;
    }

    private boolean ok(int r, int c, int m, int n) {
      if (r < 0 || r >= m || c < 0 || c >= n) return false;
      return true;
    }
}
