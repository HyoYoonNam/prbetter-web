package prbetter.core.mapper;

/**
 * json 직렬화 중 발생하는 예외를 감싸서 던지기 위한 커스텀 예외이다.
 */

public final class JsonSerializeException extends RuntimeException {
    public JsonSerializeException(Throwable cause) {
        super(cause);
    }

    public JsonSerializeException(String message, Throwable cause) {
        super(message, cause);
    }
}
