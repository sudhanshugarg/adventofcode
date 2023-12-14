import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

class Day14 {
  public static Map<Long, List<Long>> cycleCounts = new HashMap<>();
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

      List<Long> totals = new ArrayList<>();

      int cycles = 1000000000;
      for (long i = 0; i < cycles; i++) {
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
        totals.add(total);
        //System.out.println("cycles " + String.valueOf(i+1) + " : " + String.valueOf(total));
        List<Long> cycleCount = cycleCounts.getOrDefault(total, new ArrayList<Long>());
        cycleCount.add(i);
        cycleCounts.put(total, cycleCount);
        if (i > 10000 && foundPattern(totals, cycles)) break;
      }
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


  public static boolean foundPattern(List<Long> totals, long cycles) {
    boolean found = false;
    for (Map.Entry<Long, List<Long>> entry : cycleCounts.entrySet()) {
      long total = entry.getKey();
      List<Long> cycleCount = entry.getValue();
      if (arithmeticProgression(total, cycleCount)) {
        long[] ad = new long[2];
        calculateAandD(totals, total, cycleCount, ad);
        calculateCount(ad, cycles, totals);
        found = true;
        break;
      }
    }
    return found;
  }

  public static boolean arithmeticProgression(long total, List<Long> c) {
    if (c.size() < 20) return false;
    //take last 10
    boolean flag = true;
    int n = c.size();
    long diff = c.get(n-10) - c.get(n-11);
    for (int i = n-10; flag && i < n; i++) {
      flag = (c.get(i) - c.get(i-1) == diff);
    }
    
    return flag;
  }


  public static void calculateAandD(List<Long> totals, long curr, List<Long> cycleCount, long[] ad) {
    int n = cycleCount.size();
    Long diff = cycleCount.get(n-1) - cycleCount.get(n-2);

    Long next = cycleCount.get(n-1);
    int i;
    for (i = n - 2; i >= 0; i--) {
      next -= diff;
      if (next != cycleCount.get(i)) {
        break;
      }
    }
    i++;
    ad[1] = diff;

    Long cycleNum = cycleCount.get(i);
    next = cycleNum - diff;
    while (next >= 0 && ((1 * totals.get(next.intValue())) == (1 * totals.get(cycleNum.intValue())))) {
      cycleNum = cycleNum - 1;
      next = next - 1;
    }
    ad[0] = cycleNum;
  }


  public static void calculateCount(long[] ad, Long cycles, List<Long> totals) {
    if (cycles < ad[0]) {
      System.out.println("cycle not reached");
      System.out.println(totals.get(cycles.intValue()));
      return; 
    }

    int k;
    for (int i = 0; i < ad[1]; i++) {
      k = (int) ad[0] + i;
    }

    cycles -= (ad[0] + 1);
    Long rem = cycles % ad[1];
    System.out.println(cycles);
    rem += ad[0];
    System.out.println("A = " + String.valueOf(ad[0]) + ", D = " + String.valueOf(ad[1]));
    System.out.println(totals.get(rem.intValue()));
  }

}
