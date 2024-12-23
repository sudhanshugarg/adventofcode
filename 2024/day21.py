import json
import logging
import sys
import time
import os
from typing import List
from abc import ABC, abstractmethod


class Keypad(ABC):
    def __init__(self, keypad: List[List[int]], max_key: int):
        self.keypad = keypad
        self.m = len(self.keypad)
        self.n = len(self.keypad[0])

        self.keyToPos = [[0, 0] for i in range(max_key + 1)]
        for i in range(self.m):
            for j in range(self.n):
                self.keyToPos[self.keypad[i][j]] = [i, j]

        self.max_key = max_key
        self.A = max_key
        self.shortest_path = [["" for j in range(self.max_key + 1)] for i in range(self.max_key + 1)]
        self.dirs = [[0, -1], [-1, 0], [0, 1], [1, 0]]
        self.move = ["<", "^", ">", "v"]
        self.move_dict: dict[str, int] = {
            "<": 0,
            "^": 1,
            ">": 2,
            "v": 3
        }
        self._create_shortest_paths()

    def _create_shortest_paths(self):
        for i in range(self.max_key + 1):
            # for i in range(2, 3):
            start = self.keyToPos[i]
            queue: List[List[int]] = [[start[0], start[1], 0, ""]]
            left = 0
            added = [[False for j in range(self.n)] for i in range(self.m)]
            added[start[0]][start[1]] = True

            while left < len(queue):
                curr = queue[left]
                left += 1
                for j in range(4):
                    nr = curr[0] + self.dirs[j][0]
                    nc = curr[1] + self.dirs[j][1]

                    if nr < 0 or nr >= self.m or nc < 0 or nc >= self.n:
                        continue

                    # print(f"checking [{nr},{nc}]: {self.keypad[nr][nc]}")
                    if added[nr][nc] or self.keypad[nr][nc] < 0:
                        continue

                    added[nr][nc] = True
                    self.shortest_path[i][self.keypad[nr][nc]] = f"{curr[3]}{self.move[j]}"
                    # print(f"path from {i} to {self.keypad[nr][nc]} = {self.shortest_path[i][self.keypad[nr][nc]]}")
                    queue.append([nr, nc, curr[2] + 1, self.shortest_path[i][self.keypad[nr][nc]]])

    def _get_path_pair(self, key1: int, key2: int):
        return self.shortest_path[key1][key2] + "A"

    @abstractmethod
    def get_path(self, s: str):
        pass


class NumericKeypad(Keypad):
    def __init__(self):
        keypad = [
            [7, 8, 9],
            [4, 5, 6],
            [1, 2, 3],
            [-1, 0, 10]
        ]

        super().__init__(keypad, 10)

    def get_path(self, s: str):
        path = ""
        start = self.A
        for i in range(len(s)):
            if s[i] == 'A':
                end = self.A
            else:
                end = ord(s[i]) - ord('0')

            # print(f"going from {start} to {end}, path is {self._get_path_pair(start, end)}")
            path += self._get_path_pair(start, end)
            start = end

        return path


class DirectionalKeypad(Keypad):
    def __init__(self):
        keypad = [
            [-1, 1, 4],
            [0, 3, 2]
        ]
        super().__init__(keypad, 4)

    def get_path(self, s: str):
        path = ""
        start = self.A
        for i in range(len(s)):
            if s[i] == 'A':
                end = self.A
            else:
                end = self.move_dict[s[i]]

            # print(f"going from {start} to {end}, path is {self._get_path_pair(start, end)}")
            path += self._get_path_pair(start, end)
            start = end

        return path

class Day21:
    def __init__(self, filename: str):
        self.input = []
        with open(filename, 'r') as f:
            line = f.readline().rstrip("\n")
            self.input.append(line)

        self.numeric = NumericKeypad()
        self.directional = DirectionalKeypad()

    def part1(self):
        for i in range(len(self.input)):
            p1 = self.numeric.get_path(self.input[i])
            # p2 = self.directional.get_path(p1)
            p3 = self.directional.get_path(p1)
            print(f"for {self.input[i]}, path={p3}, len: {len(p3)}")
        return len(self.input)

    def part2(self):
        return -1


def run(args):
    day = Day21(args[1])
    print(day.part1())
    print(day.part2())


if __name__ == "__main__":
    run(sys.argv)
