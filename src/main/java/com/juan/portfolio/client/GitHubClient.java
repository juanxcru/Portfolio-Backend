package com.juan.portfolio.client;

import com.juan.portfolio.model.dto.GitHubRepoDTO;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Component
public class GitHubClient {

    private final GitHubClientProps props;

    private final RestClient restClient;

    public GitHubClient (GitHubClientProps props){
        this.props = props;
        this.restClient = RestClient.builder()
                            .baseUrl(props.baseUrl())
                            .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + props.token())
                            .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                            .defaultHeader(HttpHeaders.USER_AGENT, props.userAgent())
                            .build();

    }

    //ToDo: Only repos with topic: 'portfolio' (maybe change our user to 'org' in github)
    public ResponseEntity<GitHubRepoDTO[]> listUserRepos(@Nullable String etag) {

         return restClient.get()
                                .uri("/users/{user}/repos?per_page=30&sort=updated", props.username())
                                    .headers(h -> {
                                        if (etag != null && !etag.isEmpty()) {
                                            h.set(HttpHeaders.IF_NONE_MATCH, etag);
                                        }
                                    })
                                .retrieve()
                                .toEntity(GitHubRepoDTO[].class);


    }


}
