import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Collections;
import java.lang.Enum;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.Set;
import java.util.HashSet;
import java.math.BigDecimal;
import java.math.RoundingMode;

//run with java -Xss12040k Day23 23_sample
class Day24 {

  public static void main(String[] args) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(args[0]));
      String line = reader.readLine();
      int id = 0;

      List<Hailstone> hs = new ArrayList<>();
      while (line != null) {
        Hailstone h = new Hailstone(line);
        hs.add(h);
        System.out.println(h);
        line = reader.readLine();
      }
      reader.close();

      int total = part1(hs);
      System.out.println("part 1: " + String.valueOf(total));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static int part1(List<Hailstone> hs) {
    int n = hs.size();
    int ans = 0;
    BigDecimal low = new BigDecimal("200000000000000");
    BigDecimal high = new BigDecimal("400000000000000");
    for (int i = 0; i < n; i++) {
      Hailstone hi = hs.get(i);
      for (int j = i+1; j < n; j++) {
        Hailstone hj = hs.get(j);
        System.out.println("testing");
        System.out.println(hi);
        System.out.println(hj);
        if (hi.meetsXY(hj, low, high)) {
          System.out.println("YES");
          ans++;
        } else {
          System.out.println("NO");
        }
        System.out.println();
      }
    }
    return ans;
  }
}

class Hailstone {
  public BigDecimal px, py, pz;
  public BigDecimal vx, vy, vz;
  public BigDecimal a, b, c;

  Hailstone(String s) {
    int pos = 0;
    for (int i = 0; i < s.length(); i++) if (s.charAt(i) == '@') {
      pos = i; break;
    }

    String[] init = s.substring(0, pos-1).split(",");
    px = new BigDecimal(init[0].trim());
    py = new BigDecimal(init[1].trim());
    pz = new BigDecimal(init[2].trim());

    String[] velocities = s.substring(pos+1).split(",");
    vx = new BigDecimal(velocities[0].trim());
    vy = new BigDecimal(velocities[1].trim());
    vz = new BigDecimal(velocities[2].trim());

    a = vy;
    b = vx.multiply(new BigDecimal("-1"));
    c = vx.multiply(py).add(vy.multiply(px).negate());
  }

  private static boolean inRange(BigDecimal x, BigDecimal low, BigDecimal high) {
    return (x.compareTo(low) >= 0) && (x.compareTo(high) <= 0);
  }

  public static BigDecimal intersection(BigDecimal p1, BigDecimal p2, BigDecimal q1, BigDecimal q2) {
    return p2.multiply(q1).add(p1.multiply(q2).negate());
  }

  public boolean isVelocityZero() {
    return this.vx.equals(BigDecimal.ZERO) && this.vy.equals(BigDecimal.ZERO);
  }

  public boolean meetsXY(Hailstone h, BigDecimal low, BigDecimal high) {
    if (this.px.equals(h.px) && this.py.equals(h.py)) {
      //check if the point lies within the range
      return Hailstone.inRange(px, low, high) && Hailstone.inRange(py, low, high);
    }

    if (this.isVelocityZero() || h.isVelocityZero()) {
      System.out.println("sugarg tell me both velocities are not zero");
      return false;
    }

    //check if parallel
    if (this.a.multiply(h.b).equals(this.b.multiply(h.a))) {
      //if intercepts are not equal, then they can never intersect
      if (!this.c.multiply(h.a).equals(h.c.multiply(this.a))) return false;

      //since the lines are identical, its possible the stones will intersect.
      System.out.println("sugarg Lets see if this is a case");
      return false;
    }

    BigDecimal x = BigDecimal.ONE;
    BigDecimal y = BigDecimal.ONE;
    if (!this.a.equals(BigDecimal.ZERO)) {
      y = Hailstone.intersection(this.a, h.a, this.c, h.c);
      y = y.divide(Hailstone.intersection(this.b, h.b, this.a, h.a), 2, RoundingMode.HALF_UP);
      x = y.negate().multiply(this.b).add(this.c.negate()).divide(this.a, 2, RoundingMode.HALF_UP);
    } else {
      x = Hailstone.intersection(this.b, h.b, this.c, h.c);
      x = x.divide(Hailstone.intersection(this.a, h.a, this.b, h.b), 2, RoundingMode.HALF_UP);
      y = x.negate().multiply(this.a).add(this.c.negate()).divide(this.b, 2, RoundingMode.HALF_UP);
    }

    if (!inRange(x, low, high) || !inRange(y, low, high)) return false;

    //now that we have the point of intersection, for each, find whether its in the future
    //or in the past
    BigDecimal t1 = BigDecimal.ONE;
    BigDecimal t2 = BigDecimal.ONE;
    if (!this.vx.equals(BigDecimal.ZERO)) {
      t1 = x.add(this.px.negate()).divide(this.vx, 2, RoundingMode.HALF_UP);
    } else {
      t1 = y.add(this.py.negate()).divide(this.vy, 2, RoundingMode.HALF_UP);
    }

    if (!h.vx.equals(BigDecimal.ZERO)) {
      t2 = x.add(h.px.negate()).divide(h.vx, 2, RoundingMode.HALF_UP);
    } else {
      t2 = y.add(h.py.negate()).divide(h.vy, 2, RoundingMode.HALF_UP);
    }

    if (t1.compareTo(BigDecimal.ZERO) >= 0 && t2.compareTo(BigDecimal.ZERO) >= 0) {
      System.out.println(x + "," + y);
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return "pos = " + px + "," + py + "," + pz + " velocity = " + vx + "," + vy + "," + vz + " line = " + a + "," + b + "," + c;
  }
}
