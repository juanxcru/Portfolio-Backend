package com.juan.portfolio.service;


import com.juan.portfolio.client.GitHubClient;
import com.juan.portfolio.model.dto.GitHubRepoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ProjectService {

    @Autowired
    GitHubClient gitHubClient;

    @Autowired
    CacheService cacheService;


    public List<GitHubRepoDTO> listProjects(){

        try{
            String etag = cacheService.getEtag();

            ResponseEntity<GitHubRepoDTO[]> resp = gitHubClient.listUserRepos(etag);

            if( resp.getStatusCode().value() == 304){
                return cacheService.getCachedRepos();
            }

            if( resp.getStatusCode().value() != 200){
                return cacheService.getCachedRepos();
            }

            //maybe..
            if (resp.getBody() == null){
                return cacheService.getCachedRepos();
            }

            List<GitHubRepoDTO> filtered = Arrays.stream(resp.getBody())
                    .filter(r -> r.topics() != null && r.topics().contains("portfolio"))
                    .toList();

            popPortfolioTopic(filtered);

            cacheService.update(filtered, resp.getHeaders().getETag());

            return filtered;
        }catch (Exception e){
            System.out.println(e.toString());
            return List.of();
        }


    }

    private void popPortfolioTopic(List<GitHubRepoDTO> listDTO){
        listDTO.forEach(r -> {
            r.topics().remove("portfolio");
        });
    }

}