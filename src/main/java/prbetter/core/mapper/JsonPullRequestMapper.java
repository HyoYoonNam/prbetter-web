package prbetter.core.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.cfg.MutableCoercionConfig;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import prbetter.core.domain.PullRequest;

/**
 * 이 클래스는 json 문자열을 {@link PullRequest} 객체로 매핑하는 책임을 가진다.
 *
 * <p>이 클래스는 인스턴스 생성과 상속이 불가능한 정적 유틸 클래스이다.
 */

public final class JsonPullRequestMapper {
    private static final Class<PullRequest> PULL_REQUEST_CLASS = PullRequest.class;
    private static final Class<PullRequest[]> PULL_REQUEST_ARRAY_CLASS = PullRequest[].class;

    private static final Consumer<MutableCoercionConfig> strictTypeConfig = (config) -> {
        Arrays.stream(CoercionInputShape.values())
                .filter(shape -> shape != CoercionInputShape.String)
                .forEach(shape -> config.setCoercion(shape, CoercionAction.Fail));
    };

    private static final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new ParameterNamesModule())
            .withCoercionConfig(String.class, strictTypeConfig)
            .enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES) // 객체에 있는 프로퍼티가 json에 없으면 예외 발생
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // json에 있는 프로퍼티가 객체에 없으면 무시하고 진행
            .build();

    private JsonPullRequestMapper() {
    }

    /**
     * 단일 json 객체를 받아 {@link PullRequest}로 매핑한다.
     *
     * @throws JsonDeserializeException 매핑 중 오류가 생기면 발생한다.
     */
    public static PullRequest mapFromObject(String jsonString) {
        if (!isJsonObject(jsonString)) {
            throw new IllegalArgumentException("json 객체가 아닙니다.");
        }

        try {
            return mapper.readValue(jsonString, PULL_REQUEST_CLASS);
        } catch (StreamReadException e) {
            throw new JsonDeserializeException("json 형식에 맞지 않는 입력입니다.", e);
        } catch (DatabindException e) {
            throw new JsonDeserializeException("json 프로퍼티를 객체에 바인딩 할 수 없습니다.", e);
        } catch (JsonProcessingException e) { // api 문서상으로는 위 두 예외만 명시되어 있지만, 구현상으로는 해당 예외도 던지기 때문에 잡을 필요가 있다.
            throw new JsonDeserializeException("json 처리 도중 예외가 발생했습니다.", e);
        }
    }

    /**
     * json 배열을 받아 {@link PullRequest}로 매핑한다.
     *
     * @throws JsonDeserializeException 매핑 중 오류가 생기면 발생한다.
     */
    public static List<PullRequest> mapFromArray(String jsonString) {
        if (!isJsonArray(jsonString)) {
            throw new IllegalArgumentException("json 배열이 아닙니다.");
        }

        try {
            return List.of(mapper.readValue(jsonString, PULL_REQUEST_ARRAY_CLASS));
        } catch (StreamReadException e) {
            throw new JsonDeserializeException("json 형식에 맞지 않는 입력입니다.", e);
        } catch (DatabindException e) {
            throw new JsonDeserializeException("json 프로퍼티를 객체에 바인딩 할 수 없습니다.", e);
        } catch (JsonProcessingException e) { // api 문서상으로는 위 두 예외만 명시되어 있지만, 구현상으로는 해당 예외도 던지기 때문에 잡을 필요가 있다.
            throw new JsonDeserializeException("json 처리 도중 예외가 발생했습니다.", e);
        }
    }

    private static boolean isJsonObject(String jsonString) {
        return jsonString.startsWith(JsonToken.START_OBJECT.asString());
    }

    private static boolean isJsonArray(String jsonString) {
        return jsonString.startsWith(JsonToken.START_ARRAY.asString());
    }
}
