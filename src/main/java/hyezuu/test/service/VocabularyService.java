package hyezuu.test.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hyezuu.test.constant.Constant;
import hyezuu.test.domain.Vocabulary;
import hyezuu.test.repository.VocabularyRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class VocabularyService {

    private final VocabularyRepository repository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${alan.api.default-url}")
    private String defaultUrl;

    @Value("${alan.api.client-id}")
    private String clientId;


    @Transactional
    public void parseVocabularyFromJson(String json) {
        try {
            JsonNode rootNode = objectMapper.readTree(json);
            String content = rootNode.path("content").asText();

            List<Vocabulary> vocabularies = extractVocabularies(content);

            for (Vocabulary vocabulary : vocabularies) {
                saveVocabulary(vocabulary);
            }
        } catch (IOException e) {
            log.error("Error parsing JSON: ", e);
            throw new RuntimeException("Failed to parse vocabulary from JSON", e);
        }
    }

    private List<Vocabulary> extractVocabularies(String content) {
        List<Vocabulary> vocabularies = new ArrayList<>();
        Pattern pattern = Pattern.compile(
            "(\\d+)\\. vocabulary: (.+?)\\s+meaning: (.+?)\\s+example: (.+?)(?=\\n\\d+\\.|\\n)",
            Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String word = matcher.group(2).trim();
            String meaning = matcher.group(3).trim();
            String example = matcher.group(4).trim();
            vocabularies.add(new Vocabulary(word, meaning, example));
        }

        return vocabularies;
    }


    @Scheduled(cron = "10 55 6 * * *")
    @Transactional
    public void scheduleApiCallAndParse() {
        String level = "A2"; // 또는 다른 방식으로 레벨을 결정
        String jsonResponse = callApi(defaultUrl, clientId, level);
        parseVocabularyFromJson(jsonResponse);
    }

    public String callApi(String apiUrl, String clientId, String level) {
        String content = String.format(Constant.DEFAULT_PROMPT, level);
        String requestUrl = String.format("%s?content=%s&client_id=%s", apiUrl, content, clientId);
        log.info("Calling API: {}", requestUrl);
        String response = restTemplate.getForObject(requestUrl, String.class);
        log.info("API response received");
        log.info("Response: {}", response);
        return response;
    }

    @Transactional
    public void saveVocabulary(Vocabulary vocabulary) {
        if (!repository.existsByVocabulary(vocabulary.getVocabulary())) {
            repository.save(vocabulary);
            log.info("Saved new vocabulary: {}", vocabulary.getVocabulary());
        } else {
            log.info("Vocabulary already exists: {}", vocabulary.getVocabulary());
        }
    }
}