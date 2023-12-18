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

class Day18 {
  public static void main(String[] args) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(args[0]));
      String line = reader.readLine();
      List<String> directions = new ArrayList<>();

      while (line != null) {
        directions.add(line);
        line = reader.readLine();
      }
      reader.close();

      CircularPath c = new CircularPath();
      int total = 0;
      total = c.findInterior(directions);
      System.out.println("part 1: " + String.valueOf(total));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

class CircularPath {
  CircularPath() {}

  public int findInterior(List<String> d) {
    int n = d.size();

    Map<Integer, List<Integer>> border = new HashMap<>();
    int R = 800;
    int C = 400;
    char[][] grid = new char[R][C];
    for (int i = 0; i < R; i++)
      for (int j = 0; j < C; j++)
        grid[i][j] = '.';

    List<Integer> row0 = new ArrayList<>();
    row0.add(0);
    //border.add(row0);
    border.put(0, row0);
    int minr = 0, maxr = 0, minc = 0, maxc = 0;

    char A;
    int steps;
    int r = 0, c = 0, r2 = 400, c2 = 0;
    grid[r2][c2] = '#';
    char X = '#';
    for (int i = 0; i < n; i++) {
      String[] s = d.get(i).split("\\s+");
      A = s[0].charAt(0);
      steps = Integer.parseInt(s[1]);
      X = A;

      switch (A) {
        case 'R':
        for (int j = 1; j <= steps; j++) {
          border.get(r).add(c+j);
          grid[r2][c2+j] = X;
        }
        c += steps;
        c2 += steps;
        maxc = maxc < c ? c : maxc;
        break;
        case 'D':
        //first update size
        int m = border.size();
        //if ((r + steps + 1) > m) {
        //  for (int j = m; j < r+steps+1; j++) border.add(new ArrayList<>());
        //}
        for (int j = 1; j <= steps; j++) {
          border.putIfAbsent(r+j, new ArrayList<>());
          border.get(r+j).add(c);
          grid[r2+j][c2] = X;
        }
        r += steps;
        r2 += steps;
        maxr = maxr < r ? r : maxr;
        break;
        case 'L':
        for (int j = 1; j <= steps; j++) {
          border.get(r).add(c-j);
          grid[r2][c2-j] = X;
        }
        c -= steps;
        c2 -= steps;
        minc = minc > c ? c : minc;
        break;
        case 'U':
        //if (steps > r) {
        //  for (int j = r; j < steps; j++) {
        //    border.add(0, new ArrayList<>());
        //  }
        //  r = steps;
        //}
        for (int j = 1; j <= steps; j++) {
          border.putIfAbsent(r-j, new ArrayList<>());
          border.get(r-j).add(c);
          grid[r2-j][c2] = X;
        }
        r -= steps;
        r2 -= steps;
        minr = minr > r ? r : minr;
        break;
        default:
        System.out.println("should never be printed");
      }
    }
    System.out.println(minr);
    System.out.println(maxr);
    System.out.println(minc);
    System.out.println(maxc);

    int m = maxr - minr + 1;
    System.out.println("depth = " + String.valueOf(m));
    int dug = 0;
    for (int i = minr; i <= maxr; i++) {
      Collections.sort(border.get(i));
      //System.out.println(border.get(i));
    }
    //printTrench(border, minr, maxr, minc, maxc);
    //printTrench2(grid, R, C, minr, maxr, minc, maxc);

    border.put(minr-1, new ArrayList<>());
    border.put(maxr+1, new ArrayList<>());
    for (int i = minr; i <= maxr; i++) {
      int rowdug = shootRay(border.get(i), border.get(i-1), border.get(i+1));
      printTrenchRow(border, i, minc, maxc);
      System.out.print(" : ");
      System.out.println(rowdug);
      /*
      try {
        System.in.read();
      } catch (IOException e) {
        e.printStackTrace();
      }
      */
      dug += rowdug;
    }

    return dug;
  }

  public int shootRay(List<Integer> s, List<Integer> prev, List<Integer> next) {
    if (s.size() == 0) return 0;
    int n = s.size();

    boolean in = false;
    int dug = 0;
    for (int i = 0; i < n;) {
      String istr = String.valueOf(i);
      //System.out.print(" 1. i = " + istr + ", dug = " + dug);
      if ((i > 0) && (s.get(i) == s.get(i-1))) {
        i++;
        continue;
      }
  
      //check if horizontal or vertical pipe.
      //in can only be true once i >= 1;
      if (in) dug += (s.get(i) - 1) - s.get(i-1);
      //System.out.print(" 2. i = " + istr + ", dug = " + dug);

      int j = i+1;
      while(j < n && (s.get(j) == s.get(j-1) + 1)) j++;
      int len = j-i;
      if (len == 1) { //vertical pipe
        in = !in;
      } else {
        //check ends of pipe.
        int c1 = s.get(i), c2 = s.get(j-1);
        //System.out.println("looking for " + String.valueOf(c1) + " and " + String.valueOf(c2) + " in " + prev);
        //System.out.println("looking for " + String.valueOf(c1) + " and " + String.valueOf(c2) + " in " + next);
        //find c1 in i-1 and i+1
        boolean upperLeft = binSearch(prev, c1);
        boolean lowerLeft = binSearch(next, c1);
        boolean upperRight = binSearch(prev, c2);
        boolean lowerRight = binSearch(next, c2);
        String msg = String.valueOf(upperLeft) + "," + String.valueOf(lowerLeft) + "," + String.valueOf(upperRight) + "," + String.valueOf(lowerRight);
        //System.out.println(msg);
        if ((upperLeft && lowerRight) || (upperRight && lowerLeft)) {
          //System.out.print(" # changing in # ");
          in = !in;
        }
      }
      dug += len;
      i = j;
      istr = String.valueOf(i);
      //System.out.print(" 3. i = " + istr + ", dug = " + dug);
      //System.out.println();
    }
    return dug;
  }

  public void printTrench(Map<Integer, List<Integer>> border, int minr, int maxr, int minc, int maxc) {
    for (int i = minr; i <= maxr; i++) {
      printTrenchRow(border, i, minc, maxc);
      System.out.println();
    }
  }

  public void printTrenchRow(Map<Integer, List<Integer>> border, int r, int minc, int maxc) {
    int p = 0;
    char h = '#', d = '.';
    List<Integer> row = border.get(r);
    for (int j = minc; j <= maxc; j++) {
      if (p < row.size() && (row.get(p) == j)) {
        if (r == 0 && j == 0) System.out.print('S');
        else System.out.print(h);

        p++;
        while(p < row.size() && (row.get(p) == row.get(p-1))) p++;
      } else System.out.print(d);
    }
  }


  public void printTrench2(char[][] g, int R, int C, int minr, int maxr, int minc, int maxc) {
    for (int i = minr; i <= maxr; i++) {
      for (int j = minc; j <= maxc; j++) System.out.print(g[400 + i][j]);
      System.out.println();
    }
  }

  public boolean binSearch(List<Integer> arr, int target) {
    int low = 0, high = arr.size() - 1;
    int mid;
    
    while(low <= high) {
      mid = (low + high) / 2;
      if (arr.get(mid) == target) return true;
      else if (arr.get(mid) < target) {
        low = mid + 1;
      } else high = mid - 1;
    }
    return false;
  }
}
