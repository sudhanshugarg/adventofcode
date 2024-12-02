import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

class Day9 {
  public static void main(String[] args) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(args[0]));
      String line = reader.readLine();
      long next = 0, total = 0;
      while(line != null) {
        next = nextNumber(line);
        total += next;
        line = reader.readLine();
      }
      reader.close();
      System.out.println(total);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static long nextNumber(String s) {
    String[] sequence = s.split("\\s+");
    List<Long> seq = new ArrayList<>();
    for (int i = 0; i < sequence.length; i++) {
      seq.add(Long.parseLong(sequence[i]));
    }
    int n = sequence.length;
    //calculateNext(seq);
    calculatePrev(seq);
    System.out.println(seq.get(n));
    return seq.get(n);
  }

  public static void calculateNext(List<Long> seq) {
    if (checkEqual(seq)) {
      seq.add(seq.get(0));
      return;
    }
    int n = seq.size();
    List<Long> nextDiff = diffs(seq);
    calculateNext(nextDiff);
    seq.add(nextDiff.get(n-1) + seq.get(n-1));
  }

  public static void calculatePrev(List<Long> seq) {
    if (checkEqual(seq)) {
      seq.add(seq.get(0));
      return;
    }
    int n = seq.size();
    List<Long> nextDiff = diffs(seq);
    calculatePrev(nextDiff);
    seq.add(seq.get(0) - nextDiff.get(n-1));
  }


  public static List<Long> diffs(List<Long> seq) {
    List<Long> nextDiff = new ArrayList<>();
    for (int i = 1; i < seq.size(); i++) {
      nextDiff.add(i-1, seq.get(i) - seq.get(i-1));
    }
    return nextDiff;
  }

  public static boolean checkEqual(List<Long> seq) {
    boolean equal = true;
    for (int i = 1; i < seq.size() && equal; i++) {
      equal = (seq.get(i) == seq.get(i-1));
    }
    return equal;
  }
}
