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
    public static final String BENCHMARK_FOLDER = "benchmarks/";
    public static final String CONV_GRAPH_FOLDER = "benchmarks/convergence_graphs/";

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

    public static Solution weakPerturbation(List<Node> allNd, Solution inputSolution) {
        List<Node> alreadySelected = inputSolution.getSelNodes();
        List<Node> notSelectedNodes = new ArrayList<>(allNd);
        notSelectedNodes.removeAll(alreadySelected);

        if(notSelectedNodes.size() == 0){
            // Posso solo rimuovere
            int randomIndex = new Random().nextInt(alreadySelected.size());
            Node toRem = allNd.get(randomIndex);
            inputSolution.removeNode(toRem);
        } else {
            // Posso anche aggiungere nodi alla perturbazione
            int randomIndexAdd = new Random().nextInt(notSelectedNodes.size());
            int randomIndexRem = new Random().nextInt(alreadySelected.size());
            Node toRem = allNd.get(randomIndexRem);
            Node toAdd = allNd.get(randomIndexAdd);
            inputSolution.removeNode(toRem);
            inputSolution.addNode(toAdd);
        }

//        Solution inputSolutionChecked = completeSol(inputSolution, allNd);

        return inputSolution;
    }

    public static Solution strongPerturbation(List<Node> allNd, Solution inputSolution) {

        List<Node> alreadySelected = inputSolution.getSelNodes();
        List<Node> notSelectedNodes = new ArrayList<>(allNd);
        notSelectedNodes.removeAll(alreadySelected);

        int max = (int)alreadySelected.size() / 2;
        int min = 1;
        int pert_times = (int)Math.floor(Math.random()*(max-min+1)+min);

        if(notSelectedNodes.size() == 0){
            // Posso solo rimuovere
            for (int i = 0; i < pert_times; i++){
                int randomIndex = new Random().nextInt(alreadySelected.size());
                Node toRem = allNd.get(randomIndex);
                inputSolution.removeNode(toRem);
                alreadySelected = inputSolution.getSelNodes();
                notSelectedNodes = new ArrayList<>(allNd);
                notSelectedNodes.removeAll(alreadySelected);
            }
        } else {
            // Posso anche aggiungere nodi alla perturbazione
            for (int i=0; i<pert_times; i++){
                int randomIndexAdd = new Random().nextInt(notSelectedNodes.size());
                int randomIndexRem = new Random().nextInt(alreadySelected.size());

                Node toRem = allNd.get(randomIndexRem);
                Node toAdd = allNd.get(randomIndexAdd);
                inputSolution.removeNode(toRem);
                inputSolution.addNode(toAdd);

                alreadySelected = inputSolution.getSelNodes();
                notSelectedNodes = new ArrayList<>(allNd);
                notSelectedNodes.removeAll(alreadySelected);
            }

        }

//        Solution inputSolutionChecked = completeSol(inputSolution, allNd);

        return inputSolution;
    }

    public static LocalSearchObj localSearch(Solution inputSolution, List<Node> allNd){
        int iterator = 0;

        List<Node> alreadySelected = inputSolution.getSelNodes();
        List<Node> notSelectedNodes = new ArrayList<>(allNd);
        notSelectedNodes.removeAll(alreadySelected);

        int approx_cost_best = inputSolution.getTotalCost();
        int approx_cost = 0;
        Node toRem = null;
        Node toAdd = null;

        if (notSelectedNodes.size() > 0 ){
            // Posso provare lo swap dei nodi selezionati

            for (int i = 0; i < alreadySelected.size(); i++){
                Node wantToRemove = alreadySelected.get(i);
                for (int j=0; j< notSelectedNodes.size(); j++){
                    Node wantToAdd = alreadySelected.get(j);

                    approx_cost = inputSolution.getTotalCost() - wantToRemove.getWeight() + wantToAdd.getWeight();
                    iterator += 1;

                    if (approx_cost < inputSolution.getTotalCost()){
                        if (approx_cost_best > approx_cost) {
                            toRem = wantToRemove;
                            toAdd = wantToAdd;
                            approx_cost_best = approx_cost;
                            break;
                        }
                    }
                }
            }
        } else {
            // Posso solo eliminare nodi per 'alleggerire' la soluzione
            for (int i = 0; i < alreadySelected.size(); i++){
                Node wantToRemove = alreadySelected.get(i);
                approx_cost = inputSolution.getTotalCost() - wantToRemove.getWeight();
                iterator += 1;

                if (approx_cost < inputSolution.getTotalCost()){
                    if (approx_cost_best > approx_cost) {
                        toRem = wantToRemove;
                        approx_cost_best = approx_cost;
                        break;
                    }
                }
            }
        }
        if (toAdd != null) {
            inputSolution.addNode(toAdd);
        }
        if (toRem != null) {
            inputSolution.removeNode(toRem);
        }

        Solution inputSolChecked = completeSol(inputSolution, allNd);
        LocalSearchObj toReturn = new LocalSearchObj(inputSolChecked, iterator);

        return toReturn;
    }

    public static Solution acceptanceCriteria(Solution prevSol, Solution newSol, List<Solution> termMemory, int lockCounter){

        if (prevSol.getTotalCost() > newSol.getTotalCost()){
            return newSol;
        }
//        else if (lockCounter % 25 == 0) {
//            int randomIndex = new Random().nextInt(termMemory.size());
//            Solution alreadyFoundedSol = termMemory.get(randomIndex);
//            return alreadyFoundedSol;
//        }

        return prevSol;
    }

    public static Solution completeSol(Solution inputSol, List<Node> allAvailableNodes){
        boolean isComplete = inputSol.isComplete();

        while (isComplete == false) {
            List<Node> alreadySelected = inputSol.getSelNodes();
            List<Node> notSelectedNodes = new ArrayList<>(allAvailableNodes);
            notSelectedNodes.removeAll(alreadySelected);

            int rndVal = (int)Math.floor(Math.random()*(2-1+1)+1);

            if (rndVal == 1){
                notSelectedNodes.sort(Comparator.comparing(Node::getWeight));
            } else {
                notSelectedNodes.sort(Comparator.comparing(Node::getEdgeSize).reversed());
            }

            inputSol.addNode(notSelectedNodes.get(0));
            isComplete = inputSol.isComplete();

        }

        return inputSol;
    }

    public static IlsObj IteratedLocalSearch(Graph instanceGraph, String instancePath) throws PythonExecutionException, IOException {
        int currentIter = 1;
        int iterBsToRet = 1;
        long startTime = System.nanoTime();

        final List<Integer> coordY = new ArrayList<>();
        final List<Integer> coordX = new ArrayList<>();

        final List<Node> allNd = instanceGraph.getNodes();
        int allNdSize = allNd.size();
//        estraggo tutti gli edge del grafo
        final List<Edge> allEdgesOfGraph = new ArrayList<>();
        for (int i = 0; i<allNdSize; i++){
            List<Edge> currentEdgeList = allNd.get(i).getEdgeList();
            int currEdgeListSize = currentEdgeList.size();
            for (int j = 0; j < currEdgeListSize; j++){
                if (! allEdgesOfGraph.contains(currentEdgeList.get(j))){
                    allEdgesOfGraph.add(currentEdgeList.get(j));
                }

            }
        }

        Solution currentSol = new Solution(instancePath, allEdgesOfGraph);
        Solution bestSolutionToRet = new Solution(instancePath, allEdgesOfGraph);

//        for (int i=0; i < allNdSize; i++){
//            currentSol.addNode(allNd.get(i));
//            bestSolutionToRet.addNode(allNd.get(i));
//        }

//        Same benchmarks like starting with worst solution
        boolean isComplete = currentSol.isComplete();
        while (isComplete == false) {
            int randomIndex = new Random().nextInt(allNd.size());
            currentSol.addNode(allNd.get(randomIndex));
            bestSolutionToRet.addNode(allNd.get(randomIndex));
            isComplete = currentSol.isComplete();
        }

        coordY.add(currentSol.getTotalCost());
        coordX.add(currentIter);

        List<Solution> termMemory = new ArrayList<>();
        int lockCounter = 0;
        while (currentIter < MAX_EVALS){
//            Solution perturbedSolution = strongPerturbation(allNd, currentSol);
            Solution perturbedSolution = weakPerturbation(allNd, currentSol);
            LocalSearchObj localSearchObj = localSearch(perturbedSolution, allNd);
            Solution lsSolution = localSearchObj.getSolution();

            if (!termMemory.contains(lsSolution)){
                termMemory.add(lsSolution);
            }

            currentSol = acceptanceCriteria(lsSolution, currentSol, termMemory, lockCounter);

            if (currentSol.getTotalCost() < bestSolutionToRet.getTotalCost()){
                bestSolutionToRet = currentSol;
                iterBsToRet = currentIter + localSearchObj.getIteration();
            } else {
                lockCounter+=1;
            }

            currentIter+= localSearchObj.getIteration();

            coordY.add(currentSol.getTotalCost());
            coordX.add(currentIter);

        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.

        IlsObj toRet = new IlsObj(bestSolutionToRet, iterBsToRet, durationMs);

        System.out.println("Execution time for " + bestSolutionToRet.getInstanceName() + ": " + durationMs +" ms\n");

        Plot plt = Plot.create();
        plt.plot().add(coordX, coordY, "o-");
        plt.xlabel("Iteration");
        plt.ylabel("Cost");
        plt.title("Convergence graph for: " + bestSolutionToRet.getInstanceName());
        plt.savefig(CONV_GRAPH_FOLDER + bestSolutionToRet.getInstanceName() +".png");
        plt.executeSilently();

        return toRet;
    }

    public static void main(String[] args) throws IOException, PythonExecutionException {
        List<String[]> ilsInfo = new ArrayList<>();

        // Esecuzione su directory!
        File folder = new File(FOLDER_INSTANCES);
        File[] listOfFiles = folder.listFiles((dir, name) -> !name.equals(".DS_Store"));
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("Executing ILS on: " + listOfFiles[i].getName());

                Graph instGraph = getInstance(listOfFiles[i].toString());
                IlsObj ilsObj = IteratedLocalSearch(instGraph, listOfFiles[i].getName());
                String[] ilsRes = {listOfFiles[i].getName(), String.valueOf(ilsObj.getSolution().getTotalCost()), String.valueOf(ilsObj.getIterSolutionFounded()), String.valueOf(ilsObj.getElapsedTime()) };
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
