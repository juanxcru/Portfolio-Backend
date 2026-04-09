package com.juan.portfolio.service;

import com.juan.portfolio.model.dto.CVInfoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CVInfoServiceTest {

    private CVInfoService service;

    @BeforeEach
    void setUp() throws IOException {
        service = new CVInfoService(new ObjectMapper());
    }

    // --- idioma ---

    @Test
    void getInfo_withEnglishLocale_returnsTitleInEnglish() {
        CVInfoDTO dto = service.getInfo("en");

        assertThat(dto).isNotNull();
        assertThat(dto.title()).isEqualTo("Backend Developer");
    }

    @Test
    void getInfo_withSpanishLocale_returnsTitleInSpanish() {
        CVInfoDTO dto = service.getInfo("es");

        assertThat(dto).isNotNull();
        assertThat(dto.title()).isEqualTo("Desarrollador Backend");
    }

    @Test
    void getInfo_withUnknownLocale_fallsBackToEnglish() {
        CVInfoDTO dto = service.getInfo("zh");

        assertThat(dto).isNotNull();
        assertThat(dto.title()).isEqualTo("Backend Developer");
    }

    @Test
    void getInfo_withNullLocale_fallsBackToEnglish() {
        CVInfoDTO dto = service.getInfo(null);

        assertThat(dto).isNotNull();
        assertThat(dto.title()).isEqualTo("Backend Developer");
    }

    @Test
    void getInfo_withLocalePrefix_esMX_resolvedAsSpanish() {
        CVInfoDTO dto = service.getInfo("es-MX");

        assertThat(dto).isNotNull();
        assertThat(dto.title()).isEqualTo("Desarrollador Backend");
    }

    // --- campos de texto plano ---

    @Test
    void getInfo_emailIsAlwaysTheSame() {
        CVInfoDTO dto = service.getInfo("en");

        assertThat(dto).isNotNull();
        assertThat(dto.email()).isEqualTo("dinarjuanc@gmail.com");
    }

    @Test
    void getInfo_githubLinkIsPresent() {
        CVInfoDTO dto = service.getInfo("en");

        assertThat(dto).isNotNull();
        assertThat(dto.github()).isEqualTo("https://github.com/juanxcru");
    }

    // --- stack ---

    @Test
    void getInfo_stackContainsExpectedCategories() {
        CVInfoDTO dto = service.getInfo("en");

        assertThat(dto).isNotNull();
        Map<String, List<String>> stack = dto.stack();
        assertThat(stack).containsKeys("frameworks", "languages", "database", "other");
    }

    @Test
    void getInfo_stackFrameworksIncludesSpring() {
        CVInfoDTO dto = service.getInfo("en");

        assertThat(dto).isNotNull();
        assertThat(dto.stack().get("frameworks")).contains("Spring");
    }

    // --- experience ---

    @Test
    void getInfo_experienceListIsNotEmpty() {
        CVInfoDTO dto = service.getInfo("en");

        assertThat(dto).isNotNull();
        assertThat(dto.experience()).isNotEmpty();
    }

    @Test
    void getInfo_firstExperienceHasCompany() {
        CVInfoDTO dto = service.getInfo("en");

        assertThat(dto).isNotNull();
        assertThat(dto.experience().get(0).company()).isNotBlank();
    }

    @Test
    void getInfo_experienceRoleChangesWithLocale() {
        CVInfoDTO en = service.getInfo("en");
        CVInfoDTO es = service.getInfo("es");

        assertThat(en).isNotNull();
        assertThat(es).isNotNull();

        assertThat(en.experience().get(0).role()).isEqualTo("Backend Developer");
        assertThat(es.experience().get(0).role()).isEqualTo("Desarrollador Backend");
    }

    @Test
    void getInfo_experienceBulletsArePresent() {
        CVInfoDTO dto = service.getInfo("en");

        assertThat(dto).isNotNull();
        dto.experience().forEach(exp ->
            assertThat(exp.bullets()).isNotEmpty()
        );
    }
}
