package com.crm.group7.service;

import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import com.mailgun.model.message.Message;
import com.mailgun.model.message.MessageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Value("${mailgun.api.key}") // Legge la chiave API di Mailgun
    private String apiKey;

    @Value("${mailgun.domain}") // Legge il dominio Mailgun configurato
    private String domain;

    @Value("${mailgun.from.address}") // Legge l'indirizzo email del mittente configurato
    private String fromAddress;

    public void sendMailgunEmail(String toEmail, String subject, String textBody) {
        // Crea un'istanza dell'API di Mailgun, configurata con la chiave API
        MailgunMessagesApi mailgunMessagesApi = MailgunClient.config(apiKey).createApi(MailgunMessagesApi.class);

        // Costruisce il messaggio email con mittente, destinatario, oggetto e corpo del messaggio
        Message message = Message.builder()
                .from(fromAddress)
                .to(toEmail)
                .subject(subject)
                .text(textBody)
                .build();

        try {
            // Invia il messaggio tramite Mailgun
            MessageResponse response = mailgunMessagesApi.sendMessage(domain, message);
            System.out.println("Mailgun Success - ID: " + response.getId() + ", Message: " + response.getMessage());
        } catch (Exception e) {
            System.err.println("Mailgun Error: Could not send email. " + e.getMessage());
        }
    }
}
