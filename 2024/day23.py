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
        self.degree = dict()
        self.starts_with_t = dict()
        self.adj_matrix = dict()
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
                self.degree[node_index] = 1
                self.adj_matrix[edge[0]] = []
                if edge[0][0] == 't':
                    self.starts_with_t[node_index] = True
                else:
                    self.starts_with_t[node_index] = False

                node_index += 1
            else:
                self.degree[self.str_int_map[edge[0]]] += 1

            if edge[1] not in self.str_int_map:
                self.str_int_map[edge[1]] = node_index
                self.int_str_map[node_index] = edge[1]
                self.degree[node_index] = 1
                self.adj_matrix[edge[1]] = []
                if edge[1][0] == 't':
                    self.starts_with_t[node_index] = True
                else:
                    self.starts_with_t[node_index] = False

                node_index += 1
            else:
                self.degree[self.str_int_map[edge[1]]] += 1

            self.adj_matrix[edge[0]].append(edge[1])
            self.adj_matrix[edge[1]].append(edge[0])

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
            visited = {}
            for c1 in cliques:
                visited[c1] = False

            curr_k = index + 2
            for c1 in cliques:
                if visited[c1]:
                    continue
                for c2 in cliques:
                    if visited[c2]:
                        continue
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
                    if self.degree[u] < curr_k or self.degree[v] < curr_k:
                        continue

                    # print(f"vertex: [{u},{v}]")
                    if self.g[u][v]:
                        next_clique = set(c1)
                        next_clique.add(v)
                        can_clique_grow = True
                        for elem in next_clique:
                            if self.degree[elem] < (index + 2):
                                can_clique_grow = False
                                break
                                # there is a problem
                        if can_clique_grow:
                            self.k_cliques[index].add(frozenset(next_clique))
                            # remove cliques that will not be visited

                            outer_vertices = {u, v}
                            common_elems = set(c1.intersection(c2))
                            for elem in common_elems:
                                common_elems_copy = set(common_elems)
                                common_elems_copy.remove(elem)
                                not_needed_clique = frozenset(outer_vertices.union(common_elems_copy))
                                # cliques = cliques.remove(not_needed_clique)
                                visited[not_needed_clique] = True


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

    def print_degrees(self):
        degree_counts = [0 for i in range(self.n)]
        for node in range(self.n):
            for j in range(self.degree[node]):
                degree_counts[j] += 1

        for i in range(self.n):
            print(f"degree {i+1}: nodes: {degree_counts[i]}")

        for i in range(self.n):
            print(self.degree[i])

    def print_adj_matrix(self):
        for key in self.adj_matrix.keys():
            print(f"vertex {key}, adj: {self.adj_matrix[key]}")

    def part2(self):
        # self.print_adj_matrix()
        # self.print_degrees()
        # all nodes are of degree 13. So max clique is possibly 14 or lower.

        k = 4
        print(f"nodes = {self.n}")
        print(f"2-cliques: {len(self.k_cliques[2-2])}")
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
