package com.example.guided_app.fragment.model;

public class guidedModel {
   private String guidedBy;
   private String requestedBy;
   private  long requestedAt;
   private long guidedAt;

    public guidedModel() {

    }

    public String getGuidedBy() {
        return guidedBy;
    }

    public void setGuidedBy(String guidedBy) {
        this.guidedBy = guidedBy;
    }

    public long getGuidedAt() {
        return guidedAt;
    }

    public void setGuidedAt(long guidedAt) {
        this.guidedAt = guidedAt;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public long getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(long requestedAt) {
        this.requestedAt = requestedAt;
    }
}
