package com.iats.app.model;

public class Candidate {
    private Integer candidateId;
    private String fullName;
    private String email;
    private String phone;
    private String resumeUrl;
    private String resumeText;

    public Candidate(String fullName, String email, String phone, String resumeUrl, String resumeText) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.resumeUrl = resumeUrl;
        this.resumeText = resumeText;
    }

    public Integer getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Integer candidateId) {
        this.candidateId = candidateId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public String getResumeText() {
        return resumeText;
    }
}
