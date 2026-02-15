package com.iats.app.model;

public class Recruiter {
    private Integer recruiterId;
    private String fullName;
    private String email;
    private String company;

    public Recruiter(String fullName, String email, String company) {
        this.fullName = fullName;
        this.email = email;
        this.company = company;
    }

    public Integer getRecruiterId() {
        return recruiterId;
    }

    public void setRecruiterId(Integer recruiterId) {
        this.recruiterId = recruiterId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getCompany() {
        return company;
    }
}
