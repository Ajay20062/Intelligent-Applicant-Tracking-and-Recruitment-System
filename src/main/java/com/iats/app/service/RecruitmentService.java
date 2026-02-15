package com.iats.app.service;

import com.iats.app.model.Application;
import com.iats.app.model.ApplicationReportRow;
import com.iats.app.model.ApplicationScreening;
import com.iats.app.model.Candidate;
import com.iats.app.model.CandidateRankingRow;
import com.iats.app.model.DashboardStats;
import com.iats.app.model.Interview;
import com.iats.app.model.Job;
import com.iats.app.model.Recruiter;
import com.iats.app.repository.ApplicationRepository;
import com.iats.app.repository.ApplicationScreeningRepository;
import com.iats.app.repository.CandidateRepository;
import com.iats.app.repository.InterviewRepository;
import com.iats.app.repository.JobRepository;
import com.iats.app.repository.RecruiterRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class RecruitmentService {
    private static final Map<String, Integer> DEFAULT_SKILL_WEIGHTS = buildDefaultWeights();
    private static final Map<String, String> SKILL_ALIASES = buildSkillAliases();

    private final RecruiterRepository recruiterRepository;
    private final JobRepository jobRepository;
    private final CandidateRepository candidateRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationScreeningRepository applicationScreeningRepository;
    private final InterviewRepository interviewRepository;

    public RecruitmentService() {
        this.recruiterRepository = new RecruiterRepository();
        this.jobRepository = new JobRepository();
        this.candidateRepository = new CandidateRepository();
        this.applicationRepository = new ApplicationRepository();
        this.applicationScreeningRepository = new ApplicationScreeningRepository();
        this.interviewRepository = new InterviewRepository();
    }

    public int addRecruiter(String fullName, String email, String company) throws SQLException {
        if (fullName == null || fullName.isBlank() || email == null || email.isBlank()) {
            throw new IllegalArgumentException("Recruiter name and email are required.");
        }
        Recruiter recruiter = new Recruiter(fullName, email, company);
        return recruiterRepository.save(recruiter);
    }

    public int addJob(int recruiterId, String title, String department, String location, String requiredSkills) throws SQLException {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Job title is required.");
        }
        Job job = new Job(recruiterId, title, department, location, requiredSkills, "Open");
        return jobRepository.save(job);
    }

    public int addCandidate(String fullName, String email, String phone, String resumeUrl, String resumeText) throws SQLException {
        if (fullName == null || fullName.isBlank() || email == null || email.isBlank()) {
            throw new IllegalArgumentException("Candidate name and email are required.");
        }
        Candidate candidate = new Candidate(fullName, email, phone, resumeUrl, resumeText);
        return candidateRepository.save(candidate);
    }

    public int applyToJob(int jobId, int candidateId) throws SQLException {
        Application application = new Application(jobId, candidateId, "Applied");
        int applicationId = applicationRepository.save(application);
        screenApplication(applicationId);
        return applicationId;
    }

    public int scheduleInterview(int applicationId, LocalDateTime scheduledAt, String interviewType) throws SQLException {
        if (scheduledAt == null) {
            throw new IllegalArgumentException("Scheduled date and time are required.");
        }
        if (interviewType == null || interviewType.isBlank()) {
            throw new IllegalArgumentException("Interview type is required.");
        }
        String normalizedType = interviewType.trim();
        if (!normalizedType.equals("Phone") && !normalizedType.equals("Video") && !normalizedType.equals("Onsite")) {
            throw new IllegalArgumentException("Interview type must be Phone, Video, or Onsite.");
        }
        if (interviewRepository.hasConflict(scheduledAt)) {
            throw new IllegalArgumentException("Interview conflict detected: another interview is already scheduled within 60 minutes.");
        }
        Interview interview = new Interview(applicationId, scheduledAt, normalizedType, "Scheduled");
        updateApplicationStatus(applicationId, "Interviewing");
        return interviewRepository.save(interview);
    }

    public boolean updateApplicationStatus(int applicationId, String status) throws SQLException {
        String normalized = normalizeApplicationStatus(status);
        return applicationRepository.updateStatus(applicationId, normalized);
    }

    public List<Application> listApplications() throws SQLException {
        return applicationRepository.findAll();
    }

    public ApplicationScreening screenApplication(int applicationId) throws SQLException {
        Optional<Application> applicationOptional = applicationRepository.findById(applicationId);
        if (applicationOptional.isEmpty()) {
            throw new IllegalArgumentException("Application not found.");
        }

        Application application = applicationOptional.get();
        Optional<Job> jobOptional = jobRepository.findById(application.getJobId());
        Optional<Candidate> candidateOptional = candidateRepository.findById(application.getCandidateId());

        if (jobOptional.isEmpty() || candidateOptional.isEmpty()) {
            throw new IllegalArgumentException("Job or candidate not found for screening.");
        }

        Job job = jobOptional.get();
        Candidate candidate = candidateOptional.get();
        Map<String, Integer> requiredSkills = parseWeightedSkills(job.getRequiredSkills());
        String normalizedResumeText = normalizeText(candidate.getResumeText());
        Set<String> resumeTokens = parseSkills(candidate.getResumeText());

        List<String> matches = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        int totalWeight = requiredSkills.values().stream().mapToInt(Integer::intValue).sum();
        int matchedWeight = 0;

        for (Map.Entry<String, Integer> entry : requiredSkills.entrySet()) {
            String canonicalSkill = entry.getKey();
            int weight = entry.getValue();
            if (matchesSkill(canonicalSkill, normalizedResumeText, resumeTokens)) {
                matches.add(canonicalSkill + "(" + weight + ")");
                matchedWeight += weight;
            } else {
                missing.add(canonicalSkill + "(" + weight + ")");
            }
        }

        double score = totalWeight == 0 ? 0 : (matchedWeight * 100.0) / totalWeight;

        String recommendation;
        if (score >= 70) {
            recommendation = "Strong Match";
        } else if (score >= 40) {
            recommendation = "Moderate Match";
        } else {
            recommendation = "Low Match";
        }

        ApplicationScreening screening = new ApplicationScreening(
                applicationId,
                Math.round(score * 100.0) / 100.0,
                String.join(", ", matches),
                String.join(", ", missing),
                recommendation
        );
        applicationScreeningRepository.upsert(screening);
        applyAutomationRules(applicationId, screening, requiredSkills, missing);
        return screening;
    }

    public Optional<ApplicationScreening> getScreeningResult(int applicationId) throws SQLException {
        return applicationScreeningRepository.findByApplicationId(applicationId);
    }

    public List<ApplicationReportRow> getApplicationReport() throws SQLException {
        return applicationRepository.getApplicationReportRows();
    }

    public List<CandidateRankingRow> getTopCandidatesForJob(int jobId, int limit) throws SQLException {
        if (jobId <= 0) {
            throw new IllegalArgumentException("Job ID must be greater than 0.");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0.");
        }
        return applicationRepository.findTopCandidatesForJob(jobId, limit);
    }

    public String generateSummaryReport() throws SQLException {
        List<ApplicationReportRow> rows = getApplicationReport();
        long hired = rows.stream().filter(row -> "Hired".equals(row.getStatus())).count();
        long rejected = rows.stream().filter(row -> "Rejected".equals(row.getStatus())).count();
        long interviewing = rows.stream().filter(row -> "Interviewing".equals(row.getStatus())).count();
        double avgScore = rows.stream().mapToDouble(ApplicationReportRow::getScore).average().orElse(0);

        return """
                ATS Summary Report
                ------------------
                Total Applications: %d
                Hired: %d
                Rejected: %d
                Interviewing: %d
                Average Screening Score: %.2f
                """.formatted(rows.size(), hired, rejected, interviewing, avgScore);
    }

    public Path exportApplicationReportCsv(String fileName) throws SQLException, IOException {
        List<ApplicationReportRow> rows = getApplicationReport();
        Path reportsDir = Path.of("reports");
        Files.createDirectories(reportsDir);
        Path outputPath = reportsDir.resolve(fileName);

        StringBuilder csv = new StringBuilder();
        csv.append("Application ID,Candidate,Job Title,Status,Screening Score,Recommendation\n");
        for (ApplicationReportRow row : rows) {
            csv.append(row.getApplicationId()).append(",")
                    .append(escapeCsv(row.getCandidateName())).append(",")
                    .append(escapeCsv(row.getJobTitle())).append(",")
                    .append(row.getStatus()).append(",")
                    .append(String.format("%.2f", row.getScore())).append(",")
                    .append(escapeCsv(row.getRecommendation())).append("\n");
        }
        Files.writeString(outputPath, csv.toString(), StandardCharsets.UTF_8);
        return outputPath;
    }

    public List<Recruiter> listRecruiters() throws SQLException {
        return recruiterRepository.findAll();
    }

    public DashboardStats getDashboardStats() throws SQLException {
        return new DashboardStats(
                recruiterRepository.count(),
                jobRepository.count(),
                candidateRepository.count(),
                applicationRepository.count(),
                interviewRepository.count()
        );
    }

    private String normalizeApplicationStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Application status is required.");
        }
        String normalized = status.trim();
        if (!normalized.equals("Applied")
                && !normalized.equals("Screening")
                && !normalized.equals("Interviewing")
                && !normalized.equals("Rejected")
                && !normalized.equals("Hired")) {
            throw new IllegalArgumentException("Status must be Applied, Screening, Interviewing, Rejected, or Hired.");
        }
        return normalized;
    }

    private Set<String> parseSkills(String skillsText) {
        if (skillsText == null || skillsText.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(skillsText.toLowerCase(Locale.ROOT).split("[,\\s;/|]+"))
                .map(String::trim)
                .filter(token -> !token.isBlank())
                .map(token -> SKILL_ALIASES.getOrDefault(token, token))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Map<String, Integer> parseWeightedSkills(String skillsText) {
        Map<String, Integer> weighted = new HashMap<>();
        if (skillsText == null || skillsText.isBlank()) {
            return weighted;
        }

        String[] parts = skillsText.split(",");
        for (String part : parts) {
            String token = part.trim();
            if (token.isBlank()) {
                continue;
            }

            String skillName = token;
            int weight = -1;

            if (token.contains(":")) {
                String[] kv = token.split(":", 2);
                skillName = kv[0].trim();
                weight = parseWeightOrDefault(kv[1].trim(), skillName);
            } else if (token.contains("=")) {
                String[] kv = token.split("=", 2);
                skillName = kv[0].trim();
                weight = parseWeightOrDefault(kv[1].trim(), skillName);
            }

            String canonical = canonicalSkill(skillName);
            if (canonical.isBlank()) {
                continue;
            }
            if (weight <= 0) {
                weight = DEFAULT_SKILL_WEIGHTS.getOrDefault(canonical, 3);
            }
            weighted.put(canonical, Math.max(weighted.getOrDefault(canonical, 0), weight));
        }
        return weighted;
    }

    private int parseWeightOrDefault(String value, String skillName) {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed <= 0) {
                return DEFAULT_SKILL_WEIGHTS.getOrDefault(canonicalSkill(skillName), 3);
            }
            return parsed;
        } catch (NumberFormatException ex) {
            return DEFAULT_SKILL_WEIGHTS.getOrDefault(canonicalSkill(skillName), 3);
        }
    }

    private boolean matchesSkill(String canonicalSkill, String normalizedResumeText, Set<String> resumeTokens) {
        if (canonicalSkill.contains(" ")) {
            if (normalizedResumeText.contains(canonicalSkill)) {
                return true;
            }
        }
        if (resumeTokens.contains(canonicalSkill)) {
            return true;
        }

        for (Map.Entry<String, String> aliasEntry : SKILL_ALIASES.entrySet()) {
            if (!aliasEntry.getValue().equals(canonicalSkill)) {
                continue;
            }
            String alias = aliasEntry.getKey();
            if (alias.contains(" ")) {
                if (normalizedResumeText.contains(alias)) {
                    return true;
                }
            } else if (resumeTokens.contains(alias)) {
                return true;
            }
        }
        return false;
    }

    private void applyAutomationRules(int applicationId, ApplicationScreening screening, Map<String, Integer> requiredSkills, List<String> missing) throws SQLException {
        boolean missingMandatorySkill = missing.stream().anyMatch(skill -> {
            int start = skill.lastIndexOf('(');
            int end = skill.lastIndexOf(')');
            if (start < 0 || end < 0 || end <= start + 1) {
                return false;
            }
            try {
                int weight = Integer.parseInt(skill.substring(start + 1, end));
                return weight >= 5;
            } catch (NumberFormatException ex) {
                return false;
            }
        });

        if (missingMandatorySkill) {
            applicationRepository.updateStatus(applicationId, "Rejected");
            return;
        }

        if (screening.getScore() >= 80) {
            applicationRepository.updateStatus(applicationId, "Screening");
        } else if (!requiredSkills.isEmpty() && screening.getScore() < 35) {
            applicationRepository.updateStatus(applicationId, "Rejected");
        }
    }

    private String canonicalSkill(String rawSkill) {
        if (rawSkill == null) {
            return "";
        }
        String normalized = normalizeText(rawSkill);
        if (normalized.isBlank()) {
            return "";
        }
        return SKILL_ALIASES.getOrDefault(normalized, normalized);
    }

    private String normalizeText(String value) {
        if (value == null) {
            return "";
        }
        return value.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9+.# ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static Map<String, Integer> buildDefaultWeights() {
        Map<String, Integer> weights = new HashMap<>();
        weights.put("java", 5);
        weights.put("spring", 5);
        weights.put("spring boot", 5);
        weights.put("sql", 4);
        weights.put("mysql", 4);
        weights.put("python", 4);
        weights.put("javascript", 3);
        weights.put("react", 3);
        weights.put("communication", 2);
        weights.put("problem solving", 3);
        return weights;
    }

    private static Map<String, String> buildSkillAliases() {
        Map<String, String> aliases = new HashMap<>();
        aliases.put("js", "javascript");
        aliases.put("javascript", "javascript");
        aliases.put("spring", "spring");
        aliases.put("spring boot", "spring");
        aliases.put("springboot", "spring");
        aliases.put("mysql", "mysql");
        aliases.put("mariadb", "mysql");
        aliases.put("postgres", "sql");
        aliases.put("postgresql", "sql");
        aliases.put("sql", "sql");
        aliases.put("problem-solving", "problem solving");
        aliases.put("problem solving", "problem solving");
        aliases.put("communication skills", "communication");
        aliases.put("comm skills", "communication");
        return aliases;
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
