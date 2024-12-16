import json
import logging
import sys
import time
import os
from typing import List

class Day12:
    def __init__(self, filename: str):
        self.input = []
        with open(filename, 'r') as f:
            for line in f:
                self.input.append(line.rstrip('\n'))

        self.m = len(self.input)
        self.n = len(self.input[0])
        #print("#" + str(self.input[0][3]) + "#")

    def part1(self):
        return self.m

    def part2(self):
        return self.n


def run(args):
    day = Day12(args[1])
    print(day.part1())
    print(day.part2())


if __name__ == "__main__":
    run(sys.argv)
