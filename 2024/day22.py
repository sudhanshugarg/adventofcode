import json
import logging
import sys
import time
import os
from typing import List

class Day22:
    def __init__(self, filename: str):
        with open(filename, 'r') as f:
            self.input = f.readlines()

    def part1(self):
        total = 0
        nth = 2000
        for i in self.input:
            nth_secret = self.find_nth_secret(int(i), nth)
            print(f"{int(i)}: {nth_secret}")
            total += nth_secret
        return total

    def find_nth_secret(self, secret: int, n: int):
        for i in range(n):
            secret = self.find_next_secret(secret)
        return secret

    def mix_and_prune(self, tmp: int, secret: int):
        # mix
        tmp ^= secret
        # prune
        return tmp % 16777216

    def find_next_secret(self, secret: int):
        # step 1
        secret = self.mix_and_prune(secret * 64, secret)
        # step 2
        secret = self.mix_and_prune(secret >> 5, secret)
        # step 3
        secret = self.mix_and_prune(secret * 2048, secret)
        return secret

    def part2(self):
        return -1


def run(args):
    day = Day22(args[1])
    print(day.part1())
    print(day.part2())


if __name__ == "__main__":
    run(sys.argv)
