package com.khaleoc;

import java.util.LinkedList;
import java.util.List;

public class Graph {

    private boolean adjMatrix[][];
    private int numVertices;
    private List<Node> nodes;

    // Initialize the matrix
    public Graph(int numVertices, List<Integer> weights) {
        this.numVertices = numVertices;
        adjMatrix = new boolean[numVertices][numVertices];
        this.nodes = new LinkedList<>();
        for (int i=0; i<weights.size(); i++){
            this.nodes.add(new Node(i, weights.get(i)));
        }
    }

    // Add edges
    public void addEdge(int i, int j) {
        this.nodes.get(i).addEdge(i,j);

        adjMatrix[i][j] = true;
        adjMatrix[j][i] = true;
    }

    // Remove edges
    public void removeEdge(int i, int j) {
        adjMatrix[i][j] = false;
        adjMatrix[j][i] = false;
    }

    public void printInfo(){
        System.out.println("Grafo con " + numVertices + " vertici");
        System.out.println("Totale numero di nodi:" + nodes.size());
        int totEdges = 0;
        int totalWeight = 0;
        for (int i = 0; i< nodes.size(); i++){
            Node currentNode = nodes.get(i);
            System.out.println(currentNode.toString());
            totalWeight += currentNode.weight;
            List<Edge> currenEdgeList = currentNode.getEdgeList();
            for (int j = 0; j < currenEdgeList.size(); j++ ){
                System.out.println(currenEdgeList.get(j).toString());
            }
            totEdges += currenEdgeList.size();
        }
        System.out.println("Edges totali " + totEdges);
        System.out.println("Peso totale " + totalWeight);

    }

    // Print the matrix
    public String printAdjMatrix() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < numVertices; i++) {
            s.append(i + ": ");
            for (boolean j : adjMatrix[i]) {
                s.append((j ? 1 : 0) + " ");
            }
            s.append("\n");
        }
        return s.toString();
    }
}
