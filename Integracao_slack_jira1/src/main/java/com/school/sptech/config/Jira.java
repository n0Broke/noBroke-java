package com.school.sptech.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

public class Jira {

    private static String baseUrl = "";
    private static String authHeader = "";
    private static HttpClient httpClient = null;
    private final ObjectMapper objectMapper;

    public Jira(String baseUrl, String email, String apiToken) {
        if (baseUrl != null && baseUrl.endsWith("/")) {
            this.baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        } else {
            this.baseUrl = baseUrl;
        }

        // AGORA, usamos as variáveis de classe já corrigidas
        String auth = email + ":" + apiToken;
        this.authHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        this.objectMapper = new ObjectMapper();
    }

    private static HttpRequest.Builder baseRequest() {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/rest/api/3/issue"))
                .timeout(Duration.ofSeconds(60))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
    }

    private static String sendRequest(HttpRequest request) throws Exception {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        int status = response.statusCode();

        if (status >= 200 && status < 300) {
            return response.body();
        }

        throw new RuntimeException("Jira request failed: " + status + " - " + response.body());
    }

    public static String createIssue(String projectKey, String summary, String issueType, String description) throws Exception {

        String jsonbody = """
                {
                  "fields": {
                    "project": {
                      "key": "%s"
                    },
                    "summary": "%s",
                    "description": {
                      "type": "doc",
                      "version": 1,
                      "content": [
                        {
                          "type": "paragraph",
                          "content": [
                            {
                              "type": "text",
                              "text": "%s"
                            }
                          ]
                        }
                      ]
                    },
                    "issuetype": {
                      "name": "%s"
                    },
                    "assignee": null
                  }
                }
                """.formatted(projectKey, summary, description, issueType);

        HttpRequest request = baseRequest()
                .POST(HttpRequest.BodyPublishers.ofString(jsonbody))
                .build();

        return sendRequest(request);
    }

    // metodo que vai retornar uma string da resposta do jira
    public static String searchIssues(String jql) throws Exception {
        System.out.println("Executando search issues");

        // endpoint
        // pega a url base (baseUrl) junto com a rota oficial da api do jira feita para buscas ("/rest/api/3/search/jql")
        // URI.create junta essa url base com a rota e forma uma url valida que o java consegue ler
        URI searchUri = URI.create(baseUrl + "/rest/api/3/search/jql");

        // payload
        // corpo que vai enviar para a api no formato em JSON
        // contexto: json usa aspas duplas para definir chaves e valores, se a pesquisa do jira tiver aspas duplas tambem o json vai quebrar, no replace ele coloca
        // barras invertidas antes das aspas da pesquisa para avisar ao computador considerar as aspas como um texto comum, nao como um fechamento
        String requestBody = String.format("{\"jql\": \"%s\", \"maxResults\": 50}", jql.replace("\"", "\\\""));

        // request = requisição http
        // uri e timeout define o destino e encerra a tentativa em 60 segundos
        // headers sao os metadados da requisição:
        //  "Authorization" passa a chave de segurança gerada
        //  "Content-Type" avisa pro jira que esta enviando o metadado no formato json
        //  "Accept" avisa pro jira responder em formato json
        // POST é o metodo http. O GET busca os dados na url, e o POST é usado para enviar o payload para o servidor processar
        // sendRequest basicamente pega todo esse objeto http e passa pra função sendRequest que devolve a string json com os dados da tarefas e o return joga
        // essa string para fora do metodo para ser lida pela tela
        HttpRequest request = HttpRequest.newBuilder()
                .uri(searchUri)
                .timeout(Duration.ofSeconds(60))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return sendRequest(request);
    }

    public String searchIssuesData(String jql) throws Exception {
        URI searchUri = URI.create(baseUrl + "/rest/api/3/search/jql");

        String requestBody = String.format("{\"jql\": \"%s\", \"maxResults\": 50, \"fields\": [\"created\", \"resolutiondate\"]}", jql.replace("\"", "\\\""));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(searchUri)
                .timeout(Duration.ofSeconds(60))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
    return sendRequest(request);
    }

    public String searchIssuesCampo(String jql, String campo1, String campo2) throws Exception {
        URI searchUri = URI.create(baseUrl + "/rest/api/3/search/jql");

        String requestBody = String.format(
                "{\"jql\": \"%s\", \"maxResults\": 50, \"fields\": [\"%s\", \"%s\", \"summary\"]}",
                jql.replace("\"", "\\\""), campo1, campo2
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(searchUri)
                .timeout(Duration.ofSeconds(60))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return sendRequest(request);
    }
}
