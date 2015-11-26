package com.company;

import java.io.IOException;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.sun.glass.ui.SystemClipboard;
import com.sun.tools.internal.jxc.ap.Const;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static String[] readInputFile(final String fileName) throws IOException{
        File file = new File(fileName);
        String content = Files.toString(file, Charsets.UTF_8);
        String[] lines = content.split("\n");

        return lines;
    }

    public static void writeOutput(final String output, final String filename) throws IOException {
        File file = new File(filename);
        Files.write(output, file, Charsets.UTF_8);
    }

    public static ArrayList<City> getCityData(final String fileName) throws Exception {
        int currentLine = 0;
        final int dimension;
        final String[] input = Main.readInputFile(fileName);

        City[] cities;
        String[] line = input[currentLine].split(" : ");

        if (line[0].equalsIgnoreCase("Dimension")) {
            dimension = Integer.parseInt(line[1]); // count starts from 1
            cities = new City[dimension];
        }
        else
            throw new Exception("Dimension not specified!");

        currentLine++;
        line = input[currentLine].split(" : ");
        if (line[0].equalsIgnoreCase("Capacity"))
            Consts.capacity = Integer.parseInt(line[1]);
        else
            throw new Exception("Capacity not specified!");

        currentLine++;
        line = input[currentLine].split(" ");
        if (line[0].equalsIgnoreCase("Node_Coord_Section")) {
            for (int i = 0; i < dimension; i++) {
                currentLine++;
                line = input[currentLine].split(" ");
                cities[i] = new City(i + 1, Integer.parseInt(line[1]), Integer.parseInt(line[2]));
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

    public static void main(String[] args) throws Exception {
        // reading in the data
        Consts.cities = Main.getCityData("fruitybun250.vrp.txt");
        Consts.depot  = new City(Consts.cities.get(0));
        Consts.cities.remove(0);

//        Consts.cities.forEach(city -> System.out.println(city.getDemand()));

        GeneticAlgorithm a1 = new GeneticAlgorithm();
        GeneticAlgorithm a2 = new GeneticAlgorithm();

        Callable<Population> task1 = () -> {
            a1.run(200);
            return a1.getPopulation();
        };

        Callable<Population> task2 = () -> {
            a2.run(200);
            return a2.getPopulation();
        };

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Chromosone best = null;

        for (int i = 0; i < 5; i++) {
            Future<Population> f1 = executor.submit(task1);
            Future<Population> f2 = executor.submit(task2);

            Population p1 = f1.get();
            Population p2 = f2.get();

            if (p1.getPopulation().get(0).getFitness() < p2.getPopulation().get(0).getFitness())
                best = new Chromosone(p1.getPopulation().get(0));
            else
                best = new Chromosone(p2.getPopulation().get(0));

            Population temp = new Population(p1);
            p1.mergePopulation(p2);
            p2.mergePopulation(temp);
        }
        executor.shutdown();

        System.out.println("Best score: " + best.getFitness());

        String output = "login it12754 1381\nname Ioan Troana\n";
        output += "algorithm Genetic Algorithm with specialised crossover and mutation\n";
        output += "cost " + best.getFitness() + "\n";
        output += best.getPath();
        Main.writeOutput(output, "best-solution.txt");
    }
}
