package com.juan.portfolio.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "githubclient")
public record GitHubClientProps(
        String baseUrl,
        String token,
        String userAgent,
        String username) {}
