import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;
import java.lang.Comparable;
import java.util.regex.Pattern;

class Solution {
  public static void main(String[] args) {
    try {
      BufferedReader bufreader = new BufferedReader(new FileReader(args[0]));
      String line = bufreader.readLine();
      List<String> locations = new ArrayList<>();
      while(line != null) {
	locations.add(line);
        line = bufreader.readLine();
      }
      bufreader.close();

      Day5 day = new Day5(locations);
      long total = day.part1();
      System.out.println(total);
      total = day.part2();
      System.out.println(total);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}


public class Day5 {
    private int[][] deps;
    private List<int[]> ordering;
    private Map<Integer, List<Integer>> adjList;
    private Map<Integer, Set<Integer>> priorPageList;
    Day5(List<String> arr) {
      int n = 0;
      for (int i = 0; i < arr.size(); i++) {
        if (arr.get(i).length() == 0) {
          deps = new int[i][2];
          parse(arr, deps);
          n = i;
          break;
        }
      }
      ordering = new ArrayList<>();
      parse(arr, ordering, n);

      //adjList = new HashMap<>();
      //parseList(deps, adjList);
      //priorPageList = new HashMap<>();
      //createPriorList(adjList, priorPageList);
    }

    private void createPriorList(Map<Integer, List<Integer>> adjList, Map<Integer, Set<Integer>> priorPageList) {
      Set<Integer> nodes = adjList.keySet();
      for (Integer node : nodes) {
        dfsHelper(node, adjList, priorPageList);
      }
    }

    private void dfsHelper(Integer node, Map<Integer, List<Integer>> adjList, Map<Integer, Set<Integer>> priorPageList) {
      if (priorPageList.containsKey(node)) return;

      priorPageList.put(node, new HashSet<>());

      List<Integer> pages = adjList.get(node);
      for (Integer previousPage : pages) {
        dfsHelper(previousPage, adjList, priorPageList);
        priorPageList.get(node).addAll(priorPageList.get(previousPage));
        priorPageList.get(node).add(previousPage);
      }
    }
    
    private void parseList(List<int[]> deps, Map<Integer, List<Integer>> adjList) {
      int[] nextDep;
      for (int i = 0; i < deps.size(); i++) {
        nextDep = deps.get(i);
        if (!adjList.containsKey(nextDep[0])) {
          adjList.put(nextDep[0], new ArrayList());
        }
        if (!adjList.containsKey(nextDep[1])) {
          adjList.put(nextDep[1], new ArrayList());
        }

        adjList.get(nextDep[1]).add(nextDep[0]);
      }
    }

    private void parse(List<String> arr, int[][] deps) {
      int n = deps.length;
      String dependency;
      for (int i = 0; i < n; i++) {
        dependency = arr.get(i);
        String[] pages = dependency.split("\\|");
        deps[i][0] = Integer.parseInt(pages[0]);
        deps[i][1] = Integer.parseInt(pages[1]);
      }
    }

    private void printArr(int[] arr) {
      for (int i = 0; i < arr.length; i++) {
        System.out.print(String.format("%d,", arr[i]));
      }
      System.out.println();
    }

    private void parse(List<String> arr, List<int[]> ordering, int n) {
      for (int i = n+1; i < arr.size(); i++) {
        String[] nums = arr.get(i).split(",");
        int[] next = new int[nums.length];
        for (int j = 0; j < nums.length; j++) {
          next[j] = Integer.parseInt(nums[j]);
        }
        //printArr(next);
        ordering.add(next);
      }
    }

    public long part1() {
      int[] update;
      long total = 0;
      for (int i = 0; i < ordering.size(); i++) {
        update = ordering.get(i);
        createMetaData(update);
        if (isCorrect(update)) total += update[update.length/2];
      }
      return total;
    }

    private void createMetaData(int[] update) {
      Set<Integer> consideredPages = new HashSet<>();
      for (int i = 0; i < update.length; i++) consideredPages.add(update[i]);

      adjList = new HashMap<>();
      priorPageList = new HashMap<>();

      List<int[]> filteredDeps = new ArrayList<>();
      for (int i = 0; i < deps.length; i++) {
        if (consideredPages.contains(deps[i][0]) && consideredPages.contains(deps[i][1])) {
          int[] nextDep = new int[2];
          nextDep[0] = deps[i][0];
          nextDep[1] = deps[i][1];
          filteredDeps.add(nextDep);
        }
      }
      parseList(filteredDeps, adjList);
      createPriorList(adjList, priorPageList);
    }

    private boolean isCorrect(int[] update) {
      int n = update.length;
      Set<Integer> visited = new HashSet<>();
      for (int i = 0; i < n; i++) {
//        System.out.println(String.format("currently on %d, visited=%s", update[i], visited));
//        System.out.println(String.format("prior of %d, =%s", update[i], priorPageList.get(update[i])));
        if (visited.contains(update[i])) return false;
        visited.addAll(priorPageList.get(update[i]));
        visited.add(update[i]);
      }
      return true;
    }


    private int[] fix(int[] update) {
      int n = update.length;
      int[] fixed = new int[n];

      for (int i = 0; i < n; i++) {
        fixed[n - 1 - priorPageList.get(update[i]).size()] = update[i];
      }

      return fixed;
    }

    public long part2() {
      int[] update, fixed;
      long total = 0;
      for (int i = 0; i < ordering.size(); i++) {
        update = ordering.get(i);
        createMetaData(update);
        if (!isCorrect(update)) {
          fixed = fix(update);
          total += fixed[fixed.length/2];
        }
      }
      return total;
    }
}
