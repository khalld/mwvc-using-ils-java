package com.khaleoc;

public class IlsObj {
    public Solution2 solution;
    public int iterSolutionFounded;
    public long elapsedTime;

    public IlsObj(Solution2 solution, int iterSolutionFounded, long elapsedTime) {
        this.solution = solution;
        this.iterSolutionFounded = iterSolutionFounded;
        this.elapsedTime = elapsedTime;
    }

    public Solution2 getSolution() {
        return solution;
    }

    public int getIterSolutionFounded() {
        return iterSolutionFounded;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }


}
