package com.school.sptech.config;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.pdfa.PdfADocument;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RelatorioHandler implements HttpHandler{

    // definindo as cores que vao ser usadas no relatorio (funciona igual o var do JS que a gente usou pra fazer a mudança de tema claro e escuro)
    private static final DeviceRgb AZUL_ESCURO = new DeviceRgb(26, 60, 110);
    private static final DeviceRgb CINZA = new DeviceRgb(242, 242, 242);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            Jira jiraClient = new Jira(
                    "",
                    "",
                    ""
            );
            String respostaJira = jiraClient.searchIssuesCampo("project = TES AND assignee is not EMPTY", "assignee", "status");

            List<String> nomes = new ArrayList<>();
            List<Integer> resolvido = new ArrayList<>();
            List<Integer> aberto = new ArrayList<>();

            int i = 0;

            while (true) {
                int idxNome = respostaJira.indexOf("\"displayName\":\"", i);
                if (idxNome == -1) break;

                int inicio = idxNome + 15;
                int fim = respostaJira.indexOf("\"", inicio);
                String nome = respostaJira.substring(inicio, fim);

                int idxStatus = respostaJira.indexOf("\"status\":{", fim);
                int idxNomeStatus = respostaJira.indexOf("\"name\":\"", idxStatus);
                int inicioStatus = idxNomeStatus + 8;
                int fimStatus = respostaJira.indexOf("\"", inicioStatus);
                String status = respostaJira.substring(inicioStatus, fimStatus);
                boolean feito = status.equals("Feito");

                int posicao = nomes.indexOf(nome);
                if (posicao == -1) {
                    nomes.add(nome);
                    if (feito) {
                        resolvido.add(1);
                        aberto.add(0);
                    } else {
                        resolvido.add(0);
                        aberto.add(1);
                    }
                } else {
                    if (feito) {
                        resolvido.set(posicao, resolvido.get(posicao) + 1);
                    } else {
                        aberto.set(posicao, aberto.get(posicao) + 1);
                    }
                }
                i = fim + 1;
            }

            // cria o pdf em memoria
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            doc.setMargins(40, 40, 40, 40);

            // Adiciona dois titulos no pdf
            doc.add(new Paragraph("Produtividade da Equipe"))
                    .setFontSize(18).setBold()
                    .setFontColor(AZUL_ESCURO)
                    .setTopMargin(20);

            String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            doc.add(new Paragraph("Gerado em: " + dataHora)
                    .setBackgroundColor(AZUL_ESCURO).setFontColor(ColorConstants.WHITE)
                    .setPadding(6).setTextAlignment(TextAlignment.CENTER));

            // cria uma tabela com 3 colunas, os numeros sao as porcentagens de largura de cada coluna
            float[] columnWidths = {50f, 25f, 25f};
            Table tabela = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

            // cabeçalho da tabela
            // percorre o array de titulos e cria uma celula de cabeçalho para cada um, com o fundo azul e o texto centralizado
            String[] headers = {"Nome do Membro", "Tickets Resolvidos", "Tickets Abertos"};

            for (String h : headers) {
                tabela.addHeaderCell(new Cell().add(new Paragraph(h).setBold())
                        .setBackgroundColor(AZUL_ESCURO).setFontColor(ColorConstants.WHITE)
                        .setPadding(6).setTextAlignment(TextAlignment.CENTER));
            }

            // linhas da tabela
            // pra cada membro ele adiciona uma linha na tabela, o par alterna entre true e false a cada volta fazendo as linhas ficarem cinza e branca
            boolean par = false;
            for (int j = 0; j < nomes.size(); j++) {
                DeviceRgb bg;

                if (par == true) {
                    bg = CINZA;
                    par = false;
                } else {
                    bg = new DeviceRgb(255, 255, 255);
                    par = true;
                }

                tabela.addCell(celula(nomes.get(j), bg, TextAlignment.LEFT));
                tabela.addCell(celula(String.valueOf(resolvido.get(j)), bg, TextAlignment.CENTER));
                tabela.addCell(celula(String.valueOf(aberto.get(j)), bg, TextAlignment.CENTER));
            }

            doc.add(tabela);

            // por fim, ele fecha e envia o pdf
            doc.close();

            byte[] pdfBytes = baos.toByteArray();

            exchange.getResponseHeaders().add("Content-Type", "application/pdf");
            exchange.getResponseHeaders().add("Content-Disposition", "attachment; filename=\"relatorio-membros.pdf\"");
            exchange.sendResponseHeaders(200, pdfBytes.length);

            OutputStream os = exchange.getResponseBody();
            os.write(pdfBytes);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            String erro = "{\"erro\": \"Erro ao gerar PDF\"}";
            byte[] bytes = erro.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(500, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.getResponseBody().close();
        }
    }

    // apenas um "atalho" pra nao ficar repetindo codigo, em vez de escrever toda a formatação da celula toda vez
    private Cell celula(String texto, DeviceRgb bg, TextAlignment align) {
        return new Cell().add(new Paragraph(texto).setFontSize(10))
                .setBackgroundColor(bg).setPadding(5)
                .setTextAlignment(align);
    }
}

