import json
import logging
import sys
import time
import os
from typing import List

class DayX:
    def __init__(self, filename: str):
        with open(filename, 'r') as f:
            self.input = f.readlines()

    def part1(self):
        return len(self.input)

    def part2(self):
        return -1


def run(args):
    day = DayX(args[1])
    print(day.part1())
    print(day.part2())


if __name__ == "__main__":
    run(sys.argv)
