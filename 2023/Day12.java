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
    String[] springs = s[1].split(",");
    for (int i = 0; i < springs.length; i++) {
      seq.add(Integer.parseInt(springs[i]));
    }

    //long q = countHelper(s[0], 0, currPattern, seq);
    long q = countWays2(s[0], seq);
    System.out.println(input + ":::" + String.valueOf(q));
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

  public static String generate(String s, int copyCount) {
    String result = s;
    for (int i = 1; i < copyCount; i++) {
      result += "?" + s;
    }
    return result;
  }

  public static List<Integer> generateInt(List<Integer> seq, int copyCount) {
    List<Integer> nextSeq = new ArrayList<>();
    for (int i = 0; i < copyCount; i++) nextSeq.addAll(seq);
    return nextSeq;
  }

  public static long countWays2(String input, List<Integer> inputSeq) {
    String s = generate(input, 5);
    List<Integer> seq = generateInt(inputSeq, 5);
    int m = s.length();
    int n = seq.size();
    long[][] dp = new long[m+1][n+1];

    for (int i = 0; i < m+1; i++)
    for(int j = 0; j < n+1; j++) {
      dp[i][j] = 0;
    }

    //base cases
    //case 1
    dp[m][n] = 1;

    //case 2
    for (int i = m-1; i >= 0; i--) {
      if (s.charAt(i) != '#') {
        dp[i][n] = 1;
      } else {
        break;
      }
    }

    for (int i = m-1; i >= 0; i--) {
      for (int j = n-1; j >= 0; j--) {
        if (s.charAt(i) == '.') {
          dp[i][j] = dp[i+1][j];
        } else if (s.charAt(i) == '#') {
          //starting from i, can we get that many #'s
          dp[i][j] = countHash(s, dp, seq, i, j, m);
        } else { //?
          //count from both and update.
          //set to .
          dp[i][j] = dp[i+1][j];
          //set to #
          dp[i][j] += countHash(s, dp, seq, i, j, m);
        }
      }
    }

    //printArr(dp, s);

    return dp[0][0];
  }

  public static long countHash(String s, long[][] dp, List<Integer> seq, int i, int j, int m) {
    int count = 1, k = 0;
    long result = 0;
    for (k = i+1; k < (m) && count < seq.get(j); k++) {
      if (s.charAt(k) != '.') {
        count++;
      } else {
        break;
      }
    }
    if (count == seq.get(j)) {
      if (k == m) result = dp[k][j+1];
      else if ((k < m) && s.charAt(k) != '#') result = dp[k+1][j+1];
    }

    return result;
  }

  public static void printArr(long[][] dp, String s) {
    System.out.println("dp arr");
    for (int i = 0; i < dp.length; i++) {
      if (i < s.length()) System.out.print(s.charAt(i) + " : ");
      else System.out.print("E : ");
      for (int j = 0; j < dp[0].length; j++) {
        System.out.print(dp[i][j]);
      }
      System.out.println(" " + String.valueOf(i));
    }
  }
}
