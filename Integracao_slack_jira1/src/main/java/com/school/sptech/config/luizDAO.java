package com.school.sptech.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class luizDAO {
    public Limites buscarLimitesLuiz(Integer fk_servidor) {
        String sql = """
        SELECT
            servidor.nome AS nome_servidor,
            tipo_componente.nome_componente,
            tipo_componente.fk_componente,
            tipo_componente.valor_max_critico,
            tipo_componente.valor_min_critico
            FROM tipo_componente
            JOIN servidor
            ON tipo_componente.fk_servidor = servidor.id_servidor
            WHERE nome_componente = 'ram_percent'
            AND fk_servidor = ?;
        """;

        // Gerencia o fechamento da conexão de forma automática e segura
        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, fk_servidor);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nome_servidor = rs.getString("nome_servidor");
                    String nome_componente = rs.getString("nome_componente");
                    double max = rs.getDouble("valor_max_critico");
                    double min = rs.getDouble("valor_min_critico");

                    Limites limites = new Limites(nome_servidor, nome_componente, max, min);
                    return limites;
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar limites no MatheusDAO: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}