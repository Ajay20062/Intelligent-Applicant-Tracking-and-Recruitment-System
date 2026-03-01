package com.ats.service;

import com.ats.model.ApplyRequest;
import com.ats.model.JobCreateRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AtsService {

    private final JdbcTemplate jdbcTemplate;

    public AtsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getJobs() {
        return jdbcTemplate.queryForList("SELECT * FROM Jobs");
    }

    public Optional<Map<String, Object>> getJobById(int id) {
        List<Map<String, Object>> jobs = jdbcTemplate.queryForList("SELECT * FROM Jobs WHERE job_id = ?", id);
        return jobs.isEmpty() ? Optional.empty() : Optional.of(jobs.get(0));
    }

    public long createJob(JobCreateRequest request) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO Jobs (recruiter_id, title, department, location) VALUES (?, ?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            statement.setInt(1, request.recruiter_id());
            statement.setString(2, request.title());
            statement.setString(3, request.department());
            statement.setString(4, request.location());
            return statement;
        }, keyHolder);

        Number key = keyHolder.getKey();
        return key == null ? -1 : key.longValue();
    }

    public long applyForJob(ApplyRequest request) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO Applications (job_id, candidate_id, status) VALUES (?, ?, 'Applied')",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            statement.setInt(1, request.job_id());
            statement.setInt(2, request.candidate_id());
            return statement;
        }, keyHolder);

        Number key = keyHolder.getKey();
        return key == null ? -1 : key.longValue();
    }

    public List<Map<String, Object>> getApplications() {
        String sql = """
                SELECT
                    a.application_id,
                    c.full_name AS candidate_name,
                    c.email AS candidate_email,
                    j.title AS job_title,
                    j.department,
                    j.location,
                    a.status,
                    a.created_at
                FROM Applications a
                INNER JOIN Candidates c ON a.candidate_id = c.candidate_id
                INNER JOIN Jobs j ON a.job_id = j.job_id
                ORDER BY a.created_at DESC
                """;

        return jdbcTemplate.queryForList(sql);
    }
}
