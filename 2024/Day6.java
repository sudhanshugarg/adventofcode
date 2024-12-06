import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

      Day6 day = new Day6(locations);
      long total = day.part1();
      System.out.println(total);
      total = day.part2();
      System.out.println(total);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}


public class Day6 {
    private List<String> inputs;
    private int m,n;
    private int sr, sc;
    private int facing;
    private List<List<Integer>> rowObstacles;
    private List<List<Integer>> colObstacles;

    Day6(List<String> arr) {
        inputs = arr;
        m = inputs.size();
        n = inputs.get(0).length();
        rowObstacles = new ArrayList<>();
        colObstacles = new ArrayList<>();

        char c;
        for (int i = 0; i < m; i++)
          rowObstacles.add(new ArrayList<>());
        for (int i = 0; i < n; i++)
          colObstacles.add(new ArrayList<>());

        List<Integer> currRow, currCol;
        for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
          c = inputs.get(i).charAt(j);
          switch(c) {
            case '<':
            facing = 0;
            sr = i; sc = j;
            break;
            case '^':
            facing = 1;
            sr = i; sc = j;
            break;
            case '>':
            facing = 2;
            sr = i; sc = j;
            break;
            case 'v':
            facing = 3;
            sr = i; sc = j;
            break;
            case '#':
            rowObstacles.get(i).add(j);
            colObstacles.get(j).add(i);
            break;
          }
        }}
    }

    public long part1() {
      //simple simulation
      boolean outOfMaze = false;
      System.out.println(String.format("init: m: %d, n: %d, facing: %d, posn:[%d, %d]", m, n, facing, sr, sc));

      int r = sr, c = sc, f = facing;
      List<Segment> segments = new ArrayList<>();
      boolean[] goingOut = new boolean[1];
      goingOut[0] = false;
      while(!goingOut[0]) {
        Segment s = travel(r, c, f, goingOut);
        //System.out.println(s);
        segments.add(s);
        //outOfMaze = isGoingOut(s, f);

        if (s.isRow) {
          //r is unchanged
          if (f == 0) c = s.start;
          else c = s.end;
        } else {
          //c is unchanged
          if (f == 1) r = s.start;
          else r = s.end;
        }

        f++;
        if (f == 4) f = 0;
      }
      return countTravelled(segments); 
    }

    private Segment travel(int r, int c, int f, boolean[] goingOut) {
      List<Integer> row, col;
      int rowNum, colNum;
      switch(f) {
        case 0: //L row movement
        row = rowObstacles.get(r);
        //need the first rowNum with obstacle < r
        colNum = search(true, row, c, false, goingOut);
        return new Segment(r, true, colNum, c);
        case 1: //U column movement
        col = colObstacles.get(c);
        //need the first rowNum with obstacle < r
        rowNum = search(false, col, r, false, goingOut);
        return new Segment(c, false, rowNum, r);
        case 2: //R column movement
        row = rowObstacles.get(r);
        //need the first rowNum with obstacle > c
        colNum = search(true, row, c, true, goingOut);
        return new Segment(r, true, c, colNum);
        case 3: //D column movement
        col = colObstacles.get(c);
        //need the first rowNum with obstacle < r
        rowNum = search(false, col, r, true, goingOut);
        return new Segment(c, false, r, rowNum);
      }
      return null;
    }

    private int search(boolean isRow, List<Integer> line, int toFind, boolean increasing, boolean[] goingOut) {
      int ns = line.size();
      if (increasing) {
        for (int i = 0; i < ns; i++) {
          if (line.get(i) > toFind) return line.get(i) - 1;
        }
        goingOut[0] = true;
        if (isRow) return m-1;
        else return n-1;
      } else {
        for (int i = ns - 1; i >= 0; i--) {
          if (line.get(i) < toFind) return line.get(i) + 1;
        }
        goingOut[0] = true;
        return 0;
      }
    }

    private boolean isGoingOut(Segment s, int f) {
      switch(f) {
        case 0: //L
          return (
              (!s.isRow && s.id == 0) ||
              (s.isRow && s.start == 0)
          );
        case 1: //U
          return (
              (s.isRow && s.id == 0) ||
              (!s.isRow && s.start == 0)
          );
        case 2: //R
          return (
              (!s.isRow && s.id == n-1) ||
              (s.isRow && s.end == m-1)
          );
        case 3: //D
          return (
              (s.isRow && s.id == m-1) ||
              (!s.isRow && s.end == n-1)
          );
      }
      return false;
    }

    private long countTravelled(List<Segment> segments) {
      boolean[][] grid = new boolean[m][n];

      Segment s;
      for (int i = 0; i < segments.size(); i++) {
        s = segments.get(i);
        if (s.isRow) {
          for (int j = s.start; j <= s.end; j++) {
            grid[s.id][j] = true;
          }
        } else {
          for (int j = s.start; j <= s.end; j++) {
            grid[j][s.id] = true;
          }
        }
      }

      long total = 0;
      for (int i = 0; i < m; i++)
      for (int j = 0; j < n; j++)
        if (grid[i][j]) total++;

      return total;
    }

    public long part2() { return 0; }
}


class Segment {
  int id;
  boolean isRow;
  int start;
  int end;

  Segment(int a, boolean b, int c, int d) {
    id = a;
    isRow = b;
    start = c;
    end = d;

    if (start > end) {
      int tmp = start;
      start = end;
      end = tmp;
    }
  }


  @Override
  public String toString() {
    if (isRow) return String.format("row: %d, [%d, %d]", id, start, end);
    else return String.format("col: %d, [%d, %d]", id, start, end);
  }
}
