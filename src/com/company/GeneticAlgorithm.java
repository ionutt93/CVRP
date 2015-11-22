package com.company;

public class GeneticAlgorithm {
    private Population p;
    private Chromosone bestSolution;
    private final int duplicateCheckRate = 50;

    public GeneticAlgorithm() throws Exception {
        p = new Population(Consts.populationSize);
        bestSolution = new Chromosone();
    }


    public void run(int iterations) {
        for (int i = 0; i < 500; i++) {

            p.evaluatePopulation();
//            System.out.println("p.evaluatePopulation();");
            p.trimPopulation();
//            System.out.println("p.trimPopulation();");

            final Chromosone bestSolutionInPopulation = p.getPopulation().get(0);
            if (bestSolutionInPopulation.getFitness() < bestSolution.getFitness()) {
                bestSolution = new Chromosone(bestSolutionInPopulation);
            }

            p.selectParents();
//            System.out.println("p.selectParents();");
            p.crossover();
//            System.out.println("p.crossover();");
            p.mutation();
//            System.out.println("p.mutation();");

            if (i % 50 == 0)
                System.out.println(bestSolution.getFitness());
        }

        System.out.println("best solution: " + bestSolution.getFitness());
        System.out.println();
        bestSolution.getAlleles().forEach(city -> System.out.print(city.getIndex() + "->"));
        System.out.print(bestSolution.getPath());
    }
}
