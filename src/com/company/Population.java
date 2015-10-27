package com.company;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by ioan on 26/10/2015.
 */
public class Population {
    private ArrayList<City> cities;
    private ArrayList<ArrayList<Integer>> population;
    private int capacity;

    public ArrayList<City> getCities() {
        return cities;
    }

    public ArrayList<ArrayList<Integer>> getPopulation() {
        return population;
    }

    public void printPopulation() {
        population.stream().forEach((chromosone) -> System.out.println(chromosone.toString()));
    }

    public int getCapacity() {
        return capacity;
    }

    public Population(final String inputFile) throws IOException, Exception{
        cities = buildCities(readInputFile(inputFile));
        population = new ArrayList<ArrayList<Integer>>(cities.size());
        generatePopulation();
        printPopulation();
    };

    public String[] readInputFile(final String fileName) throws IOException{
        File file = new File(fileName);
        String content = Files.toString(file, Charsets.UTF_8);
        String[] lines = content.split("\n");

        return lines;
    }

    public ArrayList<City> buildCities(final String[] input) throws Exception {
        int currentLine = 0;
        final int dimension;

        City[] cities;
        String[] line = input[currentLine].split(" : ");

        if (line[0].equalsIgnoreCase("Dimension")) {
            dimension = Integer.parseInt(line[1]);
            cities = new City[dimension];
        }
        else
            throw new Exception("Dimension not specified!");

        currentLine++;
        line = input[currentLine].split(" : ");
        if (line[0].equalsIgnoreCase("Capacity"))
            capacity = Integer.parseInt(line[1]);
        else
            throw new Exception("Capacity not specified!");

        currentLine++;
        line = input[currentLine].split(" ");
        if (line[0].equalsIgnoreCase("Node_Coord_Section")) {
            for (int i = 0; i < dimension; i++) {
                currentLine++;
                line = input[currentLine].split(" ");
                cities[i] = new City(Integer.parseInt(line[1]), Integer.parseInt(line[2]));
            }
        } else
            throw new Exception("Coordinates not specified!");

        currentLine++;
        line = input[currentLine].split(" ");
        if (line[0].equalsIgnoreCase("Demand_Section")) {
            for (int i = 0; i < dimension; i++) {
                currentLine++;
                line = input[currentLine].split(" ");
                cities[i].setDemand(Integer.parseInt(line[1]));
            }
        } else
            throw new Exception("Demand values not specified!");

        System.out.println("Allocation finished");

        ArrayList<City> result = new ArrayList<City>(cities.length);
        result.addAll(Arrays.asList(cities));

        return result;
    }

    public void generatePopulation() {
        final int minBuses;
        final int totalDemand = cities.stream().map((city) -> city.getDemand()).reduce(0, (a, b) -> a + b);
        
        minBuses = (int) Math.ceil(totalDemand / capacity);
        ArrayList<Integer> chromosone;

        for (int k = 0; k < cities.size(); k++) {
            chromosone = new ArrayList<Integer>(cities.size() + minBuses * 2);
            for (int i = 0; i < (minBuses * 2); i++) {
                chromosone.add(0);
            }
            for (int i = 1; i < cities.size(); i++) {
                chromosone.add(i);
            }
            Collections.shuffle(chromosone);
            population.add(chromosone);
        }
    }

    public ArrayList<Integer> chrossover(ArrayList<Integer> c1, ArrayList<Integer> c2) {
        return new ArrayList<Integer>();
    }
}
