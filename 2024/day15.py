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

        # self.g = [[self._getWide(gridstr[i][j], i, j) for j in range(self.n)] for i in range(self.m)]
        self.g = []
        self.row = self.r
        self.col = self.c * 2
        for i in range(self.m):
            row = []
            for j in range(self.n):
                next2 = self._getWide(self.grid[i][j], i, j)
                row.append(next2[0])
                row.append(next2[1])
            self.g.append(row)

        self.p = 2 * self.n
        self.movement = {
            '<': 0,
            '^': 1,
            '>': 2,
            'v': 3,
        }

    def _getWide(self, s: str, i: int, j: int):
        if s == "#":
            return "##"
        elif s == "O":
            return "[]"
        elif s == ".":
            return ".."
        elif s == "@":
            return ".."
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

    def _printAndComputeGrid(self, gridmap: List[List[str]], r: int, c: int, should_print: bool = False):
        total = 0
        m = len(gridmap)
        n = len(gridmap[0])
        for i in range(m):
            line = ""
            for j in range(n):
                if i == r and j == c:
                    line += '@'
                else:
                    line += gridmap[i][j]

                if gridmap[i][j] == 'O' or gridmap[i][j] == '[':
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

        self._printAndComputeGrid(self.grid, self.r, self.c, True)
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

        return self._printAndComputeGrid(self.grid, self.r, self.c, True)

    def _dfsLeft(self, should_move: bool, r: int, c: int):
        # print(f"Entering to go left [{r}, {c}]")
        if c <= 0:
            return False

        if self.g[r][c] == '.':
            # print(f"found empty at [{r}, {c}]")
            if should_move:
                self.g[r][c] = '['
                self.g[r][c + 1] = ']'
            return True

        if self.g[r][c] == '#':
            return False

        if self.g[r][c] == '[':
            print(f"something is wrong [{r}{c}], {self.g[r][c]}")
            return False

        # has to be ]
        canMove = self._dfsLeft(should_move, r, c - 2)
        if canMove and should_move:
            self.g[r][c] = '['
            self.g[r][c + 1] = ']'
        return canMove


    def _dfsRight(self, should_move: bool, r: int, c: int):
        if c > (self.p - 2):
            return False

        if self.g[r][c] == '.':
            if should_move:
                self.g[r][c - 1] = '['
                self.g[r][c] = ']'
            return True

        if self.g[r][c] == '#':
            return False

        if self.g[r][c] == ']':
            print(f"something is wrong [{r}{c}], {self.g[r][c]}")
            return False

        # has to be [
        canMove = self._dfsRight(should_move, r, c + 2)
        if canMove and should_move:
            self.g[r][c - 1] = '['
            self.g[r][c] = ']'
        return canMove

    def _dfsUp(self, should_move: bool, r: int, c: int):
        if r <= 0:
            return False

        if self.g[r][c] == '.':
            return True

        if self.g[r][c] == '#':
            return False

        if self.g[r][c] == '[':
            canMove = self._dfsUp(should_move, r - 1, c) and self._dfsUp(should_move, r - 1, c + 1)
            if canMove and should_move:
                self.g[r - 1][c] = '['
                self.g[r - 1][c + 1] = ']'
                self.g[r][c] = '.'
                self.g[r][c + 1] = '.'
            return canMove

        if self.g[r][c] == ']':
            canMove = self._dfsUp(should_move, r - 1, c) and self._dfsUp(should_move, r - 1, c - 1)
            if canMove and should_move:
                self.g[r - 1][c] = ']'
                self.g[r - 1][c - 1] = '['
                self.g[r][c] = '.'
                self.g[r][c - 1] = '.'
            return canMove

    def _dfsDown(self, should_move: bool, r: int, c: int):
        if r >= (self.m - 1):
            return False

        if self.g[r][c] == '.':
            return True

        if self.g[r][c] == '#':
            return False

        if self.g[r][c] == '[':
            canMove = self._dfsDown(should_move, r + 1, c) and self._dfsDown(should_move, r + 1, c + 1)
            if canMove and should_move:
                self.g[r + 1][c] = '['
                self.g[r + 1][c + 1] = ']'
                self.g[r][c] = '.'
                self.g[r][c + 1] = '.'
            return canMove

        if self.g[r][c] == ']':
            canMove = self._dfsDown(should_move, r + 1, c) and self._dfsDown(should_move, r + 1, c - 1)
            if canMove and should_move:
                self.g[r + 1][c] = ']'
                self.g[r + 1][c - 1] = '['
                self.g[r][c] = '.'
                self.g[r][c - 1] = '.'
            return canMove


    def part2(self):
        # start from self.row and self.col
        # for each direction, try and move there. If you can move, no issues
        # if you have some obstacle, then do accordingly
        self._printAndComputeGrid(self.g, self.row, self.col, True)

        for i in range(len(self.directions)):
            d = self.directions[i]
            # print(f"after direction {d}")
            self._move(self.movement[d])

        return self._printAndComputeGrid(self.g, self.row, self.col, True)


    def _move(self, d: int):
        nr = self.row + self.dirs[d][0]
        nc = self.col + self.dirs[d][1]

        # now we are using self.m and self.p for max row and max col
        # print(f"[{nr}, {nc}] with direction [{d}]")
        if nr < 0 or nr >= (self.m - 1) or nc < 0 or nc >= (self.p - 2) or (self.g[nr][nc] == '#'):
            return

        # now it can only be [ or ]
        # this means we need to do two dfs
        # first dfs is to get whether movement is possible
        canMove = False

        if d == 0:
            canMove = self._dfsLeft(False, nr, nc)
            if canMove:
                self._dfsLeft(True, nr, nc)
                self.g[self.row][self.col] = '.'
        elif d == 1:
            canMove = self._dfsUp(False, nr, nc)
            if canMove:
                self._dfsUp(True, nr, nc)
                self.g[self.row][self.col] = '.'
        elif d == 2:
            canMove = self._dfsRight(False, nr, nc)
            if canMove:
                self._dfsRight(True, nr, nc)
                self.g[self.row][self.col] = '.'
        elif d == 3:
            canMove = self._dfsDown(False, nr, nc)
            if canMove:
                self._dfsDown(True, nr, nc)
                self.g[self.row][self.col] = '.'
        else:
            print(f"incorrect direction {d}")
            return

        if canMove:
            self.row = nr
            self.col = nc
            self.g[self.row][self.col] = '.'
        # self._printAndComputeGrid(self.g, self.row, self.col, True)

def run(args):
    day = Day15(args[1])
    print(day.part1())
    print(day.part2())


if __name__ == "__main__":
    run(sys.argv)
