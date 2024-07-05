package net.khofo.encumber.overlays;


public class BaseItem implements GroupItem {
    private final String name;
    private final double weight;

    public BaseItem(String name, double weight) {
        this.name = name;
        this.weight = weight;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public String getName() {
        return name;
    }
}