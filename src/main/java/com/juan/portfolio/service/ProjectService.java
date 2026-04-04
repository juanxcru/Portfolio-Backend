package com.juan.portfolio.service;


import com.juan.portfolio.client.GitHubClient;
import com.juan.portfolio.model.dto.GitHubRepoDTO;
import com.juan.portfolio.model.dto.ProjectDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Service
public class ProjectService {

    @Autowired
    GitHubClient gitHubClient;

    @Autowired
    CacheService cacheService;


    public List<GitHubRepoDTO> listProjects(){

        String etag = cacheService.getEtag();

        ResponseEntity<GitHubRepoDTO[]> resp = gitHubClient.listUserRepos(etag);

        if( resp.getStatusCode().value() == 304){
            return cacheService.getCachedRepos();
        }

        if (resp.getBody() == null){
            return cacheService.getCachedRepos();
        }

        List<GitHubRepoDTO> filtered = Arrays.stream(resp.getBody())
                .filter(r -> r.topics() != null && r.topics().contains("portfolio"))
                .toList();

        cacheService.update(filtered, resp.getHeaders().getETag());

        return filtered;


    }

}