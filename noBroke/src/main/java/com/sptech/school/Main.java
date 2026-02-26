package com.sptech.school;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Scanner sc = new Scanner(System.in);

        System.out.print("Olá! Insira seu nome: ");
        String name = sc.next();

        System.out.printf("Boas vindas ao sistema de algoritimo Java %s!\n", name);

        TimeUnit.SECONDS.sleep(3);

        System.out.println("Processando...");

        TimeUnit.SECONDS.sleep(3);

        System.out.println("Processo realizado com sucesso!");
    }
}
