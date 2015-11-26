package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Chromosone implements Comparable<Chromosone> {
    private Double          fitness;
    private int weakestLocation;
    private ArrayList<City> alleles;

    public int getWeakestLocation() {
        return weakestLocation;
    }

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
        computeFitness();
    }

    public String getPath() {
        String allPaths = "";
        int i = 0;

        while (i < this.alleles.size()) {
            String path = "1->";
            int remaining = Consts.capacity;

            while (remaining > 0 && i < this.alleles.size()) {
                if (remaining - this.alleles.get(i).getDemand() >= 0) {
                    remaining -= this.alleles.get(i).getDemand();
                    path += this.alleles.get(i).getIndex() + "->";
                    i++;
                } else break;
            }

            path += "1\n";
            allPaths += path;
        }

        return allPaths;

//        String path = "";
//        String connector = "->";
//        double remainingCapacity = Consts.capacity;
//
//        path += "1" + connector;
//        for (City value : this.getAlleles()) {
//            if (remainingCapacity >= value.getDemand()) {
//                remainingCapacity -= value.getDemand();
//                path += value.getIndex() + connector;
//            } else {
//                remainingCapacity = Consts.capacity;
//                path += "1\n1" + connector;
//            }
//        }
//        path += "1";
//        return path;
    }

    private City next(City current, ArrayList<City> parent, ArrayList<Boolean> available) {
        int i = 0;

        for (int j = 0; j < parent.size(); j++) {
            if (parent.get(j).getIndex() == current.getIndex()) {
                i = j;
                break;
            }
        }

        if (i + 1 == parent.size() || !available.get(parent.get(i + 1).getIndex())) {
            double minDistance = 99999;
            int position = 0;
            for (int j = 0; j < available.size(); j++) {
                if (available.get(j) == true) {
//                    double d = current.distanceTo(Consts.cities.get(j - 2));
//                    if (d < minDistance) {
//                        minDistance = d;
//                        position = j;
//                    }
                    return Consts.cities.get(j - 2);
                }
            }
        } else return parent.get(i + 1);

        return null;
    }

    public Chromosone SCX(Chromosone other) {
        ArrayList<City> p1 = new ArrayList<>(alleles);
        ArrayList<City> p2 = new ArrayList<>(other.getAlleles());
        ArrayList<City> c  = new ArrayList<>(p1.size());
        ArrayList<Boolean> available = new ArrayList<>(p1.size());

        for (int i = 0; i < 251; i++) {
            available.add(true);
        }
        available.set(0, false);
        available.set(1, false);

        City p = null;
        int i = 0;
        if (new Random().nextBoolean())
            p = p1.get(i);
        else p = p2.get(i);

        c.add(p);
        available.set(p.getIndex(), false);

        while (c.size() < p1.size()) {
            final City n1 = next(p, p1, available);
            final City n2 = next(p, p2, available);

            if (n1 == null || n2 == null) {
                System.err.println("next function not working");
                System.exit(1);
            }

            if (p.distanceTo(n1) < p.distanceTo(n2)) {
                c.add(n1);
                available.set(n1.getIndex(), false);
                p = n1;
            } else {
                c.add(n2);
                available.set(n2.getIndex(), false);
                p = n2;
            }
        }

        return new Chromosone(c);
    }

    // TODO: 09/11/2015 Test if the compute fitness method works correctly 
    public void computeFitness() {
        double            distanceSum       = 0;
        int               remainingCapacity = Consts.capacity;
        int               i                 = 0;
        double maxDistance = 0;

//        this.alleles.forEach(a -> { if (a.getDemand() == 0) System.out.println("DEMAND IS 0");});

        while (i < alleles.size() - 1) {
            distanceSum += Consts.depot.distanceTo(alleles.get(i));
            remainingCapacity -= alleles.get(i).getDemand();
//            if (Consts.depot.distanceTo(alleles.get(i)) > maxDistance) {
//                maxDistance = Consts.depot.distanceTo(alleles.get(i));
//                weakestLocation = i;
//            }
            while (remainingCapacity > 0 && i < alleles.size() - 1) {
                if (remainingCapacity >= alleles.get(i + 1).getDemand()) {
                    distanceSum += alleles.get(i).distanceTo(alleles.get(i + 1));
//                    if (alleles.get(i).distanceTo(alleles.get(i + 1)) > maxDistance) {
//                        maxDistance = alleles.get(i).distanceTo(alleles.get(i + 1));
//                        weakestLocation = i;
//                    }
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
            SwapValues(this.alleles.get(i), others.get(i), copy);
        }

//        for (int i = 0; i < copy.size() - 1; i++) {
//            for (int j = i + 1; j < copy.size(); j++) {
//                if (copy.get(i).getIndex() == copy.get(j).getIndex()) {
//                    System.out.println("Duplicate");
//                    System.out.println(copy.get(i).getIndex() + " " + copy.get(j).getIndex());
//                }
//            }
//        }

        return new Chromosone(copy);
    }

    public ArrayList<Chromosone> crossover(Chromosone other, int type) {
        ArrayList<Chromosone> result = new ArrayList<Chromosone>(2);

        if (type == 0) {
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
        } else {
            result.add(SCX(other));
        }

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

    private ArrayList<City> improveOneLocation() {
        ArrayList<City> mutated = new ArrayList<City>(alleles);
        int randPosition = new Random().nextInt(mutated.size() - 2);
        boolean swapped = false;
        int tries = 25;
        double d1 = 0;
        double d2 = 0;

        while (swapped == false && tries != 0) {
            d1 = mutated.get(randPosition).distanceTo(mutated.get(randPosition + 1));
            d2 = mutated.get(randPosition).distanceTo(mutated.get(randPosition + 2));
            if (d1 > d2) {
                final City t = new City(mutated.get(randPosition + 1));
                mutated.set(randPosition + 1, mutated.get(randPosition + 2));
                mutated.set(randPosition + 2, t);

                swapped = true;
            } else {
                randPosition = new Random().nextInt(mutated.size() - 2);
                tries--;
            }
        }

        return mutated;
    }

    public void mutation() {
        final boolean doMutation1 = new Random().nextInt(100) <= 40;
        final boolean doMutation2 = new Random().nextInt(100) <= 60;
        final boolean doMutation3 = new Random().nextInt(100) <= 70;

        if (doMutation3) {
            this.alleles = improveOneLocation();
        } else {
            ArrayList<City> mutated = new ArrayList<City>(alleles);
            if (doMutation1) {
                final int s = mutated.size();
                for (int i = 0; i < s; i++) {
                    final City temp = mutated.get(i);
                    mutated.set(i, mutated.get(s - i - 1));
                    mutated.set(s - i - 1, temp);
                }
            }
            if (doMutation2) {
                final int i1 = new Random().nextInt(mutated.size());
                final int i2 = new Random().nextInt(mutated.size());

                final City temp = new City(mutated.get(i1));
                mutated.set(i1, new City(mutated.get(i2)));
                mutated.set(i2, temp);
            }
            this.alleles = mutated;
        }
        computeFitness();
    }

    @Override
    public int compareTo(Chromosone other) {
        try {
            if (this.getFitness().doubleValue() < other.getFitness().doubleValue())
                return -1;
            if (this.getFitness().doubleValue() > other.getFitness().doubleValue())
                return 1;
            return 0;
        } catch (IllegalArgumentException e) {
            System.out.println("bug");
            return 0;
        }
    }
}
