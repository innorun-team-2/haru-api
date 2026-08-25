package org.example.haruapi.global.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

@Service
public class SlackNotificationService {

    private final RestClient restClient = RestClient.create();

    @Value("${slack.webhook-url}")
    private String webhookUrl;

    public void sendException(Exception e) {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        String stackTrace = sw.toString();

        if (stackTrace.length() > 2500) {
            stackTrace = stackTrace.substring(0, 2500)
                    + "\n... (truncated)";
        }

        String message =
                "🚨 *HARU API 500 ERROR*\n\n" +
                        "*Exception:* `" + e.getClass().getSimpleName() + "`\n" +
                        "*Message:* " + e.getMessage() + "\n\n" +
                        "*Stack Trace:*\n```" +
                        stackTrace +
                        "```";

        try {
            String response = restClient.post()
                    .uri(webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("text", message))
                    .retrieve()
                    .body(String.class);

            System.out.println("Slack response: " + response);

        } catch (Exception slackException) {
            System.err.println("Slack notification failed!");
            slackException.printStackTrace();
        }
    }
}