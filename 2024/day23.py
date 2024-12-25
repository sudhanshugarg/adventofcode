import json
import logging
import sys
import time
import os
from typing import List

class Day23:
    def __init__(self, filename: str):
        with open(filename, 'r') as f:
            self.input = f.readlines()

        self.k_cliques: List[set] = list()
        self.g = None
        self.str_int_map = dict()
        self.int_str_map = dict()
        self.starts_with_t = dict()
        self.n = None

        self.build_graph()
        # print(self.str_int_map)
        # print(self.int_str_map)

    def build_graph(self):
        edge_number = len(self.input)
        node_index = 0
        for i in range(edge_number):
            edge = self.input[i].rstrip("\n").split("-")
            if edge[0] not in self.str_int_map:
                self.str_int_map[edge[0]] = node_index
                self.int_str_map[node_index] = edge[0]
                if edge[0][0] == 't':
                    self.starts_with_t[node_index] = True
                else:
                    self.starts_with_t[node_index] = False

                node_index += 1

            if edge[1] not in self.str_int_map:
                self.str_int_map[edge[1]] = node_index
                self.int_str_map[node_index] = edge[1]
                if edge[1][0] == 't':
                    self.starts_with_t[node_index] = True
                else:
                    self.starts_with_t[node_index] = False

                node_index += 1

        self.n = node_index
        self.g = [[False for j in range(self.n)] for i in range(self.n)]

        edges = set()
        for i in range(edge_number):
            edge = self.input[i].rstrip("\n").split("-")
            u = self.str_int_map[edge[0]]
            v = self.str_int_map[edge[1]]
            self.g[u][v] = True
            self.g[v][u] = True

            edge_set = frozenset({u, v})
            edges.add(edge_set)

        self.k_cliques.append(edges)

    #  self.k_cliques starts 2 index behind.
    #  i.e. self.k_cliques[0] is cliques of size 2 (edges)
    #  i.e. self.k_cliques[1] is cliques of size 3 (edges)
    def build_k_clique(self, k: int):
        if len(self.k_cliques) >= (k-1):  # len k-1 means cliques of size k already present
            return

        start = len(self.k_cliques)

        for index in range(start, k - 1):
            self.k_cliques.append(set())
            cliques = self.k_cliques[index - 1]
            # print(index)
            # print(cliques)
            # go through every pair of cliques

            for c1 in cliques:
                for c2 in cliques:
                    # print(f"comparing: {c1}, {c2}")
                    d1 = c1.difference(c2)
                    if len(d1) != 1:
                        continue
                    d2 = c2.difference(c1)
                    if len(d2) != 1:
                        continue

                    # print(f"found: {d1}, {d2}")
                    u = next(iter(d1))
                    v = next(iter(d2))
                    # print(f"vertex: [{u},{v}]")
                    if self.g[u][v]:
                        next_clique = set(c1)
                        next_clique.add(v)
                        self.k_cliques[index].add(frozenset(next_clique))

    def print_and_count_cliques(self, k: int, should_print: bool = False):
        starting_with_t = 0
        cliques = self.k_cliques[k - 2]  # set
        all_cliques = []
        for clique in cliques:  # frozenset
            found = False
            c = []
            for elem in clique:  # element of frozenset
                c.append(self.int_str_map[elem])
                if not found and self.starts_with_t[elem]:
                    starting_with_t += 1
                    found = True
            all_cliques.append(c)

        if should_print:
            print(all_cliques)

        return starting_with_t, all_cliques

    def part1(self):
        k = 3
        self.build_k_clique(k)
        starting_with_t, all_cliques = self.print_and_count_cliques(k)
        return starting_with_t

    def part2(self):
        k = 4
        print(f"3-cliques: {len(self.k_cliques[3-2])}")
        while True:
            self.build_k_clique(k)
            print(f"{k}-cliques: {len(self.k_cliques[k-2])}")
            if len(self.k_cliques[k-2]) == 0:
                break
            k += 1

        starting_with_t, all_cliques = self.print_and_count_cliques(k - 1, True)
        return ','.join(sorted(all_cliques[0]))


def run(args):
    day = Day23(args[1])
    print(day.part1())
    print(day.part2())


if __name__ == "__main__":
    run(sys.argv)
