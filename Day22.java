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
      int n = bricks.size();
      //g is a directed graph
      //g[i][j] = 1 means brick i depends on brick j
      int g[][] = new int[n][n];
      for (int i = 0; i < n; i++)
        for (int j = 0; j < n; j++)
          g[i][j] = 0;

      int total = part1(bricks, g);
      System.out.println("part 1: " + String.valueOf(total));

      total = part2(g, n);
      System.out.println("part 2: " + String.valueOf(total));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static int part2(int[][] g, int n) {
    boolean[] disintegrated = new boolean[n];

    Queue<Integer> q = new ArrayDeque<>();
    int total = 0;
    int count = 0;
    boolean willFall = false;

    for (int b = 0; b < n; b++) {
      q.clear();
      for (int i = 0; i < n; i++) disintegrated[i] = false;
      q.add(b); //disintegrating b. lets make the dominoes fall.

      while(!q.isEmpty()) {
        int next = q.poll();
        disintegrated[next] = true;

        //now, go through all the bricks that depend on this
        for (int i = 0; i < n; i++) {
          if (g[i][next] == 0) continue;

          //check if everything i depends on is disintegrated.
          //if so, add it to the queue.
          willFall = true;
          for (int j = 0; willFall && j < n; j++) {
            if (g[i][j] == 1 && !disintegrated[j]) willFall = false;
          }
          if (willFall) q.add(i);
        }
      }

      count = -1;
      for (int i = 0; i < n; i++)
        if (disintegrated[i]) count++;

      //System.out.println("for brick: " + String.valueOf(b) + ", count = " + String.valueOf(count));
      total += count;
    }
    return total;
  }

  public static void printOverlap(List<Brick> bricks, int limit) {
    int n = bricks.size();
    n = n > limit ? limit : n;

    for (int i = 0; i < n; i++) {
      System.out.print(bricks.get(i).label + ":");
    for (int j = 0; j < n; j++) {
      if (i == j) {
        System.out.print("S");
        continue;
      }
      if (bricks.get(i).overlap(bricks.get(j))) 
        System.out.print("1");
      else System.out.print("0");
    }
    System.out.println();
    }
  }

  public static int part1(List<Brick> bricks, int[][] g) {
    //bricks are sorted by lower z.
    int n = bricks.size();

    Brick bi, bj;
    int z0 = 0;
    for (int i = 0; i < n; i++) {
      bi = bricks.get(i);
      z0 = 0;
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

    Collections.sort(bricks);
    for (int i = 1; i < n; i++) {
      bi = bricks.get(i);
      for (int j = 0; j < i; j++) {
        bj = bricks.get(j);
        if (bj.z[1] == bi.z[0] && bi.overlap(bj)) {
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

  public static void printB(List<Brick> bricks, int limit) {
    for (int i = 0; i < bricks.size() && i < limit; i++) {
      System.out.print(bricks.get(i).label + ":");
      System.out.print(String.valueOf(bricks.get(i).x[0] + 1) + "," + String.valueOf(bricks.get(i).y[0] + 1) + "," + String.valueOf(bricks.get(i).z[0] + 1));
      System.out.println("~" + String.valueOf(bricks.get(i).x[1]) + "," + String.valueOf(bricks.get(i).y[1]) + "," + String.valueOf(bricks.get(i).z[1]));
    }
  }

  public static void printPosn(List<Brick> bricks, int limit) {
    long[] x = new long[2];
    long[] y = new long[2];
    long[] z = new long[2];

    Brick b = bricks.get(0);
    x[0] = b.x[0]+1; x[1] = b.x[1];
    y[0] = b.y[0]+1; y[1] = b.y[1];
    z[0] = b.z[0]+1; z[1] = b.z[1];
    for (int i = 1; i < bricks.size() && i < limit; i++) {
      b = bricks.get(i);
      x[0] = x[0] > (b.x[0]+1) ? b.x[0]+1 : x[0];
      y[0] = y[0] > b.y[0]+1 ? b.y[0]+1 : y[0];
      z[0] = z[0] > b.z[0]+1 ? b.z[0]+1 : z[0];

      x[1] = x[1] < b.x[1] ? b.x[1] : x[1];
      y[1] = y[1] < b.y[1] ? b.y[1] : y[1];
      z[1] = z[1] < b.z[1] ? b.z[1] : z[1];
    }

    System.out.println("z vs x");
    printFalling(z, x, bricks, limit, true);
    System.out.println();
    System.out.println();
    System.out.println("z vs y");
    printFalling(z, y, bricks, limit, false);
    System.out.println();
    System.out.println();
  }

  public static void printFalling(long[] z, long[] m, List<Brick> bricks, int limit, boolean isX) {
    boolean flag = false;
    long[] w = new long[2];
    for (long i = z[1]; i >= z[0]; i--) {
    for (long j = m[0]; j <= m[1]; j++) {
      flag = false;
      for (int k = 0; k < bricks.size() && k < limit; k++) {
        Brick b = bricks.get(k);
        w = b.x;
        if (!isX) w = b.y;
        if ((i >= (b.z[0]+1) && i <= b.z[1]) && (j >= (w[0]+1) && j <= w[1])) {
          System.out.print(b.label);
          flag = true;
          break;
        }
      }
      if (!flag) System.out.print('.');
    }
    System.out.println(" " + String.valueOf(i));
    }
  }

  public static String getStr(long[] arr) {
    return String.valueOf(arr[0]) + "," + String.valueOf(arr[1]);
  }
}

class Brick implements Comparable<Brick> {
  public long[] x;
  public long[] y;
  public long[] z;
  public int id;
  public char label;
  public String in;

  Brick() {}
  Brick(int identity, String input) {
    int tilde = 0;
    in = input;
    for (int i = 0; i < input.length(); i++) {
      if (input.charAt(i) == '~') {
        tilde = i;
        break;
      }
    }

    String[] left = input.substring(0, tilde).split(",");
    String[] right = input.substring(tilde+1).split(",");

    id = identity;
    label = 'A';
    label += id;
    x = new long[2];
    y = new long[2];
    z = new long[2];
    x[0] = Long.parseLong(left[0]); x[1] = Long.parseLong(right[0]);
    y[0] = Long.parseLong(left[1]); y[1] = Long.parseLong(right[1]);
    z[0] = Long.parseLong(left[2]); z[1] = Long.parseLong(right[2]);
    long l = x[0] + y[0] + z[0];
    long r = x[1] + y[1] + z[1];
    if (l > r) {
      System.out.println("eeennnhhh: something is off: " + input);
    }
    x[0]--; y[0]--; z[0]--;
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
      if (z[1] < other.z[1]) return -1;
      else if (z[1] == other.z[1]) return 0;
      else return 1;
    }
    return 1;
  }
}
