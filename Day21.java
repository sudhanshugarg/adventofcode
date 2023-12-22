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

      total = part2(input, start, Integer.parseInt(args[1]));
      System.out.println("part 2: " + String.valueOf(total));
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
    if (exactSteps > 64) exactSteps = 64;

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
          if (input.get(i).charAt(j) == '#') continue;
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

    return printState(prev, m, n, input, false);
  }

  public static void calculateOnesFromLeft(int[][] ones, List<String> input, int m, int n) {
    int available = 1;
    for (int i = 0; i < m; i++) {
      for (int j = n-2; j < n+2; j++) ones[i][j] = 0;

      if (input.get(i).charAt(n-1) != '#') ones[i][n-1] = 1;
      if (input.get(i).charAt(n-2) != '#') ones[i][n-2] = 1;
      for (int j = n-3; j >= 0; j--) {
        if (input.get(i).charAt(j) != '#') available = 1;
        else available = 0;
        ones[i][j] = available + ones[i][j+2];
      }
    }
  }

  public static int countOnesUptoRight(int r, int c, int[][] ones) {
    if (c % 2 == 1) { //starting 1 from second col
      return ones[r][1] - ones[r][c+2];
    } else { //starting 1 from first col
      return ones[r][0] - ones[r][c+2];
    }
  }

  public static long part2(List<String> input, Point start, int exactSteps) {
    int m = input.size();
    int n = input.get(0).length();

    int k = exactSteps;
    int N = 2 * k + 1;

    int[][] ones = new int[m][n+2];
    calculateOnesFromLeft(ones, input, m, n);

    long ans = 0;
    int testing = 105;
    //line number k.
    int d, c_left, c_right, box_left, box_right, index_left, index_right, num_boxes_center, row;
    long rowcount = 0;
    for (int line = 0; line < N; line++) {
      if (line < k) d = k - line;
      else d = line - k;

      //line starts from column d to column N-d-1 inclusive
      c_left = d; //91
      c_right = N - d - 1; //393 - 91 -1 = 301

      box_left = c_left / n; // 0
      index_left = c_left % n; //91

      box_right = c_right / n; //2
      index_right = c_right % n; //301 % 131 = 39

      num_boxes_center = 0;
      row = line % m; //105, but 106th line

      //now, add up 1's.
      rowcount = 0;
      String msg = "";
      if (box_left != box_right) {
        if (line == testing) System.out.println("a" + String.valueOf(rowcount));

        rowcount += ones[row][index_left]; //ones[105][91]
        if (line == testing) System.out.println("b" + String.valueOf(rowcount));
        rowcount += countOnesUptoRight(row, index_right, ones); //ones[105][1] - ones[105][39+2]
        if (line == testing) System.out.println("c" + String.valueOf(rowcount));

        num_boxes_center = box_right - 1 - box_left;
        rowcount += ones[row][0] * (num_boxes_center / 2);
        rowcount += ones[row][1] * (num_boxes_center / 2);
        if (line == testing) System.out.println("d" + String.valueOf(rowcount));
        if (index_left % 2 == 1) rowcount += ones[row][0];
        else rowcount += ones[row][1];
        if (line == testing) System.out.println("e1" + String.valueOf(ones[row][0]));
        if (line == testing) System.out.println("e2" + String.valueOf(ones[row][1]));
        if (line == testing) System.out.println("e" + String.valueOf(rowcount));

      } else {
        rowcount = ones[row][index_left] - ones[row][index_right+2];
        if (line == testing) System.out.println("f" + String.valueOf(rowcount));
      }
      ans += rowcount;
      if (line == testing) {
        System.out.print("row = " + String.valueOf(line) + ", left col = " + String.valueOf(c_left) + ", right col = " + String.valueOf(c_right));
        System.out.println(" " + String.valueOf(rowcount));
      }
    }
    return ans;
  }

  public static long printState(int[][] arr, int m, int n, List<String> input, boolean shouldPrint) {
    long ans = 0;
    long rowcount;
    for (int i = 0; i < m; i++) {
      rowcount = 0;
      for (int j = 0; j < n; j++) {
        if (shouldPrint) {
          if (input.get(i).charAt(j) == '#') System.out.print('#');
          else System.out.print(arr[i][j]);
        }
        if (arr[i][j] > 0) rowcount ++;
      }
      ans += rowcount;
      if (shouldPrint) System.out.println(" " + String.valueOf(rowcount));
    }
    if (shouldPrint) System.out.println();
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
