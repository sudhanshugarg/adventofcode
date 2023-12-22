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

class DayTest {
  public static void main(String[] args) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(args[0]));
      String line = reader.readLine();
      reader.close();

      System.out.println(calculateOnes(args[1], args[2], line));
      
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static int calculateOnes(String l, String r, String line) {
    int lval = Integer.parseInt(l);
    int rval = Integer.parseInt(r);
    int ans = 0;
    for (int i = lval; i <= rval; i++) {
      if (line.charAt(i) == '1') ans++;
    }
    return ans;
  }

}
