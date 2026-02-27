package com.juan.portfolio.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubRepoDTO(
        long id,
        String name,
        @JsonProperty("full_name") String fullName,
        String url,
        @JsonProperty("html_url") String htmlUrl,
        String description,
        boolean fork,
        @JsonProperty("updated_at") String updatedAt,
        String language,
        List<String> topics
) {}