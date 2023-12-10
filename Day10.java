import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

class Day10 {
  public static int[][] dir7 = {{1, 0},{0, -1}};
  public static int[][] dirPipe = {{-1, 0},{1, 0}};
  public static int[][] dirL = {{-1, 0},{0, 1}};
  public static int[][] dirJ = {{-1, 0},{0, -1}};
  public static int[][] dirF = {{1, 0},{0, 1}};
  public static int[][] dirS = {{1, 0},{0, 1}};
  public static int[][] dirDash = {{0, -1},{0, 1}};
  public static int[][] dirDot = {};
  public static int startRow = -1;
  public static int startCol = -1;
  public static int m = -1;
  public static int n = -1;

  public static void main(String[] args) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(args[0]));
      char[][] maze = new char[150][150];
      String line = reader.readLine();
      n = line.length();
      m = 0;

      while(line != null) {
        if (line.length() != n) {
          System.out.println("all rows not equal");
        }
        for (int i = 0; i < line.length(); i++) {
          maze[m][i] = line.charAt(i);
          if (line.charAt(i) == 'S') {
            startRow = m;
            startCol = i;
            System.out.println("Starting is " + String.valueOf(startRow) + "," + String.valueOf(startCol));
          }
        }
        line = reader.readLine();
        m++;
      }
      reader.close();
      System.out.println("maze size is " + String.valueOf(m) + "," + String.valueOf(n));

      int loopLength = getLoopLength(maze);
      System.out.println("loop length is " + String.valueOf(loopLength));
      System.out.println(loopLength / 2);

    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e2) {
      e2.printStackTrace();
    }
  }

  public static int getLoopLength(char[][] maze) {
    int currLength = 0;
    int[] loopSize = new int[1];
    int maxLoop = -1;
    boolean [][] visited = new boolean[m][n];
    for (int i = 0; i < m; i++) {
      for(int j = 0; j < n; j++) {
        visited[i][j]  = false;
      }
    }
    visited[startRow][startCol] = true;

    loopSize[0] = 0;
    dfs(maze, startRow-1, startCol, 0, loopSize, visited);
    maxLoop = maxLoop < loopSize[0] ? loopSize[0] : maxLoop;

    loopSize[0] = 0;
    dfs(maze, startRow+1, startCol, 0, loopSize, visited);
    maxLoop = maxLoop < loopSize[0] ? loopSize[0] : maxLoop;

    loopSize[0] = 0;
    dfs(maze, startRow, startCol-1, 0, loopSize, visited);
    maxLoop = maxLoop < loopSize[0] ? loopSize[0] : maxLoop;

    loopSize[0] = 0;
    dfs(maze, startRow, startCol+1, 0, loopSize, visited);
    maxLoop = maxLoop < loopSize[0] ? loopSize[0] : maxLoop;

    return maxLoop + 1;
  }

  public static void dfs(char[][] maze, int r, int c, int len, int[] loopSize, boolean[][] visited) {
    if (maze[r][c] == '.') return;

    if (visited[r][c]) {
      if (r == startRow && c == startCol) {
        System.out.println("reached back to S at " + String.valueOf(r) + "," + String.valueOf(c) + " with len " + String.valueOf(len));
        loopSize[0] = len > loopSize[0] ? len : loopSize[0];
      }
      return;
    }
    //System.out.println("currently visiting " + maze[r][c] + "," + String.valueOf(r) + ":" + String.valueOf(c) + " with len " + String.valueOf(len));

    visited[r][c] = true;
    int[][] dir = dirDot;
    if (maze[r][c] == 'S') dir = dirS;
    else if (maze[r][c] == '-') dir = dirDash;
    else if (maze[r][c] == '7') dir = dir7;
    else if (maze[r][c] == '|') dir = dirPipe;
    else if (maze[r][c] == 'L') dir = dirL;
    else if (maze[r][c] == 'J') dir = dirJ;
    else if (maze[r][c] == 'F') dir = dirF;
    else {
      visited[r][c] = false;
      return;
    }

    for (int i = 0; i < dir.length; i++) {
      int nextr = r + dir[i][0];
      int nextc = c + dir[i][1];

      if (nextr < 0 || nextr >= m || nextc < 0 || nextc >= n) continue;
      dfs(maze, nextr, nextc, len+1, loopSize, visited);
    }
    visited[r][c] = false;
  }
}
