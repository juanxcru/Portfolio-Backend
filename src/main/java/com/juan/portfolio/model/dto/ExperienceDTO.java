package com.juan.portfolio.model.dto;

import java.util.List;

public record ExperienceDTO(
        String company,
        String role,
        String from,
        String to,
        String description,
        List<String> bullets

) {
}
