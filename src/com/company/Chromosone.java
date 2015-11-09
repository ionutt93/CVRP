package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;

public class Chromosone {
    private Double          fitness;
    private ArrayList<City> alleles;

    public Double getFitness() {
        return fitness;
    }

    public Chromosone() {
        alleles = new ArrayList<City>(Consts.cities);
        Collections.shuffle(alleles);
        computeFitness();
    }

    // TODO: 09/11/2015 Test if the compute fitness method works correctly 
    public void computeFitness() {
        ArrayList<Double> distances         = new ArrayList<Double>(alleles.size());
        double            distanceSum       = 0;
        int               remainingCapacity = Consts.capacity;
        int               i                 = 0;

        distanceSum += Consts.depot.distanceTo(alleles.get(0));
        remainingCapacity -= alleles.get(0).getDemand();

        while (i < alleles.size()) {
            distanceSum += Consts.depot.distanceTo(alleles.get(i));
            remainingCapacity -= alleles.get(i).getDemand();

            while (remainingCapacity > 0) {
                if (remainingCapacity >= alleles.get(i + 1).getDemand()) {
                    distances.add(alleles.get(i).distanceTo(alleles.get(i + 1)));
                    remainingCapacity -= alleles.get(i + 1).getDemand();
                    i++;
                } else {
                    remainingCapacity = -1;
                }
            }

            distances.add(alleles.get(i).distanceTo(Consts.depot));
            remainingCapacity = Consts.capacity;
            i++;
        }

        for (Double d : distances) {
            distanceSum += d;
        }

        fitness = distanceSum;
    }
}
