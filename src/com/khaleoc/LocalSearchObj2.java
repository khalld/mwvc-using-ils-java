package com.khaleoc;


public class LocalSearchObj2 {
    public Solution2 solution;
    public int iteration;

    public LocalSearchObj2(Solution2 solution, int iteration) {
        this.solution = solution;
        this.iteration = iteration;
    }

    public int getIteration() {
        return iteration;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    public Solution2 getSolution() {
        return solution;
    }

    public void setSolution(Solution2 solution) {
        this.solution = solution;
    }
}
