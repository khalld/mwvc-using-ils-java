package com.khaleoc;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main2 {
    public static final int MAX_EVALS = 20000;
    public static final String BENCHMARK_FOLDER = "bench-new/";
    public static final String CONV_GRAPH_FOLDER = "bench-new/conv_g/";

    static class Vertex{
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

    static ArrayList<Edge> getNotFoundedEdge(ArrayList<Edge> allEdges, ArrayList<Edge> selected, List<Node> allNd){
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

        return notFounded;
    }


    // FIXME:
    static boolean checkValidity(ArrayList<Edge> allEdges, ArrayList<Edge> selected, List<Node> allNd){
        ArrayList<Edge> notFounded = getNotFoundedEdge(allEdges, selected, allNd);

//        while (notFounded.size() == 0 ){
            // TODO: trova tra i nodi non selezionati quello mancante ed aggiungi quello con minore costo
//            selected.add(notFounded.get(0));
//            notFounded = getNotFoundedEdge(allEdges, selected, allNd);
//        }

        return true;
    }

    public static Solution2 getInitialSolution(ArrayList<Edge> graph, ArrayList<Vertex> allVertices, List<Node> allNd) throws Exception {

        ArrayList<Vertex> selectedVertex = new ArrayList<>();
        ArrayList<Edge> selectedEges = new ArrayList<>();
        int totalWeight = 0;

        for(Edge edge : graph){
            Vertex fromVertex = allVertices.get(edge.source);
            Vertex toVertex = allVertices.get(edge.dest);

            if(fromVertex.isExplored() == true || toVertex.isExplored() == true){
                continue;
            }

            if(fromVertex.getWeight() < toVertex.getWeight()){
                    fromVertex.setExplored(true);
                    totalWeight += fromVertex.getWeight();
                    selectedVertex.add(fromVertex);
                    selectedEges.add(edge);
                }
                else{
                    toVertex.setExplored(true);
                    totalWeight += toVertex.getWeight();
                    selectedVertex.add(toVertex);
                    selectedEges.add(edge);
                }
            }

        boolean validity = checkValidity(graph, selectedEges, allNd);
//
        if (validity == false){
            throw new Exception("Cannot return a solution not valid!");
        }
//
//        System.out.println("Total Weight: "+totalWeight);

        Solution2 toRet = new Solution2(selectedVertex, selectedEges, totalWeight);

        return toRet;
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

    public static Solution2 weakPerturbation(List<Edge> allEdgesOfGraph, List<Vertex> allNd, Solution2 inputSolution) {
        List<Vertex> alreadySelected = inputSolution.getSelectedVertex();
        List<Vertex> notSelectedNodes = new ArrayList<>(allNd);
        notSelectedNodes.removeAll(alreadySelected);

        int randomIndex = new Random().nextInt(alreadySelected.size());
        Vertex toRem = allNd.get(randomIndex);
        inputSolution.removeVertex(toRem);

        // TODO Checkvalidity

        return inputSolution;
    }

    public static Solution2 acceptanceCriteria(Solution2 prevSol, Solution2 newSol){

        if (prevSol.getCost() > newSol.getCost()){
            return newSol;
        }

        return prevSol;
    }

    public static LocalSearchObj2 localSearch(Solution2 inputSolution, List<Node> allNd){
        int iterator = 1000; // FIXME

        LocalSearchObj2 toReturn = new LocalSearchObj2(inputSolution, iterator);

        return toReturn;
    }

    public static IlsObj IteratedLocalSearch(Graph instanceGraph, String instancePath) throws Exception {
        int currentIter = 1;
        int iterBsToRet = 1;
        long startTime = System.nanoTime();

        final List<Integer> coordY = new ArrayList<>();
        final List<Integer> coordX = new ArrayList<>();

        final List<Node> allNd = instanceGraph.getNodes();

        final ArrayList<Edge> allEdgesOfGraph = new ArrayList<>();
        for (int i = 0; i<allNd.size(); i++){
            List<Edge> currentEdgeList = allNd.get(i).getEdgeList();
            int currEdgeListSize = currentEdgeList.size();
            for (int j = 0; j < currEdgeListSize; j++){
                allEdgesOfGraph.add(currentEdgeList.get(j));
            }
        }

        ArrayList<Vertex> allVertices = new ArrayList<>();

        for (int i=0; i< allNd.size(); i++){
            allVertices.add(new Vertex(i, allNd.get(i).getWeight(), allNd.get(i).getEdgeList()));
        }

        Solution2 currentSol = getInitialSolution(allEdgesOfGraph, allVertices, allNd);
        Solution2 bestSolutionToRet = new Solution2(currentSol.getSelectedVertex(), currentSol.getSelectedEdges(), currentSol.getCost());

        coordY.add(currentSol.getCost());
        coordX.add(currentIter);

        while (currentIter < MAX_EVALS){

            // TODO: perturbation
            Solution2 perturbedSolution = weakPerturbation(allEdgesOfGraph, allVertices, currentSol);

            // Controlla completezza sol.

            // TODO: localSearch
            LocalSearchObj2 lsSol = localSearch(perturbedSolution, allNd);

            // TODO: Miglioralo
            currentSol = acceptanceCriteria(perturbedSolution, perturbedSolution);

            currentIter+=lsSol.getIteration();
            coordY.add(currentSol.getCost());
            coordX.add(currentIter);

        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.

//        IlsObj toRet = new IlsObj(bestSolutionToRet, iterBsToRet, durationMs);
//
//        System.out.println("Execution time for " + bestSolutionToRet.getInstanceName() + ": " + durationMs +" ms\n");

        // TODO: Decommenta
        Plot plt = Plot.create();
        plt.plot().add(coordX, coordY, "o-");
        plt.xlabel("Iteration");
        plt.ylabel("Cost");
        plt.title("Convergence graph for: " + instancePath);
        plt.savefig(CONV_GRAPH_FOLDER + instancePath +".png");
        plt.executeSilently();

        return null;
    }

    public static void main(String[] args) throws Exception {

        // TODO: Copia tutto da main 1
        Graph instGraph = getInstance("wvcp-instances/vc_200_3000_05.txt");

        IteratedLocalSearch(instGraph, "temp");

    }

}