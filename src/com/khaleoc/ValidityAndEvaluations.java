package com.khaleoc;

public class ValidityAndEvaluations{
    public boolean validity;
    public int evaluations;

    public ValidityAndEvaluations(boolean validity, int evaluations) {
        this.validity = validity;
        this.evaluations = evaluations;
    }

    public boolean isValidity() {
        return validity;
    }

    public void setValidity(boolean validity) {
        this.validity = validity;
    }

    public int getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(int evaluations) {
        this.evaluations = evaluations;
    }
}