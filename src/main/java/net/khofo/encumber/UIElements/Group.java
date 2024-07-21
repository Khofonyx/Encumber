package net.khofo.encumber.UIElements;

import java.util.ArrayList;
import java.util.List;

public class Group implements GroupItem {

    /**
     * name: the group's name
     * weight: the group's weight
     * subGroups: a list of the subgroups to this group
     * expanded: whether or not the group is expanded or collapsed
     */
    private final String name;
    private double weight;
    private final List<GroupItem> subGroups;
    private boolean expanded;

    /**
     * Default constructor to initialize a Group.
     */
    public Group(String name, double weight) {
        this.name = name;
        this.weight = weight;
        this.subGroups = new ArrayList<>();
        this.expanded = false;
    }

    /**
     * Method to add an element to this group's subGroups
     */
    public void addSubGroup(GroupItem subGroup) {
        subGroups.add(subGroup);
    }

    /**
     * Method to return the list of subgroups
     */
    public List<GroupItem> getSubGroups() {
        return subGroups;
    }

    /**
     * Method to return the expanded boolean
     */
    public boolean isExpanded() {
        return expanded;
    }

    /**
     * Method to invert the expanded boolean's value. If it was true it becomes false and vice versa
     */
    public void toggleExpanded() {
        this.expanded = !this.expanded;
        if (!this.expanded) {
            // If we just closed the group, collapse the children UI elements
            collapseChildren(this);
        }
    }

    /**
     * Method to close a group's child groups
     */
    private void collapseChildren(Group group) {
        // Get all the children of the group
        for (GroupItem child : group.getSubGroups()) {
            // if the child element is a group, set it's expanded boolean to false, and collapse it's children recursively.
            if (child instanceof Group) {
                ((Group) child).setExpanded(false);
                collapseChildren((Group) child);
            }
        }
    }

    /**
     * Method to set the expanded boolean
     */
    private void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    /**
     * Method to return the weight of the group
     */
    @Override
    public double getWeight() {
        return weight;
    }

    /**
     * Method to set the weight of the group
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Method to get the name of the group
     */
    @Override
    public String getName() {
        return name;
    }
}