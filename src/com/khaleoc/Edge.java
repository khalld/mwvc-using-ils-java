package com.khaleoc;

public class Edge {
    int source;
    int dest;

    public Edge(int source, int dest) {
        this.source = source;
        this.dest = dest;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "source=" + source +
                ", dest=" + dest +
                '}';
    }
}