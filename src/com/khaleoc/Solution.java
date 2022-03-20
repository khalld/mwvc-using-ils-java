package com.khaleoc;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public List<Node> all_nodes;
    public List<Edge> all_edges;

    public List<Node> sel_nodes;
    public List<Edge> reached_edges;

    public int total_cost;
    public boolean isComplete;
    public String instanceName;

    public Solution(String instanceName, List<Node> all_nodes) {
        String[] completePath = instanceName.split("/");
        String instanceFile = completePath[3].replace(".txt", "");
        this.instanceName = instanceFile;
        this.all_nodes = all_nodes;
        // TODO:
        this.all_edges = all_edges;
        this.sel_nodes = new ArrayList<>();
        this.reached_edges = new ArrayList<>();
        this.total_cost = 0;
        this.isComplete = false;
    }

    public void checkComplete(){
    //  FIXME:
        this.isComplete = true;
    }

    public void make_xml(){
        // TODO:
    }

    public void make_graph(){
        // TODO:
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }
}
