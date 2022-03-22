package com.khaleoc;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import com.opencsv.CSVWriter;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static final int MAX_EVALS = 20000;
    public static final String FOLDER_INSTANCES = "wvcp-instances";
    public static final String BENCHMARK_FOLDER = "benchmark/";
    public static final String CONV_GRAPH_FOLDER = BENCHMARK_FOLDER + "conv_g/";

    static int percent(int a, int b) {
        float result = 0;
        result = ((b - a) * 100) / a;

        return (int)result;
    }

    static SolutionKnowledge getNotFoundedEdge(ArrayList<Edge> allEdges, ArrayList<Edge> selected, List<Vertex> allVertex){
        int iter = 1;
        ArrayList<Edge> allEdgesFounded = new ArrayList<>();
        for (int i = 0; i < selected.size(); i++){
            Edge currentEdge = selected.get(i);
            Vertex from = allVertex.get(currentEdge.getSource());

            for (int j = 0; j < from.getAdjList().size(); j++){
                if(!allEdgesFounded.contains(from.getAdjList().get(j))){
                    allEdgesFounded.add(from.getAdjList().get(j));
                }
                iter+=1;
            }

            Vertex dest = allVertex.get(currentEdge.getDest());

            for (int j = 0; j < dest.getAdjList().size(); j++){
                if(!allEdgesFounded.contains(dest.getAdjList().get(j))){
                    allEdgesFounded.add(dest.getAdjList().get(j));
                }
                iter+=1;
            }
            iter+=1;
        }

        ArrayList<Edge> notFounded = new ArrayList<>();
        for (Edge e : allEdges) {
            if (!allEdgesFounded.contains(e)) {
                notFounded.add(e);
            }
            iter+=1;
        }

        SolutionKnowledge knowledge = new SolutionKnowledge(iter, notFounded);

        return knowledge;
    }

    static ValidityAndEvaluations isValidSolution(Solution inputSol, ArrayList<Edge> allEdges, ArrayList<Vertex> allVertex){
        SolutionKnowledge knowledgeAboutSolution = getNotFoundedEdge(allEdges, inputSol.getSelectedEdges(), allVertex);

        if ( knowledgeAboutSolution.getNotFoundedEges().size() == 0){
            ValidityAndEvaluations result = new ValidityAndEvaluations(true, knowledgeAboutSolution.getEvaluations());
            return result;
        }
        ValidityAndEvaluations result = new ValidityAndEvaluations(true, knowledgeAboutSolution.getEvaluations());
        return result;
    }

    static LocalSearchObj localSearch(Solution inputSol, ArrayList<Edge> allEdges, ArrayList<Vertex> allVertex){
        int iter = 1;

        ValidityAndEvaluations validityAndEvaluations = isValidSolution(inputSol, allEdges, allVertex);

        boolean isValid = validityAndEvaluations.isValidity();

        while (isValid == false){

            SolutionKnowledge sk = getNotFoundedEdge(allEdges, inputSol.getSelectedEdges(), allVertex);
            iter+= sk.getEvaluations();
            ArrayList<Edge> notFounded = sk.getNotFoundedEges();

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
                Vertex bestWeightValue = candidatesVertex.get(0);
                for (Vertex cv: candidatesVertex){
                    if (bestWeightValue.getWeight() > cv.getWeight()){
                        bestWeightValue = cv;
                    }
                    iter+=1;
                }
                inputSol.addVertex(bestWeightValue);
            } else {
                Vertex bestNumNeighborsValue = candidatesVertex.get(0);
                for (Vertex cv: candidatesVertex){
                    if (bestNumNeighborsValue.getAdjListSize() < cv.getAdjListSize()){
                        bestNumNeighborsValue = cv;
                    }
                    iter+=1;
                }
                inputSol.addVertex(bestNumNeighborsValue);
            }

            validityAndEvaluations = isValidSolution(inputSol, allEdges, allVertex);
            iter+=validityAndEvaluations.getEvaluations();
            isValid = validityAndEvaluations.isValidity();
        }

        LocalSearchObj toRet = new LocalSearchObj(inputSol, iter);

        return toRet;
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

        return initialSol;
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

    public static Solution weakPerturbation(ArrayList<Vertex> allVertex, Solution inputSolution) {
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

        return inputSolution;
    }

    public static Solution secondPerturbationChoice(ArrayList<Vertex> allVertex, Solution inputSolution) {
        List<Vertex> alreadySelected = inputSolution.getSelectedVertex();
        List<Vertex> notSelectedNodes = new ArrayList<>(allVertex);
        notSelectedNodes.removeAll(alreadySelected);

        int randomIndexAdd = new Random().nextInt(alreadySelected.size());
        Vertex toAdd = allVertex.get(randomIndexAdd);
        inputSolution.addVertex(toAdd);

        return inputSolution;
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

    public static IlsObj IteratedLocalSearch(Graph instanceGraph, String instancePath) throws Exception, PythonExecutionException {
        long startTime = System.nanoTime();

        final List<Integer> coordY = new ArrayList<>();
        final List<Integer> coordX = new ArrayList<>();

        final ArrayList<Edge> allEdgesOfGraph = new ArrayList<>();
        ArrayList<Vertex> allVertices = new ArrayList<>();

        for (Node n: instanceGraph.getNodes()){
            List<Edge> currentEdgeList = n.getEdgeList();
            allVertices.add(new Vertex(n.getId(), n.getWeight(), n.getEdgeList()));

            for (Edge e: currentEdgeList){
                allEdgesOfGraph.add(e);
            }
        }

        Solution currentSol = getInitialSolution(instancePath, allEdgesOfGraph, allVertices, allVertices);
        Solution bestSolutionToRet = new Solution(instancePath, currentSol.getSelectedVertex(), currentSol.getSelectedEdges(), currentSol.getCost());

        Solution worstSolution = new Solution(instancePath, allVertices);

        int currentIter = 1;
        int iterBsToRet = 1;

        coordY.add(currentSol.getCost());
        coordX.add(currentIter);

        double eps = percent(currentSol.getCost(), worstSolution.getCost());

        while (currentIter < MAX_EVALS){

            System.out.println("iter-> " + currentIter + " tot cost-> " + currentSol.getCost());

            Solution perturbedSolution;

            if (eps < 25){
                perturbedSolution = weakPerturbation(allVertices, currentSol);
            } else {
                perturbedSolution = secondPerturbationChoice(allVertices, currentSol);
            }

            LocalSearchObj lsSol = localSearch(perturbedSolution, allEdgesOfGraph, allVertices);

            currentIter += lsSol.getIteration();

            currentSol = acceptanceCriteria(lsSol.getSolution(), currentSol);

            if(currentSol.getCost() < bestSolutionToRet.getCost()){
                bestSolutionToRet = new Solution(instancePath, currentSol.getSelectedVertex(), currentSol.getSelectedEdges(), currentSol.getCost());
                iterBsToRet = currentIter;
            }

            coordY.add(currentSol.getCost());
            coordX.add(currentIter);

            eps = percent(currentSol.getCost(), worstSolution.getCost());

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