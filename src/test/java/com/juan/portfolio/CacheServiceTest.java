package com.juan.portfolio.service;

import com.juan.portfolio.model.dto.GitHubRepoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CacheServiceTest {

    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new CacheService();
    }

    @Test
    void isEmpty_whenNewInstance_returnsTrue() {
        assertThat(cacheService.isEmpty()).isTrue();
    }

    @Test
    void isEmpty_afterUpdate_returnsFalse() {
        List<GitHubRepoDTO> repos = List.of(
            new GitHubRepoDTO(1L, "repo-a", "https://github.com/u/repo-a", "desc", null, List.of("portfolio"))
        );

        cacheService.update(repos, "\"abc123\"");

        assertThat(cacheService.isEmpty()).isFalse();
    }

    @Test
    void getEtag_afterUpdate_returnsCorrectEtag() {
        cacheService.update(List.of(), "\"etag-xyz\"");

        assertThat(cacheService.getEtag()).isEqualTo("\"etag-xyz\"");
    }

    @Test
    void getCachedRepos_afterUpdate_returnsUpdatedList() {
        List<GitHubRepoDTO> repos = List.of(
            new GitHubRepoDTO(1L, "repo-a", "https://github.com/u/repo-a", "desc", null, List.of("portfolio")),
            new GitHubRepoDTO(2L, "repo-b", "https://github.com/u/repo-b", "desc", null, List.of())
        );

        cacheService.update(repos, "\"tag\"");

        assertThat(cacheService.getCachedRepos()).hasSize(2);
        assertThat(cacheService.getCachedRepos().get(0).name()).isEqualTo("repo-a");
    }

    @Test
    void update_replacesExistingCache() {
        List<GitHubRepoDTO> first = List.of(
            new GitHubRepoDTO(1L, "old-repo", "https://github.com/u/old", "old", null, List.of())
        );
        List<GitHubRepoDTO> second = List.of(
            new GitHubRepoDTO(2L, "new-repo", "https://github.com/u/new", "new", null, List.of())
        );

        cacheService.update(first, "\"etag-1\"");
        cacheService.update(second, "\"etag-2\"");

        assertThat(cacheService.getCachedRepos()).hasSize(1);
        assertThat(cacheService.getCachedRepos().get(0).name()).isEqualTo("new-repo");
        assertThat(cacheService.getEtag()).isEqualTo("\"etag-2\"");
    }
}
