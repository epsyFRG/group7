package com.crm.group7.service;

import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import com.mailgun.model.message.Message;
import com.mailgun.model.message.MessageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Value("${mailgun.api.key}")
    private String apiKey;

    @Value("${mailgun.domain}")
    private String domain;

    @Value("${mailgun.from.address}")
    private String fromAddress;

    public void sendMailgunEmail(String toEmail, String subject, String textBody) {
        MailgunMessagesApi mailgunMessagesApi = MailgunClient.config(apiKey).createApi(MailgunMessagesApi.class);

        Message message = Message.builder()
                .from(fromAddress)
                .to(toEmail)
                .subject(subject)
                .text(textBody)
                .build();

        try {
            MessageResponse response = mailgunMessagesApi.sendMessage(domain, message);
            System.out.println("Mailgun Success - ID: " + response.getId() + ", Message: " + response.getMessage());
        } catch (Exception e) {
            System.err.println("Mailgun Error: Could not send email. " + e.getMessage());
        }
    }
}
