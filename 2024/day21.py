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
        self.dirs = [[-1, 0], [0, 1], [1, 0], [0, -1]]  # U R D L
        self.move = ["^", ">", "v", "<", "A"]
        self.move_dict: dict[str, int] = {
            "<": 3,
            "^": 0,
            ">": 1,
            "v": 2,
            "A": 4
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

    def print_shortest_paths(self):
        for i in range(self.max_key + 1):
            for j in range(self.max_key + 1):
                print(f"shortest from {i} to {j} is {self._get_path_pair(i, j)}")

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

            # print(f"n: going from {start} to {end}, path is {self._get_path_pair(start, end)}")
            path += self._get_path_pair(start, end)
            start = end

        return path


class DirectionalKeypad(Keypad):
    def __init__(self):
        keypad = [
            [-1, 0, 4],
            [3, 2, 1]
        ]
        super().__init__(keypad, 4)
        self.shortest_path[3][4] = ">>^"
        self.shortest_path[4][3] = "v<<"

    def get_path(self, s: str):
        path = ""
        start = self.A
        for i in range(len(s)):
            if s[i] == 'A':
                end = self.A
            else:
                end = self.move_dict[s[i]]

            # print(f"d: going from {self.move[start]} to {self.move[end]}, path is {self._get_path_pair(start, end)}")
            path += self._get_path_pair(start, end)
            start = end

        return path

    def get_source_path(self, s: str):
        curr = self.keyToPos[self.A]
        path = ""
        for i in range(len(s)):
            key = self.move_dict[s[i]]
            # print(curr)
            if key == self.A:
                which = self.keypad[curr[0]][curr[1]]
                path += self.move[which]
                continue
            curr[0] += self.dirs[key][0]
            curr[1] += self.dirs[key][1]

        return path

class Day21:
    def __init__(self, filename: str):
        self.input = []
        with open(filename, 'r') as f:
            for line in f:
                self.input.append(line.rstrip("\n"))

        print(self.input)

        self.numeric = NumericKeypad()
        self.directional = DirectionalKeypad()

    def part1(self):
        # self.numeric.print_shortest_paths()
        # print("directional now")
        # self.directional.print_shortest_paths()

        # print(self.directional._get_path_pair(3, 0))
        # print(self.directional._get_path_pair(3, 1))
        # print(self.directional._get_path_pair(3, 2))
        # print(self.directional._get_path_pair(3, 4))
        # print(self.directional._get_path_pair(3, 3))
        for i in range(len(self.input)):
            p1 = self.numeric.get_path(self.input[i])
            print(f"p1: {p1}")
            # p1a = "v<<A>>^A"
            # p1b = "v<<A>^>A"
            p2 = self.directional.get_path(p1)
            print(f"p2: {p2}")
            p3 = self.directional.get_path(p2)
            p3 = "<v<A>>^AvA^A<vA<AA>>^AAvA<^A>AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A"
            print(f"p3: {p3}")
            # p3a = self.directional.get_path(p1a)
            # print("next one now")
            # p3b = self.directional.get_path(p1b)
            # print(f"for {p1a}, path={p3a}, len: {len(p3a)}")
            # print(f"for {p1b}, path={p3b}, len: {len(p3b)}")
            p2_reverse = self.directional.get_source_path(p3)
            print(f"p2_reverse: {p2_reverse}")
            if p2 == p2_reverse:
                print("yes p2 and reverse are good")
            else:
                print("NO, not equal")
            # print(f"for {self.input[i]}, path={p3}, len: {len(p3)}")
        return len(self.input)

    def part2(self):
        return -1


def run(args):
    day = Day21(args[1])
    print(day.part1())
    print(day.part2())


if __name__ == "__main__":
    run(sys.argv)
