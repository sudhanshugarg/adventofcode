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
        # print("#" + str(self.input[0][3]) + "#")
        self.dirs = [[0, -1], [-1, 0], [0, 1], [1, 0]]  # L, U, R, D

    def part1(self):
        # create a 2D visited array
        visited = [[False for j in range(self.n)] for i in range(self.m)]
        total = 0

        for i in range(self.m):
            for j in range(self.n):
                if visited[i][j]:
                    continue

                id = self.input[i][j]
                perimeter_area = self._dfsHelper(id, i, j, visited)
                # print(id, int(perimeter_area[0]), int(perimeter_area[1]))
                total += perimeter_area[0] * perimeter_area[1]

        return total

    def _dfsHelper(self, id: str, r: int, c: int, visited: List[List[bool]]):
        visited[r][c] = True
        perimeter_area = [0, 1]

        for i in range(4):
            nr = r + self.dirs[i][0]
            nc = c + self.dirs[i][1]
            if (nr < 0) or (nr >= self.m) or (nc < 0) or (nc >= self.n) or (self.input[nr][nc] != id):
                perimeter_area[0] += 1
                continue

            if visited[nr][nc]:
                continue

            perimeter_area_rest = self._dfsHelper(id, nr, nc, visited)
            perimeter_area[0] += perimeter_area_rest[0]
            perimeter_area[1] += perimeter_area_rest[1]

        return perimeter_area


    def part2(self):
        return self.n


def run(args):
    day = Day12(args[1])
    print(day.part1())
    print(day.part2())


if __name__ == "__main__":
    run(sys.argv)
