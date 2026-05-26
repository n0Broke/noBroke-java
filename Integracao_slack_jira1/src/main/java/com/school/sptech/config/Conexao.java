package com.school.sptech.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Conexao {

    private static final String URL = "jdbc:mysql://localhost:3306/noBroke";
    private static final String USUARIO = "root";
    private static final String SENHA = "sptech";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    public static void testarConexao() {
        try {

            Connection conexao = conectar();

            System.out.println("Conectou no banco!");

            conexao.close();

        } catch (SQLException e) {

            System.out.println("Erro ao conectar!");
            e.printStackTrace();

        }

    }
}