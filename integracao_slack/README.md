# Slack Integration

Este projeto demonstra como integrar uma aplicação Java com o Slack usando webhooks para enviar mensagens automaticamente.

## 📋 Sobre Incoming Webhooks

Incoming Webhooks permitem que aplicações externas postem mensagens diretamente em canais do Slack sem a necessidade de um bot estar constantemente conectado. É uma forma simples e segura de integrar sua aplicação com o Slack.

Para mais informações, consulte a [documentação oficial do Slack](https://docs.slack.dev/messaging/sending-messages-using-incoming-webhooks/).

## 🚀 Como Gerar um Webhook no Slack

Siga os passos abaixo para criar um webhook e integrá-lo com sua aplicação Java:

### Passo 1: Criar uma Conta no Slack
- Acesse [slack.com](https://slack.com)
- Clique em "Criar nova conta" ou faça login se já possuir uma conta
- Preencha os dados solicitados

### Passo 2: Criar um Workspace
- Após criar a conta, você será redirecionado para criar um novo workspace
- Escolha um nome para seu workspace
- **Nota:** Você não precisa usar um plano pago. O plano gratuito é suficiente para testes e desenvolvimento

### Passo 3: Criar um App "From Scratch"
- Acesse [api.slack.com/apps](https://api.slack.com/apps)
- Clique em "Create New App"
- Selecione a opção "From scratch"
- Defina um nome para seu app (ex: "Webhook Integration")
- Selecione o workspace que criou no passo anterior
- Clique em "Create App"

### Passo 4: Acessar Incoming Webhooks
- No painel do seu app, clique em "Incoming Webhooks" no menu esquerdo
- Ative a opção "Incoming Webhooks" clicando no toggle switch

### Passo 5: Criar um Novo Webhook
- Na mesma página, role para baixo até a seção "Webhook URLs for Your Workspace"
- Clique no botão "Add New Webhook"

### Passo 6: Escolher Canal de Destino
- Uma janela de autorização aparecerá
- Selecione o workspace onde seu app está instalado
- Selecione o canal onde as mensagens serão postadas (você pode escolher um canal já existente ou criar um novo)

### Passo 7: Copiar a URL do Webhook
- Após autorizar, você receberá uma URL do webhook
- A URL terá este formato: `https://hooks.slack.com/services/YOUR/WEBHOOK/URL`
- Copie esta URL

### Passo 8: Integrar com Seu Código Java
- Abra o arquivo `src/main/java/br/com/bandtec/slack/config/Slack.java`
- Localize a constante `URL`
- Substitua a URL de exemplo pela URL do webhook que você copiou:

```java
private static final String URL = "https://hooks.slack.com/services/SEU/WEBHOOK/URL";
```

### Passo 9: Executar o Código
- Compile e execute seu projeto Java
- A mensagem será enviada para o canal Slack que você configurou
- Você deve ver a mensagem aparecer no canal e um status de sucesso no console (Status: 200)

## 📝 Exemplo de Uso

Para enviar uma mensagem para o Slack usando este código, use a classe `Slack`:

```java
import org.json.JSONObject;
import br.com.bandtec.slack.config.Slack;

public class App {
    public static void main(String[] args) throws Exception {
        JSONObject message = new JSONObject();
        message.put("text", "Olá! Esta é uma mensagem enviada pelo Slack Integration!");
        
        Slack.sendMessage(message);
    }
}
```

## ⚠️ Segurança

- **Nunca compartilhe sua URL do webhook** publicamente ou suba no GitHub
- Se a URL vazar, desative o webhook no painel do Slack e crie um novo

## 🛠️ Requisitos

- Java 21 ou superior
- Conta no Slack (gratuita)

## 📚 Recursos Úteis

- [Documentação de Incoming Webhooks do Slack](https://docs.slack.dev/messaging/sending-messages-using-incoming-webhooks/)
- [Documentação de Estilização de Mensagem do Slack](https://docs.slack.dev/messaging/formatting-message-text/)
- [Slack API Documentation](https://api.slack.com)
- [JSON Message Formatting](https://api.slack.com/messaging/composing/layouts)

---

**Autor:** Diego Brito  
**Email:** diego.lima@bandtec.com.br

