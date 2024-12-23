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
            return True

        if d in self.possible_designs:
            return self.possible_designs[d]

        self.possible_designs[d] = False
        for t in self.towels:
            if not self.is_prefix(t, d):
                # print(f"{t} is not a prefix of {d}")
                continue

            possible = self._is_possible_helper(d[len(t):])
            if possible:
                self.possible_designs[d] = True
                break

        # print(f"testing design: {d}, result: {self.possible_designs[d]}")
        return self.possible_designs[d]

    def part1(self):
        possible = 0
        n = len(self.designs)
        for i in range(n):
            if self._is_possible_helper(self.designs[i]):
                possible += 1
        return possible

    def part2(self):
        return -1


def run(args):
    day = Day19(args[1])
    print(day.part1())
    print(day.part2())


if __name__ == "__main__":
    run(sys.argv)
