package org.example.proiectjava.service;

import org.example.proiectjava.database.DatabaseConfig;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.json.JSONArray;

import java.sql.*;
import java.util.*;

public class ScheduleService {

    public static JSONArray generateSchedule() {
        Graph<GraphNode, DefaultEdge> graph = generateGraph();
        // Placeholder for coloring logic
        JSONArray schedule = new JSONArray();
        return schedule;
    }

    private static Graph<GraphNode, DefaultEdge> generateGraph() {
        Graph<GraphNode, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        Map<Integer, List<GroupProfessorLink>> groupProfessorLinks = getGroupProfessorLinks();

        Map<Integer, Professor> professors = getProfessors();
        Map<String, Group> groups = getGroups();

        List<GraphNode> nodes = new ArrayList<>();
        for (List<GroupProfessorLink> links : groupProfessorLinks.values()) {
            for (GroupProfessorLink link : links) {
                GraphNode node = new GraphNode(link.getProfessorId(), link.getCourseId(), link.getGroupId());
                graph.addVertex(node);
                nodes.add(node);
            }
        }

        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                GraphNode node1 = nodes.get(i);
                GraphNode node2 = nodes.get(j);

                if (areAdjacent(node1, node2)) {
                    graph.addEdge(node1, node2);
                }
            }
        }

        return graph;
    }

    private static boolean areAdjacent(GraphNode node1, GraphNode node2) {
        return node1.getProfessorId() == node2.getProfessorId() || node1.getGroupId() == node2.getGroupId();
    }

    private static Map<Integer, List<GroupProfessorLink>> getGroupProfessorLinks() {
        Map<Integer, List<GroupProfessorLink>> groupProfessorLinks = new HashMap<>();
        String query = "SELECT * FROM GroupProfessorLink";
        try (Connection connection = DatabaseConfig.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                GroupProfessorLink link = new GroupProfessorLink(rs.getInt("prof_id"), rs.getInt("course_id"), rs.getInt("group_id"));
                groupProfessorLinks.computeIfAbsent(link.getCourseId(), k -> new ArrayList<>()).add(link);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groupProfessorLinks;
    }

    private static Map<Integer, Professor> getProfessors() {
        Map<Integer, Professor> professors = new HashMap<>();
        String query = "SELECT * FROM Professors";
        try (Connection connection = DatabaseConfig.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Professor professor = new Professor(rs.getInt("ID"), rs.getString("FIRST_NAME"), rs.getString("LAST_NAME"));
                professors.put(professor.getId(), professor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return professors;
    }

    private static Map<String, Group> getGroups() {
        Map<String, Group> groups = new HashMap<>();
        String query = "SELECT * FROM Groups";
        try (Connection connection = DatabaseConfig.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Group group = new Group(rs.getInt("ID"), rs.getString("GROUP_NAME"));
                groups.put(group.getGroupName(), group);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    public static void main(String[] args) {
        Graph<GraphNode, DefaultEdge> graph = generateGraph();

        // Afișarea nodurilor
        System.out.println("Noduri:");
        for (GraphNode node : graph.vertexSet()) {
            System.out.println(node);
        }

        // Afișarea muchiilor
        System.out.println("Muchii:");
        for (DefaultEdge edge : graph.edgeSet()) {
            GraphNode source = graph.getEdgeSource(edge);
            GraphNode target = graph.getEdgeTarget(edge);
            System.out.println(source + " -- " + target);
        }
    }

    static class GraphNode {
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

    static class GroupProfessorLink {
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

    static class Professor {
        private final int id;
        private final String firstName;
        private final String lastName;

        public Professor(int id, String firstName, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public int getId() {
            return id;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }

    static class Group {
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
}
