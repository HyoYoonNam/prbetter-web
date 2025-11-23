package prbetter.web.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import prbetter.util.FileUtils;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

@Slf4j
public final class MailSendService {
    private static final String PERSONAL_NAME = "PR better";
    private final String HOST = "smtp.gmail.com";
    private final String FROM = "prbetter.noreply@gmail.com";
    private final String USERNAME = FileUtils.readString("src/main/resources/secret/MAIL_SERVICE_USERNAME").strip();
    private final String PASSWORD = FileUtils.readString("src/main/resources/secret/MAIL_SERVICE_PASSWORD").strip();

    private final Session session;

    public MailSendService() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", "587");

        this.session = Session.getInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USERNAME, PASSWORD);
                    }
                });
    }

    public void send(String to, String subject, String text) {
        try {
            log.info("[Send email to {}] Try", to);
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM, PERSONAL_NAME));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
            log.info("[Send email to {}] Success", to);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.info("메일 전송중 예외 발생: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
