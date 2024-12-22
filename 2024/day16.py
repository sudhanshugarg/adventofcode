import json
import logging
import sys
import time
import os
import heapq
from typing import List
from functools import total_ordering

@total_ordering
class Node:
    def __init__(self, pos: List[int], a: int, b: int):
        self.pos = pos
        self.direction = a
        self.distance = b
        self.path = []

    def __eq__(self, other):
        return self.distance == other.distance

    def __lt__(self, other):
        return self.distance <= other.distance

class Day16:
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

        # L U R D
        self.dirs = [[0, -1], [-1, 0], [0, 1], [1, 0]]
        self.best_tile = [[False for j in range(self.n)] for i in range(self.m)]

    def _update_best_tile(self, path: List[Node]):
        for curr in path:
            self.best_tile[curr.pos[0]][curr.pos[1]] = True

    def part1(self):
        visited = [[[False for k in range(4)] for j in range(self.n)] for i in range(self.m)]

        pq = []
        mind = -1
        # east = 2
        heapq.heappush(pq, Node(self.s, 2, 0))
        while len(pq) > 0:
            curr = heapq.heappop(pq)
            if curr.pos[0] == self.e[0] and curr.pos[1] == self.e[1]:
                if mind == -1 or curr.distance == mind:
                    mind = curr.distance
                    self._update_best_tile(curr.path)
                else:
                    return mind

            visited[curr.pos[0]][curr.pos[1]][curr.direction] = True

            for i in range(4):
                nd = (curr.direction + i) % 4
                nr = curr.pos[0] + self.dirs[nd][0]
                nc = curr.pos[1] + self.dirs[nd][1]

                if self.input[nr][nc] == '#' or visited[nr][nc][nd]:
                    continue

                distance = curr.distance
                if i == 0:
                    distance += 1
                elif i == 1 or i == 3:
                    distance += 1001
                else:
                    distance += 2001

                nextNode = Node([nr, nc], nd, distance)
                nextNode.path = list(curr.path)
                nextNode.path.append(curr)
                heapq.heappush(pq, nextNode)
        return mind


    def part2(self):
        # assumes part1 has been run
        tiles = 0
        for i in range(self.m):
            for j in range(self.n):
                if self.best_tile[i][j]:
                    tiles += 1
        return tiles + 1 #for E tile



def run(args):
    day = Day16(args[1])
    print(day.part1())
    print(day.part2())


if __name__ == "__main__":
    run(sys.argv)
