import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

class Day16 {
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
      Cave c = new Cave();

      int total = c.energize(floor);
      System.out.println(total);

      total = c.energizeAny(floor);
      System.out.println(total);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}


class Cave {
  Cave() {}
  public static int[][] dirs = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}}; //down, up, left, right

  public int energize(List<String> floor) {
    int m = floor.size();
    int n = floor.get(0).length();

    boolean[][][] visited = new boolean[m][n][4];
    resetVisited(visited, m, n);

    //dir: 0 = down, 1 = up, 2 = left, 3 = right
    energizeHelper(floor, 0, 0, 3, visited);

    return countEnergized(visited, m, n);
  }

  public void resetVisited(boolean[][][] visited, int m, int n) {
    for (int i = 0; i < m; i++) 
      for (int j = 0; j < n; j++) 
        for (int k = 0; k < 4; k++)
          visited[i][j][k] = false;
  }

  public int countEnergized(boolean[][][] visited, int m, int n) {
    int v = 0;
    for (int i = 0; i < m; i++)
      for (int j = 0; j < n; j++) 
        for (int k = 0; k < 4; k++)
          if (visited[i][j][k]) {
            v++;
            break;
          }
    return v;
  }

  public int energizeAny(List<String> floor) {
    int m = floor.size();
    int n = floor.get(0).length();

    boolean[][][] visited = new boolean[m][n][4];
    int maxEnergized = -1;

    //top row, r = 0
    for (int a = 0; a < n; a++) {
      resetVisited(visited, m, n);
      energizeHelper(floor, 0, a, 0, visited);
      int v = countEnergized(visited, m, n);
      maxEnergized = maxEnergized < v ? v : maxEnergized;
    }

    //bottom row, r = m-1
    for (int a = 0; a < n; a++) {
      resetVisited(visited, m, n);
      energizeHelper(floor, m-1, a, 1, visited);
      int v = countEnergized(visited, m, n);
      maxEnergized = maxEnergized < v ? v : maxEnergized;
    }

    //left col, c = 0
    for (int a = 0; a < m; a++) {
      resetVisited(visited, m, n);
      energizeHelper(floor, a, 0, 3, visited);
      int v = countEnergized(visited, m, n);
      maxEnergized = maxEnergized < v ? v : maxEnergized;
    }

    //right col, c = n-1
    for (int a = 0; a < m; a++) {
      resetVisited(visited, m, n);
      energizeHelper(floor, a, n-1, 2, visited);
      int v = countEnergized(visited, m, n);
      maxEnergized = maxEnergized < v ? v : maxEnergized;
    }
    return maxEnergized;
  }

  public void energizeHelper(List<String> floor, int r, int c, int d, boolean[][][] visited) {
    //do a dfs from each position, and direction.
    int m = floor.size();
    int n = floor.get(0).length();

    //printVisited(visited, r, c, d);

    //visited[i][j][k] means i,j cell was visited with light traveling in k direction
    if (r < 0 || r >= m || c < 0 || c >= n || visited[r][c][d]) return;
    visited[r][c][d] = true;
    char t = floor.get(r).charAt(c);
    int nextd = d;
    if (t == '.') {
      energizeHelper(floor, r + dirs[d][0], c + dirs[d][1], d, visited);
    } else if (t == '-') {
      //r, c is the splitter
      //check if direction is left or right
      if (d == 2 || d == 3) {
        energizeHelper(floor, r + dirs[d][0], c + dirs[d][1], d, visited);
      } else {
        energizeHelper(floor, r + dirs[2][0], c + dirs[2][1], 2, visited);
        energizeHelper(floor, r + dirs[3][0], c + dirs[3][1], 3, visited);
      }
    } else if (t == '|') {
      if (d == 0 || d == 1) {
        energizeHelper(floor, r + dirs[d][0], c + dirs[d][1], d, visited);
      } else {
        energizeHelper(floor, r + dirs[0][0], c + dirs[0][1], 0, visited);
        energizeHelper(floor, r + dirs[1][0], c + dirs[1][1], 1, visited);
      }
    } else if (t == '/') {
      if (d == 0) nextd = 2; //down to left
      else if (d == 1) nextd = 3; //up to right
      else if (d == 2) nextd = 0; //left to down
      else if (d == 3) nextd = 1; //right to up
      energizeHelper(floor, r + dirs[nextd][0], c + dirs[nextd][1], nextd, visited);

    } else if (t == '\\') {
      if (d == 0) nextd = 3; //down to right
      else if (d == 1) nextd = 2; //up to left
      else if (d == 2) nextd = 1; //left to up 
      else if (d == 3) nextd = 0; //right to down
      energizeHelper(floor, r + dirs[nextd][0], c + dirs[nextd][1], nextd, visited);
    }
  }

  public void printVisited(boolean [][][] visited, int r, int c, int d) {
    int m = visited.length;
    int n = visited[0].length;
    int p = visited[0][0].length;
    boolean flag = false;
    
    String dir = "down";
    if (d == 1) dir = "up";
    else if (d == 2) dir = "left";
    else if (d == 3) dir = "right";

    System.out.println("Visiting cell " + String.valueOf(r) + "," + String.valueOf(c) + " : going " + dir);

    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        flag = false;
        for (int k = 0; !flag && k < p; k++) {
          flag = visited[i][j][k];
        }
        if (flag) System.out.print('#');
        else System.out.print('.');
      }
      System.out.println();
    }
    System.out.println();
  }
}
