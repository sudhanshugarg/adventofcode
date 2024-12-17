import json
import logging
import sys
import time
import os
from typing import List, Optional


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
                perimeter_area = self._dfsHelper(id, i, j, visited, None, None)
                # print(id, int(perimeter_area[0]), int(perimeter_area[1]))
                total += perimeter_area[0] * perimeter_area[1]

        return total

    def _dfsHelper(self, id: str, r: int, c: int,
                   visited: List[List[bool]],
                   border_tiles: Optional[List[List[int]]],
                   borders_present: Optional[List[List[List[bool]]]]):
        visited[r][c] = True
        perimeter_area = [0, 1]

        for i in range(4):
            nr = r + self.dirs[i][0]
            nc = c + self.dirs[i][1]
            if (nr < 0) or (nr >= self.m) or (nc < 0) or (nc >= self.n) or (self.input[nr][nc] != id):
                perimeter_area[0] += 1
                # print(f"here with {r}, {c}, {i}")
                if border_tiles is not None:
                    # print(f"here with {r}, {c}, {i} and adding it")
                    # [r, c] is a border tile
                    borders_present[r][c][i] = True
                    border_tile = [r, c, i]
                    border_tiles.append(border_tile)
                continue

            if visited[nr][nc]:
                continue

            perimeter_area_rest = self._dfsHelper(id, nr, nc, visited, border_tiles, borders_present)
            perimeter_area[0] += perimeter_area_rest[0]
            perimeter_area[1] += perimeter_area_rest[1]

        return perimeter_area


    def part2(self):
        # compute the border and area separately.
        # from part1 code, do the following
        # find all the tiles that have borders, and put them into a separate queue
        # each time you dequeue, check if this tile's border has already been counted.

        borders_visited = [[[False for k in range(4)] for j in range(self.n)] for i in range(self.m)]
        borders_present = [[[False for k in range(4)] for j in range(self.n)] for i in range(self.m)]
        total = 0

        # create a 2D visited array
        visited = [[False for j in range(self.n)] for i in range(self.m)]
        for i in range(self.m):
            for j in range(self.n):
                if visited[i][j]:
                    continue

                id = self.input[i][j]
                border_tiles = list()
                perimeter_area = self._dfsHelper(id, i, j, visited, border_tiles, borders_present)
                discounted_perimeter = self._find_discounted_perimeter(id, border_tiles, borders_present, borders_visited)
                # print(id, discounted_perimeter, perimeter_area[1])
                total += discounted_perimeter * perimeter_area[1]

        return total

    def _getside(self, side: int):
        if side == 0:
            return "L"
        elif side == 1:
            return "U"
        elif side == 2:
            return "R"
        elif side == 3:
            return "D"
        else:
            return "KYA_RE"

    def _find_discounted_perimeter(self, id: str,
                                   border_tiles: List[List[int]],
                                   borders_present: List[List[List[bool]]],
                                   borders_visited: List[List[List[bool]]]):
        # take each tile from border_tiles
        perimeter = 0
        for border_tile in border_tiles:
            r = border_tile[0]
            c = border_tile[1]
            side = border_tile[2]
            if borders_visited[r][c][side]:
                continue

            perimeter += 1
            borders_visited[r][c][side] = True
            tiles_covered = 1

            if side == 0 or side == 2: #L or R

                # go up
                nr = r - 1
                nc = c

                while nr >= 0:
                    adj = self.input[nr][nc]
                    if adj != id or not borders_present[nr][nc][side]:
                        break

                    tiles_covered += 1
                    borders_visited[nr][nc][side] = True
                    nr -= 1

                # go down
                nr = r + 1
                nc = c

                while nr < self.m:
                    adj = self.input[nr][nc]
                    if adj != id or not borders_present[nr][nc][side]:
                        break

                    tiles_covered += 1
                    borders_visited[nr][nc][side] = True
                    nr += 1

            else: #U or D
                # go left
                nr = r
                nc = c - 1

                while nc >= 0:
                    adj = self.input[nr][nc]
                    if adj != id or not borders_present[nr][nc][side]:
                        break

                    tiles_covered += 1
                    borders_visited[nr][nc][side] = True
                    nc -= 1

                # go right
                nr = r
                nc = c + 1

                while nc < self.n:
                    adj = self.input[nr][nc]
                    if adj != id or not borders_present[nr][nc][side]:
                        break

                    tiles_covered += 1
                    borders_visited[nr][nc][side] = True
                    nc += 1

            # print(id, r, c, self._getside(side), tiles_covered)

        return perimeter


def run(args):
    day = Day12(args[1])
    print(day.part1())
    print(day.part2())


if __name__ == "__main__":
    run(sys.argv)
