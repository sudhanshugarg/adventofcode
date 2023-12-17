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

class Day17 {
  public static void main(String[] args) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(args[0]));
      String line = reader.readLine();
      List<String> floor = new ArrayList<>();

      while (line != null) {
        floor.add(line);
        line = reader.readLine();
      }
      reader.close();
      DjikstraCity c = new DjikstraCity();
      int total = c.minHeat(floor, 3);
      System.out.println(total);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}


class City {
  City() {}
  public static int[][] dirs = {{1, 0}, {0, -1}, {-1, 0}, {0, 1}}; //clockwise down, left, up, right

  public int minHeat(List<String> floor, int maxContiguous) {
    int m = floor.size();
    int n = floor.get(0).length();
    int k = maxContiguous;

    //heat[i][j][k][d] means minHeat to get 
    //from i,j to m-1,n-1, when you ENTERED i,j
    //with k moves already done in direction d
    int[][][][] heat = new int[m][n][k+1][4];
    boolean[][][][] visited = new boolean[m][n][k+1][4];
    char[][] order = new char[m][n];
    resetHeat(heat, m, n, k+1, visited, order);

    //dir: 0 = down, 1 = left, 2 = up, 3 = right
    //when we start, we start from 0,1 and 1,0,
    //having moved 1 already, in right and down
    //directions respectively
    //base case
    for (int i = 0; i < k+1; i++)
      for (int l = 0; l < 4; l++) 
        heat[m-1][n-1][i][l] = 0;

    heatHelper(floor, k, 0, 1, 3, heat, 1, visited, order); //right
    heatHelper(floor, k, 1, 0, 0, heat, 1, visited, order); //down
    System.out.println("sugarg");
    System.out.println(heat[0][1][1][3]);
    System.out.println(heat[1][0][1][0]);

    return heat[0][1][1][3] < heat[1][0][1][0] ? heat[0][1][1][3] : heat[1][0][1][0];
  }

  public void resetHeat(int[][][][] heat, int m, int n, int p, boolean[][][][] visited, char[][] order) {
    for (int i = 0; i < m; i++) 
      for (int j = 0; j < n; j++) {
        order[i][j] = '.';
        for (int k = 0; k < p; k++)
          for (int l = 0; l < 4; l++) {
            heat[i][j][k][l] = -1;
            visited[i][j][k][l] = false;
          }
      }
  }

  public static void updateMinHeat(int[][][][] heat, boolean[][][][] visited, int m, int n, 
                                   int r, int c, int d, int nextd, int sameDir, int contig, int t) {
    int nr = r + dirs[nextd][0];
    int nc = c + dirs[nextd][1];
    if (nr < 0 || nr >= m || nc < 0 || nc >= n) return;

    if (heat[r][c][sameDir][d] == -1 || (heat[r][c][sameDir][d] > (t + heat[nr][nc][contig][nextd]))) {
      heat[r][c][sameDir][d] = t + heat[nr][nc][contig][nextd];
      System.out.println("updating.." + String.valueOf(heat[r][c][sameDir][d]));
    }
  }

  public boolean heatHelper(List<String> floor, int k, int r, int c, int d, int[][][][] heat, int sameDir,
                         boolean[][][][] visited, char[][] order) {
    //do a dfs from each position, and direction.
    int m = floor.size();
    int n = floor.get(0).length();

    //heat[i][j][k] means i,j cell was heat with light traveling in k direction
    if (r < 0 || r >= m || c < 0 || c >= n) return false;
    if (heat[r][c][sameDir][d] >= 0) {
      System.out.print("found and returning true: ");
      printVisited(heat, m, n, r, c, d, sameDir, floor.get(r).charAt(c), order, floor);
      return true;
    }
    if (visited[r][c][sameDir][d]) {
      System.out.print("not found and returning false: ");
      printVisited(heat, m, n, r, c, d, sameDir, floor.get(r).charAt(c), order, floor);
      return false;
    }

    int t = floor.get(r).charAt(c) - '0';
    visited[r][c][sameDir][d] = true;
    char oldDir = order[r][c];
    order[r][c] = getDir(d);
    printVisited(heat, m, n, r, c, d, sameDir, floor.get(r).charAt(c), order, floor);
    boolean foundpath = false, nextdfs = false;

    int reverseDir = (d + 2) % 4;
    int contig = 0;
    for (int nextd = 0; nextd < 4; nextd++) {
      if (nextd == reverseDir) continue; //cannot go back
      if (nextd == d) {
        if (sameDir < k) contig = sameDir + 1;
        else continue;
      } else {
        contig = 1;
      }
      nextdfs = heatHelper(floor, k, r + dirs[nextd][0], c + dirs[nextd][1], nextd, heat, contig, visited, order);
      if (nextdfs) {
        updateMinHeat(heat, visited, m, n, r, c, d, nextd, sameDir, contig, t);
      }
      foundpath = foundpath || nextdfs;
    }
    visited[r][c][sameDir][d] = false;
    order[r][c] = oldDir;
    return foundpath;
  }

  public char getDir(int d) {
    if (d == 0) return 'v';
    else if (d == 1) return '<';
    else if (d == 2) return '^';
    else if (d == 3) return '>';
    else return '#';
  }

  public void printVisited(int [][][][] heat, int m, int n, int r, int c, int d, int sameDir, char x, char[][] order, List<String> floor) {
    boolean flag = false;
    
    String dir = "down";
    if (d == 1) dir = "left";
    else if (d == 2) dir = "up";
    else if (d == 3) dir = "right";

    System.out.println("Visiting cell " + x + " : " +  String.valueOf(r) + "," + String.valueOf(c) + " : going " 
    + dir + ", so far " + String.valueOf(sameDir) + " times");

    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        if (order[i][j] != '.') System.out.print(order[i][j]);
        else System.out.print(floor.get(i).charAt(j));
      }
      System.out.println();
    }
    System.out.println();
  }
}


class Block implements Comparable<Block> {
  public int r;
  public int c;
  public int d;
  public int sameDir;
  public int dist;

  Block(int i, int j, int direction, int contig, int val) {
    r = i;
    c = j;
    d = direction;
    sameDir = contig;
    dist = val;
  }

  @Override
  public int compareTo(Block other) {
    if (dist < other.dist) return -1;
    return 1;
  }
}



class DjikstraCity {
  DjikstraCity() {}
  public static int[][] dirs = {{1, 0}, {0, -1}, {-1, 0}, {0, 1}}; //clockwise down, left, up, right
  public PriorityQueue<Block> q = new PriorityQueue<>();

  public void resetHeat(int m, int n, int p, boolean[][][][] visited) {
    for (int i = 0; i < m; i++) 
      for (int j = 0; j < n; j++) {
        for (int k = 0; k < p; k++)
          for (int l = 0; l < 4; l++) {
            visited[i][j][k][l] = false;
          }
      }
  }

  public int minHeat(List<String> floor, int maxContiguous) {
    int m = floor.size();
    int n = floor.get(0).length();
    int k = maxContiguous;

    //heat[i][j][k][d] means minHeat to get 
    //from i,j to m-1,n-1, when you ENTERED i,j
    //with k moves already done in direction d
    boolean[][][][] visited = new boolean[m][n][k+1][4];
    resetHeat(m, n, k+1, visited);

    //dir: 0 = down, 1 = left, 2 = up, 3 = right
    //when we start, we start from 0,1 and 1,0,
    //having moved 1 already, in right and down
    //directions respectively
    int t = floor.get(0).charAt(1) - '0'; //right
    Block b1 = new Block(0, 1, 3, 1, t);

    t = floor.get(1).charAt(0) - '0'; //down
    Block b2 = new Block(1, 0, 0, 1, t);
    Block top = b1;

    q.add(b1);
    q.add(b2);

    int reverseDir, contig, nextr, nextc;
    while(q.size() > 0) {
      top = q.poll();
      if (top.r == m-1 && top.c == n-1) {
        return top.dist;
      }
      if (visited[top.r][top.c][top.sameDir][top.d]) continue;
      visited[top.r][top.c][top.sameDir][top.d] = true;

      //check all 4 directions, and add them if not visited.
      reverseDir = (top.d + 2) % 4;
      contig = 0;
      for (int nextd = 0; nextd < 4; nextd++) {
        nextr = top.r + dirs[nextd][0];
        nextc = top.c + dirs[nextd][1];
        if (nextr < 0 || nextr >= m || nextc < 0 || nextc >= n) continue;

        if (nextd == reverseDir) continue; //cannot go back
        if (nextd == top.d) {
          if (top.sameDir < k) contig = top.sameDir + 1;
          else continue;
        } else {
          contig = 1;
        }
        if (visited[nextr][nextc][contig][nextd]) continue;
        //Block(int i, int j, int direction, int contig, int val)
        t = floor.get(nextr).charAt(nextc) - '0';
        Block next = new Block(nextr, nextc, nextd, contig, top.dist + t);
        q.add(next);
      }
    }
    return -2;
  }
}

