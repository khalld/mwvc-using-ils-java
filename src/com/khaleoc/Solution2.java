package com.khaleoc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


public class Solution2 {
    public List<Main2.Vertex> selectedVertex;
    public List<Edge> selectedEdges;
    int cost;

    public Solution2(List<Main2.Vertex> selectedVertex, List<Edge> selectedEdges, int cost) {
        this.selectedVertex = selectedVertex;
        this.selectedEdges = selectedEdges;
        this.cost = cost;
    }

    public List<Main2.Vertex> getSelectedVertex() {
        return selectedVertex;
    }

    public void setSelectedVertex(List<Main2.Vertex> selectedVertex) {
        this.selectedVertex = selectedVertex;
    }

    public List<Edge> getSelectedEdges() {
        return selectedEdges;
    }

    public void setSelectedEdges(List<Edge> selectedEdges) {
        this.selectedEdges = selectedEdges;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
