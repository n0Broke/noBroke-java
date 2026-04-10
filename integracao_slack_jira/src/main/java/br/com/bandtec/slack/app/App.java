package br.com.bandtec.slack.app;

import br.com.bandtec.slack.config.Slack;
import java.io.IOException;
import org.json.JSONObject;

/**
 *
 * @author Diego Brito <diego.lima@bandtec.com.br>
 */
public class App {

    public static void main(String[] args) throws IOException, InterruptedException {

        double cpu = 85.4;
        double RAM = 45.8;
        double Disco = 78.0;
        int latencia = 31;
        int processos = 289;

        
        JSONObject json = new JSONObject();

        json.put("text", "ALERTA DE CPU ACIMA DO LIMITE!" + "\n" +
                "CPU: " + cpu + "%" +"\n" +
                "RAM: " + RAM +"%" +"\n" +
                "Disco: " + Disco + "GB" +"\n" +
                "Latencia: "+ latencia + "ms" +"\n" +
                "processos: "+ processos);


        Slack.sendMessage(json);
    }
}
