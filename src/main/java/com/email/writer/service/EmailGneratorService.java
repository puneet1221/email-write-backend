package com.email.writer.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.email.writer.dto.EmailRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EmailGneratorService {
	private final WebClient webclient;

	public EmailGneratorService(WebClient.Builder clientbuilder) {
		this.webclient = clientbuilder.build();
	}

	@Value("${gemini-api-url}")
	private String geminiApiUrl;
	@Value("${gemini-api-key}")
	private String geminiApiKey;

	public String generateEmailReply(EmailRequest emailRequest) {
		// Build the prompt
		String prompt = buildPrompt(emailRequest);

		// Build the format
		Map<String, Object> requestBody = Map.of("contents",
				new Object[] { Map.of("parts", new Object[] { Map.of("text", prompt) }) });

		// Send the Request & get Rrespinse
		String response = webclient.post().uri(geminiApiUrl).header("X-goog-api-key", geminiApiKey) /*
																									 * MakerSuite keys
																									 * are free API keys
																									 * provided by
																									 * Google AI Studio
																									 * (formerly
																									 * MakerSuite) to
																									 * let developers
																									 * use Gemini models
																									 * (like gemini-pro,
																									 * gemini-1.5-flash,
																									 * etc.) without
																									 * needing Google
																									 * Cloud billing.
																									 * 
																									 * 
																									 */
				.header("Content-Type", "application/json").bodyValue(requestBody).retrieve().bodyToMono(String.class)
				.block();
		// extract the content from response
		return extractResponseContent(response);

	}

	private String extractResponseContent(String response) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(response);
			return rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
		} catch (Exception ex) {
			return "Error Processing request " + ex.getMessage();
		}
	}

	private String buildPrompt(EmailRequest emailRequest) {
		StringBuilder sb = new StringBuilder();
		sb.append("Create a  reply for the following " + "email content. Please dont add subject line.");
		if (emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
			sb.append("Use a ").append(emailRequest.getTone()).append(" tone");
		}
		sb.append("\n Original Email:\n").append(emailRequest.getEmailContent());
		return sb.toString();
	}
}
