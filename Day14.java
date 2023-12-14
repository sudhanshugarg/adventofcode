import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

class Day14 {
  public static void main(String[] args) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(args[0]));
      String line = reader.readLine();
      List<String> p = new ArrayList<>();
      while(line != null) {
        p.add(line);
        line = reader.readLine();
      }
      reader.close();
      int m = p.size();
      int n = p.get(0).length();
      char[][] np = new char[m][n];
      for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
          np[i][j] = p.get(i).charAt(j);
        }
      }
      long total = 0;

      int cycles = 1000000000;
      for (int i = 0; i < cycles; i++) {
        tiltNorth(np); //north
        //printNorth(np);
        np = transposeRight(np);
        tiltNorth(np); //west
        //printWest(np);
        np = transposeRight(np);
        tiltNorth(np); //south
        //printSouth(np);
        np = transposeRight(np);
        tiltNorth(np); //east
        //printEast(np);
        np = transposeRight(np);
        //printPlatform(np);
        total = computeLoad(np);
        System.out.println("cycles " + String.valueOf(i+1) + " : " + String.valueOf(total));
      }
      System.out.println(total);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void tiltNorth(char [][] p) {
    int m = p.length;
    int n = p[0].length;

    int k = 0;
    for (int j = 0; j < n; j++) {
      for (int i = 1; i < m; i++) {
        if (p[i][j] != 'O') continue;
        k = i-1;
        while(k >= 0 && p[k][j] == '.') k--;
        if ((k+1) != i) {
          p[k+1][j] = 'O';
          p[i][j] = '.';
        }
      }
    }
  }

  public static char[][] transposeRight(char [][] p) {
    int m = p.length;
    int n = p[0].length;
    char[][] next = new char[n][m];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        next[i][j] = p[m-1-j][i];
      }
    }
    return next;
  }


  public static long computeLoad(char[][] p) {
    int m = p.length;
    int n = p[0].length;
    int mult = m;
    long total = 0;
    for (int i = 0; i < m; i++, mult--) {
      for (int j = 0; j < n; j++) {
        if (p[i][j] == 'O') {
          total += mult;
        }
      }
    }
    return total;
  }

  public static char[][] rotate(char[][] p, int times) {
    char[][] np = p;
    for (int i = 0; i < times; i++) {
      np = transposeRight(np);
    }
    return np;
  }

  public static void printPlatform(char[][] p) {
    int m = p.length;
    int n = p[0].length;
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        System.out.print(p[i][j]);
      }
      System.out.println();
    }
    System.out.println();
  }

  public static void printNorth(char[][] p) {
    System.out.println("north");
    printPlatform(p);
  }

  public static void printWest(char[][] p) {
    System.out.println("west");
    char[][] np = rotate(p, 3);
    printPlatform(np);
  }

  public static void printSouth(char[][] p) {
    System.out.println("south");
    char[][] np = rotate(p, 2);
    printPlatform(np);
  }

  public static void printEast(char[][] p) {
    System.out.println("east");
    char[][] np = rotate(p, 1);
    printPlatform(np);
  }




}
