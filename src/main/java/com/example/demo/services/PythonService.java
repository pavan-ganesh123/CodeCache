package com.example.demo.services;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.ProblemMetadata;

@Service
public class PythonService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HttpServletRequest httpServletRequest;

    public ProblemMetadata fetchProblemDetails(String link) {

        Map<String, String> request =
                Map.of("link", link);

        // Get Authorization header from current request
        String authHeader =
                httpServletRequest.getHeader("Authorization");

        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", authHeader);

        HttpEntity<Map<String, String>> entity =
                new HttpEntity<>(request, headers);

        ResponseEntity<ProblemMetadata> response =
                restTemplate.exchange(
                        "http://localhost:5000/fetch-problem",
                        HttpMethod.POST,
                        entity,
                        ProblemMetadata.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException(
                    "Failed to fetch problem details from Python backend");
        }

        if (response.getBody() == null) {
            throw new RuntimeException(
                    "Python backend returned empty response");
        }

        return response.getBody();
    }
}