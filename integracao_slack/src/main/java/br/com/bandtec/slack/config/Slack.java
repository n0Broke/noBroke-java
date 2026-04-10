package br.com.bandtec.slack.config;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

/**
 *
 * @author Diego Brito <diego.lima@bandtec.com.br>
 */
public class Slack {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String URL = System.getenv("SLACK_WEBHOOK_URL");

    public static void sendMessage(JSONObject content) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(content.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.printf("Status: %s%n", response.statusCode());

        System.out.printf("Response: %s%n", response.body());
    }
}
