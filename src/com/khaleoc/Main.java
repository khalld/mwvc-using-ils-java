package com.khaleoc;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
                    currentGraph.addEdge(i,j);
                }
            }
        }

        return currentGraph;
    }

    public static void IteratedLocalSearch(Solution solution) throws PythonExecutionException, IOException {
        // TODO:
        int iter = 0;
        List<Integer> coordY = new ArrayList<>();
        List<Integer> coordX = new ArrayList<>();

        int totCostTemp = 1000;
        while (iter < MAX_EVALS){
            iter+=100;
            totCostTemp-=10;
            coordY.add(totCostTemp);
            coordX.add(iter);
        }

        Plot plt = Plot.create();
        plt.plot().add(coordX, coordY, "o-");
        plt.xlabel("Iteration");
        plt.ylabel("Cost");
        plt.title("Convergence graph for: " + solution.getInstanceName());
        plt.savefig("benchmarks/convergence_graphs/" + solution.getInstanceName() );

    }

    public static void main(String[] args) throws IOException, PythonExecutionException {

        String instancePath = "wvcp-instances/SPI/1/vc_20_60_01.txt";
        Graph instGraph = getInstance(instancePath);
        List<Node> allNd = instGraph.getNodes();
        Solution worstSolution = new Solution(instancePath, allNd);
        IteratedLocalSearch(worstSolution);

//        instGraph.printInfo();

    }
}
