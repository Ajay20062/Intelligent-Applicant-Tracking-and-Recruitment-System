package com.iats.app.model;

public class ApplicationScreening {
    private Integer screeningId;
    private Integer applicationId;
    private double score;
    private String matchedSkills;
    private String missingSkills;
    private String recommendation;

    public ApplicationScreening(Integer applicationId, double score, String matchedSkills, String missingSkills, String recommendation) {
        this.applicationId = applicationId;
        this.score = score;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
        this.recommendation = recommendation;
    }

    public Integer getScreeningId() {
        return screeningId;
    }

    public void setScreeningId(Integer screeningId) {
        this.screeningId = screeningId;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public double getScore() {
        return score;
    }

    public String getMatchedSkills() {
        return matchedSkills;
    }

    public String getMissingSkills() {
        return missingSkills;
    }

    public String getRecommendation() {
        return recommendation;
    }
}
