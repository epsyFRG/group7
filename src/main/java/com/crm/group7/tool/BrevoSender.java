package com.crm.group7.tool;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class BrevoSender {

    private static final Logger log = LoggerFactory.getLogger(BrevoSender.class);
    private final String apiKey;
    private final String fromEmail;
    private final String fromName;

    // Iniettiamo i valori da application.properties
    public BrevoSender(@Value("${brevo.api.key}") String apiKey,
                       @Value("${brevo.sender.email}") String fromEmail,
                       @Value("${brevo.sender.name}") String fromName) {
        this.apiKey = apiKey;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
    }

    // Endpoint API v3 di Brevo per email transazionali
    private final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    public void sendEmailGenerica(String emailDestinatario, String nomeDestinatario, String oggetto, String testoMessaggio) {

        // 1. Definiamo mittente e destinatario come richiesto da Brevo
        Map<String, String> sender = Map.of(
                "email", this.fromEmail,
                "name", this.fromName
        );

        Map<String, String> recipient = Map.of(
                "email", emailDestinatario,
                "name", nomeDestinatario
        );

        // Brevo si aspetta una lista di destinatari
        List<Map<String, String>> toList = List.of(recipient);

        // 2. Payload JSON
        // Converto il testo semplice in un minimo di HTML per la formattazione
        String htmlContent = "<html><body>Ciao " + nomeDestinatario + ",<br><br>"
                + testoMessaggio.replaceAll("\n", "<br>") // Sostituisce a-capo con <br>
                + "<br><br>Saluti,<br>" + this.fromName
                + "</body></html>";

        Map<String, Object> payload = Map.of(
                "sender", sender,
                "to", toList,
                "subject", oggetto,
                "htmlContent", htmlContent
        );

        // 3. Chiamata POST con Unirest
        HttpResponse<JsonNode> response = Unirest.post(BREVO_API_URL)
                .header("api-key", this.apiKey) // Autenticazione Brevo
                .header("Content-Type", "application/json")
                .body(payload) // Inviamo il payload come JSON
                .asJson();

        // 4. Gestiamo la risposta
        // Brevo restituisce 201 Created in caso di successo
        if (response.getStatus() != 201) {
            log.error("Brevo ha restituito un errore [{}]: {}", response.getStatus(), response.getBody());
            throw new RuntimeException("Errore nell'invio dell'email: " + response.getBody().toString());
        } else {
            log.info("Email inviata con successo via Brevo a {}. MessageId: {}",
                    emailDestinatario, response.getBody().getObject().getString("messageId"));
        }
    }
}

