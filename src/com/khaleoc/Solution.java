package com.khaleoc;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Solution {
    public List<Edge> allEdges;

    public List<Node> selNodes;
    public List<Edge> reachedEdges;

    public int totalCost;
    public boolean isComplete;
    public String instanceName;

    public Solution(String instanceName, List<Edge> allEdges) {
        String[] completePath = instanceName.split("/");
        String instanceFile = completePath[1].replace(".txt", "");
        this.instanceName = instanceFile;

        this.allEdges = allEdges;

        this.selNodes = new ArrayList<>();
        this.reachedEdges = new ArrayList<>();
        this.totalCost = 0;
        this.isComplete = false;
    }

    public void calcReachedEdges(){
        this.reachedEdges = new ArrayList<>();
        int selNodesSize = this.selNodes.size();

        for (int i = 0; i<selNodesSize; i++){
            List<Edge> currentEdgeList = this.selNodes.get(i).getEdgeList();
            int currEdgeListSize = currentEdgeList.size();
            for (int j = 0; j < currEdgeListSize; j++){
                if(! this.reachedEdges.contains(currentEdgeList.get(j))) {
                    this.reachedEdges.add(currentEdgeList.get(j));
                }
            }
        }
    }

    public void addNode(Node n){

        boolean isAlreadyPresent = this.selNodes.contains(n);

        if (isAlreadyPresent == false){
            this.selNodes.add(n);
            this.calcReachedEdges();
            this.updateCost();
            this.checkValidity();
        } // else throw new RuntimeException("Cannot add a node that is already in selected nodes!");

    }

    public void removeNode(Node n){
        boolean isAlreadyPresent = this.selNodes.contains(n);

        if (isAlreadyPresent == true){
            this.selNodes.remove(n);
            this.calcReachedEdges();
            this.updateCost();
            this.checkValidity();
        } // else throw new RuntimeException("Cannot remove a node that is not in selected nodes!");

    }

    public void updateCost(){
        this.totalCost = 0;
        int selNodesSize = selNodes.size();
        for (int i=0; i < selNodesSize; i++){
            this.totalCost+= selNodes.get(i).weight;
        }
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void checkValidity(){
        this.reachedEdges.sort(Comparator.comparing(Edge::getSource));
        this.isComplete = this.reachedEdges.equals(this.allEdges);
    }

    public boolean isComplete() {
        return isComplete;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public List<Node> getSelNodes() {
        return selNodes;
    }

}
