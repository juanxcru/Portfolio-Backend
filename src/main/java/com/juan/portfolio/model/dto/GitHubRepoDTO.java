package com.juan.portfolio.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubRepoDTO(
        long id,
        String name,
        @JsonProperty("html_url")
        String htmlUrl,
        String description,
        String homepage,
        List<String> topics
) {}