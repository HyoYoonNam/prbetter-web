package prbetter.web.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import prbetter.util.FileUtils;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * 이 클래스는 외부로 이메일을 전송하는 책임을 가진다.
 */

@Slf4j
public final class MailSendService {
    private static final String PERSONAL_NAME = "PR better";
    private final String HOST = "smtp.gmail.com";
    private final String FROM = "prbetter.noreply@gmail.com";
    private final String USERNAME = FileUtils.readString("secret/MAIL_SERVICE_USERNAME").strip();
    private final String PASSWORD = FileUtils.readString("secret/MAIL_SERVICE_PASSWORD").strip();

    private final Session session;

    /** 메일 전송에 중복적으로 사용되는 세션을 초기화한다. */
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

    /**
     * 입력받은 수신자에게 이메일을 전송한다.
     *
     * @param to        수신자의 이메일 주소
     * @param subject   메일 제목
     * @param text      메일 본문
     * @throws MailServiceException 메일 전송 중 인코딩이나 메시지 관련 에러가 생겨 발송에 실패하면 발생한다.
     * @see MessagingException
     * @see UnsupportedEncodingException
     */
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
            throw new MailServiceException("메일 전송중 예외가 발생했습니다", e);
        }
    }
}
