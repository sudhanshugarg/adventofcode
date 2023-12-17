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
      int total = 0;
      total = c.minHeat(floor, 1, 3);
      System.out.println("part 1: " + String.valueOf(total));

      total = c.minHeat(floor, 4, 10);
      System.out.println("part 2: " + String.valueOf(total));
    } catch (IOException e) {
      e.printStackTrace();
    }
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

  @Override
  public String toString() {
    return "Block: " + String.valueOf(r) + "," + 
            String.valueOf(c) + ", dir = " + String.valueOf(d) + 
            ", soFar = " + String.valueOf(sameDir) + ", dist = " + String.valueOf(dist);
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

  public int minHeat(List<String> floor, int minContiguous, int maxContiguous) {
    int m = floor.size();
    int n = floor.get(0).length();
    int k = maxContiguous;
    q.clear();

    //heat[i][j][k][d] means minHeat to get 
    //from i,j to m-1,n-1, when you ENTERED i,j
    //with k moves already done in direction d
    boolean[][][][] visited = new boolean[m][n][k+1][4];
    resetHeat(m, n, k+1, visited);

    //dir: 0 = down, 1 = left, 2 = up, 3 = right
    //when we start, we start from 0,1 and 1,0,
    //having moved 1 already, in right and down
    //directions respectively
    int t = 0; //right
    for (int i = 0; i < minContiguous; i++) {
      t += floor.get(0).charAt(i+1) - '0';
    }
    Block b1 = new Block(0, minContiguous, 3, minContiguous, t);

    t = 0;
    for (int i = 0; i < minContiguous; i++) {
      t += floor.get(i+1).charAt(0) - '0';
    }
    Block b2 = new Block(minContiguous, 0, 0, minContiguous, t);
    Block top = b1;

    q.add(b1);
    q.add(b2);

    int reverseDir, contig, nextr, nextc, nextDist, nr, nc;
    while(q.size() > 0) {
      top = q.poll();
      if (top.r == m-1 && top.c == n-1) {
        return top.dist;
      }
      if (visited[top.r][top.c][top.sameDir][top.d]) continue;
      //System.out.println(top.toString());
      visited[top.r][top.c][top.sameDir][top.d] = true;

      //check all 4 directions, and add them if not visited.
      reverseDir = (top.d + 2) % 4;
      contig = 0;
      for (int nextd = 0; nextd < 4; nextd++) {

        if (nextd == reverseDir) continue; //cannot go back
        if (nextd == top.d) {
          if (top.sameDir < k) contig = top.sameDir + 1;
          else continue;
          nextr = top.r + dirs[nextd][0];
          nextc = top.c + dirs[nextd][1];
          if (nextr < 0 || nextr >= m || nextc < 0 || nextc >= n) continue;
          t = floor.get(nextr).charAt(nextc) - '0';
          nextDist = top.dist + t;
        } else {
          //changing direction
          //need to go 4 steps
          contig = minContiguous;
          nextr = top.r + minContiguous * dirs[nextd][0];
          nextc = top.c + minContiguous * dirs[nextd][1];
          if (nextr < 0 || nextr >= m || nextc < 0 || nextc >= n) continue;
          nextDist = top.dist;
          for (int i = 0; i < minContiguous; i++) {
            nr = top.r + (i+1) * dirs[nextd][0];
            nc = top.c + (i+1) * dirs[nextd][1];
            t = floor.get(nr).charAt(nc) - '0';
            nextDist += t;
          }
        }
        if (visited[nextr][nextc][contig][nextd]) continue;

        //Block(int i, int j, int direction, int contig, int val)
        Block next = new Block(nextr, nextc, nextd, contig, nextDist);
        q.add(next);
      }
    }
    return -2;
  }
}
