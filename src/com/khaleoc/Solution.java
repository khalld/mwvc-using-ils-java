package com.khaleoc;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public String instanceName;
    public List<Vertex> selectedVertex;
    public ArrayList<Edge> selectedEdges;
    int cost;
    int iteration;

    public Solution(String instanceName, List<Vertex> selectedVertex, ArrayList<Edge> selectedEdges, int cost) {
        this.instanceName = instanceName.replace(".txt", "");
        this.selectedVertex = selectedVertex;
        this.selectedEdges = selectedEdges;
        this.cost = cost;
        this.iteration = 0;
    }

    public List<Vertex> getSelectedVertex() {
        return selectedVertex;
    }

    public void setSelectedVertex(List<Vertex> selectedVertex) {
        this.selectedVertex = selectedVertex;
    }

    public ArrayList<Edge> getSelectedEdges() {
        return selectedEdges;
    }

    public int getCost() {
        return cost;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void addVertex(Vertex v){

        boolean isAlreadyPresent = this.getSelectedVertex().contains(v);

        if (isAlreadyPresent == false){
            this.selectedVertex.add(v);
            this.calcReachedEdges();
        } // else throw new RuntimeException("Cannot add a node that is already in selected nodes!");

    }

    public void removeVertex(Vertex v){
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

    public int getIteration() {
        return iteration;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }
}
