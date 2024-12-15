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
      String stones = bufreader.readLine();
      bufreader.close();

      Day11 day = new Day11(stones, 75);
      long total = day.part1();
      System.out.println(total);
      total = day.part2();
      System.out.println(total);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}


public class Day11 {
  private List<Long> stones;
  private Map<Long, Long>[] maps;

  private int maxBlinks;
  Day11(String s, int blinks) {
    stones = new ArrayList<>();
    String[] sp = s.split("\\s+");
    for (int i = 0; i < sp.length; i++) {
      stones.add(Long.parseLong(sp[i]));
    }
    maxBlinks = blinks;
    maps = new Map[maxBlinks];
    for (int i = 0; i < maxBlinks; i++) {
      maps[i] = new HashMap<>();
    }

    //maps[24] is the map that contains the number of stones it becomes, after the stone splits 25 times
    //maps[0] is the map that contains the number of stones it becomes, after the stone splits 1 times
    maps[0].put(0L, 1L);
  }

  public long common(int blinks) {
    long total = 0;
    long stone;
    for (int i = 0; i < stones.size(); i++) {
      stone = stones.get(i);
      total += runDp(stone, blinks);
      //total += maps[blinks-1].get(stone);
    }
    return total;
  }

  private long runDp(long stone, int blinks) {
    if (blinks <= 0) return 1;
    if (maps[blinks-1].containsKey(stone))
      return maps[blinks-1].get(stone);

    long stoneSplits = 0;

    //execute 1 blink
    //using the conditions
    long next;
    if (stone == 0) {
      stoneSplits += runDp(1L, blinks - 1);
    } else {
      String numStr = String.valueOf(stone);
      if (numStr.length() % 2 == 1) {
        next = stone * 2024;
        stoneSplits += runDp(next, blinks - 1);
      } else {
        int len = numStr.length();
        long leftStone = Long.parseLong(numStr.substring(0,len/2));
        long rightStone = Long.parseLong(numStr.substring(len/2));
        stoneSplits += runDp(leftStone, blinks - 1);
        stoneSplits += runDp(rightStone, blinks - 1);
      }
    }

    maps[blinks-1].put(stone, stoneSplits);
    return stoneSplits;
  }

  public long part1() {
    return common(25);
  }

  public long part2() {
    return common(75);
  }
}
