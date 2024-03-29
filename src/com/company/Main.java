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

//        System.out.println("Allocation finished");

        ArrayList<City> result = new ArrayList<City>(cities.length);
        result.addAll(Arrays.asList(cities));

        return result;
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            if (Integer.parseInt(args[0]) > 0 && Integer.parseInt(args[0]) < 55)
                Consts.runningTime = Integer.parseInt(args[0]);
            else
                Consts.runningTime = 10;
        }

        // reading in the data
        Consts.cities = Main.getCityData("fruitybun250.vrp");
        Consts.depot  = new City(Consts.cities.get(0));
        Consts.cities.remove(0);

//        Consts.cities.forEach(city -> System.out.println(city.getDemand()));

        GeneticAlgorithm a1 = new GeneticAlgorithm(0);
        GeneticAlgorithm a2 = new GeneticAlgorithm(0);
        GeneticAlgorithm a3 = new GeneticAlgorithm(1);
        GeneticAlgorithm a4 = new GeneticAlgorithm(1);

        final int iters = 400;
        Callable<Population> task1 = () -> {
            a1.run(iters);
            return a1.getPopulation();
        };

        Callable<Population> task2 = () -> {
            a2.run(iters);
            return a2.getPopulation();
        };

        Callable<Population> task3 = () -> {
            a3.run(iters);
            return a3.getPopulation();
        };

        Callable<Population> task4 = () -> {
            a4.run(iters);
            return a4.getPopulation();
        };

        ExecutorService executor = Executors.newFixedThreadPool(4);
        Chromosone best = new Chromosone();

        long startTime = System.currentTimeMillis();
        long duration = Consts.runningTime * 60 * 1000;

        while(System.currentTimeMillis() < startTime + duration) {
            Future<Population> f1 = executor.submit(task1);
            Future<Population> f2 = executor.submit(task2);
            Future<Population> f3 = executor.submit(task3);
            Future<Population> f4 = executor.submit(task4);

            try {
                Population p1 = f1.get();
                Population p2 = f2.get();
                Population p3 = f3.get();
                Population p4 = f4.get();

//            if (p1.getPopulation().get(0).getFitness() < p2.getPopulation().get(0).getFitness())
//                best = new Chromosone(p1.getPopulation().get(0));
//            else
//                best = new Chromosone(p2.getPopulation().get(0));

                if (p1.getPopulation().get(0).getFitness() < best.getFitness())
                    best = new Chromosone(p1.getPopulation().get(0));
                if (p2.getPopulation().get(0).getFitness() < best.getFitness())
                    best = new Chromosone(p2.getPopulation().get(0));
                if (p3.getPopulation().get(0).getFitness() < best.getFitness())
                    best = new Chromosone(p3.getPopulation().get(0));
                if (p4.getPopulation().get(0).getFitness() < best.getFitness())
                    best = new Chromosone(p4.getPopulation().get(0));


                Population t1 = new Population(p1);
                Population t2 = new Population(p2);
                Population t3 = new Population(p3);
                Population t4 = new Population(p4);

                p1.mergePopulation(t2);
                p1.mergePopulation(t3);
                p1.mergePopulation(t4);

                p2.mergePopulation(t1);
                p2.mergePopulation(t3);
                p2.mergePopulation(t4);

                p3.mergePopulation(t1);
                p3.mergePopulation(t2);
                p3.mergePopulation(t4);

                p4.mergePopulation(t1);
                p4.mergePopulation(t2);
                p4.mergePopulation(t3);
//                System.out.println("-----------------------------");
            }
            catch (Exception e) {

            }
        }
        executor.shutdown();

//        System.out.println("Best score: " + best.getFitness());

        String output = "login it12754 52247\nname Ioan Troana\n";
        output += "algorithm Genetic Algorithm with specialised crossover and mutation\n";
        output += "cost " + best.getFitness() + "\n";
        output += best.getPath();
        Main.writeOutput(output, "best-solution.txt");
    }
}
