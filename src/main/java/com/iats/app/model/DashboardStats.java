package com.iats.app.model;

public class DashboardStats {
    private final int recruiters;
    private final int jobs;
    private final int candidates;
    private final int applications;
    private final int interviews;

    public DashboardStats(int recruiters, int jobs, int candidates, int applications, int interviews) {
        this.recruiters = recruiters;
        this.jobs = jobs;
        this.candidates = candidates;
        this.applications = applications;
        this.interviews = interviews;
    }

    public int getRecruiters() {
        return recruiters;
    }

    public int getJobs() {
        return jobs;
    }

    public int getCandidates() {
        return candidates;
    }

    public int getApplications() {
        return applications;
    }

    public int getInterviews() {
        return interviews;
    }
}
