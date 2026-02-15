package com.iats.app.model;

public class ApplicationReportRow {
    private final int applicationId;
    private final String candidateName;
    private final String jobTitle;
    private final String status;
    private final double score;
    private final String recommendation;

    public ApplicationReportRow(int applicationId, String candidateName, String jobTitle, String status, double score, String recommendation) {
        this.applicationId = applicationId;
        this.candidateName = candidateName;
        this.jobTitle = jobTitle;
        this.status = status;
        this.score = score;
        this.recommendation = recommendation;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getStatus() {
        return status;
    }

    public double getScore() {
        return score;
    }

    public String getRecommendation() {
        return recommendation;
    }
}
