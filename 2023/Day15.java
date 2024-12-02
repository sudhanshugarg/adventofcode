import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

class Day15 {
  public static HashFunc h = new HashFunc();
  public static void main(String[] args) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(args[0]));
      String line = reader.readLine();
      reader.close();
      int total = 0;

      String[] actions = line.split(",");

      //part 1
      total = h.computeHashes(actions);
      System.out.println(total);

      //part 2
      int MAX = 256;
      List<Box> boxes = new ArrayList<>(MAX);
      for (int i = 0; i < MAX; i++) {
        Box b = new Box(i);
        boxes.add(b);
      }

      for (int i = 0; i < actions.length; i++) {
        act(actions[i], boxes);
      }

      //compute total
      total = 0;
      int curr = 0;
      Box b;
      for (int i = 0; i < MAX; i++) {
        b = boxes.get(i);
        curr = 0;
        for (int j = 0; j < b.lenses.size(); j++) {
          curr += (b.id + 1) * (j+1) * b.lenses.get(j).focal;
        }
        if (curr > 0) System.out.println("Box " + String.valueOf(i) + ", value: " + String.valueOf(curr));
        total += curr;
      }
      System.out.println(total);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public static void act(String action, List<Box> boxes) {
    int n = action.length();
    int boxId;
    //System.out.println("After " + action);
    if (action.charAt(n-1) == '-') {
      String label = action.substring(0, n-1);
      boxId = h.computeHash(label);
      boxes.get(boxId).removeIfPresent(label);
    } else {
      String label = action.substring(0, n-2);
      int f = action.charAt(n-1) - '0';
      Lens l = new Lens(label, f);
      boxId = h.computeHash(label);
      boxes.get(boxId).addOrReplace(l);
    }
  }
}

class HashFunc {
  public HashFunc() {}

  public int computeHashes(String[] arr) {
    int n = arr.length;

    int total = 0;
    int curr;
    for (int i = 0; i < n; i++) {
      curr = computeHash(arr[i]);
      total += curr;
      //System.out.println(arr[i] + ":" + String.valueOf(curr));
    }
    return total;
  }

  public int computeHash(String s) {
    int v = 0;
    for (int i = 0; i < s.length(); i++) {
      v += (int) s.charAt(i);
      v *= 17;
      v %= 256;
    }
    return v;
  }
}

class Box {
  public int id;
  public String ids;
  public LinkedList<Lens> lenses;
  public Box(int identity) {
    id = identity;
    ids = String.valueOf(id);
    lenses = new LinkedList<Lens>();
  }

  public void addOrReplace(Lens l) {
    boolean replaced = false;
    for (int i = 0; !replaced && i < lenses.size(); i++) {
      if (lenses.get(i).label.equals(l.label)) {
        lenses.get(i).focal = l.focal;
        replaced = true;
      }
    }

    if (!replaced) {
      lenses.add(l);
    }
  }

  public void removeIfPresent(String lensLabel) {
    int i = 0;
    for (i = 0; i < lenses.size(); i++) {
      if (lenses.get(i).label.equals(lensLabel)) {
        break;
      }
    }

    if (i != lenses.size()) {
      lenses.remove(i);
    }
  }

  @Override
  public String toString() {
    String s = "Box " + ids + " : " ;
    for (int i = 0; i < lenses.size(); i++) {
      s += lenses.get(i).toString() + " ";
    }
    return s;
  }
}


class Lens {
  public String label;
  public int focal;
  public Lens(String s, int v) {
    label = s;
    focal = v;
  }

  @Override
  public String toString() {
    return "[" + label + " " + String.valueOf(focal) + "]";
  }
}
