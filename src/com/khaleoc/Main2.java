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

        public Vertex(int id, int weight) {
            this.id = id;
            this.weight = weight;
        }
    }

    static class SimpleSolution{
        ArrayList<String> selectedVertex;
        int totalWeight;

        public SimpleSolution(ArrayList<String> selectedVertex, int totalWeight) {
            this.selectedVertex = selectedVertex;
            this.totalWeight = totalWeight;
        }

    }

    public static void findMinimumWeightedVertexCoverApprox(List<Edge> graph, List<Node> allNodes, int[] weights){
        //Build String array of integer vertex names if no string names are provided
        String[] integerNames = new String[weights.length];
        List<Vertex> vertices = new ArrayList<>();
        for(int i=0; i<weights.length; i++){
            vertices.add(new Vertex(i, weights[i]));
            integerNames[i] = i+"";
        }
        findMinimumWeightedVertexCoverApprox(graph, weights, allNodes, integerNames);
    }

    public static SimpleSolution findMinimumWeightedVertexCoverApprox(List<Edge> graph, int[] weights, List<Node> allNodes,  String[] vertexNames){
        int[] remainingWeights = Arrays.copyOf(weights, weights.length);

        ArrayList<String> vertexCoverNodes = new ArrayList<String>();
        List<Edge> selectedEges = new ArrayList<>();
        List<Node> selectedNodes = new ArrayList<>();
        int totalWeight = 0;

        for(Edge edge : graph){
            int fromVertex = edge.source;
            int toVertex = edge.dest;
            if(remainingWeights[fromVertex]==0 || remainingWeights[toVertex]==0){		//skip edges if either vertex is already tight
                continue;
            }

            if(remainingWeights[fromVertex] < remainingWeights[toVertex]){		//fromVertex weight is smaller
                int smallerWeight = remainingWeights[fromVertex];
                remainingWeights[fromVertex] = 0;	//1 vertex becomes tight (greedy)
                remainingWeights[toVertex] -= smallerWeight;
                totalWeight += weights[fromVertex];
                vertexCoverNodes.add(vertexNames[fromVertex]);
                selectedNodes.add(allNodes.get(fromVertex));
                selectedEges.add(edge);
            }
            else{		//toVertex weight is smaller or they're equal
                int smallerWeight = remainingWeights[toVertex];
                remainingWeights[toVertex] = 0;		//1 vertex becomes tight (greedy)
                remainingWeights[fromVertex] -= smallerWeight;
                totalWeight += weights[toVertex];
                vertexCoverNodes.add(vertexNames[toVertex]);
                selectedNodes.add(allNodes.get(fromVertex));
                selectedEges.add(edge);

            }
//            System.out.println("Chose Edge "+edge);
        }

        SimpleSolution sol = new SimpleSolution(graph, vertexCoverNodes, totalWeight);

        return sol;
//        System.out.println("\nVertex Cover: "+vertexCoverNodes);
//        System.out.println("Total Weight: "+totalWeight);
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
//                    if (i < j){
                        currentGraph.addEdge(i,j);
//                    }
                }
            }
        }

        return currentGraph;
    }


    public static void main(String[] args) throws IOException, PythonExecutionException {
        Graph instGraph = getInstance("wvcp-instances/vc_20_60_01.txt");


        final List<Node> allNd = instGraph.getNodes();
        int graphDim = allNd.size();
        final List<Edge> allEdgesOfGraph = new ArrayList<>();
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

        findMinimumWeightedVertexCoverApprox(allEdgesOfGraph, allNd, weights);



        System.out.println("AAAA");
    }

}