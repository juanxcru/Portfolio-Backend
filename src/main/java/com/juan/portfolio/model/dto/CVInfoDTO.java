package com.juan.portfolio.model.dto;

import java.util.List;
import java.util.Map;

public record CVInfoDTO(

        String title,
        String subtitle,
        String availability,
        String avail_short,
        String coverLetter,
        String location,
        String email,
        String whatsapp,
        String github,
        String linkedin,
        Map<String,List<String>> stack,
        String bio1,
        String bio2,
        String bio3,
        List<ExperienceDTO> experience
){}
