package com.school.sptech.config;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class Rede {

    private int id_servidor;
    private String nome_servidor;
    private String hora_coleta;
    private double latencia_resposta_ms;
    private double net_bytes_sent_gb;
    private double net_bytes_recv_gb;
    private double jitter_ms;
    private double packet_loss_percent;
    private double upload_mbps;
    private double download_mbps;

    private String mensagemSlack;
    private String summary;
    private String description;

    richardDAO dao = new richardDAO();

    public void pegarValoresJsonRede(String json) {
        JSONArray array = new JSONArray(json);
        JSONObject objeto = array.getJSONObject(array.length() - 1);

        this.id_servidor = objeto.getInt("id_servidor");
        this.nome_servidor = objeto.getString("home_broker");
        this.hora_coleta = objeto.getString("timestamp");
        this.latencia_resposta_ms = objeto.getDouble("latencia_resposta_ms");
        this.net_bytes_sent_gb = objeto.getDouble("net_bytes_sent_gb");
        this.net_bytes_recv_gb = objeto.getDouble("net_bytes_recv_gb");
        this.jitter_ms = objeto.getDouble("jitter_ms");
        this.packet_loss_percent = objeto.getDouble("packet_loss_percent");
        this.upload_mbps = objeto.getDouble("upload_mbps");
        this.download_mbps = objeto.getDouble("download_mbps");
    }

    public Boolean validarAlerta() {
        List<Limites> listaLimites = dao.buscarLimitesRede(id_servidor);

        for (Limites limites : listaLimites) {

            if (limites.nome_componente.equalsIgnoreCase("latencia_resposta_ms") && latencia_resposta_ms >= limites.max) {
                this.mensagemSlack = """
                        Latência de resposta acima do limite máximo!
                        
                        Servidor: %s
                        Horário da coleta: %s
                        
                        Latência atual: %.2f ms
                        Latência máxima: %.2f ms
                        """.formatted(nome_servidor, hora_coleta, latencia_resposta_ms, limites.max);

                this.summary = "Latência de resposta acima do limite máximo";
                this.description = "O servidor " + nome_servidor + " está com latência de resposta de "
                        + latencia_resposta_ms + " ms, acima do limite de " + limites.max + " ms.";

                return true;

            } else if (limites.nome_componente.equalsIgnoreCase("jitter_ms") && jitter_ms >= limites.max) {
                this.mensagemSlack = """
                        Jitter de rede acima do limite crítico!
                        
                        Servidor: %s
                        Horário da coleta: %s
                        
                        Jitter atual: %.2f ms
                        Jitter máximo: %.2f ms
                        """.formatted(nome_servidor, hora_coleta, jitter_ms, limites.max);

                this.summary = "Jitter de rede acima do limite crítico";
                this.description = "O servidor " + nome_servidor + " está com jitter de "
                        + jitter_ms + " ms, acima do limite de " + limites.max + " ms.";

                return true;

            } else if (limites.nome_componente.equalsIgnoreCase("packet_loss_percent") && packet_loss_percent >= limites.max) {
                this.mensagemSlack = """
                        Perda de pacotes acima do limite máximo!
                        
                        Servidor: %s
                        Horário da coleta: %s
                        
                        Perda atual: %.2f%%
                        Perda máxima: %.2f%%
                        """.formatted(nome_servidor, hora_coleta, packet_loss_percent, limites.max);

                this.summary = "Perda de pacotes acima do limite máximo";
                this.description = "O servidor " + nome_servidor + " está com perda de pacotes de "
                        + packet_loss_percent + "%, acima do limite de " + limites.max + "%.";

                return true;

            } else if (limites.nome_componente.equalsIgnoreCase("net_bytes_sent_gb") && net_bytes_sent_gb >= limites.max) {
                this.mensagemSlack = """
                        Volume de dados enviados acima do limite máximo!
                        
                        Servidor: %s
                        Horário da coleta: %s
                        
                        Enviado atual: %.2f GB
                        Máximo permitido: %.2f GB
                        """.formatted(nome_servidor, hora_coleta, net_bytes_sent_gb, limites.max);

                this.summary = "Volume de bytes enviados acima do limite máximo";
                this.description = "O servidor " + nome_servidor + " enviou " + net_bytes_sent_gb
                        + " GB, acima do limite de " + limites.max + " GB.";

                return true;

            } else if (limites.nome_componente.equalsIgnoreCase("net_bytes_recv_gb") && net_bytes_recv_gb >= limites.max) {
                this.mensagemSlack = """
                        Volume de dados recebidos acima do limite máximo!
                        
                        Servidor: %s
                        Horário da coleta: %s
                        
                        Recebido atual: %.2f GB
                        Máximo permitido: %.2f GB
                        """.formatted(nome_servidor, hora_coleta, net_bytes_recv_gb, limites.max);

                this.summary = "Volume de bytes recebidos acima do limite crítico";
                this.description = "O servidor " + nome_servidor + " recebeu " + net_bytes_recv_gb
                        + " GB, acima do limite de " + limites.max + " GB.";

                return true;

            } else if (limites.nome_componente.equalsIgnoreCase("upload_mbps") && upload_mbps <= limites.min) {
                this.mensagemSlack = """
                        Velocidade de upload abaixo do limite máximo!️
                        
                        Servidor: %s
                        Horário da coleta: %s
                        
                        Upload atual: %.2f Mbps
                        Mínimo esperado: %.2f Mbps
                        """.formatted(nome_servidor, hora_coleta, upload_mbps, limites.min);

                this.summary = "Upload abaixo do limite mínimo";
                this.description = "O servidor " + nome_servidor + " está com upload de "
                        + upload_mbps + " Mbps, abaixo do mínimo de " + limites.min + " Mbps.";

                return true;

            } else if (limites.nome_componente.equalsIgnoreCase("download_mbps") && download_mbps <= limites.min) {
                this.mensagemSlack = """
                        Velocidade de download abaixo do limite máximo!️
                        
                        Servidor: %s
                        Horário da coleta: %s
                        
                        Download atual: %.2f Mbps
                        Mínimo esperado: %.2f Mbps
                        """.formatted(nome_servidor, hora_coleta, download_mbps, limites.min);

                this.summary = "Download abaixo do limite mínimo";
                this.description = "O servidor " + nome_servidor + " está com download de "
                        + download_mbps + " Mbps, abaixo do mínimo de " + limites.min + " Mbps.";

                return true;
            }
        }
        this.mensagemSlack = "Servidor estável.";
        this.summary = "Status OK - Rede estável";
        this.description = "Todos os índices estão abaixo do limite crítico.";
        return false;
    }

    public String getMensagemSlack() {
        return mensagemSlack;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }
}