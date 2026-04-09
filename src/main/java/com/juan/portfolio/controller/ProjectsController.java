package com.juan.portfolio.controller;

import com.juan.portfolio.model.dto.GitHubRepoDTO;
import com.juan.portfolio.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectsController {

    @Autowired
    ProjectService projectService;

    @GetMapping("")
    public ResponseEntity<List<GitHubRepoDTO>> getProjects(){

        List<GitHubRepoDTO> projects = projectService.listProjects();

        if (projects != null && !projects.isEmpty()){
            return ResponseEntity.status(200).body(projects);
        }else{
            return ResponseEntity.status(404).body(List.of());
        }

    }
}
