import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

class Day12 {
  public static void main(String[] args) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(args[0]));
      String line = reader.readLine();
      long total = 0;
      while(line != null) {
        long ways = countWays(line);
        total += ways;
        line = reader.readLine();
      }
      System.out.println();
      System.out.println(total);
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static long countWays(String input) {
    List<Integer> seq = new ArrayList<>();
    String currPattern = "";

    String[] s = input.split(" ");
    //System.out.println("$" + s[0] + "$");
    //System.out.println("$" + s[1] + "$");
    String[] springs = s[1].split(",");
    for (int i = 0; i < springs.length; i++) {
      seq.add(Integer.parseInt(springs[i]));
    }

    long q = countHelper(s[0], 0, currPattern, seq);
    //System.out.println(input + ":::" + String.valueOf(q));
    return q;
  }

  public static long countHelper(String input, int start, String currPattern, List<Integer> seq) {
    if (start >= input.length()) {
      if (matches(currPattern, seq)) return 1;
      return 0;
    }

    if (input.charAt(start) != '?') return countHelper(input, start+1, currPattern + input.charAt(start), seq);
    //choose or dont
    return countHelper(input, start+1, currPattern + ".", seq) + countHelper(input, start+1, currPattern + "#", seq);
  }

  public static boolean matches(String s, List<Integer> seq) {
    int i = 0;
    int j = 0;

    boolean flag = true;
    int start, end;
    while(i < s.length() && j < seq.size() && flag) {
      while(i < s.length() && s.charAt(i) == '.') i++;
      if (i >= s.length()) break;
      start = end = i;
      while(end < s.length() && s.charAt(end) == '#') end++;
      if (seq.get(j) != (end-start)) {
        flag = false;
      } else {
        i = end;
        j++;
      }
    }

    
    while(i < s.length() && s.charAt(i) == '.') i++;
    return flag && (j == seq.size() && (i == s.length()));
  }
}
