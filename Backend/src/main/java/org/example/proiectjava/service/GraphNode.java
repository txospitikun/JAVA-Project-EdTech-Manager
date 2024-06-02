package org.example.proiectjava.service;

import java.util.Objects;

public class GraphNode {
    private final int professorId;
    private final int courseId;
    private final int groupId;

    public GraphNode(int professorId, int courseId, int groupId) {
        this.professorId = professorId;
        this.courseId = courseId;
        this.groupId = groupId;
    }

    public int getProfessorId() {
        return professorId;
    }

    public int getCourseId() {
        return courseId;
    }

    public int getGroupId() {
        return groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphNode graphNode = (GraphNode) o;
        return professorId == graphNode.professorId &&
                courseId == graphNode.courseId &&
                groupId == graphNode.groupId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(professorId, courseId, groupId);
    }

    @Override
    public String toString() {
        return "GraphNode{" +
                "professorId=" + professorId +
                ", courseId=" + courseId +
                ", groupId=" + groupId +
                '}';
    }
}
