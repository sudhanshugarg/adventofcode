import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class Day6 {
  public static void main(String[] args) {
    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(args[0]));
      String time = reader.readLine();
      String distance = reader.readLine();

      String[] timesStr = time.split("\\s+");
      String[] distancesStr = distance.split("\\s+");

      long[] times = new long[10];
      long[] distances = new long[10];

      for (int i = 1; i < timesStr.length; i++) {
        times[i-1] = Long.parseLong(timesStr[i]);
        distances[i-1] = Long.parseLong(distancesStr[i]);
      }
      int n = timesStr.length - 1;

      long result = 1, ways;
      BoatRace race = new BoatRace();
      for (int i = 0; i < n; i++) {
        ways = race.countWays(times[i], distances[i]);
        result *= ways;
      }
      System.out.println(result);
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

class BoatRace {
  BoatRace() {}
  public long countWays(long t, long d) {
    long ways = 0;
    for (long i = 1; i < t; i++) {
      long d2 = i * (t - i);
      if (d2 > d) ways++;
    }
    return ways;
  }
}
