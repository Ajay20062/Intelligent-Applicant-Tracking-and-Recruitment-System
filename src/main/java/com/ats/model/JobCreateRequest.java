package com.ats.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JobCreateRequest(
        @NotBlank String title,
        @NotBlank String department,
        @NotBlank String location,
        @NotNull Integer recruiter_id
) {
}
