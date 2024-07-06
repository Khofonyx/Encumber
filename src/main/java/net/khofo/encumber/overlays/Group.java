package net.khofo.encumber.overlays;

import java.util.ArrayList;
import java.util.List;

public class Group implements GroupItem {
    private final String name;
    private double weight;
    private final List<GroupItem> subGroups;
    private boolean expanded;

    public Group(String name, double weight) {
        this.name = name;
        this.weight = weight;
        this.subGroups = new ArrayList<>();
        this.expanded = false;
    }

    public void addSubGroup(GroupItem subGroup) {
        subGroups.add(subGroup);
    }

    public List<GroupItem> getSubGroups() {
        return subGroups;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void toggleExpanded() {
        this.expanded = !this.expanded;
        if (!this.expanded) {
            // Collapse all child groups
            collapseChildren(this);
        }
    }

    private void collapseChildren(Group group) {
        for (GroupItem child : group.getSubGroups()) {
            if (child instanceof Group) {
                ((Group) child).setExpanded(false);
                collapseChildren((Group) child);
            }
        }
    }

    private void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String getName() {
        return name;
    }
}