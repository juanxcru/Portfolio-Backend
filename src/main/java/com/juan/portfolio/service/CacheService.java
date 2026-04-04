package com.juan.portfolio.service;

import com.juan.portfolio.model.dto.GitHubRepoDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CacheService {

    private List<GitHubRepoDTO> cachedRepos = List.of();

    private String etag;

    public List<GitHubRepoDTO> getCachedRepos() {
        return cachedRepos;
    }

    public String getEtag() {
        return etag;
    }

    public boolean isEmpty(){
        return cachedRepos.isEmpty();
    }

    public void update (List <GitHubRepoDTO> updRepos, String etag){
        this.etag = etag;
        this.cachedRepos = updRepos;
    }

}

