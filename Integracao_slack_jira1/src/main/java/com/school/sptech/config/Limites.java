package com.school.sptech.config;

public class Limites {

    public double max;
    public double min;
    public String nome_servidor;
    public String nome_componente;
    public String fk_componente;

    public Limites(String nome_servidor, String nome_componente, double max, double min) {
        this.nome_servidor = nome_servidor;
        this.nome_componente = nome_componente;
        this.fk_componente = fk_componente;
        this.max = max;
        this.min = min;
    }
}
