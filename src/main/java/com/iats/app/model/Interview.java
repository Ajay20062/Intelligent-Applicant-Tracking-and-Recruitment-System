package com.iats.app.model;

import java.time.LocalDateTime;

public class Interview {
    private Integer interviewId;
    private Integer applicationId;
    private LocalDateTime scheduledAt;
    private String interviewType;
    private String status;

    public Interview(Integer applicationId, LocalDateTime scheduledAt, String interviewType, String status) {
        this.applicationId = applicationId;
        this.scheduledAt = scheduledAt;
        this.interviewType = interviewType;
        this.status = status;
    }

    public Integer getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Integer interviewId) {
        this.interviewId = interviewId;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public String getInterviewType() {
        return interviewType;
    }

    public String getStatus() {
        return status;
    }
}
