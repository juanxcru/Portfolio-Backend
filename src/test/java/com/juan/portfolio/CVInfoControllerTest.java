package com.juan.portfolio.controller;

import com.juan.portfolio.model.dto.CVInfoDTO;
import com.juan.portfolio.service.CVInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CVInfoController.class)
class CVInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CVInfoService cvInfoService;

    private CVInfoDTO dummyDto(String title) {
        return new CVInfoDTO(title, null, null, null, null, null,
                null, null, null, null, Map.of(), null, null, null, List.of());
    }

    @Test
    void getInfo_withAcceptLanguageEn_callsServiceWithEn() throws Exception {
        when(cvInfoService.getInfo("en")).thenReturn(dummyDto("Backend Developer"));

        mockMvc.perform(get("/info")
                .header("Accept-Language", "en")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cvInfoService).getInfo(eq("en"));
    }

    @Test
    void getInfo_withAcceptLanguageEs_callsServiceWithEs() throws Exception {
        when(cvInfoService.getInfo("es")).thenReturn(dummyDto("Desarrollador Backend"));

        mockMvc.perform(get("/info")
                .header("Accept-Language", "es")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cvInfoService).getInfo(eq("es"));
    }

    @Test
    void getInfo_withNoAcceptLanguageHeader_callsServiceWithEn() throws Exception {
        when(cvInfoService.getInfo("en")).thenReturn(dummyDto("Backend Developer"));

        mockMvc.perform(get("/info").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cvInfoService).getInfo(eq("en"));
    }

    @Test
    void getInfo_respondsWithJson() throws Exception {
        when(cvInfoService.getInfo("en")).thenReturn(dummyDto("Backend Developer"));

        mockMvc.perform(get("/info").header("Accept-Language", "en"))
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
