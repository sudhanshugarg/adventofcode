import json
import logging
import sys
import time
import os
from typing import List

class Day17:
    def __init__(self, filename: str):
        with open(filename, 'r') as f:
            self.input = f.readlines()

        self.a = self._parseRegister(self.input[0])
        self.b = self._parseRegister(self.input[1])
        self.c = self._parseRegister(self.input[2])
        self.instructions = self._parseInstructions(self.input[4])
        # print(f"{self.a+1}, {self.b+1}, {self.c+1}")
        # print(f"{self.instructions}")

    def _parseRegister(self, s: str):
        return int(s.split(':')[1])

    def _parseInstructions(self, s: str):
        arr = s.split(':')[1].split(",")
        return list(map(lambda x: int(x), arr))

    def _combo(self, operand: int):
        if operand < 4:
            return operand
        elif operand == 4:
            return self.a
        elif operand == 5:
            return self.b
        elif operand == 6:
            return self.c
        else:
            print(f"something wrong, seeing operand {operand}")
            return -1

    def part1(self):
        index = 0
        n = len(self.instructions)
        out = ""
        while index < n:
            opcode = self.instructions[index]
            operand = self.instructions[index + 1]
            # print(f"opcode:{opcode}, operand:{operand}, a:{self.a}, b:{self.b}, c:{self.c}")

            if opcode == 0:  # adv
                self.a = int(self.a / pow(2, self._combo(operand)))
            elif opcode == 1:  # bxl
                self.b = self.b ^ operand
            elif opcode == 2:  # bst
                self.b = self._combo(operand) % 8
            elif opcode == 3:  # jnz
                if self.a != 0:
                    index = operand
                    continue
            elif opcode == 4:  # bxc
                self.b = self.b ^ self.c
            elif opcode == 5:  # out
                val = self._combo(operand) % 8
                out += f"{val},"
            elif opcode == 6:  # bdv
                self.b = int(self.a / pow(2, self._combo(operand)))
            elif opcode == 7:  # cdv
                self.c = int(self.a / pow(2, self._combo(operand)))
            else:
                print(f"unexpected opcode: {opcode}")
            index += 2

        return out.rstrip(',')

    def part2(self):
        return -1


def run(args):
    day = Day17(args[1])
    print(day.part1())
    print(f"a={day.a},b={day.b},c={day.c}")
    print(day.part2())


if __name__ == "__main__":
    run(sys.argv)
