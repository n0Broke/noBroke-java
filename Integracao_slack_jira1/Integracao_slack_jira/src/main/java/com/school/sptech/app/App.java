package com.school.sptech.app;

import com.school.sptech.config.Jira;
import com.school.sptech.config.Slack;
import com.school.sptech.env;
import org.json.JSONObject;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {

        JSONObject json = new JSONObject();


        json.put("text", """
                CPU Acima do Limite Crítico! ⚠️\s
                CPU: 95%\s
                LIMITE: 90%
                SERVIDOR: SRV-PROD-API-01
                DESCRIÇÃO: Uso de CPU atingiu 95%, acima do limite de 90%
                DATA: 22/09/2025
                HORARIO: 14:39""");

            Slack.sendMessage(json);


            String baseUrl = env.BASEURL.getValor();
            String email = env.EMAIL.getValor();
            String apiToken = env.APITOKEN.getValor();
            Jira jira = new Jira(baseUrl, email, apiToken);

       String response = jira.createIssue(
                "NOB",
                "CPU Acima do Limite Crítico",
                "Task"
       );

            System.out.println(response);
    }
}
