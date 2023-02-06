package com.example.guided_app.fragment.model;

public class NotificationModel {

    private String notificationBy;
    private long notificationAt;
    private String requsttedBy;
    private long requesttedAt;
    private String type;
    private  String postedBy;
    private  String postId;
    private boolean checkOpen;
    private  String notificationId;

    public String getNotificationBy() {
        return notificationBy;
    }

    public void setNotificationBy(String notificationBy) {
        this.notificationBy = notificationBy;
    }

    public long getNotificationAt() {
        return notificationAt;
    }

    public void setNotificationAt(long notificationAt) {
        this.notificationAt = notificationAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public boolean isCheckOpen() {
        return checkOpen;
    }

    public void setCheckOpen(boolean checkOpen) {
        this.checkOpen = checkOpen;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getRequsttedBy() {
        return requsttedBy;
    }

    public void setRequsttedBy(String requsttedBy) {
        this.requsttedBy = requsttedBy;
    }

    public long getRequesttedAt() {
        return requesttedAt;
    }

    public void setRequesttedAt(long requesttedAt) {
        this.requesttedAt = requesttedAt;
    }
}
