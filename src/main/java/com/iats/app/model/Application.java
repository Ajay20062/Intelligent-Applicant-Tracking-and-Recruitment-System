package com.iats.app.model;

public class Application {
    private Integer applicationId;
    private Integer jobId;
    private Integer candidateId;
    private String status;

    public Application(Integer jobId, Integer candidateId, String status) {
        this.jobId = jobId;
        this.candidateId = candidateId;
        this.status = status;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getJobId() {
        return jobId;
    }

    public Integer getCandidateId() {
        return candidateId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
