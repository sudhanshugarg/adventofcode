import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

class Day15 {
  public static void main(String[] args) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(args[0]));
      String line = reader.readLine();
      reader.close();
      int total = 0;

      HashFunc h = new HashFunc();
      total = h.computeHashes(line);
      System.out.println(total);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

class HashFunc {
  public HashFunc() {}

  public int computeHashes(String s) {
    String[] arr = s.split(",");
    int n = arr.length;

    int total = 0;
    int curr;
    for (int i = 0; i < n; i++) {
      curr = computeHash(arr[i]);
      total += curr;
      System.out.println(arr[i] + ":" + String.valueOf(curr));
    }
    return total;
  }

  private int computeHash(String s) {
    int v = 0;
    for (int i = 0; i < s.length(); i++) {
      v += (int) s.charAt(i);
      v *= 17;
      v %= 256;
    }
    return v;
  }
}
