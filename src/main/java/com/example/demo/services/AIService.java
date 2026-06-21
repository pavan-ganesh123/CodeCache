package com.example.demo.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.AIAnalysis;
import com.example.demo.dto.Content;
import com.example.demo.dto.GeminiRequest;
import com.example.demo.dto.GeminiResponse;
import com.example.demo.dto.Part;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AIAnalysis analyzeCode(String code) {

        String prompt = """
                Analyze the following code.

                Return ONLY valid JSON.

                {
                  "intuition":"",
                  "timeComplexity":"",
                  "spaceComplexity":""
                }

                Code:

                """ + code;

        GeminiRequest request =
                new GeminiRequest(
                        List.of(
                                new Content(
                                        List.of(
                                                new Part(prompt)
                                        )
                                )
                        )
                );

        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                        + apiKey;

        GeminiResponse response =
                restTemplate.postForObject(
                        url,
                        request,
                        GeminiResponse.class
                );

        String json =
                response.getCandidates()
                        .get(0)
                        .getContent()
                        .getParts()
                        .get(0)
                        .getText();

        // Remove markdown fences if Gemini returns them
        json = json.replace("```json", "")
                   .replace("```", "")
                   .trim();

        try {

            return objectMapper.readValue(
                    json,
                    AIAnalysis.class
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to parse Gemini response",
                    e
            );
        }
    }
}