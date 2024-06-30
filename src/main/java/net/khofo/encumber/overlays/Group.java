package net.khofo.encumber.overlays;

import java.util.ArrayList;
import java.util.List;

public class Group implements GroupItem {
    private String name;
    private double weight;
    private boolean uiClosed;
    private List<GroupItem> subGroups;  // List of GroupItem
    private int depth = 1;

    // Constructor
    public Group(String name, double weight, boolean uiClosed) {
        this.name = name;
        this.weight = weight;
        this.uiClosed = uiClosed;
        this.subGroups = new ArrayList<>();
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String groupName) {
        this.name= groupName;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isUIClosed() {
        return uiClosed;
    }

    public void setUIClosed(boolean uiClosed) {
        this.uiClosed = uiClosed;
    }

    public List<GroupItem> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(List<GroupItem> subGroups) {
        this.subGroups = subGroups;
    }

    // Add a subgroup
    public void addSubGroup(GroupItem subGroup) {
        depth++;
        this.subGroups.add(subGroup);
    }

    public int getDepth(){
        return depth;
    }

    public void setDepth(int depth){
        this.depth =  depth;
    }
}