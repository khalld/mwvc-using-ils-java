package com.khaleoc;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Node {
    int id;
    int weight;
    List<Edge> edgeList;

    public Node(int id, int weight) {
        this.id = id;
        this.weight = weight;
        this.edgeList = new LinkedList<>();
    }

    public void addEdge(int source, int dest){
        this.edgeList.add(new Edge(source, dest));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public List<Edge> getEdgeList() {
        return edgeList;
    }

    public void setEdgeList(List<Edge> edgeList) {
        this.edgeList = edgeList;
    }

    public int getEdgeSize(List<Edge> edgeList){
        return edgeList.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id == node.id && weight == node.weight && Objects.equals(edgeList, node.edgeList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, weight, edgeList);
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", weight=" + weight +
                ", edgeList=" + edgeList +
                '}';
    }
}