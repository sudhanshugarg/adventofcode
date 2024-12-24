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
        self.shortest_paths = [[[] for j in range(self.max_key + 1)] for i in range(self.max_key + 1)]
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
            # for i in range(1):
            # i is the source vertex
            self.shortest_paths[i][i].append("")

            start = self.keyToPos[i]
            queue: List[List[int]] = [[start[0], start[1], 0, ""]]
            left = 0
            # added = [[False for j in range(self.n)] for i in range(self.m)]
            # added[start[0]][start[1]] = True

            while left < len(queue):
                curr = queue[left]
                left += 1
                for j in range(4):
                    nr = curr[0] + self.dirs[j][0]
                    nc = curr[1] + self.dirs[j][1]

                    if nr < 0 or nr >= self.m or nc < 0 or nc >= self.n:
                        continue

                    # print(f"checking [{nr},{nc}]: {self.keypad[nr][nc]}")
                    if self.keypad[nr][nc] < 0:
                        continue

                    next_path = f"{curr[3]}{self.move[j]}"
                    # print(f"testing next_path: {next_path}")
                    other_paths = self.shortest_paths[i][self.keypad[nr][nc]]
                    if len(other_paths) > 0 and len(next_path) > len(other_paths[0]):
                        # print(f"yup {next_path} longer than {other_paths[0]}")
                        continue

                    # added[nr][nc] = True
                    self.shortest_paths[i][self.keypad[nr][nc]].append(next_path)
                    # print(f"path from {i} to {self.keypad[nr][nc]} = {self.shortest_path[i][self.keypad[nr][nc]]}")
                    queue.append([nr, nc, curr[2] + 1, next_path])

    def _get_paths_pair(self, key1: int, key2: int):
        with_a = []
        for path in self.shortest_paths[key1][key2]:
            with_a.append(f"{path}A")
        return with_a

    def _get_shortest_paths(self, paths: List[str]):
        if len(paths) == 0:
            return []

        lengths = dict()
        for path in paths:
            l = len(path)
            if l not in lengths:
                lengths[l] = []
            lengths[l].append(path)

        minKey = 100000000
        for key in lengths.keys():
            if key < minKey:
                minKey = key

        return lengths[minKey]


    def print_shortest_paths(self):
        for i in range(self.max_key + 1):
            for j in range(self.max_key + 1):
                paths = self._get_paths_pair(i, j)
                if len(paths) > 0:
                    print(f"shortest from {i} to {j} is {paths}")

    @abstractmethod
    def get_paths(self, s: List[str]):
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

    def get_paths(self, inputs: List[str]):
        all_paths = []
        for s in inputs:
            paths = [""]
            start = self.A
            for i in range(len(s)):
                if s[i] == 'A':
                    end = self.A
                else:
                    end = ord(s[i]) - ord('0')

                # print(f"n: going from {start} to {end}, path is {self._get_path_pair(start, end)}")
                possible_paths = self._get_paths_pair(start, end)
                next_paths = []
                for path_so_far in paths:
                    for added_path in possible_paths:
                        next_path = f"{path_so_far}{added_path}"
                        next_paths.append(next_path)

                paths = next_paths
                start = end

            all_paths += paths
        return self._get_shortest_paths(all_paths)

    def get_source_path(self, s: str):
        curr = self.keyToPos[self.A]
        path = ""
        for i in range(len(s)):
            if s[i] == "A":
                which = self.keypad[curr[0]][curr[1]]
                if which != self.A:
                    path += str(which)
                else:
                    path += "A"

                continue

            direction = self.move_dict[s[i]]
            # print(f"direction={direction} for {s[i]}")

            curr[0] += self.dirs[direction][0]
            curr[1] += self.dirs[direction][1]
            # print(f"currently at [{curr[0]}, {curr[1]}] = {self.keypad[curr[0]][curr[1]]}")

        return path

class DirectionalKeypad(Keypad):
    def __init__(self):
        keypad = [
            [-1, 0, 4],
            [3, 2, 1]
        ]
        super().__init__(keypad, 4)
        # self.shortest_paths[3][4] = ">>^"
        # self.shortest_paths[4][3] = "v<<"

    def get_paths(self, inputs: List[str]):
        all_paths = []
        for s in inputs:
            paths = [""]
            start = self.A
            for i in range(len(s)):
                if s[i] == 'A':
                    end = self.A
                else:
                    end = self.move_dict[s[i]]

                # print(f"d: going from {self.move[start]} to {self.move[end]},
                # path is {self._get_path_pair(start, end)}")
                # print(f"n: going from {start} to {end}, path is {self._get_path_pair(start, end)}")
                possible_paths = self._get_paths_pair(start, end)
                next_paths = []
                for path_so_far in paths:
                    for added_path in possible_paths:
                        next_path = f"{path_so_far}{added_path}"
                        next_paths.append(next_path)

                paths = next_paths
                start = end
            all_paths += paths

        return self._get_shortest_paths(all_paths)

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

    def get_complexity(self, s: str, n: int):
        return int(s[:-1]) * n

    def part1(self):
        # self.numeric.print_shortest_paths()
        # print("directional now")
        # self.directional.print_shortest_paths()

        total = 0
        for i in range(len(self.input)):
            p1 = self.numeric.get_paths([self.input[i]])
            # print(f"p1: {p1}, len: {len(p1[0])}")
            p2 = self.directional.get_paths(p1)
            # print(f"p2: {p2}, len: {len(p2[0])}")
            p3 = self.directional.get_paths(p2)
            # print(f"p3: {p3}, len: {len(p3[0])}")
            print(f"{self.input[i]}: {len(p3[0])}")
            total += self.get_complexity(self.input[i], len(p3[0]))

        return total

    def part2(self):
        return -1


def run(args):
    day = Day21(args[1])
    print(day.part1())
    print(day.part2())


if __name__ == "__main__":
    run(sys.argv)
