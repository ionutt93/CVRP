package com.company;

public class City {
    private final int x;
    private final int y;
    private final int index;
    private int demand;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDemand() {
        return demand;
    }

    public int getIndex() {
        return index;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    public City(int index, int x, int y) {
        this.index = index;
        this.x = x;
        this.y = y;
    }

    public City(City other) {
        this.index = other.index;
        this.x = other.x;
        this.y = other.y;
        this.demand = other.demand;
    }

    public double distanceTo(City c) {
        double dx = (this.x - c.getX()) * (this.x - c.getX());
        double dy = (this.y - c.getY()) * (this.y - c.getY());
        return Math.sqrt(dx + dy);
    }

}
