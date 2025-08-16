package com.hemant.electric_billing_api.profile;

import jakarta.validation.constraints.NotBlank;

public record CreateProfileRequest(@NotBlank String name) {}