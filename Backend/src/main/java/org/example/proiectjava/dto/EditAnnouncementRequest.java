package org.example.proiectjava.dto;

public class EditAnnouncementRequest {
    private String jwt;
    private int id;
    private String announcementTitle;
    private String announcementContent;

    public EditAnnouncementRequest(String jwt, int id, String announcementTitle, String announcementContent) {
        this.jwt = jwt;
        this.id = id;
        this.announcementTitle = announcementTitle;
        this.announcementContent = announcementContent;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
