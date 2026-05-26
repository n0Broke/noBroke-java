package com.school.sptech.config;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

// class que faz a ponte entre o fron e o java
public class Servidor {
    public static void main(String[] args) throws IOException {



        // cria um servidor na porta 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // endpoints
        // routing - mapeamento urls para classes especifias (handles)
        server.createContext("/api/kpis", new KpiHandler());
        server.createContext("/api/alertas-abertos", new KpiAlertaHandler());
        server.createContext("/api/sla", new SlaHandler());
        server.createContext("/api/membros", new UserHandler());
        server.createContext("/api/mttr", new mttrHandler());
        server.createContext("/api/relatorio/pdf", new RelatorioHandler());
        server.start();
        System.out.println("Servidor rodando na porta 8080");
    }

    // classes implemtenta o HttpHandles que faz ser obrigado usar handle  a variavel exchange contem tudo sobre a requisição do front e a resposta que vai ser enviada
    static class KpiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                // "Access-Control-Allow-Origin" sem essa linha o navegador bloqueia a requisição, porque o front esta tentando acessar o back e o astericos libera o acesso
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

                // "Content-Type" avisa o front que os dados que vao chegar é um json usando caracteres normais, no caso o utf-8
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");

                // credenciais e chave do projeto do jira fazendo a integração
                Jira jiraClient = new Jira(
                        "",
                        "",
                        ""
                );

                // aqui ele filtra as tarefas do projeto TES que ja foram concluidos, a variavel respostaJira vai guardar o texto que o jira ira devolver
                String respostaJira = jiraClient.searchIssues("project = NOB AND status = Feito");

                // contando os tickets
                // basicamente ele busca a palavra id e conta quantas vezes ela aparece
                // o indexof procura onde ta esse texto, se ele existir ele retorna a sua posição, se ele nao encontrar nada, ele retorna -1
                // (-1 porque o 0 ja esta ocupado na contagem de caracter)
                // o while diz que quando exisitir uma variavel ele vai contando +1 , contando exatamente quantas tarefas vieram
                int total = 0;
                int indice = respostaJira.indexOf("\"id\":");
                while (indice != -1) {
                    total++;
                    indice = respostaJira.indexOf("\"id\":", indice + 1);
                }

                String resposta = "{\"incidentesResolvidos\": " + total + "}";

                // essa linha pega o texto e transforma em bytes pois o servidor web do java se recusa a enviar uma string pela rede, funciona como um tradutor, literalmente
                // getBytes - o tradutor, ele vai pegar a variavel de texto (resposta) e manda o java "cortar" ela, transformando cada letra, em simbolos no seu equivalente numerico
                // StandardCharsets.UTF_8 -precisa de um dicionario para saber qual numero representa cada letra
                // e por fim byte[]  que é o resultado dessa tradução, o java precisar guardar isso em algum lugar, entao ele cria uma lista de bytes
                byte[] bytes = resposta.getBytes(StandardCharsets.UTF_8);

                // 200 - numero de codigo de status http, signifca OK deu tudo certo, e o bytes.length conta quantos bytes tem la dentro, se preparando para recber o arquivo
                // com a quantidade de bytes informado
                exchange.sendResponseHeaders(200, bytes.length);

                // OutputStream - joga em alta quantidade os dados pela rede
                // getResponseBody - da acesso ao corpo da requisição e guarda na variavel "os"
                OutputStream os = exchange.getResponseBody();

                // pega a variavel bytes, que viaja atraves da rede pelo OutputStream
                // e por fim, os.close encerra a conexão com a rede
                // OutputStream consome muita RAM e sem o os.close ele fica aberto e assim travando por falta de memoria
                os.write(bytes);
                os.close();

            } catch (Exception e) {
                e.printStackTrace();
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                String erro = "{\"erro\": \"Ocorreu um erro interno\"}";
                byte[] bytes = erro.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(500, bytes.length);
                exchange.getResponseBody().write(bytes);
                exchange.getResponseBody().close();
            }
        }
    }

    static class KpiAlertaHandler implements  HttpHandler {
        // usa o mesmo esquema que a classe anterior
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");

                Jira jiraClient = new Jira(
                        "",
                        "",
                        ""
                );
                String respostaJira = jiraClient.searchIssues("project = NOB AND status != Feito");


                int total = 0;
                int indice = respostaJira.indexOf("\"issues\":");
                if (indice != -1) {
                    String apenasIssues = respostaJira.substring(indice);
                    int pos = 0;
                    while (true) {
                        int found = apenasIssues.indexOf("\"id\":\"", pos);
                        if (found == -1) break;
                        total++;
                        pos = found + 1;
                    }
                }

                System.out.println("Total calculado  " + total);

                String resposta = "{\"alertasAbertos\": " + total + "}";

                byte[] bytes = resposta.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();

            } catch (Exception e) {
                e.printStackTrace();
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                String erro = "{\"erro\": \"Ocorreu um erro interno\"}";
                byte[] bytes = erro.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(500, bytes.length);
                exchange.getResponseBody().write(bytes);
                exchange.getResponseBody().close();
            }


        }
    }

    static class SlaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException{
            try {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");

                Jira jiraClient = new Jira(
                        "",
                        "",
                        ""
                );

                String respostaJira = jiraClient.searchIssuesData("project = NOB AND status = Feito");
                System.out.println("Resposta SLA " + respostaJira);

                int totalCards = 0;
                int dentroSla =0;

                int i =0;
                while(true) {
                    // procura a palavra created, o 'i' é 0, ele procura e acha o primeiro ticket e a variavel idxCreated passa a ser o valor de 'i'
                    // mesma coisa pro de baixo
                    int idxCreated = respostaJira.indexOf("\"created\":\"", i);
                    int idxResolved = respostaJira.indexOf("\"resolutiondate\":\"", i);

                    // e quando o while da a ultima volta, e ve se acabou os tickets, ele para a procurar(o 'i' começa do 0, portanto se nao tiver mais ticket, ele
                    // volta como -1
                    if(idxCreated == -1 ||idxResolved == -1) break;

                    // essa soma basicamente pula os caracteres para pegar apenas a data
                    // no json o valor sempre termina quando aparece outra aspas, o java procura a proxima aspa e começa a procura a partir do inicioCreated
                    // o java vai lendo a data  e quando acha uma aspa ele anota a posição dessa aspa final
                    int inicioCreated = idxCreated + 11;
                    int fimCreated    = respostaJira.indexOf("\"", inicioCreated);
                    String created    = respostaJira.substring(inicioCreated, fimCreated);

                    int inicioResolved = idxResolved + 18;
                    int fimResolved    = respostaJira.indexOf("\"", inicioResolved);
                    String resolved    = respostaJira.substring(inicioResolved, fimResolved);

                    // converte o texto da data (porque ela chega com formato de texto) e transforma em um objeto de data
                    // java.time.OffsetDateTime - o tipo do objeto
                    // .parse(created ...) vai basicamente traduzir a variavel created
                    // java.time.format.DateTimeFormatter - vai dizer exatamente a ordem que os numeros aparecem no texto do jira
                    java.time.OffsetDateTime dtCreated  = java.time.OffsetDateTime.parse(created, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxx"));
                    java.time.OffsetDateTime dtResolved = java.time.OffsetDateTime.parse(resolved, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxx"));

                    // Duration - classe que mede a distancia no tempo
                    // between(dtCreated, dtResolved) - ele basicamente subtrai o tempo que o chamado foi aberto e o tempo que ele foi resolvido e depois transforma no formato certo
                    long horas = java.time.Duration.between(dtCreated, dtResolved).toHours();

                    totalCards++;
                    if(horas < 24) {
                        dentroSla++;
                    }

                    i= idxResolved +1;
                }

                // faz a porcentagem
                double taxaSla = 0;
                if (totalCards > 0) {
                    taxaSla = ((double) dentroSla / totalCards) * 100;
                }

                // transforma o numero (que provavelmente pode vir quebrado)
                String taxaFormatada = String.format("%.1f", taxaSla).replace(",", ".");

                String resposta = "{\"taxaSla\": " + taxaFormatada + "}";

                byte[] bytes = resposta.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
                exchange.getResponseBody().close();

            } catch (Exception e) {
                e.printStackTrace();
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                String erro = "{\"erro\": \"Erro ao calcular SLA\"}";
                byte[] bytes = erro.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(500, bytes.length);
                exchange.getResponseBody().write(bytes);
                exchange.getResponseBody().close();
            }
        }
    }

    static class UserHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");

                Jira jiraClient = new Jira(
                        "",
                        "",
                        ""
                );

                String respostaJira = jiraClient.searchIssuesCampo("project = NOB AND assignee is not EMPTY", "assignee", "status");
                System.out.println("JSON COMPLETO: " + respostaJira);


                List<String> nomes = new ArrayList<>();
                List<Integer> resolvido = new ArrayList<>();
                List<Integer> aberto = new ArrayList<>();

                int i = 0;

                while (true) {
                    // depois de ter feito todas as classes com a soma eu lembrei do .length e fiz essa pra ver como ficaria e funcionou exatamente igual ao outro
                    // porem nao vou mudar de todos, mas vou deixarr esse aqui

                    // define as chaves exatas que queremos buscar no texto do JSON
                    String chaveNome = "\"displayName\":\"";
                    String chaveStatus = "\"name\":\"";

                    // busca a posição inicial do nome a partir do cursor 'i'
                    // se retornar -1 significa que acabaram os tickets e o laço deve ser encerrado
                    int idxNome = respostaJira.indexOf(chaveNome, i);
                    if (idxNome == -1) break;

                    // pega a posição da etiqueta e soma o tamanho da própria palavra
                    // isso pula a etiqueta e aponta exatamente para a primeira letra do nome
                    int inicio = idxNome + chaveNome.length();

                    // procura a próxima aspa dupla que encerra o nome
                    int fim = respostaJira.indexOf("\"", inicio);

                    // recorta o nome usando as posições de início e fim resultando no nome limpo
                    String nome = respostaJira.substring(inicio, fim);

                    // mesma coisa aqui
                    int idxStatus = respostaJira.indexOf("\"status\":{", fim);
                    int idxNomeStatus = respostaJira.indexOf(chaveStatus, idxStatus);

                    int inicioStatus = idxNomeStatus + chaveStatus.length();
                    int fimStatus = respostaJira.indexOf("\"", inicioStatus);
                    String status = respostaJira.substring(inicioStatus, fimStatus);

                    boolean feito = status.equals("Feito");

                    int posicao = nomes.indexOf(nome);

                    if (posicao == -1 ) {
                        nomes.add(nome); // se nao tiver na lista, adiciona
                        if(feito) {
                            resolvido.add(1); // 1 ponto de resolvido
                            aberto.add(0); // 0 ponto de aberto
                        } else {
                            resolvido.add(0); // 0 ponto de resolvido
                            aberto.add(1); // 1 ponto de aberto
                        }
                    } else{ // se ja estiver na lista
                        if(feito) {
                            resolvido.set(posicao, resolvido.get(posicao) + 1); // vai na posição que achou o nome na lista e atualiza o numero
                        } else {
                            aberto.set(posicao, aberto.get(posicao) + 1);
                        }
                    }

                    i = fim + 1;

                }

                // cria um "molde" pra guardar os três dados juntos num só objeto, em vez de três listas separadas
                class membro {
                    String nome;
                    int resolvido;
                    int aberto;
                    public membro(String n, int r, int a) { nome=n; resolvido=r; aberto=a; }
                }

                List<membro> listaMembros = new ArrayList<>();
                for(int j = 0; j < nomes.size(); j++) {
                    listaMembros.add(new membro(nomes.get(j), resolvido.get(j), aberto.get(j)));
                }

                // ordena os elementos e o .compare inverte a ordem, fazendo uma ordem descrescnete (do maior pro menor, do mais critico pro menos critico)
                listaMembros.sort((m1, m2) -> Integer.compare(m2.aberto, m1.aberto));

                // O StringBuilder vai acumulando tudo no mesmo lugar na memória
                // no final chama .toString uma só vez pra transformar em texto
                StringBuilder sb = new StringBuilder();

                sb.append("{\"membros\": [");
                for(int j = 0; j < listaMembros.size(); j++) {
                    sb.append("{");
                    sb.append("\"nome\": \"").append(listaMembros.get(j).nome).append("\", ");
                    sb.append("\"resolvidos\": ").append(listaMembros.get(j).resolvido).append(", ");
                    sb.append("\"abertos\": ").append(listaMembros.get(j).aberto);
                    sb.append("}");
                    if (j < listaMembros.size() - 1) sb.append(", ");
                }
                sb.append("]}");

                String resposta = sb.toString();
                byte[] bytes = resposta.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
                exchange.getResponseBody().close();
        } catch (Exception e) {
                e.printStackTrace();
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                String erro = "{\"erro\": \"Erro ao buscar membros\"}";
                byte[] bytes = erro.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(500, bytes.length);
                exchange.getResponseBody().write(bytes);
                exchange.getResponseBody().close();
            }
    }
}

    static class mttrHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");

                Jira jiraClient = new Jira(
                        "",
                        "",
                        ""
                );

                String respostaJira = jiraClient.searchIssuesData("project = NOB AND status = Feito");

                List<Long> tempos = new ArrayList<>();

                int i = 0;
                while(true) {
                    // procura a palavra created, o 'i' é 0, ele procura e acha o primeiro ticket e a variavel idxCreated passa a ser o valor de 'i'
                    // mesma coisa pro de baixo
                    int idxCreated = respostaJira.indexOf("\"created\":\"", i);
                    int idxResolved = respostaJira.indexOf("\"resolutiondate\":\"", i);

                    // e quando o while da a ultima volta, e ve se acabaram os tickets, ele para a procura (o 'i' começa do 0, portanto se nao tiver mais ticket, ele
                    // volta como -1
                    if (idxCreated == -1 || idxResolved == -1) break;

                    // essa soma basicamente pula os caracteres para pegar apenas a data
                    // no json o valor sempre termina quando aparece outra aspas, o java procura a proxima aspa e começa a procura a partir do inicioCreated
                    // o java vai lendo a data  e quando acha uma aspa ele anota a posição dessa aspa final
                    int inicioCreated = idxCreated + 11;
                    int fimCreated = respostaJira.indexOf("\"", inicioCreated);
                    String created = respostaJira.substring(inicioCreated, fimCreated);

                    int inicioResolved = idxResolved + 18;
                    int fimResolved = respostaJira.indexOf("\"", inicioResolved);
                    String resolved = respostaJira.substring(inicioResolved, fimResolved);


                    // converte o texto da data (porque ela chega com formato de texto) e transforma em um objeto de data
                    // java.time.OffsetDateTime - o tipo do objeto
                    // .parse(created ...) vai basicamente traduzir a variavel created
                    // java.time.format.DateTimeFormatter - vai dizer exatamente a ordem que os numeros aparecem no texto do jira
                    java.time.OffsetDateTime dtCreated = java.time.OffsetDateTime.parse(created, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxx"));
                    java.time.OffsetDateTime dtResolved = java.time.OffsetDateTime.parse(resolved, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxx"));

                    // Duration - classe que mede a distancia no tempo
                    // between(dtCreated, dtResolved) - ele basicamente subtrai o tempo que o chamado foi aberto e o tempo que ele foi resolvido e depois transforma no formato certo
                    long minutos = java.time.Duration.between(dtCreated, dtResolved).toHours();

                    tempos.add(minutos);

                    i = idxResolved + 1;
                }

                // começa com o primeiro valor como referência para os dois
                // Depois percorre toda a lista se achar algo menor atualiza o maisRapido, se achar algo maior atualiza o maisLento.
                long maisRapido = 0;
                long maisLento= 0;

                if (!tempos.isEmpty()) {
                    maisRapido = tempos.get(0);
                    maisLento  = tempos.get(0);

                    for (long t : tempos) {
                        if (t < maisRapido) maisRapido = t;
                        if (t > maisLento)  maisLento  = t;
                    }
                }

                String resposta = "{\"maisRapido\": " + maisRapido + ", \"maisLento\": " + maisLento + "}";

                byte[] bytes = resposta.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
                exchange.getResponseBody().close();

            }  catch (Exception e) {
                e.printStackTrace();
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                String erro = "{\"erro\": \"Erro ao calcular MTTR\"}";
                byte[] bytes = erro.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(500, bytes.length);
                exchange.getResponseBody().write(bytes);
                exchange.getResponseBody().close();
            }
        }
    }
}


