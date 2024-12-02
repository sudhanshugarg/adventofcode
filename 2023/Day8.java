import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

class Day8 {
  public static void main(String[] args) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(args[0]));
      String instructions = reader.readLine();
      String line = reader.readLine();

      int nextValue = 0;
      Integer val;
      int [][] g = new int[1000][2];
      Map<String, Integer> m = new HashMap<>();
      Map<Integer, String> rev = new HashMap<>();
      String a, b, c;

      while(line != null) {
        if (line.length() == 0) {
          line = reader.readLine();
          continue;
        }

        a = line.substring(0, 3);
        b = line.substring(7, 10);
        c = line.substring(12, 15);
        //System.out.println(a + "#" + b + "#" + c);

        val = m.putIfAbsent(a, nextValue);
        if (val == null) {
          rev.putIfAbsent(nextValue, a);
          nextValue++;
        }
        val = m.putIfAbsent(b, nextValue);
        if (val == null) {
          rev.putIfAbsent(nextValue, b);
          nextValue++;
        }
        val = m.putIfAbsent(c, nextValue);
        if (val == null) {
          rev.putIfAbsent(nextValue, c);
          nextValue++;
        }

        g[m.get(a)][0] = m.get(b);
        g[m.get(a)][1] = m.get(c);
        line = reader.readLine();
      }
      reader.close();

      //computeSteps(g, m.get("AAA"), m.get("ZZZ"), instructions);
      computeParallelSteps(g, instructions, m, rev);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void computeSteps(int[][] g, int start, int end, String instructions) {
    System.out.print(start);
    System.out.print(",");
    System.out.print(end);
    System.out.print(",");
    System.out.println(instructions);
    int steps = 0;
    boolean found = false;
    int i = 0, next;
    while (!found) {
      steps++;
      if (instructions.charAt(i) == 'L') {
        next = g[start][0];
      } else {
        next = g[start][1];
      }
      if (next == end) {
        found = true;
      } else {
        start = next;
      }
      i++;
      if (i >= instructions.length()) i = 0;
    }
    System.out.println(steps);
  }

  public static void computeParallelSteps(int[][] g, String instructions,
                                          Map<String, Integer> m, Map<Integer, String> rev) {

    Set<Integer> orig = new HashSet<>();
    Set<Integer> ends = new HashSet<>();
    Set<Integer> nexts = new HashSet<>();
    Set<Integer> starts = new HashSet<>();

//    [736, 609, 242, 682, 139, 607]
//    JSA#AAA#QFA#RLA#QLA#RXA#
//    [358, 519, 620, 493, 205, 63]
//    QCZ#ZZZ#PQZ#JJZ#LRZ#VHZ#

    String curr = "", curr2 = "";
    for (Map.Entry<String, Integer> entry : m.entrySet()) {
      String key = entry.getKey();
      Integer val = entry.getValue();
      if (key.charAt(2) == 'A') {
        orig.add(val);
        curr += rev.get(val) + "#";
      } else if (key.charAt(2) == 'Z') {
        ends.add(val);
        curr2 += rev.get(val) + "#";
      }
    }
    System.out.println(orig);
    System.out.println(curr);
    System.out.println(ends);
    System.out.println(curr2);
    System.out.println(instructions);
    List<Integer> stepList = new ArrayList<>();

    Iterator<Integer> origIt;
    origIt = orig.iterator();
    while(origIt.hasNext()) {
      starts.clear();
      int currStart = origIt.next();
      starts.add(currStart);
      System.out.println("starting with: " + rev.get(currStart));

      int steps = 0;
      int i = 0, next;
      Integer start;
      Iterator<Integer> it;
      int to;
      int times = 1;
      int foundTimes = 0;
      while (foundTimes < times) {
        steps++;
        it = starts.iterator();
        nexts.clear();

        curr = "";
        to = 1;
        if (instructions.charAt(i) == 'L') {
          to = 0;
        }

        while(it.hasNext()) {  
          start = it.next();
          next = g[start][to];
          nexts.add(next);
          curr += rev.get(next) + "#";
          if (ends.contains(next)) {
            System.out.println("found match " + curr + "," + String.valueOf(steps));
            stepList.add(steps);
            foundTimes++;
          }
        }
        //System.out.println(curr);
        starts = new HashSet<>(nexts);
        i++;
        if (i >= instructions.length()) i = 0;
      }
    }

    //now we have all teh step counts, find the lcm.
    System.out.println(stepList);
    long lcmult = stepList.get(0);
    for (int i = 1; i < stepList.size(); i++) {
      lcmult = lcm(lcmult, stepList.get(i));
    }
    System.out.println(lcmult);
  }

  public static long gcd (long a, long b) {
    if (a == 0) return b;
    if (b == 0) return a;
    if (a < b) return gcd(b % a, a);
    return gcd(a % b, b);
  }

  public static long lcm(long a, long b) {
    long mult = a / gcd(a, b);
    return mult * b;
  }
}
