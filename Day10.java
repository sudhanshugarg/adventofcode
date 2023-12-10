import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

//run with java -Xss12040k Day10 10_input

class Day10 {
  //U,D,L,R
  public static int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
  public static int[] dir7 = {1, 2};
  public static int[] dirPipe = {0, 1};
  public static int[] dirL = {0, 3};
  public static int[] dirJ = {0, 2};
  public static int[] dirF = {1, 3};
  public static int[] dirDash = {2, 3};
  public static int[] dirDot = {};
  public static int startRow = -1;
  public static int startCol = -1;
  public static int m = -1;
  public static int n = -1;

  public static char[] move7 = {'D', 'L'};
  public static char[] movePipe = {'U', 'D'};
  public static char[] moveL = {'U', 'R'};
  public static char[] moveJ = {'U', 'L'};
  public static char[] moveF = {'D', 'R'};
  public static char[] moveDash = {'L', 'R'};
  public static char[] moveDot = {};

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
    String[] loop = new String[1];
    int maxLoop = -1;
    String chosenLoop = "";
    boolean [][] visited = new boolean[m][n];
    for (int i = 0; i < m; i++) {
      for(int j = 0; j < n; j++) {
        visited[i][j]  = false;
      }
    }
    visited[startRow][startCol] = true;
    List<Character> moves = new ArrayList<>();
    List<String> paths = new ArrayList<>();

    boolean canUp = false, canDown = false, canLeft = false, canRight = false;

    canUp = maze[startRow-1][startCol] == '7' || maze[startRow-1][startCol] == 'F' || maze[startRow-1][startCol] == '|';
    if (canUp) {
    loopSize[0] = 0;
    loop[0] = "";
    System.out.println('U');
    moves.add('U');
    dfs(maze, startRow-1, startCol, 0, loopSize, visited, moves, loop);
    maxLoop = maxLoop < loopSize[0] ? loopSize[0] : maxLoop;
    paths.add(loop[0]);
    moves.remove(0);
    }
    

    canDown = maze[startRow+1][startCol] == 'L' || maze[startRow+1][startCol] == 'J' || maze[startRow+1][startCol] == '|';
    if (canDown) {
    loopSize[0] = 0;
    loop[0] = "";
    System.out.println('D');
    moves.add('D');
    dfs(maze, startRow+1, startCol, 0, loopSize, visited, moves, loop);
    maxLoop = maxLoop < loopSize[0] ? loopSize[0] : maxLoop;
    paths.add(loop[0]);
    moves.remove(0);
    }

    canLeft = maze[startRow][startCol-1] == 'F' || maze[startRow][startCol-1] == 'L' || maze[startRow][startCol-1] == '-';
    if (canLeft) {
    loopSize[0] = 0;
    loop[0] = "";
    System.out.println('L');
    moves.add('L');
    dfs(maze, startRow, startCol-1, 0, loopSize, visited, moves, loop);
    maxLoop = maxLoop < loopSize[0] ? loopSize[0] : maxLoop;
    paths.add(loop[0]);
    moves.remove(0);
    }

    canRight = maze[startRow][startCol+1] == '7' || maze[startRow][startCol+1] == 'J' || maze[startRow][startCol+1] == '-';
    if (canRight) {
    loopSize[0] = 0;
    loop[0] = "";
    System.out.println('R');
    moves.add('R');
    dfs(maze, startRow, startCol+1, 0, loopSize, visited, moves, loop);
    maxLoop = maxLoop < loopSize[0] ? loopSize[0] : maxLoop;
    paths.add(loop[0]);
    moves.remove(0);
    }

    for (int i = 0; i < paths.size(); i++) {
      if ((maxLoop + 1) == paths.get(i).length()) {
        chosenLoop = paths.get(i);
        break;
      }
    }

    findInternalTiles2(chosenLoop, maze);

    return chosenLoop.length();
  }

  public static void dfs(char[][] maze, int r, int c, int len, int[] loopSize, boolean[][] visited, List<Character> moves, String[] loop) {
    if (maze[r][c] == '.') return;

    if (visited[r][c]) {
      if (r == startRow && c == startCol) {
        System.out.println("reached back to S at " + String.valueOf(r) + "," + String.valueOf(c) + " with len " + String.valueOf(len));
        if (loopSize[0] < len) {
          loopSize[0] = len;
          loop[0] = path(moves);
        }
      }
      return;
    }

    visited[r][c] = true;
    int[] dir = dirDot;
    char[] move = moveDot;
    if (maze[r][c] == '-') {
      dir = dirDash;
      move = moveDash;
    }
    else if (maze[r][c] == '7') { 
      dir = dir7;
      move = move7;
    }
    else if (maze[r][c] == '|') { 
      dir = dirPipe;
      move = movePipe;
    }
    else if (maze[r][c] == 'L') { 
      dir = dirL;
      move = moveL;
    }
    else if (maze[r][c] == 'J') { 
      dir = dirJ;
      move = moveJ;
    }
    else if (maze[r][c] == 'F') { 
      dir = dirF;
      move = moveF;
    }
    else {
      visited[r][c] = false;
      return;
    }

    for (int i = 0; i < dir.length; i++) {
      int nextr = r + dirs[dir[i]][0];
      int nextc = c + dirs[dir[i]][1];

      if (nextr < 0 || nextr >= m || nextc < 0 || nextc >= n) continue;
      moves.add(move[i]);
      dfs(maze, nextr, nextc, len+1, loopSize, visited, moves, loop);
      moves.remove(moves.size() - 1);
    }
    visited[r][c] = false;
  }


  public static String path(List<Character> moves) {
    String p = "";
    for (int i = 0; i < moves.size(); i++) p += moves.get(i);
    return p;
  }

  public static void findInternalTiles2(String loop, char[][] maze) {
    int r = startRow, c = startCol;
    maze[r][c] = 'J';

    int[][] g = new int[m][n];
    for (int i = 0; i < m; i++)
      for (int j = 0; j < n; j++)
        g[i][j] = 0;

    for(int i = 0; i < loop.length(); i++) {
      g[r][c] = 1;
      if (loop.charAt(i) == 'U') r--;
      else if (loop.charAt(i) == 'D') r++;
      else if (loop.charAt(i) == 'L') c--;
      else if (loop.charAt(i) == 'R') c++;
    }

    int left = 2;
    int right = 3;

    for (int i = 0; i < loop.length(); i++) {
      if (loop.charAt(i) == 'U') {
        upTile(maze[r][c], r, c, g, left, right);
        r--;
      } else if (loop.charAt(i) == 'D') {
        downTile(maze[r][c], r, c, g, left, right);
        r++;
      } else if (loop.charAt(i) == 'L') {
        leftTile(maze[r][c], r, c, g, left, right);
        c--;
      } else if (loop.charAt(i) == 'R') {
        rightTile(maze[r][c], r, c, g, left, right);
        c++;
      }
    }
    filldfs(g);
  }

  public static void upTile(char cell, int r, int c, int[][] g, int left, int right) {
    if (cell == 'L') {
      updateLeft(g, r, c, left);
      updateBottom(g, r, c, left);
    } else if (cell == '|') {
      updateLeft(g, r, c, left);
      updateRight(g, r, c, right);
    } else if (cell == 'J') {
      updateRight(g, r, c, right);
      updateBottom(g, r, c, right);
    } else {
      System.out.println("error in changing tiles");
    }
  }

  public static void downTile(char cell, int r, int c, int[][] g, int left, int right) {
    if (cell == 'F') {
      updateTop(g, r, c, right);
      updateLeft(g, r, c, right);
    } else if (cell == '|') {
      updateLeft(g, r, c, right);
      updateRight(g, r, c, left);
    } else if (cell == '7') {
      updateTop(g, r, c, left);
      updateRight(g, r, c, left);
    } else {
      System.out.println("error in changing tiles");
    }
  }

  public static void leftTile(char cell, int r, int c, int[][] g, int left, int right) {
    if (cell == '7') {
      updateTop(g, r, c, right);
      updateRight(g, r, c, right);
    } else if (cell == '-') {
      updateTop(g, r, c, right);
      updateBottom(g, r, c, left);
    } else if (cell == 'J') {
      updateRight(g, r, c, left);
      updateBottom(g, r, c, left);
    } else {
      System.out.println("error in changing tiles");
    }
  }

  public static void rightTile(char cell, int r, int c, int[][] g, int left, int right) {
    if (cell == 'L') {
      updateLeft(g, r, c, right);
      updateBottom(g, r, c, right);
    } else if (cell == '-') {
      updateTop(g, r, c, left);
      updateBottom(g, r, c, right);
    } else if (cell == 'F') {
      updateLeft(g, r, c, left);
      updateTop(g, r, c, left);
    } else {
      System.out.println("error in changing tiles");
    }
  }

  /*
  public static String getTurn(String s) {
    if (s.charAt(0) > s.charAt(s.length()-1)) {
      return String(s.charAt(0)) + String(s.charAt(s.length()-1));
    } else return String(s.charAt(s.length()-1)) + String(s.charAt(0));
  }
  */

  public static void updateLeft(int[][] g, int r, int c, int val) {
    if (c-1 >= 0 && g[r][c-1] == 0) {
      g[r][c-1] = val;
    }
  }

  public static void updateRight(int[][] g, int r, int c, int val) {
    if (c+1 < n && g[r][c+1] == 0) {
      g[r][c+1] = val;
    }
  }

  public static void updateTop(int[][] g, int r, int c, int val) {
    if (r-1 >= 0 && g[r-1][c] == 0) {
      g[r-1][c] = val;
    }
  }

  public static void updateBottom(int[][] g, int r, int c, int val) {
    if (r+1 < m && g[r+1][c] == 0) {
      g[r+1][c] = val;
    }
  }

  public static void printarr(char[][] g) {
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        System.out.print(g[i][j]);
      }
      System.out.println();
    }
  }

  public static void printarr(int[][]g, char[][] maze) {
    System.out.println("both together");
    System.out.println("both together");
    System.out.println("both together");
    System.out.println("both together");
    System.out.println("both together");
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        if (g[i][j] == 1) System.out.print(maze[i][j]);
        else System.out.print(".");
      }
      System.out.println();
    }
  }


  public static void printarr(int[][] g) {
    System.out.println("Starting now...");
    System.out.println("Starting now...");
    System.out.println("Starting now...");
    System.out.println("Starting now...");
    System.out.println("Starting now...");
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        System.out.print(g[i][j]);
      }
      System.out.println();
    }
  }

  public static void filldfs(int[][] g) {
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        if (g[i][j] > 1) filldfsHelper(g, i, j, g[i][j]);
      }
    }

    int[] count =  new int[4];
    for (int i = 0; i < 4; i++) count[i] = 0;

    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        count[g[i][j]]++;
      }
    }
    for (int i = 0; i < 4; i++) {
      System.out.println(String.valueOf(i) + "::" + count[i]);
    }
  }


  public static void filldfsHelper(int[][] g, int r, int c, int val) {
    int nextr, nextc;
    for (int i = 0; i < 4; i++) {
      nextr = r + dirs[i][0];
      nextc = c + dirs[i][1];
      if (nextr < 0 || nextr >= m || nextc < 0 || nextc >= n || g[nextr][nextc] == 1 || g[nextr][nextc] == val) continue;
      if (g[nextr][nextc] != 0) {
        System.out.println("error in cell " + String.valueOf(nextr) + String.valueOf(nextc));
        continue;
      }
      g[nextr][nextc] = val;
      filldfsHelper(g, nextr, nextc, val);
    }
  }


  public static void printLoop(String loop) {
    System.out.println("Starting printing of loop....");
    System.out.println("Starting printing of loop....");
    System.out.println("Starting printing of loop....");
    System.out.println("Starting printing of loop....");
    char[][] dirmaze = new char[m][n];
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        dirmaze[i][j] = '.';
      }
    }
    int r = startRow, c = startCol;
    dirmaze[r][c] = 'S';
    for(int i = 0; i < loop.length(); i++) {
      dirmaze[r][c] = loop.charAt(i);
      if (loop.charAt(i) == 'U') {
        r--;
      }
      else if (loop.charAt(i) == 'D') {
        r++;
      }
      else if (loop.charAt(i) == 'L') {
        c--;
      }
      else if (loop.charAt(i) == 'R') {
        c++;
      }
    }
    printarr(dirmaze);
  }
}
