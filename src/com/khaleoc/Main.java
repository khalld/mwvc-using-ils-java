package com.khaleoc;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static final int MAX_EVALS = 20000;

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

    public static Solution perturbation(List<Node> allNd, Solution inputSolution) {

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

        Solution inputSolutionChecked = completeSol(inputSolution, allNd);

        return inputSolutionChecked;
    }

    public static LocalSearchBean localSearch(Solution inputSolution, List<Node> allNd){
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
        LocalSearchBean toReturn = new LocalSearchBean(inputSolChecked, iterator);

        return toReturn;
    }

    public static Solution acceptanceCriteria(Solution prevSol, Solution newSol){

        if (prevSol.getTotalCost() > newSol.getTotalCost()){
            return newSol;
        }

        return prevSol;
    }

    public static Solution completeSol(Solution inputSol, List<Node> allAvailableNodes){
        boolean isComplete = inputSol.isComplete();

        while (isComplete == false) {
            List<Node> alreadySelected = inputSol.getSelNodes();
            List<Node> notSelectedNodes = new ArrayList<>(allAvailableNodes);
            notSelectedNodes.removeAll(alreadySelected);

//            notSelectedNodes.sort(Comparator.comparing(Node::getWeight));
            notSelectedNodes.sort(Comparator.comparing(Node::getEdgeSize).reversed());

            inputSol.addNode(notSelectedNodes.get(0));
            isComplete = inputSol.isComplete();

        }

        return inputSol;
    }

    public static void IteratedLocalSearch(Graph instanceGraph, String instancePath) throws PythonExecutionException, IOException {
        int currentIter = 0;
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

        for (int i=0; i < allNdSize ; i++){
            currentSol.addNode(allNd.get(i));
            bestSolutionToRet.addNode(allNd.get(i));
        }

        coordY.add(currentSol.getTotalCost());
        coordX.add(currentIter);

        while (currentIter < MAX_EVALS){
            Solution perturbedSolution = perturbation(allNd, currentSol);
            LocalSearchBean localSearchBean = localSearch(perturbedSolution, allNd);
            Solution lsSolution = localSearchBean.getSolution();

            currentSol = acceptanceCriteria(lsSolution, currentSol);

            currentIter+= localSearchBean.getIteration();

            coordY.add(currentSol.getTotalCost());
            coordX.add(currentIter);

        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.

        System.out.println("Execution time for " + bestSolutionToRet.getInstanceName() + ": " + durationMs +" ms\n");

        Plot plt = Plot.create();
        plt.plot().add(coordX, coordY, "o-");
        plt.xlabel("Iteration");
        plt.ylabel("Cost");
        plt.title("Convergence graph for: " + bestSolutionToRet.getInstanceName());
        plt.savefig("benchmarks/convergence_graphs/" + bestSolutionToRet.getInstanceName() +".png");
        plt.executeSilently();

    }

    public static void main(String[] args) throws IOException, PythonExecutionException {
        System.out.println("\n\n");

        String instancePath = "wvcp-instances/vc_800_10000.txt";
        Graph instGraph = getInstance(instancePath);
//        instGraph.printInfo();

        IteratedLocalSearch(instGraph, instancePath);


    }
}
