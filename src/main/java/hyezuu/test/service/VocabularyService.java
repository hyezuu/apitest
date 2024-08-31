package hyezuu.test.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hyezuu.test.constant.Constant;
import hyezuu.test.domain.Grade;
import hyezuu.test.domain.Test;
import hyezuu.test.domain.Vocabulary;
import hyezuu.test.repository.TestRepository;
import hyezuu.test.repository.VocabularyRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class VocabularyService {

    private final VocabularyRepository vocabularyRepository;
    private final TestRepository testRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${alan.api.default-url}")
    private String defaultUrl;

    @Value("${alan.api.client-id}")
    private String clientId;

    @Async
    @Scheduled(cron = "30 37 14 * * *")
    public void scheduleApiCallAndParse() {
        for(String grade : Grade.getGrades()){
            String jsonResponse = callApi(defaultUrl, clientId, grade);
            parseVocabularyFromJson(jsonResponse, grade);
        }
    }

    public String callApi(String apiUrl, String clientId, String grade) {
        String content = String.format(Constant.DEFAULT_PROMPT, grade);
        String requestUrl = String.format("%s?content=%s&client_id=%s", apiUrl, content, clientId);
        log.info("Calling API: {}", requestUrl);
        String response = restTemplate.getForObject(requestUrl, String.class);
        log.info("API response received");
        log.info("Response: {}", response);
        return response;
    }

    @Transactional
    public void parseVocabularyFromJson(String json, String grade) {
        try {
            JsonNode rootNode = objectMapper.readTree(json);
            String content = rootNode.path("content").asText();

            List<Vocabulary> vocabularies = new ArrayList<>();
            List<Test> tests = new ArrayList<>();

            Pattern pattern = Pattern.compile("(\\d+)word: (\\w+)\\s+meaning: (.+?)\\s+exampleSentence: (.+?)\\s+question: (.+\\))");
            Matcher matcher = pattern.matcher(content);

            while (matcher.find()) {
                String word = matcher.group(2);
                String meaning = matcher.group(3);
                String exampleSentence = matcher.group(4);
                String question = matcher.group(5);

                Vocabulary vocabulary = Vocabulary.builder()
                    .word(word)
                    .meaning(meaning)
                    .exampleSentence(exampleSentence)
                    .grade(Grade.valueOf(grade))
                    .build();
                vocabularies.add(vocabulary);

                Test test = Test.builder()
                    .grade(Grade.valueOf(grade))
                    .question(question)
                    .answer(word)
                    .build();
                tests.add(test);
            }

            saveVocabularies(vocabularies);
            saveTests(tests);

            log.info("Parsed and saved {} vocabularies and tests for grade {}", vocabularies.size(), grade);
        } catch (IOException e) {
            log.error("Error parsing JSON: ", e);
            throw new RuntimeException("Failed to parse vocabulary from JSON", e);
        }
    }

    @Transactional
    public void saveVocabularies(List<Vocabulary> vocabularies) {
        vocabularies.removeIf(
            vocabulary -> vocabularyRepository.existsByWord(vocabulary.getWord()));
        vocabularyRepository.saveAll(vocabularies);
    }

    @Transactional
    public void saveTests(List<Test> tests) {
        tests.removeIf(test -> testRepository.existsByAnswer(test.getAnswer()));
        testRepository.saveAll(tests);
    }

    @Transactional
    public String extractValue(String line, String prefix) {
        return line.substring(line.indexOf(prefix) + prefix.length()).trim();
    }
}