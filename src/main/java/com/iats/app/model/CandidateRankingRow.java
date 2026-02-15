package com.iats.app.model;

public class CandidateRankingRow {
    private final int applicationId;
    private final int candidateId;
    private final String candidateName;
    private final double score;
    private final String recommendation;
    private final String matchedSkills;
    private final String missingSkills;

    public CandidateRankingRow(int applicationId, int candidateId, String candidateName, double score, String recommendation, String matchedSkills, String missingSkills) {
        this.applicationId = applicationId;
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.score = score;
        this.recommendation = recommendation;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public double getScore() {
        return score;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public String getMatchedSkills() {
        return matchedSkills;
    }

    public String getMissingSkills() {
        return missingSkills;
    }
}
