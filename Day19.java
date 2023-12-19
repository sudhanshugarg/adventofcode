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

class Day19 {
  public static void main(String[] args) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(args[0]));
      String line = reader.readLine();
      List<String> workflows = new ArrayList<>();
      List<String> ratings = new ArrayList<>();
      boolean flag = false;

      while (line != null) {
        if (line.length() == 0) {
          flag = true;
        } else {
          if (!flag) workflows.add(line);
          else ratings.add(line);
        }
        line = reader.readLine();
      }
      reader.close();

      long total = 0;
      Map<String, Workflow> mw = createWorkflows(workflows);
      total = part1(mw, ratings);
      System.out.println("part 1: " + String.valueOf(total));

      total = part2(mw);
      System.out.println("part 2: " + String.valueOf(total));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Map<String, Workflow> createWorkflows(List<String> workflows) {
    Map<String, Workflow> mw = new HashMap<>();
    for (int i = 0; i < workflows.size(); i++) {
      Workflow w = new Workflow(workflows.get(i));
      mw.put(w.getId(), w);
    }
    return mw;
  }

  public static long part1(Map<String, Workflow> mw, List<String> ratings) {
    long ans = 0;
    Rating r;
    String nextWorkflow = "in";

    for (int i = 0; i < ratings.size(); i++) {
      nextWorkflow = "in";
      r = new Rating(ratings.get(i));
      while(!nextWorkflow.equals("A") && !nextWorkflow.equals("R")) {
        nextWorkflow = mw.get(nextWorkflow).evaluate(r);
      }

      if (nextWorkflow.equals("A")) ans += r.sum();
    }
    return ans;
  }

  public static long part2(Map<String, Workflow> mw) {
    long[] total = new long[1];
    total[0] = 0;

    dfs(mw, "in", new Combo(), total);
    return total[0];
  }

  public static void dfs(Map<String, Workflow> mw, String wid, Combo start, long[] total) {
    if (wid.equals("A")) {
      total[0] += start.count();
      return;
    } else if (wid.equals("R")) return;

    //continue dfs.
    Workflow w = mw.get(wid);
    //System.out.println("trying out workflow with id: " + wid);
    List<Condition> conditions = w.getConditions();
    for (int i = 0; i < conditions.size() + 1; i++) {
      Combo combo = new Combo(start);

      //create combo opposite of previous conditions so far
      for (int j = 0; j < i; j++) {
        combo.updateInverseBound(conditions.get(j));
      }

      if (i != conditions.size()) {
        combo.updateBound(conditions.get(i));
        dfs(mw, conditions.get(i).result, combo, total);
      } else {
        dfs(mw, w.getLastCondition(), combo, total);
      }
    }
  }
}

class Condition {
  public char c;
  public boolean isLesser;
  public long val;
  public String result;
  public static String NO = "NO";
  Condition(String s) {
    c = s.charAt(0);
    isLesser = s.charAt(1) == '<' ? true : false;
    int colon = 0;
    for (int i = 2; i < s.length(); i++) {
      if (s.charAt(i) == ':') {
        colon = i;
        break;
      }
    }
    val = Long.parseLong(s.substring(2, colon));
    result = s.substring(colon + 1);
  }

  Condition (Condition copy) {
    this.c = copy.c;
    this.isLesser = copy.isLesser;
    this.val = copy.val;
    this.result = copy.result;
  }

  public String evaluate(Rating r) {
    switch(c) {
      case 'x': 
      if (isLesser) 
        return r.x < val ? result : NO; 
      else 
        return r.x > val ? result : NO;
      case 'm':
      if (isLesser) 
        return r.m < val ? result : NO; 
      else 
        return r.m > val ? result : NO;
      case 'a':
      if (isLesser) 
        return r.a < val ? result : NO; 
      else 
        return r.a > val ? result : NO;
      case 's':
      if (isLesser) 
        return r.s < val ? result : NO; 
      else 
        return r.s > val ? result : NO;
      default:
      System.out.println("never happen again");
      return NO;
    }
  }
}

class Workflow {
  private String id;
  private List<Condition> functions;
  private String last;

  Workflow(String s) {
    int i = 0;
    for (i = 0; i < s.length(); i++) {
      if (s.charAt(i) == '{') break;
    }
    id = s.substring(0, i);

    String conditionsStr = s.substring(i+1, s.length() - 1);
    String[] conditions = conditionsStr.split(",");

    functions = new ArrayList<>();
    for (int j = 0; j < conditions.length - 1; j++) {
      Condition c = new Condition(conditions[j]);
      functions.add(c);
    }
    last = conditions[conditions.length-1];
  }

  public String evaluate(Rating r) {
    String ans = last;
    for (int i = 0; i < functions.size(); i++) {
      ans = functions.get(i).evaluate(r);
      if (!ans.equals(Condition.NO)) return ans;
    }
    return last;
  }

  public String getId() {
    return id;
  }

  public List<Condition> getConditions() {
    return functions;
  }

  public String getLastCondition() {
    return last;
  }
}

class Rating {
  public long x, m, a, s;
  Rating(String input) {
    String[] values = input.substring(1, input.length() - 1).split(",");
    long v = 0;
    for (int i = 0; i < values.length; i++) {
      v = Long.parseLong(values[i].substring(2));
      switch (values[i].charAt(0)) {
        case 'x': x = v;
        break;
        case 'm': m = v;
        break;
        case 'a': a = v;
        break;
        case 's': s = v;
        break;
        default:
        System.out.println("never happen");
      }
    }
  }

  public long sum() {
    return x + m + a + s;
  }
}


class Combo {
  public long[] x, m, a, s;
  Combo() {
    x = new long[2];
    m = new long[2];
    a = new long[2];
    s = new long[2];
    x[0] = m[0] = a[0] = s[0] = 0;
    x[1] = m[1] = a[1] = s[1] = 4001;
  }

  Combo(Combo copy) {
    this.x = Arrays.copyOf(copy.x, copy.x.length);
    this.m = Arrays.copyOf(copy.m, copy.m.length);
    this.a = Arrays.copyOf(copy.a, copy.a.length);
    this.s = Arrays.copyOf(copy.s, copy.s.length);
  }

  public void updateBound(Condition cond) {
      switch (cond.c) {
        case 'x':
          if (cond.isLesser) {
            x[1] = cond.val < x[1] ? cond.val : x[1];
          } else {
            x[0] = cond.val > x[0] ? cond.val : x[0];
          }
        break;
        case 'm':
          if (cond.isLesser) {
            m[1] = cond.val < m[1] ? cond.val : m[1];
          } else {
            m[0] = cond.val > m[0] ? cond.val : m[0];
          }
        break;
        case 'a':
          if (cond.isLesser) {
            a[1] = cond.val < a[1] ? cond.val : a[1];
          } else {
            a[0] = cond.val > a[0] ? cond.val : a[0];
          }
        break;
        case 's':
          if (cond.isLesser) {
            s[1] = cond.val < s[1] ? cond.val : s[1];
          } else {
            s[0] = cond.val > s[0] ? cond.val : s[0];
          }
        break;
        default:
        System.out.println("never never never happen");
      }
  }

  public void updateInverseBound(Condition cond) {
    Condition inverse = new Condition(cond);
    inverse.isLesser = !inverse.isLesser;
    if (cond.isLesser) inverse.val--; //x < 325 --> x > 324
    else inverse.val++; //x > 325 --> x < 326

    updateBound(inverse);
  }

  public long count() {
    if ((x[0] >= x[1]) || (m[0] >= m[1]) || (a[0] >= a[1]) || (s[0] >= s[1])) return 0;
    long result = x[1] - (x[0] + 1);
    result *= m[1] - (m[0] + 1);
    result *= a[1] - (a[0] + 1);
    result *= s[1] - (s[0] + 1);
    return result;
  }
}
