package com.examen.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class ChatGptService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getDefinitionInMalagasy(String word) {
        try {
            // Corps du message
            Map<String, Object> message = Map.of(
                    "role", "user",
                    "content", "Hazavao amin'ny teny malagasy ilay teny hoe: " + word
            );

            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-3.5-turbo",
                    "messages", List.of(message)
            );

            // Création de la requête HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                    .build();

            // Envoi de la requête
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Debug : afficher le corps de la réponse
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());

            // Vérification de la réponse
            if (response.statusCode() != 200) {
                return "Nisy olana tamin'ny fangatahana. Code: " + response.statusCode();
            }

            Map<?, ?> json = objectMapper.readValue(response.body(), Map.class);
            Object choicesRaw = json.get("choices");

            if (choicesRaw instanceof List<?> choicesList && !choicesList.isEmpty()) {
                var firstChoice = (Map<?, ?>) choicesList.get(0);
                var messageObj = (Map<?, ?>) firstChoice.get("message");
                return (String) messageObj.get("content");
            } else {
                return "Tsy misy valiny azo avy amin'ny API.";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Tsy afaka nanao famaritana noho ny olana.";
        }
    }
}
