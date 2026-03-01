package com.ats.model;

import jakarta.validation.constraints.NotNull;

public record ApplyRequest(
        @NotNull Integer candidate_id,
        @NotNull Integer job_id
) {
}
