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
      bufreader.close();

      Day9 day = new Day9(line);
      long total = day.part1();
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

      createInitial();
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
        soFar += myRepeat(leftId, blocks);
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
            soFar += myRepeat(rightId, emptyBlocksOnTheLeft);
            //System.out.println(soFar);
            ////System.out.println(String.format("%s if loop: total=%3d", tabs, total));
            ls++;
            lexp += emptyBlocksOnTheLeft;
            blocksOnTheRight -= emptyBlocksOnTheLeft;
            emptyBlocksOnTheLeft = 0;
          } else {
            //blocksOnTheRight all got over
            total += calculateChecksum(rightId, lexp, blocksOnTheRight, tabs);
            soFar += myRepeat(rightId, blocksOnTheRight);
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
        soFar += myRepeat(rightId, blocksOnTheRight);
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
      return 0;
    }
}
