import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.lang.Comparable;

class Day7 {
  public static void main(String[] args) {
    try {
      BufferedReader bufreader = new BufferedReader(new FileReader(args[0]));
      String line = bufreader.readLine();
      List<CamelHand> hands = new ArrayList<>();
      while(line != null) {
        CamelHand camelHand = new CamelHand(line);
        hands.add(camelHand);
        line = bufreader.readLine();
      }
      bufreader.close();

      CamelHand.createOrdering();
      Collections.sort(hands);
      long total = 0;
      for (int i = 0; i < hands.size(); i++) {
        System.out.println(hands.get(i));
        total += (i+1) * hands.get(i).bid;
      }
      System.out.println(total);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

class CamelHand implements Comparable<CamelHand> {
  public String hand;
  public int bid;
  public int strength;
  public static String ordering = "AKQT98765432J";
  public static Map<Character, Integer> order;

  CamelHand(String s) {
    String[] input = s.split("\\s+");
    hand = input[0];
    try {
      bid = Integer.parseInt(input[1]);
    } catch (NumberFormatException e) {
      bid = -100000;
      e.printStackTrace();
    }
    calculateStrength();
  }

  public static void createOrdering() {
    order = new HashMap<Character, Integer>();
    for (int i = 0, j = CamelHand.ordering.length(); i < CamelHand.ordering.length(); i++, j--) {
      CamelHand.order.put(CamelHand.ordering.charAt(i), j);
    }
  }

  private void calculateStrength() {
    char[] rawHandArr = hand.toCharArray();

    int jokers = 0;
    for (int i = 0; i < rawHandArr.length; i++) {
      if (rawHandArr[i] == 'J') jokers++;
    }

    if (jokers == 5) {
      strength = 7;
      return;
    }

    //jokers < 5
    char[] handArr = new char[5 - jokers];
    for (int i = 0, j = 0; i < rawHandArr.length; i++) {
      if (rawHandArr[i] != 'J') {
        handArr[j] = rawHandArr[i];
        j++;
      }
    }

    Arrays.sort(handArr);
    int unique = 1;
    int maxCount = 1, currCount = 1;

    for (int i = 1; i < handArr.length; i++) {
      if (handArr[i] == handArr[i-1]) {
        currCount++;
        maxCount = currCount > maxCount ? currCount : maxCount;
      } else {
        unique++;
        currCount = 1;
      }
    }
    maxCount += jokers;

    strength = 1;
    if (unique == 1) strength = 7;
    else if (unique == 2 && maxCount == 4) strength = 6;
    else if (unique == 2 && maxCount == 3) strength = 5;
    else if (unique == 3 && maxCount == 3) strength = 4;
    else if (unique == 3 && maxCount == 2) strength = 3;
    else if (unique == 4 && maxCount == 2) strength = 2;
  }

  @Override
  public int compareTo(CamelHand other) {
    boolean isGreater = false;
    if (strength != other.strength) {
      isGreater = strength > other.strength;
    }
    else {
      for (int i = 0; i < hand.length(); i++) {
        if (CamelHand.order.get(hand.charAt(i)) == CamelHand.order.get(other.hand.charAt(i))) continue;
        isGreater = CamelHand.order.get(hand.charAt(i)) > CamelHand.order.get(other.hand.charAt(i));
        break;
      }
    }
    return isGreater ? 1 : -1;
  }

  @Override
  public String toString() {
    return hand + "," + String.valueOf(bid) + "," + String.valueOf(strength);
  }
}
