import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

class Day11 {
  public static long MULT_FACTOR = 1000000;
  public static void main(String[] args) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(args[0]));
      String line = reader.readLine();
      List<String> image = new ArrayList<>();
      while(line != null) {
        image.add(line);
        line = reader.readLine();
      }
      long totalPairDistances = galaxyDistances(image);
      System.out.println(totalPairDistances);
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static long galaxyDistances(List<String> image) {
    //find the x and y coordinates of each galaxy.
    //find the indices of empty rows
    //find the indices of empty cols
    //update the x and y coordinates
    //calculate distances.

    List<Galaxy> galaxies = new ArrayList<>();
    List<Long> emptyRows = new ArrayList<>();
    List<Long> emptyCols = new ArrayList<>();
    boolean flag = false;
    for (int i = 0; i < image.size(); i++) {
      flag = false;
      for (int j = 0; j < image.get(i).length(); j++) {
        if (image.get(i).charAt(j) == '#') {
          Galaxy g = new Galaxy(i, j);
          galaxies.add(g);
          flag = true;
        }
      }
      if (!flag) emptyRows.add(new Long(i));
    }

    for (int j = 0; j < image.get(0).length(); j++) {
      flag = false;
      for (int i = 0; i < image.size(); i++) {
        if (image.get(i).charAt(j) == '#') {
          flag = true;
          break;
        }
      }
      if (!flag) emptyCols.add(new Long(j));
    }
    updateGalaxyPositions(galaxies, emptyRows, emptyCols);
    return getDistances(galaxies);
  }

  public static long getDistances(List<Galaxy> galaxies) {
    long total = 0;
    for (int i = 0; i < galaxies.size(); i++) {
      for (int j = i+1; j < galaxies.size(); j++) {
        long dist = Math.abs(galaxies.get(i).r - galaxies.get(j).r) + Math.abs(galaxies.get(i).c - galaxies.get(j).c);
        total += dist;
        //System.out.println(String.valueOf(i+1) + "::" + galaxies.get(i) + ";" + String.valueOf(j+1) + "::" + galaxies.get(j) + " : " + dist);
      }
    }
    return total;
  }

  public static void printArr(List<Long> arr) {
    for (int i = 0; i < arr.size(); i++) {
      System.out.print(arr.get(i) + ",");
    }
    System.out.println();
  }

  public static void updateGalaxyPositions(List<Galaxy> galaxies, List<Long> emptyRows, List<Long> emptyCols) {
    long incr = 0;
    for (int j = 0; j < galaxies.size(); j++) {
      incr = 0;
      for (int i = 0; i < emptyRows.size(); i++) {
        if (emptyRows.get(i) < galaxies.get(j).r) incr++;
      }
      galaxies.get(j).r += incr * (MULT_FACTOR - 1);
    }

    for (int j = 0; j < galaxies.size(); j++) {
      incr = 0;
      for (int i = 0; i < emptyCols.size(); i++) {
        if (emptyCols.get(i) < galaxies.get(j).c) incr++;
        //System.out.println("incrementing col for galaxy " + String.valueOf(j) + " # " + galaxies.get(j));
      }
      galaxies.get(j).c += incr * (MULT_FACTOR - 1);
    }
  }

  public static void printG(List<Galaxy> gs) {
    for (int i = 0; i < gs.size(); i++)
      System.out.println(gs.get(i));
  }
}


class Galaxy {
  public long r;
  public long c;

  Galaxy(long a, long b) {
    r = a; c = b;
  }

  @Override
  public String toString() {
    return "Galaxy: " + String.valueOf(r) + "," + String.valueOf(c);
  }
}
