import json
import logging
import sys
import time
import os
from typing import List

class Robot:
    def __init__(self, s: str):
        self.position, self.velocity = self._parse(s)

    def __str__(self):
        return f"p={self.position} and v={self.velocity}"

    def _parse(self, s: str):
        arr = s.split(" ")
        p = arr[0].split("=")[1].split(",")
        v = arr[1].split("=")[1].split(",")
        return list(map(lambda x: int(x), p)), list(map(lambda x: int(x), v))

class Day14:
    def __init__(self, filename: str):
        with open(filename, 'r') as f:
            self.input = f.readlines()

        robots = []
        for line in self.input:
            r = Robot(line)
            robots.append(r)
            print(r)

        # print(robots)

    def part1(self):
        return len(self.input)

    def part2(self):
        return -1


def run(args):
    day = Day14(args[1])
    print(day.part1())
    print(day.part2())


if __name__ == "__main__":
    run(sys.argv)
