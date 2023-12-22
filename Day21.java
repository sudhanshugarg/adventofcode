import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Collections;
import java.lang.Enum;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.Set;
import java.util.HashSet;

class Day21 {
  //down, left, up, right 
  public static long[][] dirs = {{-1L, 0L}, {0L, -1L}, {1L, 0L}, {0L, 1L}};
  public static void main(String[] args) {
    try {
      List<String> input = new ArrayList<>();

      BufferedReader reader = new BufferedReader(new FileReader(args[0]));
      String line = reader.readLine();
      long r = 0;
      Point start = new Point();
      while (line != null) {
        for (int i = 0; i < line.length(); i++) {
          if (line.charAt(i) == 'S') {
            start = new Point(r, (long) i, 0);
          }
        }
        input.add(line);
        line = reader.readLine();
        r++;
      }
      reader.close();
      
      long total = part1b(input, start, Integer.parseInt(args[1]));
      System.out.println("part 1: " + String.valueOf(total));

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static long part1(List<String> input, Point start, int exactSteps) {
    //bfs
    Queue<Point> q = new ArrayDeque<>();
    q.add(start);
    int m = input.size();
    int n = input.get(0).length();

    boolean[][][] added = new boolean[m][n][exactSteps+1];
    for (int i = 0; i < m; i++)
    for (int j = 0; j < n; j++)
    for (int k = 0; k <= exactSteps; k++)
      added[i][j][k] = false;

    added[(int)start.r][(int)start.c][(int)start.steps] = true;

    long nr, nc;
    long canVisit = 0;
    while(!q.isEmpty()) {
      Point p = q.poll();
      if (p.steps == exactSteps) {
        canVisit++;
        continue;
      }

      for (int i = 0; i < 4; i++) {
        nr = p.r + dirs[i][0];
        nc = p.c + dirs[i][1];
        if (nr < 0 || nr >= m || nc < 0 || nc >= n || input.get((int)nr).charAt((int)nc) == '#') continue;
        if (added[(int)nr][(int)nc][(int)p.steps+1]) continue;

        Point np = new Point(nr, nc, p.steps + 1);
        q.add(np);
        added[(int)np.r][(int)np.c][(int)np.steps] = true;
      }
    }
    return canVisit;
  }

  public static long part1b(List<String> input, Point start, int exactSteps) {
    int m = input.size();
    int n = input.get(0).length();
    int[][] prev = new int[m][n];
    int[][] curr = new int[m][n];

    for (int i = 0; i < m; i++)
      for (int j = 0; j < n; j++) {
        prev[i][j] = curr[i][j] = 0;
      }

    prev[(int)start.r][(int)start.c] = 1;
    int nr, nc;
    for (int k = 0; k < exactSteps; k++) {

      for (int i = 0; i < m; i++)
        for (int j = 0; j < n; j++) {
          //if (input.get(i).charAt(j) == '#') continue;
          for (int d = 0; d < 4; d++) {
            nr = i + (int)dirs[d][0];
            nc = j + (int)dirs[d][1];
            if (nr < 0 || nr >= m || nc < 0 || nc >= m) continue;

            if (prev[nr][nc] > 0) {
              curr[i][j] = 1;
              break;
            }
          }
        }

      update(prev, curr, m, n, false);
      update(curr, curr, m, n, true);
    }

    return printState(prev, m, n, input);
  }

  public static long printState(int[][] arr, int m, int n, List<String> input) {
    long ans = 0;
    long rowcount;
    for (int i = 0; i < m; i++) {
      rowcount = 0;
      for (int j = 0; j < n; j++) {
        if (input.get(i).charAt(j) == '#') System.out.print('#');
        else System.out.print(arr[i][j]);
        if (arr[i][j] > 0) rowcount ++;
      }
      ans += rowcount;
      System.out.println(" " + String.valueOf(rowcount));
    }
    System.out.println();
    return ans;
  }

  public static void update(int[][] to, int[][] from, int m, int n, boolean setToZero) {
    for (int i = 0; i < m; i++)
      for (int j = 0; j < n; j++) {
        if (setToZero) to[i][j] = 0;
        else to[i][j] = from[i][j];
      }
  }

}

class Point {
  public long r;
  public long c;
  public long steps;

  Point() {}
  Point(long a, long b, long s) {
    r = a; c = b; steps = s;
  }
}
