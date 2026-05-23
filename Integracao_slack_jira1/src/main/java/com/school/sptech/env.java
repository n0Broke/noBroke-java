package com.school.sptech;

public enum env {

    BASEURL(""),
    EMAIL(""),
    APITOKEN("");

    private final String valor;

    env(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
