import json
import logging
import sys
import time
import os
from typing import List

class Day20:
    def __init__(self, filename: str):
        with open(filename, 'r') as f:
            self.input = f.readlines()

        self.m = len(self.input)
        self.n = len(self.input[0])

        for i in range(self.m):
            for j in range(self.n):
                if self.input[i][j] == 'S':
                    self.s = [i, j]
                elif self.input[i][j] == 'E':
                    self.e = [i, j]

        self.dirs = [[0, -1], [-1, 0], [0, 1], [1, 0]]  # L U R D
        self.position = [[-1 for j in range(self.n)] for i in range(self.m)]

    def init_racetrack_pos(self):
        self.position[self.s[0]][self.s[1]] = 0

        que = [[self.s[0], self.s[1], 0]]
        left = 0
        while left < len(que):
            curr = que[left]
            r = curr[0]
            c = curr[1]
            dist = curr[2]
            left += 1
            if r == self.e[0] and c == self.e[1]:
                break

            for i in range(4):
                nr = r + self.dirs[i][0]
                nc = c + self.dirs[i][1]
                if (nr < 0 or nr >= self.m or nc < 0 or nc >= self.n or
                        (self.position[nr][nc] >= 0 or self.input[nr][nc] == '#')):
                    continue

                self.position[nr][nc] = dist + 1
                que.append([nr, nc, dist + 1])

    def print_position(self):
        for i in range(self.m):
            row = ""
            for j in range(self.n):
                if self.position[i][j] < 0:
                    row += "# ,"
                else:
                    row += str(self.position[i][j])
                    if self.position[i][j] < 10:
                        row += " "
                    row += ","
            print(row)

    def part1(self):
        self.init_racetrack_pos()
        # self.print_position()
        freq_counter = dict()

        for i in range(1, self.m - 1):
            for j in range(1, self.n - 1):
                if self.input[i][j] != '#':
                    continue

                # go around all 6 direction pairs
                for x in range(0, 4):
                    for y in range(x + 1, 4):

                        d1 = [i + self.dirs[x][0], j + self.dirs[x][1]]
                        d2 = [i + self.dirs[y][0], j + self.dirs[y][1]]
                        # print(f"d1 = {d1}, d2 = {d2}")
                        # print(f"p1 = {self.position[d1[0]][d1[1]]}, p2 = {self.position[d2[0]][d2[1]]}")

                        if self.position[d1[0]][d1[1]] >= 0 and self.position[d2[0]][d2[1]] >= 0:
                            savings = abs(self.position[d1[0]][d1[1]] - self.position[d2[0]][d2[1]]) - 2
                            if savings not in freq_counter:
                                freq_counter[savings] = 0
                            freq_counter[savings] += 1

        total = 0
        for key in freq_counter:
            print(f"There are {freq_counter[key]} cheats that save {key} picoseconds")
            if key >= 100:
                total += freq_counter[key]

        return total

    def part2(self):
        return -1


def run(args):
    day = Day20(args[1])
    print(day.part1())
    print(day.part2())


if __name__ == "__main__":
    run(sys.argv)
