package org.example;

public class SaveGroupProfessorLinkRequest {
        private String jwt;
        private int groupId;
        private int courseId;
        private int professorId;

        public String getJwt() {
            return jwt;
        }

        public void setJwt(String jwt) {
            this.jwt = jwt;
        }

        public int getGroupId() {
            return groupId;
        }

        public void setGroupId(int groupId) {
            this.groupId = groupId;
        }

        public int getCourseId() {
            return courseId;
        }

        public void setCourseId(int courseId) {
            this.courseId = courseId;
        }

        public int getProfessorId() {
            return professorId;
        }

        public void setProfessorId(int professorId) {
            this.professorId = professorId;
        }
    }

