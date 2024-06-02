package org.example.proiectjava.service;

public class GroupProfessorLink {
    private final int professorId;
    private final int courseId;
    private final int groupId;

    public GroupProfessorLink(int professorId, int courseId, int groupId) {
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
}
