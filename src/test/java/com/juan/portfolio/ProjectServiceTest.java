package com.juan.portfolio.service;

import com.juan.portfolio.client.GitHubClient;
import com.juan.portfolio.model.dto.GitHubRepoDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private GitHubClient gitHubClient;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private ProjectService projectService;

    // --- helpers ---

    private GitHubRepoDTO repoWith(long id, String name, List<String> topics) {
        // Nota: los topics deben ser mutables porque popPortfolioTopic() llama a .remove()
        return new GitHubRepoDTO(id, name, "https://github.com/u/" + name, "desc", null, new ArrayList<>(topics));
    }

    private ResponseEntity<GitHubRepoDTO[]> okResponse(GitHubRepoDTO... repos) {
        HttpHeaders headers = new HttpHeaders();
        headers.setETag("\"etag-new\"");
        return new ResponseEntity<>(repos, headers, HttpStatus.OK);
    }

    private ResponseEntity<GitHubRepoDTO[]> notModifiedResponse() {
        return ResponseEntity.status(304).build();
    }

    // --- tests ---

    @Test
    void listProjects_whenGitHubReturns200_filtersAndReturnsPortfolioRepos() {
        GitHubRepoDTO portfolio = repoWith(1L, "cool-project", List.of("portfolio", "java"));
        GitHubRepoDTO other    = repoWith(2L, "random-repo",  List.of("python"));

        when(cacheService.getEtag()).thenReturn(null);
        when(gitHubClient.listUserRepos(null)).thenReturn(okResponse(portfolio, other));

        List<GitHubRepoDTO> result = projectService.listProjects();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("cool-project");
    }

    @Test
    void listProjects_portfolioTopicIsRemovedFromResult() {
        GitHubRepoDTO repo = repoWith(1L, "my-project", List.of("portfolio", "spring-boot"));

        when(cacheService.getEtag()).thenReturn(null);
        when(gitHubClient.listUserRepos(null)).thenReturn(okResponse(repo));

        List<GitHubRepoDTO> result = projectService.listProjects();

        assertThat(result.get(0).topics()).doesNotContain("portfolio");
        assertThat(result.get(0).topics()).contains("spring-boot");
    }

    @Test
    void listProjects_whenGitHubReturns304_returnsCachedRepos() {
        List<GitHubRepoDTO> cached = List.of(repoWith(5L, "cached-repo", List.of("java")));

        when(cacheService.getEtag()).thenReturn("\"old-etag\"");
        when(gitHubClient.listUserRepos("\"old-etag\"")).thenReturn(notModifiedResponse());
        when(cacheService.getCachedRepos()).thenReturn(cached);

        List<GitHubRepoDTO> result = projectService.listProjects();

        assertThat(result).isEqualTo(cached);
        verify(cacheService, never()).update(any(), any());
    }

    @Test
    void listProjects_whenGitHubReturnsEmptyBody_returnsCachedRepos() {
        List<GitHubRepoDTO> cached = List.of(repoWith(3L, "prev-repo", List.of()));

        when(cacheService.getEtag()).thenReturn(null);

        // ResponseEntity con body null
        HttpHeaders headers = new HttpHeaders();
        headers.setETag("\"etag\"");
        ResponseEntity<GitHubRepoDTO[]> nullBody = new ResponseEntity<>(null, headers, HttpStatus.OK);

        when(gitHubClient.listUserRepos(null)).thenReturn(nullBody);
        when(cacheService.getCachedRepos()).thenReturn(cached);

        List<GitHubRepoDTO> result = projectService.listProjects();

        assertThat(result).isEqualTo(cached);
    }

    @Test
    void listProjects_whenGitHubThrowsException_returnsEmptyList() {
        when(cacheService.getEtag()).thenReturn(null);
        when(gitHubClient.listUserRepos(null)).thenThrow(new RuntimeException("GitHub down"));

        List<GitHubRepoDTO> result = projectService.listProjects();

        assertThat(result).isEmpty();
    }

    @Test
    void listProjects_afterSuccess_updatesCacheWithFilteredRepos() {
        GitHubRepoDTO repo = repoWith(1L, "project-x", List.of("portfolio"));

        when(cacheService.getEtag()).thenReturn(null);
        when(gitHubClient.listUserRepos(null)).thenReturn(okResponse(repo));

        projectService.listProjects();

        // Verifica que se actualizó el cache con el etag recibido
        verify(cacheService).update(anyList(), eq("\"etag-new\""));
    }

    @Test
    void listProjects_whenNoReposHavePortfolioTopic_returnsEmptyList() {
        GitHubRepoDTO a = repoWith(1L, "tool-a", List.of("javascript"));
        GitHubRepoDTO b = repoWith(2L, "tool-b", List.of("css"));

        when(cacheService.getEtag()).thenReturn(null);
        when(gitHubClient.listUserRepos(null)).thenReturn(okResponse(a, b));

        List<GitHubRepoDTO> result = projectService.listProjects();

        assertThat(result).isEmpty();
    }
}
