package com.school.sptech.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IsaDAO {
    public List<Limites> buscarLimitesISA(Integer fk_servidor) {
        List<Limites> listaLimites = new ArrayList<>();

        String sql = """
        SELECT
            servidor.nome AS nome_servidor,
            tipo_componente.nome_componente,
            tipo_componente.valor_max_critico,
            tipo_componente.valor_min_critico
            FROM tipo_componente
            JOIN servidor
            ON tipo_componente.fk_servidor = servidor.id_servidor
            WHERE nome_componente LIKE '%HTTP%'
            OR nome_componente LIKE '%ordens%'
            OR nome_componente LIKE  '%rro%'
            OR nome_componente LIKE '%requisições%'
            AND fk_servidor = ?;
    """;

        try {
            Connection conexao = Conexao.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql);

            stmt.setInt(1, fk_servidor);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String nome_servidor = rs.getString("nome_servidor");
                String nome_componente = rs.getString("nome_componente");
                double max = rs.getDouble("valor_max_critico");
                double min = rs.getDouble("valor_min_critico");

                Limites limites = new Limites(nome_servidor, nome_componente, max, min);
                listaLimites.add(limites);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listaLimites;
    }
}
