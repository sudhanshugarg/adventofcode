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
    Day9(String f) {
        files = f;
    }

    public long part1() {

      long total = 0;

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
      while(rs > ls) {
        loop++;
        //each time you start, you start at ls
        blocks = files.charAt(ls) - '0';
        //currently you are at an immovable index
        total += calculateChecksum(leftId, lexp, blocks);
        //System.out.println(String.format("start of while: total=%3d", total));
        
        ls++;
        lexp += blocks;

        //move items from right to left
        emptyBlocksOnTheLeft = files.charAt(ls) - '0';
        //System.out.println(String.format("emptyBlocksOnTheLeft=%3d", emptyBlocksOnTheLeft));

        while((rs > ls) && (emptyBlocksOnTheLeft > 0)) {
          if (emptyBlocksOnTheLeft < blocksOnTheRight) {
            total += calculateChecksum(rightId, lexp, emptyBlocksOnTheLeft);
            //System.out.println(String.format("if loop: total=%3d", total));
            ls++;
            lexp += emptyBlocksOnTheLeft;
            blocksOnTheRight -= emptyBlocksOnTheLeft;
            emptyBlocksOnTheLeft = 0;
          } else {
            //blocksOnTheRight all got over
            total += calculateChecksum(rightId, lexp, blocksOnTheRight);
            //System.out.println(String.format("else loop: total=%3d", total));
            lexp += blocksOnTheRight;
            emptyBlocksOnTheLeft -= blocksOnTheRight;

            if (emptyBlocksOnTheLeft == 0) ls++;
            rs -= 2;
            
            if (ls >= rs) blocksOnTheRight = 0;
            else {
              blocksOnTheRight = files.charAt(rs) - '0';
              rightId--;
            }
          }
        }
        //System.out.println(String.format("loop %3d: ls=%3d, rs=%3d, lexp=%3d, total=%4d, blocksOnTheRight=%3d", loop, ls, rs, lexp, total, blocksOnTheRight));
        leftId++;
      }

      if (blocksOnTheRight > 0) {
        total += calculateChecksum(rightId, lexp, blocksOnTheRight);
      }

      return total;
    }

    private long calculateChecksum(long id, long left, long blocks) {
      //System.out.println(String.format("left: %d, blocks: %d, id: %d", left, blocks, id));
      return id * (- (left * (left-1) / 2) + ((left + blocks) * (left + blocks - 1) / 2));
    }

    public long part2() {
      return 0;
    }
}
