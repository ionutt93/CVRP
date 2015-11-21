package com.company;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.sun.tools.internal.jxc.ap.Const;
import com.sun.tools.javac.comp.Check;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Population {
    private ArrayList<Chromosone> population;
    private ArrayList<Integer> parentIDs;

    public ArrayList<Chromosone> getPopulation() {
        return population;
    }

    public Population(int populationSize) throws Exception {
        population = new ArrayList<Chromosone>(populationSize);
        parentIDs = new ArrayList<Integer>(populationSize);

        for (int i = 0; i < populationSize; i++) {
            population.add(new Chromosone());
        }

    }

    public void printPopulation() {
        population.stream().forEach((chr) -> System.out.println(chr.toString()));
    }

    // removes the duplicates from the list with probability of 1/50 (1 in 50 rounds) and sorts the chromosones
    // in descending order based on their fitness values
    public void evaluatePopulation() {
        Comparator<Chromosone> byFitness = (c1, c2) -> {
            if (c1.getFitness() < c2.getFitness())
                return -1;
            if (c1.getFitness() > c2.getFitness())
                return 1;
            return 0;
        };

        boolean checkForDuplicates = new Random().nextInt(100)==0;

        if (checkForDuplicates) {
            Set<Chromosone> distinctChromosones = new HashSet<Chromosone>();
            distinctChromosones.addAll(population);
            population.clear();
            population.addAll(distinctChromosones);

            if (population.size() < Consts.populationSize) {
                final int diff = Consts.populationSize - population.size();
                for (int i = 0; i < diff; i++) {
                    population.add(new Chromosone());
                }
            }
        }

        Collections.sort(population, byFitness);

        final int difference = population.size() - Consts.populationSize;
        for (int i = 0; i < difference; i++) {
            population.remove(Consts.populationSize);
        }

//        population.forEach(chromosone -> System.out.println(chromosone.getFitness()));
    }

    public void selectParents() {
        boolean selectThis;
//        for (int i = 0; i < population.size(); i++) {
//            selectThis = new Random().nextInt(4)<3;
//            if (selectThis) parentIDs.add(i);
//        }

        int fi,si;
        while(parentIDs.size() < population.size()) {
            fi  = new Random().nextInt(population.size());
            si = new Random().nextInt(population.size());
            selectThis = new Random().nextInt(4)<3;

            if (population.get(fi).getFitness() < population.get(si).getFitness()) {
                if (selectThis) parentIDs.add(fi);
                else parentIDs.add(si);
            } else if (population.get(fi).getFitness() > population.get(si).getFitness()) {
                if (selectThis) parentIDs.add(si);
                else parentIDs.add(fi);
            } else {
                if(new Random().nextBoolean() == true) parentIDs.add(si);
                else parentIDs.add(fi);
            }
        }
//        System.out.println(parentIDs.size() + "parents vs " + population.size() + " chromosones");
    }

    public void crossover() {
        if (parentIDs.size() % 2 == 1) parentIDs.remove(parentIDs.size() - 1);
        for (int i = 0; i+1 < parentIDs.size(); i+=2) {
            final Chromosone firstParent = population.get(parentIDs.get(i));
            final Chromosone secondParent = population.get(parentIDs.get(i + 1));

            final ArrayList<Chromosone> result = firstParent.crossover(secondParent);
            population.addAll(result);
        }
        parentIDs.clear();
    }

    public void mutation() {
        final int upperLimit = (int) (1 / Consts.mutationRate);
        population.forEach(chromosone -> {
            if(new Random().nextInt(upperLimit) == 0) { chromosone.mutation(); }
        });
    }

    public void trimPopulation() {
    }
}
