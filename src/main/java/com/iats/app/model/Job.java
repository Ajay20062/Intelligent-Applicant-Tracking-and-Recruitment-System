package com.iats.app.model;

public class Job {
    private Integer jobId;
    private Integer recruiterId;
    private String title;
    private String department;
    private String location;
    private String requiredSkills;
    private String status;

    public Job(Integer recruiterId, String title, String department, String location, String requiredSkills, String status) {
        this.recruiterId = recruiterId;
        this.title = title;
        this.department = department;
        this.location = location;
        this.requiredSkills = requiredSkills;
        this.status = status;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public Integer getRecruiterId() {
        return recruiterId;
    }

    public String getTitle() {
        return title;
    }

    public String getDepartment() {
        return department;
    }

    public String getLocation() {
        return location;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }

    public String getStatus() {
        return status;
    }
}
