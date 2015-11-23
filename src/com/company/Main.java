package com.company;

import java.io.IOException;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.sun.glass.ui.SystemClipboard;
import com.sun.tools.internal.jxc.ap.Const;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static String[] readInputFile(final String fileName) throws IOException{
        File file = new File(fileName);
        String content = Files.toString(file, Charsets.UTF_8);
        String[] lines = content.split("\n");

        return lines;
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

        GeneticAlgorithm algorithm = new GeneticAlgorithm();
        algorithm.run(500);
    }
}
