package com.school.sptech.config;

import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.util.List;

public class Componentes {
    private int idServidor;
    private Double CPU;
    private Double RAM;
    private Double DISCO;
    private String mensagemSlack;
    private String summary;
    private String description;

    MatheusDAO matheus = new MatheusDAO();

    public void PegarValoresJsonMatheus(String jsonMatheus) {
        JSONArray array = new JSONArray(jsonMatheus);
        JSONObject objeto = array.getJSONObject(array.length() - 1);

        this.idServidor = objeto.getInt("id_servidor");
        this.CPU = objeto.getDouble("cpu_percent");
        this.RAM = objeto.getDouble("ram_percent");
        this.DISCO = objeto.getDouble("disk_percent");
    }

    public Boolean ValidarAlerta() {
        List<Limites> listaLimites = matheus.buscarLimitesMatheus(idServidor);

        for (Limites limites : listaLimites) {
            if (limites.nome_componente.equalsIgnoreCase("cpu_percent") && CPU >= limites.max) {
                this.mensagemSlack = """
                        Porcentagem de Uso da CPU está acima do limite máximo
                        
                        Servidor: %s
                        
                        Porcentagem atual: %.2f
                        Limite máximo: %.2f
                        """.formatted(limites.nome_servidor, CPU, limites.max);

                this.summary = "Porcentagem da CPU acima do limite máximo";
                this.description = "O servidor " + limites.nome_servidor + " está com a porcentagem da CPU acima do limite crítico!";
                return true;
            } else if (limites.nome_componente.equalsIgnoreCase("ram_percent") && RAM >= limites.max) {
                this.mensagemSlack = """
                        Porcentagem de Uso da RAM está acima do limite máximo
                        
                        Servidor: %s
                        
                        Porcentagem atual: %.2f
                        Limite máximo: %.2f
                        """.formatted(limites.nome_servidor, RAM, limites.max);

                this.summary = "Porcentagem da RAM acima do limite máximo";
                this.description = "O servidor " + limites.nome_servidor + " está com a porcentagem da RAM acima do limite crítico!";
                return true;
            } else if (limites.nome_componente.equalsIgnoreCase("disk_percent") && DISCO >= limites.max) {
                this.mensagemSlack = """
                        Porcentagem de Uso da DISCO está acima do limite máximo
                        
                        Servidor: %s
                        
                        Porcentagem atual: %.2f
                        Limite máximo: %.2f
                        """.formatted(limites.nome_servidor, DISCO, limites.max);

                this.summary = "Porcentagem da DISCO acima do limite máximo";
                this.description = "O servidor " + limites.nome_servidor + " está com a porcentagem da DISCO acima do limite crítico!";
                return true;
            }
        }
        this.mensagemSlack = "Servidor estável.";
        this.summary = "Status OK - Componentes normais";
        this.description = "Todos os índices estão abaixo do limite crítico.";
        return false;
    }

    public Double getCPU() { return CPU; }
    public void setCPU(Double CPU) { this.CPU = CPU; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getDISCO() { return DISCO; }
    public void setDISCO(Double DISCO) { this.DISCO = DISCO; }

    // Corrigido para pegar dinamicamente o momento da requisição
    public String getHorarioAtual() { return String.valueOf(LocalDateTime.now()); }

    public int getIdServidor() { return idServidor; }
    public void setIdServidor(int idServidor) { this.idServidor = idServidor; }
    public String getMensagemSlack() { return mensagemSlack; }
    public void setMensagemSlack(String mensagemSlack) { this.mensagemSlack = mensagemSlack; }
    public Double getRAM() { return RAM; }
    public void setRAM(Double RAM) { this.RAM = RAM; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}

