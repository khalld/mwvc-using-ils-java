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

    public void addVertex(Main2.Vertex v){

        boolean isAlreadyPresent = this.getSelectedVertex().contains(v);

        if (isAlreadyPresent == false){
            this.selectedVertex.add(v);
            this.calcReachedEdges();
        } // else throw new RuntimeException("Cannot add a node that is already in selected nodes!");

    }

    public void removeVertex(Main2.Vertex v){
        boolean isAlreadyPresent = this.selectedVertex.contains(v);

        if (isAlreadyPresent == true){
            this.selectedVertex.remove(v);
            this.calcReachedEdges();
        } // else throw new RuntimeException("Cannot remove a node that is not in selected nodes!");

    }

    public void calcReachedEdges(){
        this.selectedEdges = new ArrayList<>();
        this.cost = 0;

        for (int i = 0; i<this.selectedVertex.size(); i++){
            this.cost+= selectedVertex.get(i).getWeight();
            List<Edge> currentEdgeList = this.selectedVertex.get(i).getAdjList();
            int currEdgeListSize = currentEdgeList.size();
            for (int j = 0; j < currEdgeListSize; j++){
                if(! this.selectedEdges.contains(currentEdgeList.get(j))) {
                    this.selectedEdges.add(currentEdgeList.get(j));
                }
            }
        }
    }
}
