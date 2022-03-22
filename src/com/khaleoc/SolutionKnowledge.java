package com.khaleoc;

import java.util.ArrayList;

public class SolutionKnowledge{
    public int evaluations;
    public ArrayList<Edge> notFoundedEges;

    public SolutionKnowledge(int evaluations, ArrayList<Edge> notFoundedEges) {
        this.evaluations = evaluations;
        this.notFoundedEges = notFoundedEges;
    }

    public int getEvaluations() {
        return evaluations;
    }

    public ArrayList<Edge> getNotFoundedEges() {
        return notFoundedEges;
    }
}