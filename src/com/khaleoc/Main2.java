package com.khaleoc;

import com.github.sh0nk.matplotlib4j.PythonExecutionException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main2 {

    static class Vertex{
        int id;
        int weight;
        boolean explored;

        public Vertex(int id, int weight) {
            this.id = id;
            this.weight = weight;
            this.explored = false;
        }

        public boolean isExplored() {
            return explored;
        }

        public void setExplored(boolean explored) {
            this.explored = explored;
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

    static boolean checkValidity(ArrayList<Edge> allEdges, ArrayList<Edge> selected, List<Node> allNd){

        ArrayList<Edge> allEdgesFounded = new ArrayList<>();
        for (int i = 0; i < selected.size(); i++){
            Edge currentEdge = selected.get(i);
            Node from = allNd.get(currentEdge.getSource());

            for (int j = 0; j < from.getEdgeList().size(); j++){
                if(!allEdgesFounded.contains(from.getEdgeList().get(j))){
                    allEdgesFounded.add(from.getEdgeList().get(j));
                }
            }

            Node dest = allNd.get(currentEdge.getDest());

            for (int j = 0; j < dest.getEdgeList().size(); j++){
                if(!allEdgesFounded.contains(dest.getEdgeList().get(j))){
                    allEdgesFounded.add(dest.getEdgeList().get(j));
                }
            }

        }

        ArrayList<Edge> notFounded = new ArrayList<>();
        for (Edge e : allEdges) {
            if (!allEdgesFounded.contains(e)) {
                notFounded.add(e);
            }
        }

        // TODO: Fixa per LPI
//        if (notFounded.size() > 0 ){
//            ArrayList<Node> undiscovered = new ArrayList<>();
//            for (int i = 0; i < notFounded.size(); i++){
//
//            }
//        }

        if (notFounded.size() > 0){
            return false;
        }

        return true;
    }

    public static void findMinimumWeightedVertexCoverApprox(ArrayList<Edge> graph, int[] weights, List<Node> allNd) throws Exception {

        ArrayList<Vertex> allVertices = new ArrayList<>();
        for(int i=0; i<weights.length; i++){
            allVertices.add(new Vertex(i, weights[i]));
        }

        int[] remainingWeights = Arrays.copyOf(weights, weights.length);

        ArrayList<Vertex> selectedVertex = new ArrayList<>();
        ArrayList<Edge> selectedEges = new ArrayList<>();
        int totalWeight = 0;

        for(Edge edge : graph){
            int fromVertex = edge.source;
            int toVertex = edge.dest;
            if(remainingWeights[fromVertex]==0 || remainingWeights[toVertex]==0){	// skippa gli edge se sono stati già selezionati
                continue;
            }

            if(remainingWeights[fromVertex] < remainingWeights[toVertex]){
                int smallerWeight = remainingWeights[fromVertex];
                remainingWeights[fromVertex] = 0;	// 1 il vertice è già stato esplorato
                remainingWeights[toVertex] -= smallerWeight;
                totalWeight += weights[fromVertex];
                selectedVertex.add(allVertices.get(fromVertex));
                selectedEges.add(edge);
            }
            else{
                int smallerWeight = remainingWeights[toVertex];
                remainingWeights[toVertex] = 0;		// vertice già esplorato
                remainingWeights[fromVertex] -= smallerWeight;
                totalWeight += weights[toVertex];
                selectedVertex.add(allVertices.get(toVertex));
                selectedEges.add(edge);
            }
        }

        boolean validity = checkValidity(graph, selectedEges, allNd);

        if (validity == false){
            throw new Exception("Cannot return a solution not valid!");
        }

        System.out.println("Total Weight: "+totalWeight);
    }

    public static Graph getInstance(String instancePath) throws IOException{
        // The format of all files is as follows: the first line contains the
        // number of nodes of the graph, the second line contains the node
        // weights, and the remaining lines contain the incidence matrix
        List<String> allfile;

        try (BufferedReader br = new BufferedReader(new FileReader(instancePath))) {
            allfile = br.lines().collect(Collectors.toList());
        }
        Integer numberOfNodes = Integer.parseInt(allfile.get(0));
        Scanner scanner = new Scanner(allfile.get(1));
        List<Integer> weights = new ArrayList<Integer>();
        while (scanner.hasNextInt()) {
            weights.add(scanner.nextInt());
        }

        List<List<Integer>> adjMatrix = new ArrayList<>();
        for (int k = 2; k < allfile.size(); k++){
            scanner = new Scanner(allfile.get(k));
            List<Integer> tempVector = new ArrayList<>();
            while (scanner.hasNextInt()) {
                tempVector.add(scanner.nextInt());
            }
            adjMatrix.add(tempVector);
        }

        scanner.close();

        Graph currentGraph = new Graph(numberOfNodes, weights);
        for (int i = 0; i < adjMatrix.size(); i++){
            for (int j = 0; j < adjMatrix.get(i).size(); j++){
                if(adjMatrix.get(i).get(j) == 1){
//                  Evito di controllare manualmente di edge
                    if (i < j){
                        currentGraph.addEdge(i,j);
                    }
                }
            }
        }

        return currentGraph;
    }

    public static void main(String[] args) throws Exception {
        Graph instGraph = getInstance("wvcp-instances/vc_20_60_01.txt");

        final List<Node> allNd = instGraph.getNodes();
        int graphDim = allNd.size();
        final ArrayList<Edge> allEdgesOfGraph = new ArrayList<>();
        for (int i = 0; i<graphDim; i++){
            List<Edge> currentEdgeList = allNd.get(i).getEdgeList();
            int currEdgeListSize = currentEdgeList.size();
            for (int j = 0; j < currEdgeListSize; j++){
                    allEdgesOfGraph.add(currentEdgeList.get(j));
            }
        }

        int[] weights = new int[graphDim];

        for (int i=0; i< graphDim; i++){
            weights[i] = allNd.get(i).getWeight();
        }

        findMinimumWeightedVertexCoverApprox(allEdgesOfGraph, weights, allNd);

        System.out.println("AAAA");
    }

}