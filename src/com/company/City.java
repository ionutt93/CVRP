package com.company;

public class City {
    private int x;
    private int y;
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

    public void setDemand(int demand) {
        this.demand = demand;
    }

    public City(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public double DistanceTo(City c) {
        double dx = (this.x - c.getX()) * (this.x - c.getX());
        double dy = (this.y - c.getY()) * (this.y - c.getY());
        return Math.sqrt(dx + dy);
    }

}
