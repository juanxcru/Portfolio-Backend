package com.juan.portfolio.service;


import com.juan.portfolio.client.GitHubClient;
import com.juan.portfolio.model.dto.GitHubRepoDTO;
import com.juan.portfolio.model.dto.ProjectDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    GitHubClient gitHubClient;


    public List<GitHubRepoDTO> listProjects(){

        gitHubClient.listUserRepos(null);
        return List.of();
    }

}