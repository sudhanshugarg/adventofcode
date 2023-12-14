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
      List<String> platform = new ArrayList<>();
      while(line != null) {
        platform.add(line);
        line = reader.readLine();
      }
      reader.close();
      char[][] np = tiltNorth(platform);
      long total = computeLoad(np);
      System.out.println(total);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static char[][] tiltNorth(List<String> p) {
    int m = p.size();
    int n = p.get(0).length();
    char[][] np = new char[m][n];
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        np[i][j] = p.get(i).charAt(j);
      }
    }

    int k = 0;
    for (int j = 0; j < n; j++) {
      for (int i = 1; i < m; i++) {
        if (np[i][j] != 'O') continue;
        k = i-1;
        while(k >= 0 && np[k][j] == '.') k--;
        if ((k+1) != i) {
          np[k+1][j] = 'O';
          np[i][j] = '.';
        }
      }
    }
    return np;
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

}
