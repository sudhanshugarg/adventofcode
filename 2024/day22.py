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
        nth = 10
        for i in self.input:
            nth_secret = self.find_nth_secret(int(i), nth)
            # print(f"{int(i)}: {nth_secret}")
            total += nth_secret
        return total

    def find_nth_secret(self, secret: int, n: int):
        for i in range(n):
            secret = self.find_next_secret(secret)
            # print(secret)
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

    def get_first_sequence(self, secret: int):
        seq = []
        for i in range(4):
            next_secret = self.find_next_secret(secret)
            seq.append((next_secret % 10) - (secret % 10))
            secret = next_secret

        return tuple(seq), secret

    def part2(self):
        # keep two dicts.
        # one for overall sum
        # one for per buyer sum
        # after all buyers are done, we find the
        # sequence for which all buyers are maximized

        overall_quad_sequence = dict()
        changes = 2000
        for i in self.input:
            secret = int(i)
            current_quad_sequence = dict()
            curr_sequence_tuple, secret = self.get_first_sequence(secret)
            # print(f"{curr_sequence_tuple}: {secret % 10}")

            current_quad_sequence[curr_sequence_tuple] = secret % 10
            if curr_sequence_tuple not in overall_quad_sequence:
                overall_quad_sequence[curr_sequence_tuple] = secret % 10
            else:
                overall_quad_sequence[curr_sequence_tuple] += secret % 10

            for j in range(changes):
                next_secret = self.find_next_secret(secret)
                curr_sequence_tuple = (curr_sequence_tuple[1], curr_sequence_tuple[2],
                                       curr_sequence_tuple[3], (next_secret % 10) - (secret % 10))
                secret = next_secret
                # print(f"{curr_sequence_tuple}: {secret % 10}")
                if curr_sequence_tuple not in current_quad_sequence:
                    current_quad_sequence[curr_sequence_tuple] = secret % 10
                    if curr_sequence_tuple not in overall_quad_sequence:
                        overall_quad_sequence[curr_sequence_tuple] = secret % 10
                    else:
                        overall_quad_sequence[curr_sequence_tuple] += secret % 10

        # all elements done
        maxValue = -1
        maxKey = 0
        for key in overall_quad_sequence.keys():
            if overall_quad_sequence[key] > maxValue:
                maxKey = key
                maxValue = overall_quad_sequence[key]

        print(f"key: {maxKey}, value: {maxValue}")
        return maxValue


def run(args):
    day = Day22(args[1])
    print(day.part1())
    print(day.part2())


if __name__ == "__main__":
    run(sys.argv)
