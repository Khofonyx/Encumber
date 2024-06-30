package net.khofo.encumber.overlays;

import net.minecraft.resources.ResourceLocation;

public class BaseItem implements GroupItem {
    private String name;
    private ResourceLocation icon;
    private double weight;

    // Constructor
    public BaseItem(String name, ResourceLocation icon, double weight) {
        this.name = name;
        this.icon = icon;
        this.weight = weight;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceLocation getIcon() {
        return icon;
    }

    public void setIcon(ResourceLocation icon) {
        this.icon = icon;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}