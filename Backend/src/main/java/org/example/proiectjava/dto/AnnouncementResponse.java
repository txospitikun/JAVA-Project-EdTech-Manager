package org.example.proiectjava.dto;

import java.util.Date;

public class AnnouncementResponse {
    private int id;
    private String announcementTitle;
    private String announcementContent;
    private Date uploadDate;

    public AnnouncementResponse(int id, String announcementTitle, String announcementContent, Date uploadDate) {
        this.id = id;
        this.announcementTitle = announcementTitle;
        this.announcementContent = announcementContent;
        this.uploadDate = uploadDate;
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

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }
}