import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

class Day13 {
  public static void main(String[] args) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(args[0]));
      String line = reader.readLine();
      long total = 0;
      List<List<String>> lavas = new ArrayList<>();
      List<String> lava = new ArrayList<>();
      while(line != null) {
        if (line.length() == 0) {
          List<String> currLava = new ArrayList<>();
          currLava.addAll(lava);
          lavas.add(currLava);

          lava = new ArrayList<>();
        } else {
          lava.add(line);
        }
        line = reader.readLine();
      }
      if (lava.size() > 0) lavas.add(lava);

      int[][] pos = new int[lavas.size()][2];
      for (int i = 0; i < pos.length; i++) {
        pos[i][0] = pos[i][1] = -1;
      }
      computePalindromePositionLava(lavas, pos);

      for (int i = 0; i < pos.length; i++) {
        if (pos[i][1] < 0) continue;
        if (pos[i][0] == 0) total += 100 * pos[i][1];
        else total += pos[i][1];
      }

      System.out.println();
      System.out.println(total);
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public static void computePalindromePositionLava(List<List<String>> lavas, int[][] pos) {
    for (int i = 0; i < lavas.size(); i++) {
      computePositionInLava(lavas.get(i), i, pos);
    }
  }

  public static void computePositionInLava(List<String> lava, int m, int[][] pos) {
    //need to update pos[m][0] --> 0 for horizontal and 1 for vertical, 
    //and pos[m][1] the actual value

    int v = findVerticalPalindrome(lava);
    int h = findHorizontalPalindrome(lava);
    
    if (v > 0) {
      pos[m][0] = 1;
      pos[m][1] = v;
    } else if (h > 0) {
      pos[m][0] = 0;
      pos[m][1] = h;
    }
  }

  public static int findVerticalPalindrome(List<String> lava) {
    int R = lava.size();
    int C = lava.get(0).length();

    int v = 0;
    boolean flag = true;
    int p = 0, q = 0;
    String row = "";
    for (int j = 1; j < C; j++) {
      flag = true;
      for (int i = 0; flag && i < R; i++) {
        row = lava.get(i);
        //for row i, does everything before j and after j match?
        p = j-1; q = j;
        while(p >= 0 && q < C && row.charAt(p) == row.charAt(q)) {
          p--; q++;
        }
        flag = ((p < 0) || (q >= C));
      }
      if (flag) v = j;
    }
    return v;
  }

  public static int findHorizontalPalindrome(List<String> lava) {
    int R = lava.size();
    int C = lava.get(0).length();

    int h = 0;
    boolean flag = true;
    int p = 0, q = 0;
    for (int i = 1; i < R; i++) {
      flag = true;
      for (int j = 0; flag && j < C; j++) {
        //for row i, does everything before j and after j match?
        p = i-1; q = i;
        while(p >= 0 && q < R && lava.get(p).charAt(j) == lava.get(q).charAt(j)) {
          p--; q++;
        }
        flag = ((p < 0) || (q >= R));
      }
      if (flag) h = i;
    }
    return h;
  }
}
