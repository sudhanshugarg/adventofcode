import heapq
import json
import logging
import sys
import time
import os
from typing import List
from functools import total_ordering

@total_ordering
class Node:
    def __init__(self, x: int, y: int, d: int):
        self.r = x
        self.c = y
        self.dist = d

    def __eq__(self, other):
        return self.dist == other.dist

    def __lt__(self, other):
        return self.dist < other.dist

class Day18:
    def __init__(self, filename: str, grid: int, take: int = -1):
        with open(filename, 'r') as f:
            self.input = f.readlines()

        self.n = grid
        self.g = [[True for j in range(self.n)] for i in range(self.n)]
        self.parse(take)
        self.dirs = [[0, -1], [-1, 0], [0, 1], [1, 0]]  # L U R D

    def parse(self, take: int):
        n = take
        if n < 0:
            n = len(self.input)

        for i in range(n):
            arr = self.input[i].split(',')
            p = list(map(int, arr))
            self.g[p[0]][p[1]] = False

    def print_grid(self):
        for i in range(self.n):
            row = ""
            for j in range(self.n):
                if self.g[i][j]:
                    row += "."
                else:
                    row += "#"
            print(row)

    def part1(self):
        pq = []
        self.print_grid()

        start = Node(0, 0, 0)
        heapq.heappush(pq, start)
        added = [[False for j in range(self.n)] for i in range(self.n)]
        added[0][0] = True

        while len(pq) > 0:
            curr = heapq.heappop(pq)

            if curr.r == (self.n - 1) and curr.c == (self.n - 1):
                return curr.dist

            for i in range(4):
                nr = curr.r + self.dirs[i][0]
                nc = curr.c + self.dirs[i][1]

                if nr < 0 or nr >= self.n or nc < 0 or nc >= self.n or added[nr][nc] or (not self.g[nr][nc]):
                    continue

                added[curr.r][curr.c] = True
                next_node = Node(nr, nc, curr.dist + 1)
                heapq.heappush(pq, next_node)

        return -1

    def part2(self):
        return -1


def run(args):
    day = Day18(args[1], int(args[2]), int(args[3]))
    print(day.part1())
    print(day.part2())


if __name__ == "__main__":
    run(sys.argv)
