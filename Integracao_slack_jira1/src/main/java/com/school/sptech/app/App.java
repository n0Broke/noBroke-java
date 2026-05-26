package com.school.sptech.app;

import com.school.sptech.config.*;
import com.school.sptech.env;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;

public class App {
    public static void main(String[] args) throws Exception {

        Conexao.testarConexao();

        Jira jiraService = new Jira(
                "",
                "",
                ""
        );

        // Loop contínuo de monitoramento em segundo plano
        while (true) {
            System.out.println("\n=== Iniciando Varredura: " + LocalDateTime.now() + " ===");

            // ------------------ FLUXO ISA ------------------
            try {
                String json = S3Conexao.S3Service.buscarJson("buckettestenobroke", "CLIENT/isa.json");
                HTTP http = new HTTP();
                http.PegarValoresJsonISA(json);

                if (http.ValidarAlerta()) {
                    System.out.println("[ALERTA CRÍTICO - ISA] Enviando para Slack e Jira...");
                    JSONObject mensagem = new JSONObject();
                    mensagem.put("text", http.getMensagemSlack());
                    Slack.sendMessage(mensagem);

                    Jira jira = new Jira(env.BASEURL.getValor(), env.EMAIL.getValor(), env.APITOKEN.getValor());
                    String response = jira.createIssue("NOB", http.getSummary(), "task", http.getDescription());
                    System.out.println("[JIRA RESP - ISA] " + response);
                } else {
                    System.out.println("[STATUS OK - ISA] Servidor estável.");
                }
            } catch (Exception e) {
                System.err.println("[ERRO - FLUXO ISA] " + e.getMessage());
            }

            // ------------------ FLUXO MATHEUS ------------------
            try {
                // Alterado para chamar o método unificado 'buscarJson'
                String jsonMatheus = S3Conexao.S3Service.buscarJson("buckettestenobroke", "CLIENT/matheus.json");
                Componentes componente = new Componentes();
                componente.PegarValoresJsonMatheus(jsonMatheus);

                if (componente.ValidarAlerta()) {
                    System.out.println("[ALERTA CRÍTICO - MATHEUS] Enviando para Slack e Jira...");
                    JSONObject mensagem = new JSONObject();
                    mensagem.put("text", componente.getMensagemSlack());
                    Slack.sendMessage(mensagem);

                    Jira jira = new Jira(env.BASEURL.getValor(), env.EMAIL.getValor(), env.APITOKEN.getValor());
                    String response = jira.createIssue("NOB", componente.getSummary(), "task", componente.getDescription());
                    System.out.println("[JIRA RESP - MATHEUS] " + response);
                } else {
                    System.out.println("[STATUS OK - MATHEUS] " + componente.getSummary());
                }
            } catch (Exception e) {
                System.err.println("[ERRO - FLUXO MATHEUS] " + e.getMessage());
            }

            try {
                System.out.println("Verificando dados no S3 para alertas");

                String json = S3Conexao.S3Service.buscarJson("buckettestenobroke", "CLIENT/gabrielly.json");
                JSONObject dados = new JSONObject(json);

                double taxaSla = dados.getDouble("taxa_sla");
                JSONArray membros = dados.getJSONArray("membros");

                // Alerta de SLA
                if (taxaSla < 60) {
                    JSONObject msg = new JSONObject();
                    msg.put("text", "*ALERTA!! - Taxa de SLA Crítica!* \n" +
                            "Taxa: " + taxaSla + "% (Mínimo é 60%)");
                    Slack.sendMessage(msg);
                }

                int totalAbertos = 0;

                // Alerta Individual
                for (int i = 0; i < membros.length(); i++) {
                    JSONObject membro = membros.getJSONObject(i);
                    String nome = membro.getString("nome");
                    int abertos = membro.getInt("abertos");

                    totalAbertos += abertos;

                    if (abertos > 10) {
                        JSONObject msg = new JSONObject();
                        msg.put("text", "*ALERTA!! - Sobrecarga!*\n" +
                                nome + " está com " + abertos + " tarefas (Limite: 10).");
                        Slack.sendMessage(msg);
                    }
                }

                // Alerta de Carga Geral
                if (membros.length() > 0) {
                    int capacidadeMaxima = membros.length() * 10;
                    int carga = (int) Math.round((double) totalAbertos / capacidadeMaxima * 100);

                    if (carga > 80) {
                        JSONObject msg = new JSONObject();
                        msg.put("text", "*ALERTA - Carga de Trabalho Alta!*\n" +
                                "Carga: " + carga + "% (Limite: 80%).");
                        Slack.sendMessage(msg);
                    }
                }


            } catch (Exception e) {
                System.out.println(" Erro ao verificar S3:");
                e.printStackTrace();
            }

            try {
                // Alterado para chamar o método unificado 'buscarJson'
                String jsonLuiz = S3Conexao.S3Service.buscarJson("buckettestenobroke", "CLIENT/luiz.json");
                PREVRAM prevram = new PREVRAM();
                prevram.PegarValoresS3JSON(jsonLuiz);

                if (prevram.Verificar_Alerta()) {
                    System.out.println("[ALERTA CRÍTICO - Luiz] Enviando para Slack e Jira...");
                    JSONObject mensagem = new JSONObject();
                    mensagem.put("text", prevram.getMensagemSlack());
                    Slack.sendMessage(mensagem);

                    Jira jira = new Jira(env.BASEURL.getValor(), env.EMAIL.getValor(), env.APITOKEN.getValor());
                    String response = jira.createIssue("NOB", prevram.getSummary(), "task", prevram.getDescription());
                    System.out.println("[JIRA RESP - Luiz] " + response);
                } else {
                    System.out.println("[STATUS OK - Luiz] " + prevram.getSummary());
                }
            } catch (Exception e) {
                System.err.println("[ERRO - FLUXO Luiz] " + e.getMessage());
            }

            // Richard

            try {
                String jsonRede = S3Conexao.S3Service.buscarJson(
                        "buckettestenobroke",
                        "CLIENT/richard.json"
                );

                Rede rede = new Rede();
                rede.pegarValoresJsonRede(jsonRede);

                if (rede.validarAlerta()) {
                    JSONObject mensagemRede = new JSONObject();
                    mensagemRede.put("text", rede.getMensagemSlack());
                    Slack.sendMessage(mensagemRede);

                    String responseRede = jiraService.createIssue(
                            "NOB",
                            rede.getSummary(),
                            "Task",
                            rede.getDescription()
                    );
                    System.out.println(responseRede);

                } else {
                    System.out.println("[STATUS OK - Richard] " + rede.getSummary());
                }
            } catch (Exception e) {
                System.err.println("[ERRO - FLUXO Richard] " + e.getMessage());
            }

            System.out.println("=== Aguardando 30 segundos para o próximo ciclo ===");
            Thread.sleep(30000);
        }
    }
}
