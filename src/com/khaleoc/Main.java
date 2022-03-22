package com.khaleoc;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import com.opencsv.CSVWriter;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static final int MAX_EVALS = 20000;
    public static final String FOLDER_INSTANCES = "wvcp-instances-red";
    public static final String BENCHMARK_FOLDER = "benchmark/";
    public static final String CONV_GRAPH_FOLDER = BENCHMARK_FOLDER + "conv_g/";

    static ArrayList<Edge> getNotFoundedEdge(ArrayList<Edge> allEdges, ArrayList<Edge> selected, List<Vertex> allVertex){
        ArrayList<Edge> allEdgesFounded = new ArrayList<>();
        for (int i = 0; i < selected.size(); i++){
            Edge currentEdge = selected.get(i);
            Vertex from = allVertex.get(currentEdge.getSource());

            for (int j = 0; j < from.getAdjList().size(); j++){
                if(!allEdgesFounded.contains(from.getAdjList().get(j))){
                    allEdgesFounded.add(from.getAdjList().get(j));
                }
            }

            Vertex dest = allVertex.get(currentEdge.getDest());

            for (int j = 0; j < dest.getAdjList().size(); j++){
                if(!allEdgesFounded.contains(dest.getAdjList().get(j))){
                    allEdgesFounded.add(dest.getAdjList().get(j));
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

    static Solution checkValidity(Solution inputSol, ArrayList<Edge> allEdges, ArrayList<Vertex> allVertex){
        ArrayList<Edge> notFounded = getNotFoundedEdge(allEdges, inputSol.getSelectedEdges(), allVertex);

        while (notFounded.size() != 0 ){
            List<Vertex> candidatesVertex = new ArrayList<>();

            for(int i=0; i<notFounded.size(); i++){
                Vertex from = allVertex.get(notFounded.get(i).getSource());
                Vertex dest = allVertex.get(notFounded.get(i).getDest());

                if(!inputSol.getSelectedVertex().contains(from)){
                    candidatesVertex.add(from);
                }

                if(!inputSol.getSelectedVertex().contains(dest)){
                    candidatesVertex.add(dest);
                }

            }

            int rndVal = (int)Math.floor(Math.random()*(2-1+1)+1);

            if (rndVal == 1){
                candidatesVertex.sort(Comparator.comparing(Vertex::getWeight));
            } else {
                candidatesVertex.sort(Comparator.comparing(Vertex::getAdjListSize).reversed());
            }

            inputSol.addVertex(candidatesVertex.get(0));

            notFounded = getNotFoundedEdge(allEdges, inputSol.getSelectedEdges(), allVertex);
        }

        return inputSol;
    }

    public static Solution getInitialSolution(String instanceName, ArrayList<Edge> graph, ArrayList<Vertex> allVertices, ArrayList<Vertex> allVertex) throws Exception {

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

        Solution initialSol = new Solution(instanceName, selectedVertex, selectedEges, totalWeight);

        Solution toRet = checkValidity(initialSol, graph, allVertex);


//        System.out.println("Total Weight: "+totalWeight);

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

    // TODO:
    public static void strongPerturbation(List<Node> allNd, Solution inputSolution) {

//        List<Node> alreadySelected = inputSolution.getSelNodes();
//        List<Node> notSelectedNodes = new ArrayList<>(allNd);
//        notSelectedNodes.removeAll(alreadySelected);
//
//        int max = (int)alreadySelected.size() / 2;
//        int min = 1;
//        int pert_times = (int)Math.floor(Math.random()*(max-min+1)+min);
//
//        if(notSelectedNodes.size() == 0){
//            // Posso solo rimuovere
//            for (int i = 0; i < pert_times; i++){
//                int randomIndex = new Random().nextInt(alreadySelected.size());
//                Node toRem = allNd.get(randomIndex);
//                inputSolution.removeNode(toRem);
//                alreadySelected = inputSolution.getSelNodes();
//                notSelectedNodes = new ArrayList<>(allNd);
//                notSelectedNodes.removeAll(alreadySelected);
//            }
//        } else {
//            // Posso anche aggiungere nodi alla perturbazione
//            for (int i=0; i<pert_times; i++){
//                int randomIndexAdd = new Random().nextInt(notSelectedNodes.size());
//                int randomIndexRem = new Random().nextInt(alreadySelected.size());
//
//                Node toRem = allNd.get(randomIndexRem);
//                Node toAdd = allNd.get(randomIndexAdd);
//                inputSolution.removeNode(toRem);
//                inputSolution.addNode(toAdd);
//
//                alreadySelected = inputSolution.getSelNodes();
//                notSelectedNodes = new ArrayList<>(allNd);
//                notSelectedNodes.removeAll(alreadySelected);
//            }
//
//        }
//
////        Solution inputSolutionChecked = completeSol(inputSolution, allNd);
//
//        return inputSolution;
    }

    public static Solution weakPerturbation(ArrayList<Edge> allEdgesOfGraph, ArrayList<Vertex> allVertex, Solution inputSolution) {
        List<Vertex> alreadySelected = inputSolution.getSelectedVertex();
        List<Vertex> notSelectedNodes = new ArrayList<>(allVertex);
        notSelectedNodes.removeAll(alreadySelected);

        if(notSelectedNodes.size() == 0){
            // Posso solo rimuovere
            int randomIndex = new Random().nextInt(alreadySelected.size());
            Vertex toRem = allVertex.get(randomIndex);
            inputSolution.removeVertex(toRem);
        } else {
            // Posso anche aggiungere nodi alla perturbazione
            int randomIndexRem = new Random().nextInt(alreadySelected.size());
            int randomIndexAdd = new Random().nextInt(alreadySelected.size());

            Vertex toRem = allVertex.get(randomIndexRem);
            inputSolution.removeVertex(toRem);
            Vertex toAdd = allVertex.get(randomIndexAdd);
            inputSolution.addVertex(toAdd);
        }


        Solution toRet = checkValidity(inputSolution, allEdgesOfGraph, allVertex);

        return toRet;
    }

    public static Solution acceptanceCriteria(Solution prevSol, Solution newSol){
        if (prevSol.getCost() > newSol.getCost()){
            return newSol;
        }

        if ((int)Math.floor(Math.random()*(2-1+1)+1) == 1){
            return newSol;
        }

        return prevSol;
    }

    public static LocalSearchObj localSearch(Solution inputSolution){
        int iterator = 1000;

        // FIXME
        Solution toCheckValidity = new Solution(inputSolution.getInstanceName(), inputSolution.getSelectedVertex(), inputSolution.getSelectedEdges(), inputSolution.getCost());

        LocalSearchObj toReturn = new LocalSearchObj(inputSolution, iterator);

        return toReturn;
    }

    public static IlsObj IteratedLocalSearch(Graph instanceGraph, String instancePath) throws Exception, PythonExecutionException {
        int currentIter = 1;
        int iterBsToRet = 1;
        long startTime = System.nanoTime();

        final List<Integer> coordY = new ArrayList<>();
        final List<Integer> coordX = new ArrayList<>();

        final ArrayList<Edge> allEdgesOfGraph = new ArrayList<>();
        for (int i = 0; i<instanceGraph.getNodes().size(); i++){
            List<Edge> currentEdgeList = instanceGraph.getNodes().get(i).getEdgeList();
            int currEdgeListSize = currentEdgeList.size();
            for (int j = 0; j < currEdgeListSize; j++){
                allEdgesOfGraph.add(currentEdgeList.get(j));
            }
        }

        ArrayList<Vertex> allVertices = new ArrayList<>();

        for (int i=0; i< instanceGraph.getNodes().size(); i++){
            allVertices.add(new Vertex(i, instanceGraph.getNodes().get(i).getWeight(), instanceGraph.getNodes().get(i).getEdgeList()));
        }

        // A PARTIRE DA QUI.. da sopra x ora nn toccare, sistema quando finisci

        Solution currentSol = getInitialSolution(instancePath, allEdgesOfGraph, allVertices, allVertices);
        Solution bestSolutionToRet = new Solution(instancePath, currentSol.getSelectedVertex(), currentSol.getSelectedEdges(), currentSol.getCost());

        coordY.add(currentSol.getCost());
        coordX.add(currentIter);

        while (currentIter < MAX_EVALS){
            // TODO: testa strong perturbation
            Solution perturbedSolution = weakPerturbation(allEdgesOfGraph, allVertices, currentSol);

            // TODO: localSearch
            LocalSearchObj lsSol = localSearch(perturbedSolution);

            currentSol = acceptanceCriteria(perturbedSolution, perturbedSolution);

            if(currentSol.getCost() < bestSolutionToRet.getCost()){
                bestSolutionToRet = new Solution(instancePath, currentSol.getSelectedVertex(), currentSol.getSelectedEdges(), currentSol.getCost());
                iterBsToRet = currentIter;
            }

            coordY.add(currentSol.getCost());
            coordX.add(currentIter);

            currentIter+=lsSol.getIteration();
        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.

        IlsObj toRet = new IlsObj(bestSolutionToRet, iterBsToRet, durationMs);

        Plot plt = Plot.create();
        plt.plot().add(coordX, coordY, "o-");
        plt.xlabel("Iteration");
        plt.ylabel("Cost");
        plt.title("Convergence graph for: " + bestSolutionToRet.getInstanceName());
        plt.savefig(CONV_GRAPH_FOLDER + bestSolutionToRet.getInstanceName() +".png");
        plt.executeSilently();

        return toRet;
    }

    public static void main(String[] args) throws Exception {

        List<String[]> ilsInfo = new ArrayList<>();

        // Esecuzione su directory!
        File folder = new File(FOLDER_INSTANCES);
        File[] listOfFiles = folder.listFiles((dir, name) -> !name.equals(".DS_Store"));
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("Executing ILS on: " + listOfFiles[i].getName());

                Graph instGraph = getInstance(listOfFiles[i].toString());
                IlsObj ilsObj = IteratedLocalSearch(instGraph, listOfFiles[i].getName());
                String[] ilsRes = {listOfFiles[i].getName(), String.valueOf(ilsObj.getSolution().getCost()), String.valueOf(ilsObj.getIterSolutionFounded()), String.valueOf(ilsObj.getElapsedTime()) };
                ilsInfo.add(ilsRes);
            }
            else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }

        List<String[]> csvData = createCsv(ilsInfo);

        // default all fields are enclosed in double quotes
        // default separator is a comma
        try (CSVWriter writer = new CSVWriter(new FileWriter(BENCHMARK_FOLDER + "results.csv"))) {
            writer.writeAll(csvData);
        }

    }



    private static List<String[]> createCsv(List<String[]> records) {
        String[] header = {"instance", "best solution", "best solution iter", "elapsed ms"};

        List<String[]> list = new ArrayList<>();
        list.add(header);

        for (int i = 0; i < records.size(); i++){
            String[] current = records.get(i);
            list.add(current);
        }

        return list;
    }

}