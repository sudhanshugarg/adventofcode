import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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

      Day10 day = new Day10(locations);
      long total = day.part1();
      System.out.println(total);
      total = day.part2();
      System.out.println(total);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}


public class Day10 {
    private final int[][] dirs = {{0, -1}, {-1, 0}, {0, 1}, {1, 0}}; //L, U, R, D
    private List<String> inputs;
    Day10(List<String> arr) {
        inputs = arr;
    }

    public long part1() {
      long total = 0;

      int m = inputs.size();
      int n = inputs.get(0).length();
      char c;

      for (int i = 0; i < m; i++)
      for (int j = 0; j < n; j++) {
        c = inputs.get(i).charAt(j);
        if (c != '9') continue;
        boolean[][] visited = new boolean[m][n];
        total += dfs(i, j, visited, c);
      }
      return total;
    }

    int dfs(int r, int c, boolean[][] visited, char topo) {
      visited[r][c] = true;

      if (topo == '0') return 1;

      int m = visited.length;
      int n = visited[0].length;

      int nr, nc;
      char nextTopo;
      int count = 0;
      for (int i = 0; i < dirs.length; i++) {
        nr = r + dirs[i][0];
        nc = c + dirs[i][1];

        if (nr < 0 || nr >= m || nc < 0 || nc >= n || visited[nr][nc]) continue;
        nextTopo = inputs.get(nr).charAt(nc);
        if (nextTopo != (topo - 1)) continue;

        count += dfs(nr, nc, visited, nextTopo);
      }
      return count;
    }

    public long part2() {
      int m = inputs.size();
      int n = inputs.get(0).length();
      long[][] waysToTrailHead = new long[m][n];
      boolean[][] addedToQueue = new boolean[m][n];

      Queue<Node> fifo = new ArrayDeque<>();

      Node curr, next;
      char c;
      for (int i = 0; i < m; i++)
      for (int j = 0; j < n; j++) {
        c = inputs.get(i).charAt(j);
        if (c == '0') {
          curr = new Node(i, j);
          fifo.offer(curr);
          waysToTrailHead[i][j] = 1;
          addedToQueue[i][j] = true;
        }
      }

      long total = 0;

      int nr, nc;
      char adj;
      while (!fifo.isEmpty()) {
        curr = fifo.poll();
        c = inputs.get(curr.r).charAt(curr.c);

        if (c == '9') {
          total += waysToTrailHead[curr.r][curr.c];
        }

        for (int i = 0; i < dirs.length; i++) {
          nr = curr.r + dirs[i][0];
          nc = curr.c + dirs[i][1];

          if (nr < 0 || nr >= m || nc < 0 || nc >= n) {
            continue;
          }

          adj = inputs.get(nr).charAt(nc);
          if (adj != (c + 1)) continue;

          waysToTrailHead[nr][nc] += waysToTrailHead[curr.r][curr.c];

          if (!addedToQueue[nr][nc]) {
            next = new Node(nr, nc);
            fifo.offer(next);
            addedToQueue[nr][nc] = true;
          }
        }
      }

      return total;
    }
}

class Node {
  public int r;
  public int c;

  Node(int a, int b) {
    r = a;
    c = b;
  }
}