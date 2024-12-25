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
        self.node_int_map = dict()
        self.n = None

        self.build_graph()

    def build_graph(self):
        edge_number = len(self.input)
        node_index = 0
        for i in range(edge_number):
            edge = self.input[i].rstrip("\n").split("-")
            if edge[0] not in self.node_int_map:
                self.node_int_map[edge[0]] = node_index
                node_index += 1
            if edge[1] not in self.node_int_map:
                self.node_int_map[edge[1]] = node_index
                node_index += 1

        self.n = node_index
        g = [[False for j in range(self.n)] for i in range(self.n)]

        edges = set()
        for i in range(edge_number):
            edge = self.input[i].rstrip("\n").split("-")
            u = self.node_int_map[edge[0]]
            v = self.node_int_map[edge[1]]
            g[u][v] = True
            g[v][u] = True

            edge_set = {u, v}
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
            # go through every pair of cliques

            for c1 in cliques:
                for c2 in cliques:
                    d1 = c1.difference(c2)
                    if len(d1) != 1:
                        continue
                    d2 = c2.difference(c1)
                    if len(d2) != 1:
                        continue

                    u = d1.pop()
                    v = d2.pop()
                    if self.g[u][v]:
                        next_clique = c1.copy()
                        next_clique.add(d2)
                        self.k_cliques[index].add(next_clique)

    def part1(self):
        self.build_k_clique(3)
        cliques = self.k_cliques[1]
        print(cliques)
        return len(cliques)

    def part2(self):
        return -1


def run(args):
    day = Day23(args[1])
    print(day.part1())
    print(day.part2())


if __name__ == "__main__":
    run(sys.argv)
