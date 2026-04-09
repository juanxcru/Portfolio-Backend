package com.juan.portfolio.controller;

import com.juan.portfolio.model.dto.GitHubRepoDTO;
import com.juan.portfolio.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @WebMvcTest levanta solo la capa web (controllers + MockMvc).
 * No levanta el contexto completo — mucho más rápido que @SpringBootTest.
 * El service se mockea con @MockBean.
 */
@WebMvcTest(ProjectsController.class)
class ProjectsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Test
    void getProjects_whenServiceReturnsRepos_responds200WithBody() throws Exception {
        List<GitHubRepoDTO> repos = List.of(
            new GitHubRepoDTO(1L, "cool-project", "https://github.com/u/cool-project",
                "A cool project", "https://cool.dev", new ArrayList<>(List.of("java")))
        );
        when(projectService.listProjects()).thenReturn(repos);

        mockMvc.perform(get("/projects").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is("cool-project")))
            .andExpect(jsonPath("$[0].html_url", is("https://github.com/u/cool-project")));
    }

    @Test
    void getProjects_whenServiceReturnsEmpty_responds404WithEmptyList() throws Exception {
        when(projectService.listProjects()).thenReturn(List.of());

        mockMvc.perform(get("/projects").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getProjects_whenServiceReturnsNull_responds404() throws Exception {
        when(projectService.listProjects()).thenReturn(null);

        mockMvc.perform(get("/projects").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void getProjects_returnsJson() throws Exception {
        when(projectService.listProjects()).thenReturn(List.of(
            new GitHubRepoDTO(1L, "repo", "url", "desc", null, new ArrayList<>())
        ));

        mockMvc.perform(get("/projects"))
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
