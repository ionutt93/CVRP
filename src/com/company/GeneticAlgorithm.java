package com.company;

public class GeneticAlgorithm {
    private Population p;
    private Chromosone bestSolution;
    private final int duplicateCheckRate = 50;

    public Chromosone getBestSolution() {
        return bestSolution;
    }

    public GeneticAlgorithm(int crossoverType) throws Exception {
        p = new Population(Consts.populationSize, crossoverType);
        bestSolution = new Chromosone();
    }

    public Population getPopulation() {
        return p;
    }

    public void run(int iterations) {
        for (int i = 0; i < iterations; i++) {

            p.selectParents();
            p.crossover();
            p.mutation();
            p.evaluatePopulation();

            final Chromosone bestSolutionInPopulation = p.getPopulation().get(0);
            if (bestSolutionInPopulation.getFitness() < bestSolution.getFitness()) {
                bestSolution = new Chromosone(bestSolutionInPopulation);
                p.resetNoImprov();
            } else p.incNoImprov();

        }

    }
}
