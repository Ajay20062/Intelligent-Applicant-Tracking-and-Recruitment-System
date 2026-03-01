package com.ats.controller;

import com.ats.model.ApplyRequest;
import com.ats.model.JobCreateRequest;
import com.ats.service.AtsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class AtsController {

    private final AtsService atsService;

    public AtsController(AtsService atsService) {
        this.atsService = atsService;
    }

    @GetMapping("/")
    public Map<String, String> index() {
        return Map.of("message", "ATS API is running successfully!");
    }

    @GetMapping("/portal")
    public RedirectView portal() {
        return new RedirectView("/index.html");
    }

    @GetMapping("/jobs")
    public ResponseEntity<?> getJobs() {
        try {
            List<Map<String, Object>> jobs = atsService.getJobs();
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<?> getJob(@PathVariable int id) {
        try {
            Optional<Map<String, Object>> job = atsService.getJobById(id);
            return job.<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "Job not found")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/jobs")
    public ResponseEntity<?> createJob(@Valid @RequestBody JobCreateRequest request) {
        try {
            long jobId = atsService.createJob(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Job created successfully", "job_id", jobId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/apply")
    public ResponseEntity<?> applyForJob(@Valid @RequestBody ApplyRequest request) {
        try {
            long applicationId = atsService.applyForJob(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Application submitted successfully", "application_id", applicationId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/applications")
    public ResponseEntity<?> getApplications() {
        try {
            List<Map<String, Object>> applications = atsService.getApplications();
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
