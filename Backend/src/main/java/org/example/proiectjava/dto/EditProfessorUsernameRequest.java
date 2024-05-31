package org.example.proiectjava.dto;

    public class EditProfessorUsernameRequest {
        private String jwt;
        private int userId;
        private String username;

        public String getJwt() {
            return jwt;
        }

        public void setJwt(String jwt) {
            this.jwt = jwt;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
// Getters și setters
    }

