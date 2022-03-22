package com.khaleoc;

import java.util.List;
import java.util.Objects;

public class Vertex{
    int id;
    int weight;
    boolean explored;
    List<Edge> adjList;

    public Vertex(int id, int weight, List<Edge> adjList) {
        this.id = id;
        this.weight = weight;
        this.explored = false;
        this.adjList = adjList;
    }

    public boolean isExplored() {
        return explored;
    }

    public void setExplored(boolean explored) {
        this.explored = explored;
    }

    public int getId() {
        return id;
    }

    public int getWeight() {
        return weight;
    }

    public List<Edge> getAdjList() {
        return adjList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return id == vertex.id && weight == vertex.weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, weight);
    }
}

