package com.company;

/**
 * Created by ioan on 02/11/2015.
 */
public class GeneticAlgorithm {
    private float mutationRate = 0.00001f;
    Population p;

    public float getMutationRate() {
        return mutationRate;
    }

    public void setMutationRate(float mutationRate) {
        if (mutationRate <= 1.0f)
            this.mutationRate = mutationRate;
        else
            System.out.println("Not a valid mutation rate");
    }

    public GeneticAlgorithm() throws Exception {
        p = new Population("fruitybun250.vrp.txt");
    };

    public void crossOver() {

    };

    public void start() {

    }

    public void stop() {

    }
}
