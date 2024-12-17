import json
import logging
import sys
import time
import os
import re
from typing import List


class ClawMachine:
    def __init__(self, A: str, B: str, Prize: str):
        self.a = self._parseLine(A)
        self.b = self._parseLine(B)
        self.prize = self._parseLine(Prize)
        self.MAX_PRESSES = 101

        # print(self.a)
        # print(self.b)
        # print(self.prize)

    def __str__(self):
        return f"A: {self.a}, B: {self.b}, Prize: {self.prize}"

    def _parseLine(self, line: str):
        arr = re.findall("\d+", line)
        return list(map(lambda x: int(x), arr))

    def minTokens(self):
        min_tokens = 0
        only_button_a = self._single_equation(self.a)
        only_button_b = self._single_equation(self.b)

        if only_button_a > 0:
            min_tokens = 3 * only_button_a

        if only_button_b > 0:
            if min_tokens == 0 or only_button_b < min_tokens:
                min_tokens = only_button_b

        # both buttons
        y_lcm = self._lcm(self.b[0], self.b[1])
        equation1_multiplier = y_lcm / self.b[0]
        equation2_multiplier = y_lcm / self.b[1]

        lhs = equation1_multiplier * self.a[0] - equation2_multiplier * self.a[1]
        rhs = equation1_multiplier * self.prize[0] - equation2_multiplier * self.prize[1]

        both_buttons = [0, 0]
        if lhs != 0 and rhs % lhs == 0:
            both_buttons[0] = rhs / lhs
            both_buttons[1] = (self.prize[0] - both_buttons[0] * self.a[0]) / self.b[0]

        if both_buttons[0] > 0 and both_buttons[1] > 0:
            cost = 3 * both_buttons[0] + both_buttons[1]
            if min_tokens == 0 or cost < min_tokens:
                min_tokens = cost

        return min_tokens

    def _single_equation(self, button: List[int]):
        condition = (self.prize[0] % button[0] == 0) and (
                self.prize[1] % button[1] == 0) and (
                            (self.prize[0] / button[0]) == (self.prize[1] / button[1]))

        if condition:
            return self.prize[0] / button[0]
        else:
            return 0

    def _lcm(self, a: int, b: int):
        return (a * b) / self._gcd(a, b)

    def _gcd(self, a: int, b: int):
        if a < b:
            return self._gcd(b, a)

        if b == 0:
            return a
        else:
            return self._gcd(b, a % b)


class Day13:
    def __init__(self, filename: str):
        with open(filename, 'r') as f:
            self.input = f.readlines()

        self.claws = []
        for i in range(0, len(self.input), 4):
            self.claws.append(ClawMachine(self.input[i], self.input[i + 1], self.input[i + 2]))

    def part1(self):
        n = len(self.claws)
        total = 0
        for i in range(n):
            next_count = self.claws[i].minTokens()
            print(self.claws[i], next_count)
            total += next_count
        return total

    def part2(self):
        return -1


def run(args):
    inputFileName = args[1]

    day = Day13(inputFileName)
    print(day.part1())
    print(day.part2())


if __name__ == "__main__":
    run(sys.argv)
