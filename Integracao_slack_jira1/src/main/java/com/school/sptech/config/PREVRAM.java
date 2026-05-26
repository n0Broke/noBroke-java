package com.school.sptech.config;

import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PREVRAM {
    private float ram;
    private float swap;
    private float correlacao;
    private float tendencia_minuto;
    private float tendencia_hora;
    private float projecao_1;
    private float projecao_2;
    private float projecao_3;
    private float projecao_4;
    private float projecao_5;
    private String tempo_saturacao;
    private String mensagemSlack;
    private String summary;
    private String description;
    private int fk_servidor;

    luizDAO luiz = new luizDAO();
    String horario_alerta = String.valueOf(LocalDateTime.now());


    public void PegarValoresS3JSON(String json){
        JSONArray array = new JSONArray(json);
        JSONObject objeto = array.getJSONObject(array.length() - 1);

        this.ram = objeto.getFloat("ram_percent");
        this.swap =objeto.getFloat("swap_percent");
        this.correlacao = objeto.getFloat("correlacao_ram_swap");
        this.tendencia_minuto = objeto.getFloat("tendencia_ram_por_minuto");
        this.tendencia_hora = objeto.getFloat("tendencia_ram_por_hora");
        this.tempo_saturacao = objeto.getString("ETA");
        this.projecao_1 = objeto.getFloat("proj1");
        this.projecao_2 = objeto.getFloat("proj2");
        this.projecao_3 = objeto.getFloat("proj3");
        this.projecao_4 = objeto.getFloat("proj4");
        this.projecao_5 = objeto.getFloat("proj5");
        this.fk_servidor = objeto.getInt("id_servidor");
    }

    public Boolean Verificar_Alerta(){
        Limites limites = luiz.buscarLimitesLuiz(fk_servidor);
        if (correlacao >= 0.8 && tendencia_minuto > 0){
            this.mensagemSlack = """
                    Foi identificado uma correlação diretamente proporcional possivelmente contingente
                    
                    Servidor: %s
                    RAM atual: %.2f
                    Tendencia por minuto: %.2f
                    Tempo até a saturação: %s
                    Projecao para o próximo registro: %.2f
                    """
                    .formatted(
                            limites.nome_servidor,
                            (double) ram,
                            (double) tendencia_minuto,
                            tempo_saturacao,
                            (double) projecao_1
                    );
            this.summary = "Correlação alta com tendencia aumentando";

            this.description = "O servidor "
            + limites.nome_servidor +
            " obteve uma correlação alta e prevê um aumento exponencial do consumo de RAM";
            return true;
        }else if (correlacao >= 0.8 && projecao_1 >= luiz.buscarLimitesLuiz(fk_servidor).max){
            this.mensagemSlack = """
                    Uma projeção da RAM indica que o componente irá atingir seu limite estabelecido
                    
                    Servidor: %s
                    RAM atual: %.2f
                    Tendencia por minuto: %.2f
                    Tempo até a saturação: %s
                    Projeção para o próximo registro: %.2f
                    """

                    .formatted(
                            limites.nome_servidor,
                            (double) ram,
                            (double) tendencia_minuto,
                            tempo_saturacao,
                            (double) projecao_1
                    );
            this.summary = "Correlação alta com tendencia aumentando";

            this.description = "O servidor "
            + limites.nome_servidor +
            " obteve uma correlação alta está constando uma projeção do consumo de RAM que excede os limites que estabeleceu";

            return true;
        } else if (correlacao >= 0.8 && projecao_2 >= luiz.buscarLimitesLuiz(fk_servidor).max) {
            this.mensagemSlack = """
                    Uma projeção da RAM indica que o componente irá atingir seu limite estabelecido
                    
                    Servidor: %s
                    RAM atual: %.2f
                    Tendencia por minuto: %.2f
                    Tempo até a saturação: %s
                    Projeção para o próximo registro: %.2f
                    """

                    .formatted(
                            limites.nome_servidor,
                            (double) ram,
                            (double) tendencia_minuto,
                            tempo_saturacao,
                            (double) projecao_1
                    );
            this.summary = "Correlação alta com tendencia aumentando";

            this.description = "O servidor "
            + limites.nome_servidor +
            " obteve uma correlação alta está constando uma projeção do consumo de RAM que excede os limites que estabeleceu";

            return true;
        } else if (correlacao >= 0.8 && projecao_3 >= luiz.buscarLimitesLuiz(fk_servidor).max) {
            this.mensagemSlack = """
                    Uma projeção da RAM indica que o componente irá atingir seu limite estabelecido
                    
                    Servidor: %s
                    RAM atual: %.2f
                    Tendencia por minuto: %.2f
                    Tempo até a saturação: %s
                    Projeção para o próximo registro: %.2f
                    """

                    .formatted(
                            limites.nome_servidor,
                            (double) ram,
                            (double) tendencia_minuto,
                            tempo_saturacao,
                            (double) projecao_1
                    );
            this.summary = "Correlação alta com tendencia aumentando";

            this.description = "O servidor "
            + limites.nome_servidor +
            " obteve uma correlação alta está constando uma projeção do consumo de RAM que excede os limites que estabeleceu";

            return true;
        } else if(projecao_4 >= luiz.buscarLimitesLuiz(fk_servidor).max){
            this.mensagemSlack = """
                    Uma projeção da RAM indica que o componente irá atingir seu limite estabelecido
                    
                    Servidor: %s
                    RAM atual: %.2f
                    Tendencia por minuto: %.2f
                    Tempo até a saturação: %s
                    Projeção para o próximo registro: %.2f
                    """

                    .formatted(
                            limites.nome_servidor,
                            (double) ram,
                            (double) tendencia_minuto,
                            tempo_saturacao,
                            (double) projecao_1
                    );
            this.summary = "Correlação alta com tendencia aumentando";

            this.description = "O servidor "
            + limites.nome_servidor +
            " obteve uma correlação alta está constando uma projeção do consumo de RAM que excede os limites que estabeleceu";

            return true;
        } else if (correlacao >= 0.8 && projecao_5 >= luiz.buscarLimitesLuiz(fk_servidor).max) {
            this.mensagemSlack = """
                    Uma projeção da RAM indica que o componente irá atingir seu limite estabelecido
                    
                    Servidor: %s
                    RAM atual: %.2f
                    Tendencia por minuto: %.2f
                    Tempo até a saturação: %s
                    Projeção para o próximo registro: %.2f
                    """

                    .formatted(
                            limites.nome_servidor,
                            (double) ram,
                            (double) tendencia_minuto,
                            tempo_saturacao,
                            (double) projecao_1
                    );
            this.summary = "Correlação alta com tendencia aumentando";

            this.description = "O servidor "
            + limites.nome_servidor +
            " obteve uma correlação alta está constando uma projeção do consumo de RAM que excede os limites que estabeleceu";

            return true;
        }
        return false;
    }

    public String getHorario_alerta() {
        return horario_alerta;
    }

    public void setHorario_alerta(String horario_alerta) {
        this.horario_alerta = horario_alerta;
    }

    public luizDAO getLuiz() {
        return luiz;
    }

    public void setLuiz(luizDAO luiz) {
        this.luiz = luiz;
    }

    public int getFk_servidor() {
        return fk_servidor;
    }

    public void setFk_servidor(int fk_servidor) {
        this.fk_servidor = fk_servidor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getMensagemSlack() {
        return mensagemSlack;
    }

    public void setMensagemSlack(String mensagemSlack) {
        this.mensagemSlack = mensagemSlack;
    }

    public String getTempo_saturacao() {
        return tempo_saturacao;
    }

    public void setTempo_saturacao(String tempo_saturacao) {
        this.tempo_saturacao = tempo_saturacao;
    }

    public float getProjecao_5() {
        return projecao_5;
    }

    public void setProjecao_5(float projecao_5) {
        this.projecao_5 = projecao_5;
    }

    public float getProjecao_4() {
        return projecao_4;
    }

    public void setProjecao_4(float projecao_4) {
        this.projecao_4 = projecao_4;
    }

    public float getProjecao_3() {
        return projecao_3;
    }

    public void setProjecao_3(float projecao_3) {
        this.projecao_3 = projecao_3;
    }

    public float getProjecao_2() {
        return projecao_2;
    }

    public void setProjecao_2(float projecao_2) {
        this.projecao_2 = projecao_2;
    }

    public float getProjecao_1() {
        return projecao_1;
    }

    public void setProjecao_1(float projecao_1) {
        this.projecao_1 = projecao_1;
    }

    public float getTendencia_hora() {
        return tendencia_hora;
    }

    public void setTendencia_hora(float tendencia_hora) {
        this.tendencia_hora = tendencia_hora;
    }

    public float getTendencia_minuto() {
        return tendencia_minuto;
    }

    public void setTendencia_minuto(float tendencia_minuto) {
        this.tendencia_minuto = tendencia_minuto;
    }

    public float getCorrelacao() {
        return correlacao;
    }

    public void setCorrelacao(float correlacao) {
        this.correlacao = correlacao;
    }

    public float getSwap() {
        return swap;
    }

    public void setSwap(float swap) {
        this.swap = swap;
    }

    public float getRam() {
        return ram;
    }

    public void setRam(float ram) {
        this.ram = ram;
    }
}
