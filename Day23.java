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

//run with java -Xss12040k Day23 23_sample 
class Day23 {
  public static int[][] dirs = {{1, 0}, {0, -1}, {-1, 0}, {0, 1}}; //down, left, up, right
  public static char[] dirStr = {'D', 'L', 'U', 'R'};

  public static void main(String[] args) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(args[0]));
      String line = reader.readLine();
      int id = 0;

      List<String> hikingmap = new ArrayList<>();
      while (line != null) {
        hikingmap.add(line);
        line = reader.readLine();
      }
      reader.close();

      int total = part1(hikingmap, true);
      System.out.println("part 1: " + String.valueOf(total));

      total = part1(hikingmap, false);
      System.out.println("part 2: " + String.valueOf(total));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static int part1(List<String> hikingmap, boolean isPart1) {
    int m = hikingmap.size();
    int n = hikingmap.get(0).length();

    char [][] trail = new char[m][n];
    int[][] longestpath = new int[m][n];
    boolean[][] tempVisited = new boolean[m][n];
    boolean[][][] permVisited = new boolean[m][n][4];
    for (int i = 0; i < m; i++)
      for (int j = 0; j < n; j++) {
        trail[i][j] = '.';
        longestpath[i][j] = 0;
        tempVisited[i][j] = false;
        if (hikingmap.get(i).charAt(j) == '#') {
          tempVisited[i][j] = true;
          for (int k = 0; k < 4; k++) permVisited[i][j][k] = true;
        } else {
          for (int k = 0; k < 4; k++) permVisited[i][j][k] = false;
        }
      }

    //ends0 is start and ends1 is end
    int[] ends = new int[2];
    for (int i = 0; i < 2; i++)
    for (int j = 0; j < n; j++) {
      if (hikingmap.get((i+n-1) % n).charAt(j) == '.') {
        ends[1-i] = j;
        break;
      }
    }
    
    if(!isPart1) {
      dfs2(hikingmap, longestpath, tempVisited, ends, 0, ends[0], 0);
      return longestpath[m-1][ends[1]];
    }

    dfs(hikingmap, longestpath, tempVisited, permVisited, ends, 0, ends[0], trail, isPart1, 3);
    /*
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        if (trail[i][j] == '.') System.out.print(hikingmap.get(i).charAt(j));
        else System.out.print(trail[i][j]);
      }
      System.out.println();
    }
    System.out.println();
    */
    return longestpath[0][ends[0]] - 1;
  }

  public static void dfs(List<String> hikingmap, int[][] longestpath, boolean[][] tempVisited, boolean[][][] permVisited, 
                         int[] ends, int r, int c, char[][] trail, boolean isPart1, int directionFrom) {
    int m = hikingmap.size();
    int n = hikingmap.get(0).length();

    //System.out.println("Visiting: " + String.valueOf(r) + "," + String.valueOf(c));
    if (permVisited[r][c][directionFrom]) return;

    if (tempVisited[r][c]) {
      System.out.println("this should never have happened");
      return;
    }

    if (r == m-1 && c == ends[1]) {
      //reached the end
      longestpath[r][c] = 1;
      permVisited[r][c][directionFrom] = true;
      return;
    }

    int nr, nc;
    char pos = hikingmap.get(r).charAt(c);
    int dirStart = 0, dirEnd = 3;
    trail[r][c] = '.';
    if (isPart1) {
    switch(pos) {
      case 'v':
        dirStart = 0; dirEnd = 0;
      break;
      case '<':
        dirStart = 1; dirEnd = 1;
      break;
      case '^':
        dirStart = 2; dirEnd = 2;
      break;
      case '>':
        dirStart = 3; dirEnd = 3;
      break;
    }}

    tempVisited[r][c] = true;
    int longest = 0;
    for (int i = dirStart; i <= dirEnd; i++) {
      nr = r + dirs[i][0];
      nc = c + dirs[i][1];
      if (nr < 0 || nr >= m || nc < 0 || nc >= n || tempVisited[nr][nc]) continue;

      dfs(hikingmap, longestpath, tempVisited, permVisited, ends, nr, nc, trail, isPart1, directionFrom);
      if (longestpath[nr][nc] > 0) {
        if (longest < 1+longestpath[nr][nc]) {
          longest = 1+longestpath[nr][nc];
          trail[r][c] = dirStr[i];
        }
      }
    }
    longestpath[r][c] = longest > longestpath[r][c] ? longest : longestpath[r][c];
    tempVisited[r][c] = false;
    if (longest > 0) permVisited[r][c][directionFrom] = true;
  }

  public static void dfs2(List<String> hikingmap, int[][] longestpath, boolean[][] tempVisited, int[] ends, int r, int c, int dist) {
    int m = hikingmap.size();
    int n = hikingmap.get(0).length();

    if (tempVisited[r][c]) {
      return;
    }

    //System.out.println("Visiting: " + String.valueOf(r) + "," + String.valueOf(c));

    if (r == m-1 && c == ends[1]) {
      //reached the end
      longestpath[r][c] = dist > longestpath[r][c] ? dist : longestpath[r][c];
      return;
    }

    int nr, nc;
    char pos = hikingmap.get(r).charAt(c);
    int dirStart = 0, dirEnd = 3;
    tempVisited[r][c] = true;
    int longest = 0;
    for (int i = dirStart; i <= dirEnd; i++) {
      nr = r + dirs[i][0];
      nc = c + dirs[i][1];
      if (nr < 0 || nr >= m || nc < 0 || nc >= n || tempVisited[nr][nc]) continue;

      dfs2(hikingmap, longestpath, tempVisited, ends, nr, nc, dist + 1);
      if (longestpath[nr][nc] > 0) {
        if (longest < 1+longestpath[nr][nc]) {
          longest = 1+longestpath[nr][nc];
        }
      }
    }
    longestpath[r][c] = longest;
    tempVisited[r][c] = false;
  }
}
