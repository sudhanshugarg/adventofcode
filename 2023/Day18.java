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
      long total = 0;
      total = c.findInterior(directions, false);
      System.out.println("part 1: " + String.valueOf(total));

      total = c.findInterior(directions, true);
      System.out.println("part 2: " + String.valueOf(total));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

class CircularPath {
  CircularPath() {}

  public long findInterior(List<String> d, boolean isPart2) {
    int n = d.size();

    Map<Long, List<Long>> border = new HashMap<>();
    List<Long> row0 = new ArrayList<>();
    row0.add(0L);
    border.put(0L, row0);
    long minr = 0, maxr = 0, minc = 0, maxc = 0;

    char A = '.';
    long steps = 0;
    long r = 0, c = 0;
    for (int i = 0; i < n; i++) {
      String[] s = d.get(i).split("\\s+");
      if (!isPart2) {
        A = s[0].charAt(0);
        steps = Long.parseLong(s[1]);
      } else {
        A = s[2].charAt(7);
        if (A == '0') A = 'R';
        else if (A == '1') A = 'D';
        else if (A == '2') A = 'L';
        else if (A == '3') A = 'U';

        steps = Long.parseLong(s[2].substring(2,7), 16);
        System.out.println(A + "," + steps);
      }

      switch (A) {
        case 'R':
        for (int j = 1; j <= steps; j++) {
          border.get(r).add(c+j);
        }
        c += steps;
        maxc = maxc < c ? c : maxc;
        break;
        case 'D':
        //first update size
        int m = border.size();
        for (int j = 1; j <= steps; j++) {
          border.putIfAbsent(r+j, new ArrayList<>());
          border.get(r+j).add(c);
        }
        r += steps;
        maxr = maxr < r ? r : maxr;
        break;
        case 'L':
        for (int j = 1; j <= steps; j++) {
          border.get(r).add(c-j);
        }
        c -= steps;
        minc = minc > c ? c : minc;
        break;
        case 'U':
        for (int j = 1; j <= steps; j++) {
          border.putIfAbsent(r-j, new ArrayList<>());
          border.get(r-j).add(c);
        }
        r -= steps;
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

    long m = maxr - minr + 1;
    System.out.println("depth = " + String.valueOf(m));
    long dug = 0;
    for (long i = minr; i <= maxr; i++) {
      Collections.sort(border.get(i));
      //System.out.println(border.get(i));
    }
    //printTrench(border, minr, maxr, minc, maxc);
    //printTrench2(grid, R, C, minr, maxr, minc, maxc);

    border.put(minr-1, new ArrayList<>());
    border.put(maxr+1, new ArrayList<>());
    for (long i = minr; i <= maxr; i++) {
      long rowdug = shootRay(border.get(i), border.get(i-1), border.get(i+1));
      //printTrenchRow(border, i, minc, maxc);
      //System.out.print(" : ");
      //System.out.println(rowdug);
      //try {
      //  System.in.read();
      //} catch (IOException e) {
      //  e.printStackTrace();
      //`}
      dug += rowdug;
    }

    return dug;
  }

  public long shootRay(List<Long> s, List<Long> prev, List<Long> next) {
    if (s.size() == 0) return 0;
    int n = s.size();

    boolean in = false;
    long dug = 0;
    for (int i = 0; i < n;) {
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
        long c1 = s.get(i), c2 = s.get(j-1);
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
      //System.out.print(" 3. i = " + istr + ", dug = " + dug);
      //System.out.println();
    }
    return dug;
  }

  public void printTrench(Map<Long, List<Long>> border, int minr, int maxr, int minc, int maxc) {
    for (long i = minr; i <= maxr; i++) {
      printTrenchRow(border, i, minc, maxc);
      System.out.println();
    }
  }

  public void printTrenchRow(Map<Long, List<Long>> border, long r, long minc, long maxc) {
    int p = 0;
    char h = '#', d = '.';
    List<Long> row = border.get(r);
    for (long j = minc; j <= maxc; j++) {
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

  public boolean binSearch(List<Long> arr, long target) {
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
