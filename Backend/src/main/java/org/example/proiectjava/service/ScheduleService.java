package org.example.proiectjava.service;

import org.example.proiectjava.database.DatabaseConfig;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.*;

public class ScheduleService {

    private static int NUM_CLASSROOMS; // Number of classrooms available
    private static final int NUM_TIME_SLOTS = 30; // 6 intervals per day * 5 days

    static {
        try (Connection connection = DatabaseConfig.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Classrooms")) {
            if (rs.next()) {
                NUM_CLASSROOMS = rs.getInt(1);
            } else {
                throw new RuntimeException("Failed to count classrooms");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray generateSchedule(int semester) {
        Graph<GraphNode, DefaultEdge> graph = generateGraph(semester);
        Map<GraphNode, Integer> nodeColors = colorGraph(graph);
        JSONArray initialSchedule = createSchedule(nodeColors);
        return assignClassrooms(initialSchedule);
    }

    private static Graph<GraphNode, DefaultEdge> generateGraph(int semester) {
        Graph<GraphNode, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        Map<Integer, List<GroupProfessorLink>> groupProfessorLinks = getGroupProfessorLinks(semester);

        List<GraphNode> nodes = new ArrayList<>();
        for (List<GroupProfessorLink> links : groupProfessorLinks.values()) {
            for (GroupProfessorLink link : links) {
                GraphNode node = new GraphNode(link.getProfessorId(), link.getCourseId(), link.getGroupId(), link.getId());
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

    private static Map<Integer, List<GroupProfessorLink>> getGroupProfessorLinks(int semester) {
        Map<Integer, List<GroupProfessorLink>> groupProfessorLinks = new HashMap<>();
        String query = "SELECT gpl.id, gpl.prof_id, gpl.course_id, gpl.group_id " +
                "FROM GroupProfessorLink gpl " +
                "JOIN Courses c ON gpl.course_id = c.ID " +
                "WHERE c.semester = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, semester);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    GroupProfessorLink link = new GroupProfessorLink(rs.getInt("id"), rs.getInt("prof_id"), rs.getInt("course_id"), rs.getInt("group_id"));
                    groupProfessorLinks.computeIfAbsent(link.getCourseId(), k -> new ArrayList<>()).add(link);
                }
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

    private static Map<GraphNode, Integer> colorGraph(Graph<GraphNode, DefaultEdge> graph) {
        Map<GraphNode, Integer> nodeColors = new HashMap<>();
        Map<Integer, Integer> dayCounts = new HashMap<>();

        for (GraphNode node : graph.vertexSet()) {
            Set<Integer> usedColors = new HashSet<>();
            for (DefaultEdge edge : graph.edgesOf(node)) {
                GraphNode neighbor = graph.getEdgeTarget(edge);
                if (neighbor.equals(node)) {
                    neighbor = graph.getEdgeSource(edge);
                }
                if (nodeColors.containsKey(neighbor)) {
                    usedColors.add(nodeColors.get(neighbor));
                }
            }

            int color = 1;
            while (usedColors.contains(color)) {
                color++;
            }

            nodeColors.put(node, color);

            // Increment count for the respective day
            int dayIndex = (color - 1) / 6;
            dayCounts.put(dayIndex, dayCounts.getOrDefault(dayIndex, 0) + 1);
        }

        return nodeColors;
    }

    private static JSONArray createSchedule(Map<GraphNode, Integer> nodeColors) {
        JSONArray schedule = new JSONArray();
        for (Map.Entry<GraphNode, Integer> entry : nodeColors.entrySet()) {
            GraphNode node = entry.getKey();
            int color = entry.getValue();
            String timeSlot = getTimeSlot(color);
            JSONObject scheduleEntry = new JSONObject();
            scheduleEntry.put("Professor_ID", node.getProfessorId());
            scheduleEntry.put("Course_ID", node.getCourseId());
            scheduleEntry.put("Group_ID", node.getGroupId());
            scheduleEntry.put("Time_Slot", timeSlot);
            scheduleEntry.put("Link_ID", node.getId());  // Add Link_ID to the schedule entry
            schedule.put(scheduleEntry);
        }
        return schedule;
    }

    private static String getTimeSlot(int color) {
        int dayIndex = (color - 1) / 6;
        int timeIndex = (color - 1) % 6;
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        String[] times = {
                "8:00-10:00",
                "10:00-12:00",
                "12:00-14:00",
                "14:00-16:00",
                "16:00-18:00",
                "18:00-20:00"
        };
        return days[dayIndex] + ", " + times[timeIndex];
    }

    private static JSONArray assignClassrooms(JSONArray initialSchedule) {
        Map<String, List<Integer>> classroomAssignments = new HashMap<>();
        JSONArray finalSchedule = new JSONArray();

        Map<String, Integer> dayLoads = new HashMap<>();
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

        for (String day : days) {
            dayLoads.put(day, 0);
        }

        try (Connection connection = DatabaseConfig.getConnection()) {
            // Clear the Schedule table before inserting new records
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("DELETE FROM Schedule WHERE id > 0");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < initialSchedule.length(); i++) {
                JSONObject entry = initialSchedule.getJSONObject(i);
                int professorId = entry.getInt("Professor_ID");
                int courseId = entry.getInt("Course_ID");
                int groupId = entry.getInt("Group_ID");
                String timeSlot = entry.getString("Time_Slot");
                int linkId = entry.getInt("Link_ID");

                String currentDay = timeSlot.split(", ")[0];
                String currentTime = timeSlot.split(", ")[1];
                int classroom = getAvailableClassroom(classroomAssignments, timeSlot);

                if (classroom == -1) {
                    timeSlot = getNextBalancedTimeSlot(classroomAssignments, entry, NUM_CLASSROOMS, dayLoads);
                    classroom = getAvailableClassroom(classroomAssignments, timeSlot);
                    currentDay = timeSlot.split(", ")[0];
                    currentTime = timeSlot.split(", ")[1];
                }

                JSONObject scheduleEntry = new JSONObject();
                scheduleEntry.put("Professor_ID", professorId);
                scheduleEntry.put("Course_ID", courseId);
                scheduleEntry.put("Group_ID", groupId);
                scheduleEntry.put("Time_Slot", timeSlot);
                scheduleEntry.put("Classroom", "C" + classroom);

                finalSchedule.put(scheduleEntry);
                classroomAssignments.computeIfAbsent(timeSlot, k -> new ArrayList<>()).add(classroom);

                // Update day load
                String day = timeSlot.split(", ")[0];
                dayLoads.put(day, dayLoads.get(day) + 1);

                // Insert into Schedule table
                String insertQuery = "INSERT INTO Schedule (WEEK_DAY, TIME_DAY, LINK_ID, CLASSROOM_ID) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
                    stmt.setString(1, currentDay);
                    stmt.setString(2, currentTime);
                    stmt.setInt(3, linkId);
                    stmt.setInt(4, classroom);
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return finalSchedule;
    }

    private static String getNextBalancedTimeSlot(Map<String, List<Integer>> classroomAssignments, JSONObject entry, int numClassrooms, Map<String, Integer> dayLoads) {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        String[] times = {
                "8:00-10:00",
                "10:00-12:00",
                "12:00-14:00",
                "14:00-16:00",
                "16:00-18:00",
                "18:00-20:00"
        };

        List<String> sortedDays = new ArrayList<>(Arrays.asList(days));
        sortedDays.sort(Comparator.comparingInt(dayLoads::get));

        for (String day : sortedDays) {
            for (String time : times) {
                String timeSlot = day + ", " + time;
                if (!classroomAssignments.containsKey(timeSlot) || classroomAssignments.get(timeSlot).size() < numClassrooms) {
                    return timeSlot;
                }
            }
        }

        throw new RuntimeException("No available time slots.");
    }

    private static int getAvailableClassroom(Map<String, List<Integer>> classroomAssignments, String timeSlot) {
        List<Integer> assignedClassrooms = classroomAssignments.getOrDefault(timeSlot, new ArrayList<>());
        for (int i = 1; i <= NUM_CLASSROOMS; i++) {
            if (!assignedClassrooms.contains(i)) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide the semester (1 or 2) as a command-line argument.");
            return;
        }
        int semester = Integer.parseInt(args[0]);
        JSONArray generatedSchedule = ScheduleService.generateSchedule(semester);
        System.out.println(generatedSchedule.toString(4));
    }

    static class GraphNode {
        private final int professorId;
        private final int courseId;
        private final int groupId;
        private final int id;

        public GraphNode(int professorId, int courseId, int groupId, int id) {
            this.professorId = professorId;
            this.courseId = courseId;
            this.groupId = groupId;
            this.id = id;
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

        public int getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GraphNode graphNode = (GraphNode) o;
            return professorId == graphNode.professorId &&
                    courseId == graphNode.courseId &&
                    groupId == graphNode.groupId &&
                    id == graphNode.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(professorId, courseId, groupId, id);
        }

        @Override
        public String toString() {
            return "GraphNode{" +
                    "professorId=" + professorId +
                    ", courseId=" + courseId +
                    ", groupId=" + groupId +
                    ", id=" + id +
                    '}';
        }
    }

    static class GroupProfessorLink {
        private final int id;
        private final int professorId;
        private final int courseId;
        private final int groupId;

        public GroupProfessorLink(int id, int professorId, int courseId, int groupId) {
            this.id = id;
            this.professorId = professorId;
            this.courseId = courseId;
            this.groupId = groupId;
        }

        public int getId() {
            return id;
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
