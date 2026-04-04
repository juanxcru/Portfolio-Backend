package com.juan.portfolio.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juan.portfolio.model.dto.CVInfoDTO;
import com.juan.portfolio.model.dto.ExperienceDTO;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class CVInfoService {
    private final JsonNode rawJson;

    public CVInfoService(ObjectMapper mapper) throws IOException {
        ClassPathResource resource = new ClassPathResource("cvinfo.json");
        try (InputStream is = resource.getInputStream()) {
            this.rawJson = mapper.readTree(is);
        }
    }

    public ResponseEntity<?> getInfo(String lang){

        String _lang = normalizeLang(lang);

        return ResponseEntity.ok(new CVInfoDTO(
                resolveTxt(rawJson.get("title"),_lang),
                resolveTxt(rawJson.get("subtitle"),_lang),
                resolveTxt(rawJson.get("availability"),_lang),
                resolveTxt(rawJson.get("avail_short"),_lang),
                resolveTxt(rawJson.get("coverLetter"),_lang),
                resolveTxt(rawJson.get("location"),_lang),
                resolveTxt(rawJson.get("email"),_lang),
                resolveTxt(rawJson.get("whatsapp"),_lang),
                resolveTxt(rawJson.get("github"),_lang),
                resolveTxt(rawJson.get("linkedin"),_lang),
                resolveStackMap(),
                resolveTxt(rawJson.get("bio1"),_lang),
                resolveTxt(rawJson.get("bio2"),_lang),
                resolveTxt(rawJson.get("bio3"),_lang),
                resolveExp(_lang)));
    }

    private String resolveTxt(JsonNode node, String lang){
        if (node == null || node.isNull()) {
            return null;
        }

        if (node.isTextual()) { // no lang specified in json
            return node.asText();
        }

        if( node.isObject()){ // expected lang in json (es-en)
           JsonNode value = node.get(lang);
            if (value != null && value.isTextual()) {
                return value.asText();
            }

            JsonNode fallback = node.get("en");
            if (fallback != null && fallback.isTextual()) {
                return fallback.asText();
            }
        }

        return null;

    }

    /*

    "experience":  -> Node
    [
      { -> Node
        "company": "",
        "role": {"es": "", "en": "" }, --> Node
        "from": "",
        "to": "",
        "description":{"es":"", "en": ""}, --> Node
        "bullets": [
          "", ""
        ]
      }
    * */

    private List<ExperienceDTO> resolveExp(String lang){
        if (rawJson == null){
            return List.of();
        }
        JsonNode expNode = rawJson.get("experience");
        if (expNode == null || !expNode.isArray()) {
            return List.of();
        }

        List<ExperienceDTO> list = new ArrayList<>();

        for (JsonNode it : expNode){
            list.add(new ExperienceDTO(
                    it.path("company").asText(" "),
                    resolveTxt(it.get("role"),lang),
                    it.path("from").asText(" "),
                    it.path("to").asText(" "),
                    resolveTxt(it.get("description"),lang),
                    resolveStrArray(it.get("bullets"), lang)
                    )
            );

        }

        return List.copyOf(list);

    }

    private Map<String,List<String>> resolveStackMap(){
        if (rawJson == null){
            return Map.of();
        }
        JsonNode stackNode = rawJson.get("stack");
        if (stackNode.isObject()){
            Map<String, List<String>> map = new HashMap<>();

            //iterate fields
            for (Iterator<String> iter = stackNode.fieldNames(); iter.hasNext(); ) {
                List<String> strArray = new ArrayList<>();
                String key = iter.next();

                if (stackNode.get(key).isArray()) { // str array
                    for(JsonNode val : stackNode.get(key) ) {
                        strArray.add(val.asText(" "));
                    }
                }else if (stackNode.get(key).isTextual()){ // if not array (should be)
                    strArray.add(stackNode.get(key).asText(" "));
                }else{
                    strArray.add(" "); //always some string if error....
                }
                map.put(key, strArray);
            }

            return Map.copyOf(map);
        }

        return Map.of();
    }

    private List<String> resolveStrArray (JsonNode node, String lang){

        if (node == null){
            return List.of();
        }

        if (node.isArray()){
            List<String> array = new ArrayList<>();

            for(JsonNode n : node){
                if (n.isObject()){  // language
                    array.add(n.get(lang).asText(" "));
                }else if( n.isTextual()){
                    array.add(n.asText(" "));
                }else{
                    return List.of();
                }
            }
            return List.copyOf(array);
        }

        return List.of();
    }

    private String normalizeLang(String lang) {
        if (lang == null) return "en";
        lang = lang.toLowerCase();
        if (lang.startsWith("es")) return "es";
        if (lang.startsWith("en")) return "en";
        return "en";
    }

}
