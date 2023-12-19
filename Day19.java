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
      total = part1(workflows, ratings);
      System.out.println("part 1: " + String.valueOf(total));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static long part1(List<String> workflows, List<String> ratings) {
    Map<String, Workflow> mw = new HashMap<>();
    for (int i = 0; i < workflows.size(); i++) {
      Workflow w = new Workflow(workflows.get(i));
      mw.put(w.getId(), w);
    }

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
