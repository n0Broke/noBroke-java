package com.school.sptech.config;

import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.util.List;

public class HTTP {
    private Integer idServidor;
    private int volume;
    private double latencia;
    private int erro5xx;
    private int qtd_sucessos;
    private int contador_erro_tecnico;
    private int contador_500;
    private int contador_501;
    private int contador_502;
    private int contador_503;
    private int contador_504;
    private int contador_505;
    private int distribuicao_erros;
    private String mensagemSlack;
    private String summary;
    private String description;

    IsaDAO isa = new IsaDAO();
    String horarioAtual = String.valueOf(LocalDateTime.now());

    public void PegarValoresJsonISA(String json){
        JSONArray array = new JSONArray(json);

        JSONObject objeto = array.getJSONObject(array.length() - 1);

        this.idServidor = objeto.getInt("id_servidor");
        this.volume = objeto.getInt("total_volume");
        this.latencia = objeto.getDouble("latencia_p95_ordens");
        this.erro5xx = objeto.getInt("contador_5xx");
        this.qtd_sucessos = objeto.getInt("qtd_sucesso");
        this.contador_erro_tecnico = 0;
        this.contador_500 = objeto.getInt("erro_500");
        this.contador_501 = objeto.getInt("erro_501");
        this.contador_502 =objeto.getInt("erro_502");
        this.contador_503 = objeto.getInt("erro_503");
        this.contador_504 = objeto.getInt("erro_504");
        this.contador_505 = objeto.getInt("erro_505");
        this.distribuicao_erros = objeto.getInt("qtd_erro_servidor");
        for (int i = 0; i < objeto.length(); i++) {
            if (objeto.getString("tipo_status").equals("erro_servidor")){
                contador_erro_tecnico++;
            }
        }
    }

    public Boolean ValidarAlerta() {
        List<Limites> listaLimites = isa.buscarLimitesISA(idServidor);

        for (Limites limites : listaLimites) {
            if (limites.nome_componente.equalsIgnoreCase("total_volume") && volume >= limites.max) {

                this.mensagemSlack = """
                        Volume de requisições está acima do limite máximo!
                        
                        Servidor: %s
                        
                        Volume atual: %.2f
                        Volume máximo: %.2f
                        """
                        .formatted(
                                limites.nome_servidor,
                                (double) volume,
                                limites.max
                        );

                this.summary = "Volume de requisições acima do limite máximo";

                this.description = "O servidor "
                        + limites.nome_servidor +
                        " está com o volume de requisições acima do limite crítico!";

                return true;

            } else if (limites.nome_componente.equalsIgnoreCase("latencia_p95_ordem") && latencia >= limites.max) {

                this.mensagemSlack = """
                        Latência P95 das ordens está acima do limite máximo!
                        
                        Servidor: %s
                        
                        Latência atual: %.2f
                        Latência máxima: %.2f
                        """
                        .formatted(
                                limites.nome_servidor,
                                latencia,
                                limites.max
                        );

                this.summary = "Latência P95 acima do limite máximo";

                this.description = "O servidor "
                        + limites.nome_servidor +
                        " está com a latência P95 acima do limite crítico!";

                return true;

            } else if (limites.nome_componente.equalsIgnoreCase("contador_5xx") && erro5xx >= limites.max) {

                this.mensagemSlack = """
                        Quantidade de erros 5xx está acima do limite máximo!
                        
                        Servidor: %s
                        
                        Quantidade atual: %.2f
                        Quantidade máxima: %.2f
                        """
                        .formatted(
                                limites.nome_servidor,
                                (double) erro5xx,
                                limites.max
                        );

                this.summary = "Quantidade de erros 5xx acima do limite máximo";

                this.description = "O servidor "
                        + limites.nome_servidor +
                        " está com a quantidade de erros 5xx acima do limite crítico!";

                return true;

            } else if (limites.nome_componente.equalsIgnoreCase("total_sucesso_ordens") && qtd_sucessos <= limites.min) {

                this.mensagemSlack = """
                        Quantidade de sucessos está abaixo do limite mínimo!
                        
                        Servidor: %s
                        
                        Quantidade atual: %.2f
                        Quantidade mínima: %.2f
                        """
                        .formatted(
                                limites.nome_servidor,
                                (double) qtd_sucessos,
                                limites.min
                        );

                this.summary = "Quantidade de sucessos abaixo do limite mínimo";

                this.description = "O servidor "
                        + limites.nome_servidor +
                        " está com a quantidade de sucessos abaixo do limite crítico!";
                return true;
            }
        }
        return false;
    }

    public Integer getIdServidor() {
        return idServidor;
    }

    public void setIdServidor(Integer idServidor) {
        this.idServidor = idServidor;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public double getLatencia() {
        return latencia;
    }

    public void setLatencia(double latencia) {
        this.latencia = latencia;
    }

    public int getErro5xx() {
        return erro5xx;
    }

    public void setErro5xx(int erro5xx) {
        this.erro5xx = erro5xx;
    }

    public int getQtd_sucessos() {
        return qtd_sucessos;
    }

    public void setQtd_sucessos(int qtd_sucessos) {
        this.qtd_sucessos = qtd_sucessos;
    }

    public int getContador_erro_tecnico() {
        return contador_erro_tecnico;
    }

    public void setContador_erro_tecnico(int contador_erro_tecnico) {
        this.contador_erro_tecnico = contador_erro_tecnico;
    }

    public int getContador_500() {
        return contador_500;
    }

    public void setContador_500(int contador_500) {
        this.contador_500 = contador_500;
    }

    public int getContador_501() {
        return contador_501;
    }

    public void setContador_501(int contador_501) {
        this.contador_501 = contador_501;
    }

    public int getContador_502() {
        return contador_502;
    }

    public void setContador_502(int contador_502) {
        this.contador_502 = contador_502;
    }

    public int getContador_503() {
        return contador_503;
    }

    public void setContador_503(int contador_503) {
        this.contador_503 = contador_503;
    }

    public int getContador_504() {
        return contador_504;
    }

    public void setContador_504(int contador_504) {
        this.contador_504 = contador_504;
    }

    public int getContador_505() {
        return contador_505;
    }

    public void setContador_505(int contador_505) {
        this.contador_505 = contador_505;
    }

    public int getDistribuicao_erros() {
        return distribuicao_erros;
    }

    public void setDistribuicao_erros(int distribuicao_erros) {
        this.distribuicao_erros = distribuicao_erros;
    }

    public String getMensagemSlack() {
        return mensagemSlack;
    }

    public void setMensagemSlack(String mensagemSlack) {
        this.mensagemSlack = mensagemSlack;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IsaDAO getIsa() {
        return isa;
    }

    public void setIsa(IsaDAO isa) {
        this.isa = isa;
    }

    public String getHorarioAtual() {
        return horarioAtual;
    }

    public void setHorarioAtual(String horarioAtual) {
        this.horarioAtual = horarioAtual;
    }
}

