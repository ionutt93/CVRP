package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Chromosone {
    private Double          fitness;
    private ArrayList<City> alleles;

    public Double getFitness() {
        return fitness;
    }

    public ArrayList<City> getAlleles() {
        return alleles;
    }

    public void setAllele(int location, City value) {
        alleles.set(location, value);
    }

    public Chromosone() {
        alleles = new ArrayList<City>(Consts.cities);
        Collections.shuffle(alleles);
        computeFitness();
    }

    public Chromosone(ArrayList<City> distribution) {
        alleles = distribution;
        computeFitness();
    }

    public Chromosone(Chromosone other) {
        this.fitness = other.getFitness();
        this.alleles = new ArrayList<City>(other.alleles);
    }

    public String getPath() {
        String path = "";
        String connector = "->";
        double remainingCapacity = Consts.capacity;

        path += "1" + connector;
        for (City value : this.alleles) {
            if (remainingCapacity >= value.getDemand()) {
                remainingCapacity -= value.getDemand();
                path += value.getIndex() + connector;
            } else {
                remainingCapacity = Consts.capacity;
                path += "1\n1" + connector;
            }
        }
        path += "1";
        return path;
    }

    // TODO: 09/11/2015 Test if the compute fitness method works correctly 
    public void computeFitness() {
        double            distanceSum       = 0;
        int               remainingCapacity = Consts.capacity;
        int               i                 = 0;

        while (i < alleles.size() - 1) {
            distanceSum += Consts.depot.distanceTo(alleles.get(i));
            remainingCapacity -= alleles.get(i).getDemand();

            while (remainingCapacity > 0 && i < alleles.size() - 1) {
                if (remainingCapacity >= alleles.get(i + 1).getDemand()) {
                    distanceSum += alleles.get(i).distanceTo(alleles.get(i + 1));
                    remainingCapacity -= alleles.get(i + 1).getDemand();
                    i++;
                } else {
                    remainingCapacity = -1;
                }
            }

            distanceSum += alleles.get(i).distanceTo(Consts.depot);
            remainingCapacity = Consts.capacity;
            i++;
        }

        fitness = distanceSum;
    }

    private Integer[] getIndexArray(ArrayList<City> a) {
        Integer[] r = new Integer[a.size()];
        for (int i = 0; i < r.length; i++) {
            r[i] = a.get(i).getIndex();
        }
        return r;
    }

    private ArrayList<City> createCityArray(ArrayList<Integer> indexes) {
        ArrayList<City> result = new ArrayList<City>(indexes.size());
        for (Integer i: indexes) {
            result.add(Consts.cities.get(i - 2));
        }
        return result;
    }

    private Chromosone getOChild(ArrayList<City> others) {
        ArrayList<Integer> matching = new ArrayList<Integer>();
        int max = 0;
        while(matching.size() != others.size()) {
            BiGraph g = new BiGraph(getIndexArray(this.alleles), getIndexArray(others));
            matching = g.getPerfectMatching();

//            System.out.println();
//            for (Integer m:
//                 matching) {
//                System.out.print(m + "|");
//            }

//            for (int i = 0; i < matching.size() - 1; i++) {
//                for (int j = i + 1; j < matching.size(); j++) {
//                    if (matching.get(i) == matching.get(j)) {
//                        System.out.println("Duplicate");
//                        System.out.println(matching.get(i) + " " + matching.get(j));
//                    }
//                }
//            }
        }
        return new Chromosone(createCityArray(matching));
    }

    private Chromosone getEChild(ArrayList<City> oChild, ArrayList<City> others) {
        ArrayList<City> copy = new ArrayList<City>(oChild);
        for (int i = 0; i < copy.size(); i++) {
            if (copy.get(i).getIndex() == this.getAlleles().get(i).getIndex()) {
                copy.set(i, others.get(i));
            }
            else if (copy.get(i).getIndex() == others.get(i).getIndex()) {
                copy.set(i, others.get(i));
            }
        }
        return new Chromosone(copy);
    }

    public ArrayList<Chromosone> crossover(Chromosone other) {
        ArrayList<Chromosone> result = new ArrayList<Chromosone>(2);
        Chromosone best = null;
        int i = 5;

        while(i != 0) {
            Chromosone child = getOChild(other.getAlleles());
            if (best != null && child.getFitness() < best.getFitness())
                best = child;
            else if (best == null)
                best = child;
            i--;
        }

        Chromosone eChild = getEChild(best.getAlleles(), other.getAlleles());
        result.add(best);
        result.add(eChild);

        return result;
    }

//    public ArrayList<Chromosone> crossover(Chromosone other) {
//        final int i1 = new Random().nextInt(alleles.size());
//        final int i2 = new Random().nextInt(alleles.size());
//
//        ArrayList<City> child1 = new ArrayList<City>(alleles);
//        ArrayList<City> child2 = new ArrayList<City>(other.getAlleles());
//
//        ArrayList<Chromosone> result = new ArrayList<Chromosone>(2);
//
//        for (int i = Math.min(i1, i2); i < Math.max(i1, i2); i++) {
//            SwapValues(this.getAlleles().get(i), other.getAlleles().get(i), child1);
//            SwapValues(this.getAlleles().get(i), other.getAlleles().get(i), child2);
//        }
//
////        if (new Random().nextInt(3)==0) {
////            System.out.println(Math.min(i1, i2) + " - " + Math.max(i1, i2));
////            alleles.forEach(a -> System.out.print(a.getIndex() + " "));
////            System.out.println();
////            other.getAlleles().forEach(a -> System.out.print(a.getIndex() + " "));
////            System.out.println();
////            child1.forEach(a -> System.out.print(a.getIndex() + " "));
////            System.out.println();
////        }
//
//        result.add(new Chromosone(child1));
//        result.add(new Chromosone(child2));
//
//        return result;
//    }

    private void SwapValues(City val1, City val2, ArrayList<City> array) {
        final int i1 = val1.getIndex();
        final int i2 = val2.getIndex();

        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getIndex() == i1) {
                array.set(i, new City(val2));
            } else if(array.get(i).getIndex() == i2) {
                array.set(i, new City(val1));
            }
        }
    }

    public void mutation() {
        final boolean swapChromosone = new Random().nextInt(1000)==0;
        if (swapChromosone) {
//            System.out.println("Full Swap");
            final int s = this.alleles.size();
            for (int i = 0; i < s; i++) {
                final City temp = this.alleles.get(i);
                this.alleles.set(i, this.alleles.get(s - i - 1));
                this.alleles.set(s - i - 1, temp);
            }
        } else {
            final int i1 = new Random().nextInt(alleles.size());
            final int i2 = new Random().nextInt(alleles.size());

            final City temp = new City(alleles.get(i1));
            alleles.set(i1, new City(alleles.get(i2)));
            alleles.set(i2, temp);
        }

    }
}
