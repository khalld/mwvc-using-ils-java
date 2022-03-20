package com.khaleoc;

public class LocalSearchObj {
    public Solution solution;
    public int iteration;

    public LocalSearchObj(Solution solution, int iteration) {
        this.solution = solution;
        this.iteration = iteration;
    }

    public int getIteration() {
        return iteration;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    public Solution getSolution() {
        return solution;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }
}
