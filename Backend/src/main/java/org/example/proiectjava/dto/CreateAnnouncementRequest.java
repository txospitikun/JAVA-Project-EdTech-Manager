package org.example.proiectjava.dto;

public class CreateAnnouncementRequest {
    private String jwt;
    private String announcementTitle;
    private String announcementContent;

    public CreateAnnouncementRequest(String jwt, String announcementTitle, String announcementContent) {
        this.jwt = jwt;
        this.announcementTitle = announcementTitle;
        this.announcementContent = announcementContent;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getAnnouncementTitle() {
        return announcementTitle;
    }

    public void setAnnouncementTitle(String announcementTitle) {
        this.announcementTitle = announcementTitle;
    }

    public String getAnnouncementContent() {
        return announcementContent;
    }

    public void setAnnouncementContent(String announcementContent) {
        this.announcementContent = announcementContent;
    }
}