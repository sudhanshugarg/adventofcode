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
import java.util.PriorityQueue;

class Solution {
  public static void main(String[] args) {
    try {
      BufferedReader bufreader = new BufferedReader(new FileReader(args[0]));
      String line = bufreader.readLine();
      bufreader.close();

      Day9 day = new Day9(line);
      long total = 0;
      total = day.part1();
      System.out.println(total);
      total = day.part2();
      System.out.println(total);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}


public class Day9 {
  private String files;
  private String init;
  Day9(String f) {
    files = f;
  }

  public long part1() {

    //createInitial();
    //System.out.println(init);
    //System.out.println();

    long total = 0;
    String soFar = "";

    //keep two indices
    int ls, rs; //lstring, rstring
    long lexp; //lexpanded, rexpanded

    ls = 0;
    lexp = 0;
    rs = files.length() - 1;
    if (files.length() % 2 == 0) rs--;

    long blocks = 0;
    long emptyBlocksOnTheLeft = 0;
    long blocksOnTheRight = 0;


    long rightId = files.length() / 2;
    if (files.length() % 2 == 0) rightId--;
    long leftId = 0;
    blocksOnTheRight = files.charAt(rs) - '0';

    int loop = 0;
    String tabs = "";
    while(rs > ls) {
      loop++;
      //each time you start, you start at ls
      blocks = files.charAt(ls) - '0';
      //currently you are at an immovable index
      tabs = myRepeat(-2, 1);
      total += calculateChecksum(leftId, lexp, blocks, tabs);
      //System.out.println(String.format("%s immovable: total=%3d", tabs, total));
//      soFar += myRepeat(leftId, blocks);
      //System.out.println(soFar);

      ls++;
      lexp += blocks;

      //move items from right to left
      emptyBlocksOnTheLeft = files.charAt(ls) - '0';
      //System.out.println(String.format("%s emptyBlocksOnTheLeft=%3d", tabs, emptyBlocksOnTheLeft));
      if (emptyBlocksOnTheLeft == 0) {
        ls++;
        leftId++;
        continue;
      }

      while((rs > ls) && (emptyBlocksOnTheLeft > 0)) {
        tabs = myRepeat(-2, 2);
        if (emptyBlocksOnTheLeft < blocksOnTheRight) {
          total += calculateChecksum(rightId, lexp, emptyBlocksOnTheLeft, tabs);
//          soFar += myRepeat(rightId, emptyBlocksOnTheLeft);
          //System.out.println(soFar);
          ////System.out.println(String.format("%s if loop: total=%3d", tabs, total));
          ls++;
          lexp += emptyBlocksOnTheLeft;
          blocksOnTheRight -= emptyBlocksOnTheLeft;
          emptyBlocksOnTheLeft = 0;
        } else {
          //blocksOnTheRight all got over
          total += calculateChecksum(rightId, lexp, blocksOnTheRight, tabs);
//          soFar += myRepeat(rightId, blocksOnTheRight);
          //System.out.println(soFar);
          ////System.out.println(String.format("%s else loop: total=%3d", tabs, total));
          lexp += blocksOnTheRight;
          emptyBlocksOnTheLeft -= blocksOnTheRight;

          if (emptyBlocksOnTheLeft == 0) ls++;
          rs -= 2;

          if (ls > rs) blocksOnTheRight = 0;
          else {
            blocksOnTheRight = files.charAt(rs) - '0';
            rightId--;
          }
        }
      }
      tabs = myRepeat(-2, 1);
      //System.out.println(String.format("%s end of inner while loop %3d: ls=%3d, rs=%3d, lexp=%3d, total=%4d, blocksOnTheRight=%3d", tabs, loop, ls, rs, lexp, total, blocksOnTheRight));
      leftId++;
    }
    //System.out.println(String.format("end outer while loop %3d: ls=%3d, rs=%3d, lexp=%3d, total=%4d, blocksOnTheRight=%3d", loop, ls, rs, lexp, total, blocksOnTheRight));

    if (blocksOnTheRight > 0) {
      total += calculateChecksum(rightId, lexp, blocksOnTheRight, "");
//      soFar += myRepeat(rightId, blocksOnTheRight);
      //System.out.println(soFar);
    }
    //System.out.println(String.format("verification: %4d", checkAgain(soFar)));

    return total;
  }

  private long calculateChecksum(long id, long left, long blocks, String tabs) {
    //System.out.println(String.format("%s id: %4d, leftExp: %4d, blocksBeingAdded: %4d", tabs, id, left, blocks));
    return id * (- (left * (left-1) / 2) + ((left + blocks) * (left + blocks - 1) / 2));
  }

  private String myRepeat(long id, long count) {
    String ans = "";
    String ids = "";
    if (id >= 0) ids = String.valueOf(id);
    if (id == -1) ids = ".";
    else if (id == -2) ids = "\t";

    for (long i = 0; i < count; i++) {
      ans += ids + ",";
    }
    if (id < -1) ans += "::";
    return ans;
  }


  private void createInitial() {
    long id = 0;
    init = "";
    for (int i = 0; i < files.length(); i++) {
      if (i % 2 == 0) {
        init += myRepeat(id, files.charAt(i) - '0');
        id++;
      } else init += myRepeat(-1, files.charAt(i) - '0');
    }
  }

  private long checkAgain(String s) {
    String[] sp = s.split(",");
    long ans = 0;
    for (int i = 0; i < sp.length; i++) {
      ans += i * Long.parseLong(sp[i]);
    }
    return ans;
  }

  public long part2() {
    long total = 0;
    long position = 0;
    List<FragFile> allFiles = new ArrayList<>();
    Map<Long, PriorityQueue<EmptyRegion>> emptyAreas = new HashMap<>();
    //2333133121414131402
    long count;
    long emptyRegionCount = 0;
    long emptyRegionStartPosition = 0;
    for (int i = 0; i < files.length(); i += 2) {
      count = files.charAt(i) - '0';

      if (count == 0) {
        //no file needs to be added
        if (i < (files.length() - 1)) { //as long as its not the last file
          emptyRegionCount += files.charAt(i + 1) - '0'; //emptyRegion gets larger
        }
      } else {
        //two things
        //update last emptyRegion seen
        EmptyRegion e = new EmptyRegion(emptyRegionStartPosition, emptyRegionCount);
        if (!emptyAreas.containsKey(e.count)) {
          emptyAreas.put(e.count, new PriorityQueue<>());
        }
        emptyAreas.get(e.count).offer(e);

        //create new file
        FragFile f = new FragFile(i/2, position, count);
        position += files.charAt(i) - '0';
        if (i < (files.length() - 1)) { //as long as its not the last file
          emptyRegionCount = files.charAt(i + 1) - '0'; //emptyRegionCount gets reset
          emptyRegionStartPosition = position;
          position += emptyRegionCount;
        } else {
          emptyRegionCount = 0;
        }
        allFiles.add(f);
      }
    }

    long checkingId = -1;
    long whichSize = 0;
    EmptyRegion e;
    for (int i = allFiles.size() - 1; i >= 0; i--) {
      FragFile f = allFiles.get(i);
      whichSize = -1;
      for (long sz = f.count; sz <= 9; sz++) {
        if (!emptyAreas.containsKey(sz) || emptyAreas.get(sz).isEmpty()) continue;
        e = emptyAreas.get(sz).peek();
        if ((whichSize == -1) || (emptyAreas.get(whichSize).peek().startPosition > e.startPosition)) {
          whichSize = sz;
        }
      }

      if (whichSize != -1) {
        e = emptyAreas.get(whichSize).peek();
        //check if there is an available position
//        if (f.id == checkingId) {
//          System.out.println(sz);
//        }
//        if (f.id == checkingId) {
//          System.out.println(String.format("first empty region, %s. total empty region count: %d", e, emptyAreas.get(sz).size()));
//          System.out.println(String.format("no. of empty regions of sz 1: %d", emptyAreas.get(1L).size()));
//        }

        if (e.startPosition < f.startPosition) {
          emptyAreas.get(whichSize).poll();

          f.startPosition = e.startPosition;
          if (e.count > f.count) {
            long newEmptyRegionCount = e.count - f.count;
            EmptyRegion newEmptyRegion = new EmptyRegion(e.startPosition + f.count, newEmptyRegionCount);
            if (!emptyAreas.containsKey(newEmptyRegionCount)) {
              emptyAreas.put(newEmptyRegionCount, new PriorityQueue<>());
            }

            emptyAreas.get(newEmptyRegionCount).offer(newEmptyRegion);
          }
        }
      }

//        if (f.id == checkingId) {
//          System.out.println(String.format("after: no. of empty regions of sz 1: %d", emptyAreas.get(1L).size()));
//        }

      total += calculateChecksum(f.id, f.startPosition, f.count, "");
    }
    //Collections.sort(allFiles);
    //System.out.println(allFiles);

    return total;
  }
}

class EmptyRegion implements Comparable<EmptyRegion> {
  public long startPosition;
  public long count;

  EmptyRegion(long a, long b) {
    startPosition = a;
    count = b;
  }

  @Override
  public int compareTo(EmptyRegion other) {
    if (this.startPosition < other.startPosition) return -1;
    else if (this.startPosition == other.startPosition) return 0;
    return 1;
  }

  @Override
  public String toString() {
    return String.format("Empty: (%d, %d)", this.startPosition, this.count);
  }
}

class FragFile implements Comparable<FragFile> {
  public long id;
  public long startPosition;
  public long count;

  FragFile(long a, long b, long c) {
    id = a;
    startPosition = b;
    count = c;
  }

  @Override
  public int compareTo(FragFile other) {
    if (this.startPosition < other.startPosition) return -1;
    else if (this.startPosition == other.startPosition) return 0;
    return 1;
  }

  @Override
  public String toString() {
    return String.format("File: (%2d, %2d, %d)", this.id, this.startPosition, this.count);
  }
}
