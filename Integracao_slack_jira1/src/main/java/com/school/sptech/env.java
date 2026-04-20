package com.school.sptech;

public enum env {

    BASEURL("https://sptech-nobroke-project.atlassian.net/"),
    EMAIL("isabela.rodrigues@sptech.school"),
    APITOKEN("ATATT3xFfGF0bQ9nP1ruLFAXRQYaY14HKzYVM9oR-9eOzkbxv0Gf9OiyR6iZk9o9swi3CzqJXUymYL6TYJGIFwrJZ91RVrCqKydzzlyc2A6sXRk0GT_0paOqndYp9gUS8fb3_n76bPu5kBwjuu1g6ZZBKBWJMksV3d1d9e2Jk-fKnFVubI-Oc7E=36CADC93");

    private final String valor;

    env(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
