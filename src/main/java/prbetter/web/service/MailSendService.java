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
    private static final String HOST = "smtp.gmail.com";
    private static final String FROM = "prbetter.noreply@gmail.com";
    private static final String USERNAME = FileUtils.readString("src/main/resources/MAIL_SERVICE_USERNAME").strip();
    private static final String PASSWORD = FileUtils.readString("src/main/resources/MAIL_SERVICE_PASSWORD").strip();

    private static final Properties props = new Properties();

    static {
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", "587");
    }

    private static final Session session = Session.getInstance(props,
            new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USERNAME, PASSWORD);
                }
            });

    private MailSendService() {
    }

    public static void send(String to, String subject, String text) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM, "PR better"));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
            log.info("Email Message to {} Sent Successfully!", to);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.info("메일 전송중 예외 발생: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
