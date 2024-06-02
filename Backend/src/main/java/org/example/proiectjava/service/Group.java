package org.example.proiectjava.service;

public class Group {
    private final int id;
    private final String groupName;

    public Group(int id, String groupName) {
        this.id = id;
        this.groupName = groupName;
    }

    public int getId() {
        return id;
    }

    public String getGroupName() {
        return groupName;
    }
}
