import json
import logging
import sys
import time
import os
from typing import List

class Day24:
    def __init__(self, filename: str):
        equations = False
        self.variables_input = []
        self.equations_input = []
        with open(filename, 'r') as f:
            for line_raw in f:
                line = line_raw.rstrip("\n")
                if len(line) == 0:
                    equations = True
                    continue

                if not equations:
                    self.variables_input.append(line)
                else:
                    self.equations_input.append(line)

        self.variables = dict()
        self.equations = dict()
        self.z_variables = []

        self.parse_variables()
        self.parse_equations()

    def parse_variables(self):
        for v_str in self.variables_input:
            v = v_str.split(":")
            self.variables[v[0]] = int(v[1])

            if v[0][0] == "z":
                self.z_variables.append(v[0])

    def parse_equations(self):
        for e_str in self.equations_input:
            e = e_str.split(" ")
            # e[0], e[1], e[2], e[4]
            self.equations[e[4]] = (e[0], e[2], e[1])
            if e[4][0] == "z":
                self.z_variables.append(e[4])

    def dfs_topological(self, v: str):
        if v in self.variables:
            return

        if v not in self.equations:
            print(f"cannot find how to compute {v}, invalid input, return")
            return

        e = self.equations[v]
        self.dfs_topological(e[0])
        self.dfs_topological(e[1])

        if e[2] == "AND":
            self.variables[v] = self.variables[e[0]] and self.variables[e[1]]
        elif e[2] == "XOR":
            self.variables[v] = self.variables[e[0]] ^ self.variables[e[1]]
        elif e[2] == "OR":
            self.variables[v] = self.variables[e[0]] or self.variables[e[1]]
        else:
            print(f"error in computing equation for {v}, {e}")
            return

    def binary_compute(self):
        ans = 0
        z_vars = sorted(self.z_variables)
        for v in z_vars:
            bit = int(v[1:])
            # print(f"{v}: {self.variables[v]}, bit: {bit}")
            ans = ans + (self.variables[v] << bit)
        return ans

    def part1(self):
        # topological sort
        for z in self.z_variables:
            self.dfs_topological(z)

        return self.binary_compute()

    def part2(self):
        return -1


def run(args):
    day = Day24(args[1])
    print(day.part1())
    print(day.part2())


if __name__ == "__main__":
    run(sys.argv)
