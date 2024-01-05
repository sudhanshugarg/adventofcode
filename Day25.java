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
import javafx.util.Pair;

//javac -classpath ".:/Users/sugarg/coding/adventofcode/apache-commons/commons-math-4.0-beta1/commons-math4-legacy-4.0-beta1.jar:" Day24.java
class Day25 {

  public static void main(String[] args) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(args[0]));
      String line = reader.readLine();

      List<String> input = new ArrayList<>();
      while (line != null) {
        input.add(line);
        line = reader.readLine();
      }
      reader.close();

      List<Pair<Integer, Integer>> allEdges = new ArrayList<>();
      boolean[][] g = parseGraph(input, allEdges);

      long p1 = part1(g, allEdges);
      System.out.println("part 1: " + String.valueOf(p1));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static boolean[][] parseGraph(List<String> input, List<Pair<Integer, Integer>> allEdges) {
    int n;
    Map<String, Integer> m = new HashMap<>();
    Map<String, List<String>> inputM = new HashMap<>();
    int next = 0;
    int curr = -1;
    int other = -1;
    int e = 0;

    for (int i = 0; i < input.size(); i++) {
      String node = input.get(i).substring(0, 3);
      curr = m.getOrDefault(node, next);
      if (curr == next) {
        m.put(node, curr);
        inputM.put(node, new ArrayList<>());
        next++;
      }

      String[] others = input.get(i).substring(5).split("\\s+");
      e += others.length;
      for (int j = 0; j < others.length; j++) {
        other = m.getOrDefault(others[j], next);
        if (other == next) {
          m.put(others[j], other);
          inputM.put(others[j], new ArrayList<>());
          next++;
        }

        inputM.get(node).add(others[j]);
      }
    }

    n = m.size();
    boolean[][] g = new boolean[n][n];

    for (String s : m.keySet()) {
      List<String> edges = inputM.get(s);
      for (int i = 0; i < edges.size(); i++) {
        g[m.get(s)][m.get(edges.get(i))] = true;
        g[m.get(edges.get(i))][m.get(s)] = true;
        Pair<Integer, Integer> p = new Pair<>(m.get(s), m.get(edges.get(i)));
        allEdges.add(p);
      }
    }
    System.out.println(n);
    System.out.println(e);
    System.out.println(m);
    System.out.println(inputM);
    System.out.println(allEdges);
    return g;
  }

  public static void setEdges(boolean[][] g, Pair<Integer, Integer> edge, boolean val) {
    g[edge.getKey()][edge.getValue()] = g[edge.getValue()][edge.getKey()] = val;
  }

  public static long part1(boolean[][] g, List<Pair<Integer, Integer>> allEdges) {
    int n = g.length;
    int e = allEdges.size();
    long[] count = new long[2];

    boolean[] visited = new boolean[n];
    for (int i = 0; i < e; i++)
    for (int j = i+1; j < e; j++)
    for (int k = j+1; k < e; k++) {
      for (int m = 0; m < n; m++) visited[m] = false;

      setEdges(g, allEdges.get(i), false);
      setEdges(g, allEdges.get(j), false);
      setEdges(g, allEdges.get(k), false);

      if (dfs(g, visited, i, j, k, count)) {
        System.out.println(allEdges.get(i));
        System.out.println(allEdges.get(j));
        System.out.println(allEdges.get(k));
        System.out.println(String.valueOf(i) + "," + String.valueOf(j) + "," + String.valueOf(k));
        System.out.println(count[0]);
        System.out.println(count[1]);
        return count[0] * count[1];
      }
      setEdges(g, allEdges.get(i), true);
      setEdges(g, allEdges.get(j), true);
      setEdges(g, allEdges.get(k), true);
    }
    return -1L;
  }

  public static int findStart(boolean[] visited) {
    for (int i = 0; i < visited.length; i++) {
      if (!visited[i]) {
        return i;
      }
    }
    return -1;
  }

  public static boolean dfs(boolean[][] g, boolean[] visited, int a, int b, int c, long[] count) {
    int n = g.length;
    count[0] = count[1] = 0;

    int start = 0;
    dfsHelper(g, visited, start);
    for (int i = 0; i < n; i++) if (visited[i]) count[0]++;

    start = findStart(visited);
    if (start == -1) return false;

    dfsHelper(g, visited, start);
    for (int i = 0; i < n; i++) if (visited[i]) count[1]++;

    if (count[1] != n) return false;
    count[1] -= count[0];
    return true;
  }

  public static void dfsHelper(boolean[][] g, boolean[] visited, int curr) {
    int n = g.length;
    if(visited[curr]) return;
    visited[curr] = true;
    for (int i = 0; i < n; i++) {
      if (g[curr][i]) dfsHelper(g, visited, i);
    }
  }
}
