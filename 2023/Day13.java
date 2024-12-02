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
      reader.close();
      if (lava.size() > 0) lavas.add(lava);

      int[][] pos = new int[lavas.size()][2];
      int[][] pos2 = new int[lavas.size()][2];
      for (int i = 0; i < pos.length; i++) {
        pos[i][0] = pos[i][1] = -1;
        pos2[i][0] = pos2[i][1] = -1;
      }
      computePalindromePositionLava(lavas, pos);
      long total = calculateTotal(pos);
      System.out.println();
      System.out.println("part 1: " + String.valueOf(total));

      //part 2
      findSmudgeAndUpdateMirror(lavas, pos, pos2);
      total = calculateTotal(pos2);
      System.out.println();
      System.out.println("part 2: " + String.valueOf(total));

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static long calculateTotal(int[][] pos) {
    long total = 0;
    for (int i = 0; i < pos.length; i++) {
      if (pos[i][1] < 0) continue;
      if (pos[i][0] == 0) total += 100 * pos[i][1];
      else total += pos[i][1];
    }
    return total;
  }


  public static void computePalindromePositionLava(List<List<String>> lavas, int[][] pos) {
    for (int i = 0; i < lavas.size(); i++) {
      computePositionInLava(lavas.get(i), i, pos);
    }
  }

  public static void computePositionInLava(List<String> lava, int m, int[][] pos) {
    //need to update pos[m][0] --> 0 for horizontal and 1 for vertical, 
    //and pos[m][1] the actual value

    List<Integer> v = findVerticalPalindrome(lava);
    List<Integer> h = findHorizontalPalindrome(lava);
    
    if (v.size() > 0) {
      pos[m][0] = 1;
      pos[m][1] = v.get(0);
    } else if (h.size() > 0) {
      pos[m][0] = 0;
      pos[m][1] = h.get(0);
    }
  }

  public static boolean computePositionInLava2(List<String> lava, int m, int[][] pos, int[][] pos2) {
    //need to update pos[m][0] --> 0 for horizontal and 1 for vertical, 
    //and pos[m][1] the actual value
    boolean found = false;

    List<Integer> v = findVerticalPalindrome(lava);
    List<Integer> h = findHorizontalPalindrome(lava);

    for (int i = 0; !found && i < v.size(); i++) {
      pos2[m][0] = 1;
      pos2[m][1] = v.get(i);
      found = isValidReplacement(m, pos, pos2);
    }
    if (found) return true;
    
    for (int i = 0; !found && i < h.size(); i++) {
      pos2[m][0] = 0;
      pos2[m][1] = h.get(i);
      found = isValidReplacement(m, pos, pos2);
    }
    if (found) return true;

    pos2[m][0] = pos2[m][1] = -1;
    return false;
  }

  public static List<Integer> findVerticalPalindrome(List<String> lava) {
    int R = lava.size();
    int C = lava.get(0).length();

    List<Integer> verticals = new ArrayList<>();

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
      if (flag) {
        verticals.add(j);
      }
    }
    //System.out.println("verticals");
    //System.out.println(verticals);
    return verticals;
  }

  public static List<Integer> findHorizontalPalindrome(List<String> lava) {
    int R = lava.size();
    int C = lava.get(0).length();

    List<Integer> horizontals = new ArrayList<>();
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
      if (flag) {
        horizontals.add(i);
      }
    }
    //System.out.println("horizontals");
    //System.out.println(horizontals);
    return horizontals;
  }

  public static void findSmudgeAndUpdateMirror(List<List<String>> lavas, int[][] pos, int[][] pos2) {
    Character c = '$', other;
    String former = "", replacement = "";
    boolean found = false;
    for (int k = 0; k < lavas.size(); k++) {
      found = false;
      List<String> lava = lavas.get(k);
      int R = lava.size();
      int C = lava.get(0).length();
      for (int i = 0; !found && i < R; i++) {
        former = lava.get(i);
        for (int j = 0; !found && j < C; j++) {
          c = former.charAt(j);
          other = c == '.' ? '#' : '.';
          //System.out.println("replacing line: " + former + ", character = " + String.valueOf(j) + ", " + c + ", with " + other);
          replacement = former.substring(0, j) + other + former.substring(j+1);
          lava.set(i, replacement);
          found = computePositionInLava2(lava, k, pos, pos2);
          lava.set(i, former);
          if(!found) {
            //System.out.println("continuing..");
          }
        }
      }
    }
  }

  public static boolean isValidReplacement(int k, int[][] pos, int[][] pos2) {
    boolean flag = (pos2[k][0] >= 0 && pos2[k][1] > 0 && (pos2[k][0] != pos[k][0] || pos2[k][1] != pos[k][1]));
    return flag;
  }

  public static void printLava(List<String> lava) {
    for (int i = 0; i < lava.size(); i++) System.out.println(lava.get(i));
    System.out.println();
  }

  public static void printPos(int[][] pos, int i) {
    System.out.println(String.valueOf(pos[i][0]) + "," + String.valueOf(pos[i][1]));

  }
}
