package org.example.proiectjava.dto;

import java.util.List;

    public class EditProfessorCoursesRequest {
        private String jwt;
        private int professorID;
        private List<String> courses;

        public String getJwt() {
            return jwt;
        }

        public void setJwt(String jwt) {
            this.jwt = jwt;
        }

        public int getProfessorID() {
            return professorID;
        }

        public void setProfessorID(int professorID) {
            this.professorID = professorID;
        }

        public List<String> getCourses() {
            return courses;
        }

        public void setCourses(List<String> courses) {
            this.courses = courses;
        }
    }
