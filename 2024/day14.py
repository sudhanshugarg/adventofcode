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
        self.m = 103
        self.n = 101
        self.m2 = int(self.m / 2)
        self.n2 = int(self.n / 2)
        with open(filename, 'r') as f:
            self.input = f.readlines()

        self.robots = []
        for line in self.input:
            r = Robot(line)
            self.robots.append(r)

    def part1(self):
        jumps = 100
        counts = [[0, 0], [0, 0]]
        for r in self.robots:
            nx = r.position[1] + jumps * r.velocity[1]
            ny = r.position[0] + jumps * r.velocity[0]

            nx %= self.m
            ny %= self.n
            # print(f"[{nx}, {ny}]")
            if (nx == self.m2) or (ny == self.n2):
                continue

            xcoord = 0
            ycoord = 0
            if nx > self.m2:
                xcoord = 1
            if ny > self.n2:
                ycoord = 1

            counts[xcoord][ycoord] += 1

        print(counts)
        return counts[0][0] * counts[0][1] * counts[1][0] * counts[1][1]

    def part2(self):
        jumps = 71


        while True:

            hq = [[0 for j in range(self.n)] for i in range(self.m)]
            for r in self.robots:
                nx = r.position[1] + jumps * r.velocity[1]
                ny = r.position[0] + jumps * r.velocity[0]

                nx %= self.m
                ny %= self.n
                hq[nx][ny] += 1
                # print(f"[{nx}, {ny}]")

            print(f"After jump {jumps}, positions are")
            self._printHq(hq)
            print()
            jumps += 101
            input("Enter to continue...")

        return 8050


    def _printHq(self, arr: List[List[int]]):
        for i in range(self.m):
            line = ""
            for j in range(self.n):
                if arr[i][j] == 0:
                    line += " "
                else:
                    line += str(arr[i][j])
            print(line)
def run(args):
    day = Day14(args[1])
    print(day.part1())
    print(day.part2())


if __name__ == "__main__":
    run(sys.argv)
