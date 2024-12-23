import json
import logging
import sys
import time
import os
from typing import List

class Day19:
    def __init__(self, filename: str):
        with open(filename, 'r') as f:
            self.input = f.readlines()

        towels = self.input[0].split(",")
        self.towels = list(map(lambda t: t.strip(" \n"), towels))
        n = len(self.input)
        self.designs = []
        for i in range(2, n):
            self.designs.append(self.input[i].rstrip("\n"))

        self.possible_designs = dict()

    def is_prefix(self, s1: str, s2: str):
        if len(s1) > len(s2):
            return False

        return s1 == s2[0:len(s1)]

    def _is_possible_helper(self, d: str):
        if len(d) == 0:
            return 1

        if d in self.possible_designs:
            return self.possible_designs[d]

        self.possible_designs[d] = 0
        for t in self.towels:
            if not self.is_prefix(t, d):
                # print(f"{t} is not a prefix of {d}")
                continue

            ways = self._is_possible_helper(d[len(t):])
            self.possible_designs[d] += ways

        # print(f"testing design: {d}, result: {self.possible_designs[d]}")
        return self.possible_designs[d]

    def part1And2(self):
        possible = 0
        ways = 0
        n = len(self.designs)
        for i in range(n):
            count = self._is_possible_helper(self.designs[i])
            if count > 0:
                possible += 1
            ways += count

        return [possible, ways]

def run(args):
    day = Day19(args[1])
    print(day.part1And2())


if __name__ == "__main__":
    run(sys.argv)
