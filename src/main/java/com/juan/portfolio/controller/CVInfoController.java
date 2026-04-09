package com.juan.portfolio.controller;

import com.juan.portfolio.model.dto.CVInfoDTO;
import com.juan.portfolio.service.CVInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("/info")
public class CVInfoController {

    @Autowired
    CVInfoService cvInfoService;

    @GetMapping("")
    public ResponseEntity<CVInfoDTO> getInfo (@RequestHeader(name = "Accept-Language", required = false) Locale locale ){

        String lang = locale != null ? locale.getLanguage() : "en";

        CVInfoDTO cvInfo = cvInfoService.getInfo(lang);

        if( cvInfo == null || cvInfo.title().isEmpty() ) {
            return ResponseEntity.status(404).body(cvInfo);
        }else{
            return ResponseEntity.status(200).body(cvInfo);
        }

    }
}
