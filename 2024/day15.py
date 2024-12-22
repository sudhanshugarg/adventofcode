import json
import logging
import sys
import time
import os
from typing import List

class Day15:
    def __init__(self, filename: str):
        with open(filename, 'r') as f:
            self.input = f.readlines()

        gridstr = []
        self.directions = ""
        self.r = 0
        self.c = 0

        inGrid = True
        for i in range(len(self.input)):
            line = self.input[i]
            if len(line) == 1:
                inGrid = False
                continue

            if inGrid:
                gridstr.append(line.rstrip("\n"))
                for j in range(len(line)):
                    if line[j] == '@':
                        self.r = i
                        self.c = j
            else:
                self.directions += line.rstrip("\n")

        self.m = len(gridstr)
        self.n = len(gridstr[0])

        self.grid = [[gridstr[i][j] for j in range(self.n)] for i in range(self.m)]
        self.grid[self.r][self.c] = '.' #change robot position to be empty
        self.dirs = [[0, -1], [-1, 0], [0, 1], [1, 0]] #L, U, R, D

        self.g = [[self._getWide(gridstr[i][j], i, j) for j in range(self.n)] for i in range(self.m)]
        self.p = 2 * self.n
        self.movement = {
            '<' : 0,
            '^' : 1,
            '>' : 2,
            'v' : 3,
        }

    def _getWide(self, s: str, i: int, j: int):
        if s == "#":
            return "##"
        elif s == "O":
            return "[]"
        elif s == ".":
            return ".."
        elif s == "@":
            self.row = i
            self.col = 2 * j
            return "@."
        else:
            print("something wrong")
            return "!!"

    def _updateLeft(self, row: int):
        nextAvailable = -1
        for j in range(self.n):
            self.nextEmpty[row][j][0] = nextAvailable
            if self.grid[row][j] == '.':
                nextAvailable = j
            elif self.grid[row][j] == '#':
                nextAvailable = -1

    def _updateRight(self, row: int):
        nextAvailable = self.n
        for j in range(self.n - 1, -1, -1):
            self.nextEmpty[row][j][2] = nextAvailable
            if self.grid[row][j] == '.':
                nextAvailable = j
            elif self.grid[row][j] == '#':
                nextAvailable = self.n

    def _updateUp(self, col: int):
        nextAvailable = -1
        for i in range(self.m):
            self.nextEmpty[i][col][1] = nextAvailable
            if self.grid[i][col] == '.':
                nextAvailable = i
            elif self.grid[i][col] == '#':
                nextAvailable = -1

    def _updateDown(self, col: int):
        nextAvailable = self.m
        for i in range(self.m - 1, -1, -1):
            self.nextEmpty[i][col][3] = nextAvailable
            if self.grid[i][col] == '.':
                nextAvailable = i
            elif self.grid[i][col] == '#':
                nextAvailable = self.m

    def _initialize_empty_cells(self):
        self.nextEmpty = [[[-1 for k in range(4)] for j in range(self.n)] for i in range(self.m)]

        # 0: L, 1: U, 2: R, 3: D
        #Right
        for i in range(self.m):
            self._updateRight(i)

        #Left
        for i in range(self.m):
            self._updateLeft(i)

        #Up
        for j in range(self.n):
            self._updateUp(j)

        #Down
        for j in range(self.n):
            self._updateDown(j)

    def _moveLeft(self):
        nc = self.c - 1
        if (nc < 0) or (self.grid[self.r][nc] == '#'):
            return
        self._moveRow(nc, 0)

    def _moveRight(self):
        nc = self.c + 1
        if (nc >= self.n) or (self.grid[self.r][nc] == '#'):
            return
        self._moveRow(nc, 2)

    def _moveRow(self, nc: int, direction: int):
        if self.grid[self.r][nc] == '.':
            self.grid[self.r][self.c] = '.'
            self.c = nc
        else:  # O
            nextAvailable = self.nextEmpty[self.r][nc][direction]
            if direction == 0 and nextAvailable < 0:
                return
            if direction == 2 and nextAvailable >= self.n:
                return

            self.grid[self.r][nc] = '.'
            self.grid[self.r][nextAvailable] = 'O'
            self.c = nc

            self._updateLeft(self.r)
            self._updateRight(self.r)
            self._updateUp(nc)
            self._updateDown(nc)
            self._updateUp(nextAvailable)
            self._updateDown(nextAvailable)

    def _moveUp(self):
        nr = self.r - 1
        if (nr < 0) or (self.grid[nr][self.c] == '#'):
            return
        self._moveCol(nr, 1)

    def _moveDown(self):
        nr = self.r + 1
        if (nr >= self.m) or (self.grid[nr][self.c] == '#'):
            return
        self._moveCol(nr, 3)

    def _moveCol(self, nr: int, direction: int):
        if self.grid[nr][self.c] == '.':
            self.grid[nr][self.c] = '.'
            self.r = nr
        else: #O
            nextAvailable = self.nextEmpty[nr][self.c][direction]
            if direction == 1 and nextAvailable < 0:
                return
            if direction == 3 and nextAvailable >= self.m:
                return

            self.grid[nr][self.c] = '.'
            self.grid[nextAvailable][self.c] = 'O'
            self.r = nr

            self._updateUp(self.c)
            self._updateDown(self.c)
            self._updateLeft(nr)
            self._updateRight(nr)
            self._updateLeft(nextAvailable)
            self._updateRight(nextAvailable)

    def _printAndComputeGrid(self, should_print: bool = False):
        total = 0
        for i in range(self.m):
            line = ""
            for j in range(self.n):
                if i == self.r and j == self.c:
                    line += '@'
                else:
                    line += self.grid[i][j]

                if self.grid[i][j] == 'O':
                    total += ((100 * i) + j)

            if should_print:
                print(line)
        if should_print:
            print()

        return total

    def part1(self):
        print(f"[{self.m}, {self.n}], start:[{self.r}, {self.c}], directions = {len(self.directions)}")
        self._initialize_empty_cells()
        # print(self.nextEmpty[3][1])

        self._printAndComputeGrid(True)
        for i in range(len(self.directions)):
            d = self.directions[i]
            if d == '<':
                self._moveLeft()
            elif d == '^':
                self._moveUp()
            elif d == '>':
                self._moveRight()
            elif d == 'v':
                self._moveDown()
            else:
                print(f"unknown direction {d}")
                return -100

            # print(f"after direction {i}: {d}")
            # self._printAndComputeGrid(True)

        return self._printAndComputeGrid(True)

    def part2(self):
        # start from self.row and self.col
        # for each direction, try and move there. If you can move, no issues
        # if you have some obstacle, then do accordingly
        for i in range(len(self.directions)):
            d = self.directions[i]
            self._move(self.movement[d])

        return -1

    def _move(self, d: int):
        nr = self.row + self.dirs[d][0]
        nc = self.col + self.dirs[d][1]

        # now we are using self.m and self.p for max row and max col
        if nr < 0 or nr >= (self.m - 1) or nc < 0 or nc >= (self.p - 2) or (self.g[nr][nc] == '#'):
            return

        if self.g[nr][nc] == '.':
            self.row = nr
            self.col = nc
            return

        # now it can only be [ or ]
        # this means we need to do two dfs
        # first dfs is to get whether movement is possible
        # second dfs is to actually do the movement, given that it is possible

def run(args):
    day = Day15(args[1])
    print(day.part1())
    print(day.part2())


if __name__ == "__main__":
    run(sys.argv)
