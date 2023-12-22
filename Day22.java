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

class Day22 {
  //down, left, up, right 
  public static long[][] dirs = {{-1L, 0L}, {0L, -1L}, {1L, 0L}, {0L, 1L}};
  public static void main(String[] args) {
    try {

      BufferedReader reader = new BufferedReader(new FileReader(args[0]));
      String line = reader.readLine();
      int id = 0;

      List<Brick> bricks = new ArrayList<>();
      while (line != null) {
        Brick b = new Brick(id, line);
        bricks.add(b);
        id++;
        line = reader.readLine();
      }
      reader.close();

      Collections.sort(bricks);
      int total = part1(bricks);
      System.out.println("part 1: " + String.valueOf(total));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static int part1(List<Brick> bricks) {
    //bricks are sorted by lower z.
    int n = bricks.size();
    int g[][] = new int[n][n];

    //g is a directed graph
    //g[i][j] = 1 means brick i depends on brick j

    Brick bi, bj;
    int z0 = 0;
    for (int i = 0; i < n; i++) {
      bi = bricks.get(i);
      for (int j = 0; j < i; j++) {
        //checking if brick i overlaps with potentially lower brick j
        bj = bricks.get(j);

        //brick j is higher than brick i, not needed to  compare right now.
        //if (bj.z[0] >= bi.z[1]) continue; 
        if (!bi.overlap(bj)) continue;

        //they overlap, so see the lowest you can drop brick i to.
        z0 = z0 < (int)bj.z[1] ? (int)bj.z[1] : z0;
      }

      int zdist = (int)(bi.z[1] - bi.z[0]);
      bi.z[0] = z0;
      bi.z[1] = z0 + zdist;
    }

    for (int i = 1; i < n; i++) {
      bi = bricks.get(i);
      for (int j = 0; j < i; j++) {
        bj = bricks.get(j);
        if (bj.z[1] == bi.z[0]) {
          g[bi.id][bj.id] = 1; //i depends on j
        }
      }
    }

    Set<Integer> cannotDestroy = new HashSet<>();
    int depends = 0;
    int supportBrick = -1;
    for (int i = 0; i < n; i++) {
      //find if brick i has a single support brick
      //if yes, that brick cannot be destroyed.
      depends = 0;
      supportBrick = -1;
      for (int j = 0; j < n; j++) {
        if (g[i][j] > 0) {
          if (depends == 0) {
            depends++;
            supportBrick = j;
          } else {
            //brick i is supported by more than 1 brick, move on.
            supportBrick = -1;
            break;
          }
        }
      }

      if (supportBrick != -1) {
        cannotDestroy.add(supportBrick);
      }
    }
    System.out.println(cannotDestroy);

    return n - cannotDestroy.size();
  }
}

class Brick implements Comparable<Brick> {
  public long[] x;
  public long[] y;
  public long[] z;
  public int id;

  Brick() {}
  Brick(int identity, String input) {
    int tilde = 0;
    for (int i = 0; i < input.length(); i++) {
      if (input.charAt(i) == '~') {
        tilde = i;
        break;
      }
    }

    String[] left = input.substring(0, tilde).split(",");
    String[] right = input.substring(tilde+1).split(",");

    id = identity;
    x = new long[2];
    y = new long[2];
    z = new long[2];
    x[0] = Long.parseLong(left[0]) - 1; x[1] = Long.parseLong(right[0]);
    y[0] = Long.parseLong(left[1]) - 1; y[1] = Long.parseLong(right[1]);
    z[0] = Long.parseLong(left[2]) - 1; z[1] = Long.parseLong(right[2]);
  }

  public boolean overlap(Brick other) {
    //only if both x and y overlap return true.
    boolean xOverlap = !((x[1] <= other.x[0]) || (other.x[1] <= x[0]));
    boolean yOverlap = !((y[1] <= other.y[0]) || (other.y[1] <= y[0]));

    return xOverlap && yOverlap;
  }

  @Override
  public int compareTo(Brick other) {
    if (z[0] < other.z[0]) return -1;
    else if (z[0] == other.z[0]) {
      return z[1] <= other.z[1] ? -1 : 1;
    }
    return 1;
  }
}
