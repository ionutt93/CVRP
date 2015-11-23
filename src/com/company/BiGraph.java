package com.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


/**
 * Created by ioan on 23/11/2015.
 */
class Touple {
    private ArrayList<Integer> val;

    public Touple(int a, int b) {
        val = new ArrayList<Integer>(2);
        val.add(a);
        val.add(b);
    }

    public Touple() {
        val = new ArrayList<Integer>(2);
    }

    public void add(int a) {
        if (val.size() < 2)
            val.add(a);
    }

    public int getPos(int a) {
        for (int i = 0; i < val.size(); i++) {
            if (val.get(i).intValue() == a) {
                return i;
            }
        }
        return -1;
    }

    public void remove(int k) {
        for (int i = val.size() - 1; i >= 0; i--) {
            if (val.get(i) == k)
                val.remove(i);
        }
    }


    public void removeOthers(int k) {
        for (int i = val.size() - 1; i >= 0; i--) {
            if (val.get(i) != k)
                val.remove(i);
        }
    }

    public int getANode() {
        final int pos = new Random().nextInt(val.size());
        return val.get(pos);
    }

    public int getSize() {
        return val.size();
    }

    public String toString() {
        String res = "";
        for (int i = 0; i < val.size(); i++) {
            res += " " + val.get(i);
        }
        return res;
    }

    public int getFirst() {
        return val.get(0);
    }

}

public class BiGraph {
    final private int                length;
    private       HashMap<Integer, Touple>    nodes;
    private       HashMap<Integer, Touple>  customers;

    public BiGraph(Integer[] a, Integer[] b) {
        length = a.length;

        nodes = new HashMap<Integer, Touple>();
        customers = new HashMap<Integer, Touple>();

        for (int i = 0; i < length; i++) {
            nodes.put(i, new Touple(a[i], b[i]));
            if (customers.containsKey(a[i]) == false)
                customers.put(a[i], new Touple());
            if (customers.containsKey(b[i]) == false)
                customers.put(b[i], new Touple());

            customers.get(a[i]).add(i);
            customers.get(b[i]).add(i);
        }
    }

    public ArrayList<Integer> getPerfectMatching() {
        ArrayList<Integer> matching = new ArrayList<Integer>(length);
        ArrayList<Boolean> remaining = new ArrayList<Boolean>();

        for (int i = 0; i < 251; i++) {
            remaining.add(true);
        }
        remaining.set(0, false);
        remaining.set(1, false);

        for (int i = 0; i < length; i++) {
            while (nodes.get(i).getSize() != 0) {
                int r = nodes.get(i).getANode();

                if (customers.get(r).getPos(i) != -1) {
                    matching.add(r);
                    remaining.set(r, false);
                    customers.get(r).removeOthers(i);
                    nodes.get(i).removeOthers(r);
                    break;
                } else {
                    nodes.get(i).remove(r);
                }
            }
        }


//        System.out.println();
//        printGraph();


        for (int i = 2; i < remaining.size(); i++) {
            if (remaining.get(i) == true) {
                int minCostPosition = 0;
                double minCost = 9999999;
                for (int j = 1; j < matching.size(); j++) {
                    final int i1 = matching.get(j - 1) - 2;
                    final int i2 = matching.get(j) - 2;
                    final int ic = i - 2;

                    if (i1 < 0 || i2 < 0 || ic < 0)
                        System.out.println(i1 + " " + i2 + " " + ic);

                    final double cost = Consts.cities.get(i1).distanceTo(Consts.cities.get(ic)) +
                            Consts.cities.get(ic).distanceTo(Consts.cities.get(i2));
                    if (cost < minCost) {
                        minCost = cost;
                        minCostPosition = j;
                    }
                }
                matching.add(minCostPosition, i);
            }
        }

        return matching;
    }

//    public ArrayList<Integer> getPerfectMatching() {
//        ArrayList<Integer> matching = new ArrayList<Integer>(length);
//        final int p = new Random().nextInt(length - 30);
//
//        for (int i = 0; i < length; i++) {
//            while (nodes.get(i).getSize() != 0) {
//                int r;
//                if (i > p && i < p + 30) {
//                    r = nodes.get(i).getANode();
//                } else {
//                    r = nodes.get(i).getFirst();
//                }
//                String n = nodes.get(i).toString();
//                String m = customers.get(r).toString();
//                if (customers.get(r).getPos(i) != -1) {
//                    matching.add(r);
//                    customers.get(r).removeOthers(i);
//                    nodes.get(i).removeOthers(r);
//                    break;
//                } else {
//                    nodes.get(i).remove(r);
//                }
//            }
//        }
//        return matching;
//    }
    public void printGraph() {
        System.out.println("nodes");
        for (int i = 0; i < length; i++) {
            if (nodes.containsKey(i))
                System.out.println(i + ": " + nodes.get(i).toString());
        }

        System.out.println("customers");
        for (int i = 0; i < length + 1; i++) {
            if (customers.containsKey(i))
                System.out.println(i + ": " + customers.get(i).toString());
        }
    }

//    public static void main(String[] args) throws Exception{
//        Consts.cities = Main.getCityData("fruitybun250.vrp.txt");
//        Consts.depot  = new City(Consts.cities.get(0));
//        Consts.cities.remove(0);
//
//        Integer[] p1 = {5, 3, 6, 2, 7, 4};
//        Integer[] p2 = {4, 2, 3, 5, 6, 7};
//
//        int i = 5;
//        while (i >= 0) {
//            BiGraph g = new BiGraph(p1, p2);
//            ArrayList<Integer> matching = g.getPerfectMatching();
////            if (matching.size() == p1.length) {
//                i--;
//                System.out.println();
//                for (int k = 0; k < matching.size(); k++)
//                    System.out.print("->" + matching.get(k));
//
////            }
//
//        }
//    }
}
