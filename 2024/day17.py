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
        self.instructions2 = self.instructions[:-2]
        # print(f"{self.a+1}, {self.b+1}, {self.c+1}")
        # print(f"{self.instructions}")

    def _parseRegister(self, s: str):
        return int(s.split(':')[1])

    def _parseInstructions(self, s: str):
        arr = s.split(':')[1].split(",")
        return list(map(lambda x: int(x), arr))

    def _combo(self, operand: int, a: int, b: int, c: int):
        if operand < 4 or operand == 7:
            return operand
        elif operand == 4:
            return a
        elif operand == 5:
            return b
        elif operand == 6:
            return c
        else:
            print(f"something wrong, seeing operand {operand}")
            return -1

    def part1(self, instr: List[int], a: int, b: int, c: int):
        index = 0
        n = len(instr)
        out = []
        while index < n:
            opcode = instr[index]
            operand = instr[index + 1]
            # print(f"opcode:{opcode}, operand:{operand}, a:{a}, b:{b}, c:{c}")

            if opcode == 0:  # adv
                a = int(a / pow(2, self._combo(operand, a, b, c)))
            elif opcode == 1:  # bxl
                b = b ^ operand
            elif opcode == 2:  # bst
                b = self._combo(operand, a, b, c) % 8
            elif opcode == 3:  # jnz
                if a != 0:
                    index = operand
                    continue
            elif opcode == 4:  # bxc
                b = b ^ c
            elif opcode == 5:  # out
                val = self._combo(operand, a, b, c) % 8
                out.append(val)
            elif opcode == 6:  # bdv
                b = int(a / pow(2, self._combo(operand, a, b, c)))
            elif opcode == 7:  # cdv
                c = int(a / pow(2, self._combo(operand, a, b, c)))
            else:
                print(f"unexpected opcode: {opcode}")
            index += 2

        return out

    def part2(self):
        # find the number of outputs
        fives = 0
        n = len(self.instructions)
        for i in range(0, n, 2):
            if self.instructions[i] == 5:
                fives += 1

        if self.instructions[n-2] == 3 and self.instructions[n-1] == 0:
            repeats = int(n / fives)
            new_instr = self.instructions[:-2]
            # note that the starting value of a is enough to determine the values of b and c for
            # each loop
            return self.find_lowest_a(0, new_instr, repeats, fives)

        return -1

    def find_lowest_a(self, a: int, instr: List[int], repeats: int, fives: int):
        return self._find_lowest_a_helper(a, instr, repeats, repeats)

    def _find_lowest_a_helper(self, a: int, instr: List[int], output_index: int, output_size: int):
        # stopping condition
        if output_index < 0:
            print("should never happen")
            return -1

        # first check that if we start with {a}, then will the output be as intended
        if output_index < output_size:
            output_arr = self.part1(instr, a, 0, 0)
            if output_arr[0] != self.instructions[output_index]:
                return -1

        # stopping condition
        if output_index == 0:
            # print(f"found {a}")
            return a

        # now try 8 different values of a
        minA = -1
        for i in range(8):
            next_a = a * 8 + i
            if next_a == 0:
                continue
            result = self._find_lowest_a_helper(next_a, instr, output_index - 1, output_size)
            if result > 0 and (minA == -1 or minA > result):
                minA = result

        return minA

def run(args):
    day = Day17(args[1])
    output_arr = day.part1(day.instructions, day.a, day.b, day.c)
    print(','.join(map(str, output_arr)))
    # print(f"a={day.a},b={day.b},c={day.c}")
    correct_a = day.part2()
    output_arr = day.part1(day.instructions, correct_a, 0, 0)
    print(f"part2: {correct_a}")
    print(','.join(map(str, output_arr)))


if __name__ == "__main__":
    run(sys.argv)
