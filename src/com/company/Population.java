package com.company;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.sun.tools.javac.comp.Check;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class Population {
    private ArrayList<Chromosone> population;

    public ArrayList<Chromosone> getPopulation() {
        return population;
    }

    public Population(int populationSize) throws IOException, Exception {
        population = new ArrayList<Chromosone>(populationSize);

        for (int i = 0; i < populationSize; i++) {
            population.add(new Chromosone());
        }
    }

    public void printPopulation() {
        population.stream().forEach((chr) -> System.out.println(chr.toString()));
    }

    public void sort() {
        Comparator<Chromosone> byFitness = (c1, c2) -> (int) (c1.getFitness() - c2.getFitness());
        Collections.sort(population, byFitness);
    }
}
