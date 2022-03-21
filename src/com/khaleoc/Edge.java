package com.khaleoc;

import java.util.Objects;

public class Edge {
    int source;
    int dest;

    public Edge(int source, int dest) {
        this.source = source;
        this.dest = dest;
    }

    public int getSource() {
        return source;
    }

    public int getDest() {
        return dest;
    }


    @Override
    public String toString() {
        return "Edge{" +
                "source=" + source +
                ", dest=" + dest +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return source == edge.source && dest == edge.dest;
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, dest);
    }
}