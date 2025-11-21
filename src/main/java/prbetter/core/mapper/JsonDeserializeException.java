package prbetter.core.mapper;

/**
 * json 역직렬화 중 발생하는 예외를 감싸서 던지기 위한 커스텀 예외이다.
 */

public final class JsonDeserializeException extends RuntimeException {
    public JsonDeserializeException(String message, Throwable cause) {
        super(message, cause);
    }
}
