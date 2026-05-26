package com.school.sptech.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class richardDAO {

    public List<Limites> buscarLimitesRede(int fkServidor) {
        List<Limites> listaLimites = new ArrayList<>();

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
            WHERE nome_componente = 'latencia_resposta_ms'
            OR nome_componente = 'net_bytes_sent_gb'
            OR nome_componente = 'net_bytes_recv_gb'
            OR nome_componente = 'jitter_ms'
            OR nome_componente = 'packet_loss_percent'
            OR nome_componente = 'upload_mbps'
            OR nome_componente = 'download_mbps'
            AND fk_servidor = ?;
    """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, fkServidor);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String nome_servidor = rs.getString("nome_servidor");
                    String nome_componente = rs.getString("nome_componente");
                    double max = rs.getDouble("valor_max_critico");
                    double min = rs.getDouble("valor_min_critico");

                    Limites limites = new Limites(nome_servidor, nome_componente, max, min);
                    listaLimites.add(limites);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar limites no MatheusDAO: " + e.getMessage());
            e.printStackTrace();
        }

        return listaLimites;
    }
}