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

class Day20 {
  public static Broadcast parseInput(List<String> input, Map<String, Flipflop> mf, Map<String, Conjunction> mc) {
    Broadcast b = new Broadcast();
    for (int i = 0; i < input.size(); i++) {
      String line = input.get(i);
        switch(line.charAt(0)) {
          case 'b':
            b = new Broadcast(line);
          break;
          case '%':
            Flipflop ff = new Flipflop(line);
            mf.put(ff.getId(), ff);
          break;
          case '&':
            Conjunction c = new Conjunction(line);
            mc.put(c.getId(), c);
          break;
          default:
          System.out.println("should never happen");
        } 
    }
    return b;
  }

  public static void main(String[] args) {
    try {
      List<String> input = new ArrayList<>();

      BufferedReader reader = new BufferedReader(new FileReader(args[0]));
      String line = reader.readLine();
      while (line != null) {
        input.add(line);
        line = reader.readLine();
      }
      reader.close();


      {
        Map<String, Flipflop> mf = new HashMap<>();
        Map<String, Conjunction> mc = new HashMap<>();
        Broadcast b = parseInput(input, mf, mc);
        updateConjunctionInputs(mf, mc);

        long total = part1(b, mf, mc, 1000);
        System.out.println("part 1: " + String.valueOf(total));
      }

      {
        Map<String, Flipflop> mf = new HashMap<>();
        Map<String, Conjunction> mc = new HashMap<>();
        Broadcast b = parseInput(input, mf, mc);
        updateConjunctionInputs(mf, mc);

        long total = part2(b, mf, mc, "xm");
        System.out.println("part 2: " + String.valueOf(total));
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static List<String> toposort(Broadcast b, Map<String, Flipflop> mf, Map<String, Conjunction> mc) {
    Map<String, Boolean> visited = new HashMap<>();
    List<String> sorted = new ArrayList<>();

    for (int i = 0; i < b.getModules().size(); i++) {
      topodfs(b.getModules().get(i), mf, mc, visited, sorted);
      if (!visited.containsKey(b.getModules().get(i)))
        sorted.add(b.getModules().get(i));
    }
    Collections.reverse(sorted);
    return sorted;
  }

  public static void topodfs(String currModule, Map<String, Flipflop> mf, Map<String, Conjunction> mc, Map<String, Boolean> v, List<String> sorted) {
    if (v.containsKey(currModule)) return;

    List<String> modules = new ArrayList<>();
    if (mf.containsKey(currModule)) {
      modules = mf.get(currModule).getModules();
    } else if (mc.containsKey(currModule)) {
      modules = mc.get(currModule).getModules();
    }

    v.put(currModule, true);
    for (int i = 0; i < modules.size(); i++) {
      topodfs(modules.get(i), mf, mc, v, sorted);
    }
    sorted.add(currModule);
  }

  public static void printInputs(Broadcast b, Map<String, Flipflop> mf, Map<String, Conjunction> mc) {
    System.out.println(b);
    System.out.println(mf);
    System.out.println(mc);
  }

  public static void updateConjunctionInputs(Map<String, Flipflop> mf, Map<String, Conjunction> mc) {
    for (Map.Entry<String, Flipflop> entry : mf.entrySet()) {
      for (String module : entry.getValue().getModules()) {
        if (mc.containsKey(module)) {
          mc.get(module).insertInput(entry.getKey());
        }
      }
    }

    for (Map.Entry<String, Conjunction> entry : mc.entrySet()) {
      for (String module : entry.getValue().getModules()) {
        if (mc.containsKey(module)) {
          mc.get(module).insertInput(entry.getKey());
        }
      }
    }
  }

  public static boolean bfs2(Broadcast b, Map<String, Flipflop> mf, Map<String, Conjunction> mc, String tofind) {
    long lows = 1, highs = 0;
    Queue<Ray> q = new ArrayDeque<>();

    for (int i = 0; i < b.getModules().size(); i++) {
      Ray r = new Ray("broadcaster", b.getModules().get(i), Pulse.LOW);
      q.add(r);
    }

    while(!q.isEmpty()) {
      Ray r = q.poll();
      if (r.pulse == Pulse.HIGH && tofind.equals(r.to)) {
        System.out.println(r);
        return true;
      }
      if (r.pulse == Pulse.LOW) lows++;
      else highs++;

      if (mf.containsKey(r.to)) {
        if (r.pulse == Pulse.HIGH) continue;

        Flipflop ff = mf.get(r.to);
        Pulse nextp = ff.inputLowPulse();
        for (int i = 0; i < ff.getModules().size(); i++) {
          Ray nextr = new Ray(r.to, ff.getModules().get(i), nextp);
          q.add(nextr);
        }
      } else if (mc.containsKey(r.to)) {
        Conjunction c = mc.get(r.to);
        Pulse nextp = c.inputPulse(r.from, r.pulse);
        for (int i = 0; i < c.getModules().size(); i++) {
          Ray nextr = new Ray(r.to, c.getModules().get(i), nextp);
          q.add(nextr);
        }
      }
    }

    return false;
  }

  public static void bfs(Broadcast b, Map<String, Flipflop> mf, Map<String, Conjunction> mc, State st) {
    long lows = 1, highs = 0;
    Queue<Ray> q = new ArrayDeque<>();

    for (int i = 0; i < b.getModules().size(); i++) {
      Ray r = new Ray("broadcaster", b.getModules().get(i), Pulse.LOW);
      q.add(r);
    }

    while(!q.isEmpty()) {
      Ray r = q.poll();
      if (r.pulse == Pulse.LOW) lows++;
      else highs++;

      if (mf.containsKey(r.to)) {
        if (r.pulse == Pulse.HIGH) continue;

        Flipflop ff = mf.get(r.to);
        Pulse nextp = ff.inputLowPulse();
        for (int i = 0; i < ff.getModules().size(); i++) {
          Ray nextr = new Ray(r.to, ff.getModules().get(i), nextp);
          q.add(nextr);
        }
      } else if (mc.containsKey(r.to)) {
        Conjunction c = mc.get(r.to);
        Pulse nextp = c.inputPulse(r.from, r.pulse);
        for (int i = 0; i < c.getModules().size(); i++) {
          Ray nextr = new Ray(r.to, c.getModules().get(i), nextp);
          q.add(nextr);
        }
      }
    }

    st.lows = lows;
    st.highs = highs;
  }

  public static long part1(Broadcast b, Map<String, Flipflop> mf, Map<String, Conjunction> mc, long cycles) {
    Map<String, Flipflop> mff = new HashMap<>();
    mff.putAll(mf);
    Map<String, Conjunction> mcc = new HashMap<>();
    mcc.putAll(mc);

    Map<String, State> ms = new HashMap<>();
    String pattern = "";
    State st = new State();
    long end = -1;
    List<State> states = new ArrayList<>();
    for (long i = 1; i <= cycles; i++) {
      //before starting cycle i
      st = new State(i, mff, mcc);
      pattern = st.getPattern();

      if (ms.containsKey(pattern)) {
        //was currently going to start cycle i;
        end = i;
        break;
      } else {
        //updating the results of cycle i into State st.
        //run cycle i
        bfs(b, mff, mcc, st);
        ms.put(pattern, st);
        states.add(st);
      }
    }

    long lows = 0, highs = 0;
    if (end == -1) {
      for (Map.Entry<String, State> entry : ms.entrySet()) {
        lows += entry.getValue().lows;
        highs += entry.getValue().highs;
      }
    } else {
      State earlierState = ms.get(pattern);
      long earlierId = earlierState.getId();
      long cycleLength = end - earlierId;
      System.out.println("Start repeat state: " + String.valueOf(earlierId));
      System.out.println("End repeat state: " + String.valueOf(end));
      System.out.println("cycle length: " + String.valueOf(cycleLength));

      long mult = (cycles - (earlierId-1)) / cycleLength;
      long remainder = (cycles - (earlierId-1)) % cycleLength;

      for (long i = 1; i < earlierId; i++) {
        lows += states.get((int)i-1).lows;
        highs += states.get((int)i-1).lows;
      }
      long cycleLows = 0, cycleHighs = 0;
      for (long i = earlierId; i < end; i++) {
        cycleLows += states.get((int)i-1).lows;
        cycleHighs += states.get((int)i-1).highs;
      }
      lows += cycleLows * mult;
      highs += cycleHighs * mult;

      for (long i = 0; i < remainder; i++) {
        lows += states.get((int)earlierId + (int)i-1).lows;
        highs += states.get((int)earlierId + (int)i-1).highs;
      }
    }
    return lows * highs;
  }

  public static long part2(Broadcast b, Map<String, Flipflop> mf, Map<String, Conjunction> mc, String tofind) {
    Map<String, Flipflop> mff = new HashMap<>();
    mff.putAll(mf);
    Map<String, Conjunction> mcc = new HashMap<>();
    mcc.putAll(mc);

    boolean flag = false;
    for (long i = 1; ; i++) {
      flag = bfs2(b, mff, mcc, tofind);
      if (flag) {
        System.out.println("first found after cycle: " + String.valueOf(i));
        return i;
      }
    }
  }

  public static List<Long> diffs(List<Long> in) {
    List<Long> res = new ArrayList<>();
    for (int i = 1; i < in.size(); i++) res.add(in.get(i) - in.get(i-1));
    return res;
  }
}

enum Pulse {
  LOW,
  HIGH;
}

class Broadcast {
  private List<String> modules;
  Broadcast() {}

  Broadcast(String line) {
    String[] arr = line.split(" -> ");
    String[] mods = arr[1].split(",");
    modules = new ArrayList<>();
    for (int i = 0; i < mods.length; i++) {
      modules.add(mods[i].trim());
    }
  }

  public List<String> getModules() {
    return modules;
  }

  @Override
  public String toString() {
    return "broadcast:" + modules.toString();
  }
}

class Flipflop {
  private boolean isOn;
  private String id;
  private List<String> modules;

  Flipflop(String line) {
    isOn = false;

    String[] arr = line.split(" -> ");
    id = arr[0].substring(1);
    String[] mods = arr[1].split(",");
    modules = new ArrayList<>();
    for (int i = 0; i < mods.length; i++) {
      modules.add(mods[i].trim());
    }
  }

  public String getId() {
    return id;
  }

  public List<String> getModules() {
    return modules;
  }

  public Pulse inputLowPulse() {
    if (isOn) {
      isOn = false;
      return Pulse.LOW;
    } else {
      isOn = true;
      return Pulse.HIGH;
    }
  }

  @Override
  public String toString() {
    String s = isOn ? ":1," : ":0,";
    return "%" + id + s + modules.toString();
  }
}

class Conjunction {
  private String id;
  private List<String> outputModules;
  private Map<String, Pulse> memory;

  Conjunction(String line) {
    String[] arr = line.split(" -> ");
    id = arr[0].substring(1);
    String[] mods = arr[1].split(",");
    outputModules = new ArrayList<>();
    for (int i = 0; i < mods.length; i++) {
      String s = mods[i].trim();
      outputModules.add(s);
    }

    memory = new HashMap<>();
  }

  public String getId() {
    return id;
  }

  public List<String> getModules() {
    return outputModules;
  }

  public Pulse inputPulse(String from, Pulse p) {
    memory.put(from, p);
    for (Map.Entry<String, Pulse> entry : memory.entrySet()) {
      if (entry.getValue().equals(Pulse.LOW)) return Pulse.HIGH;
    }
    return Pulse.LOW;
  }

  public void insertInput(String input) {
    memory.put(input, Pulse.LOW);
  }

  @Override
  public String toString() {
    return "&" + id + memory + outputModules;
  }
}

class State {
  private Map<String, Flipflop> mf;
  private Map<String, Conjunction> mc;
  private long id;

  public long lows;
  public long highs;

  State() {}

  State(long i, Map<String, Flipflop> mfi, Map<String, Conjunction> mci) {
    mf = new HashMap<>();
    mf.putAll(mfi);
    mc = new HashMap<>();
    mc.putAll(mci);

    lows = highs = 0;
    id = i;
  }

  public long getId() {
    return id;
  }

  public String getPattern() {
    return mf.toString() + "," + mc.toString();
  }

  @Override
  public String toString() {
    //get the state of each module, along with their id, and append it in order.
    return String.valueOf(id) + ":" + this.getPattern();
  }
}

class Ray {
  public String from;
  public String to;
  public Pulse pulse;

  Ray(String f, String t, Pulse p) {
    from = f;
    to = t;
    pulse = p;
  }

  @Override
  public String toString() {
    return pulse.toString() + ":" + from + "-->" + to;
  }
}
