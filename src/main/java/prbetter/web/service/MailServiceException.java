package prbetter.web.service;

/**
 * MailXxxService 계층에서 메일 전송 관련 외부 API를 호출할 때 발생하는 예외를 감싸기 위한 커스텀 예외다.
 */

public class MailServiceException extends RuntimeException {
    public MailServiceException(Throwable cause) {
        super(cause);
    }

    public MailServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
