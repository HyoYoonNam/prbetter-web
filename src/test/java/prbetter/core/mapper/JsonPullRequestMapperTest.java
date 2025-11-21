package prbetter.core.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import prbetter.core.FileUtils;
import prbetter.core.domain.PullRequest;

class JsonPullRequestMapperTest {
    @Test
    void json_문자열을_PullRequest_객체로_매핑한다() {
        String jsonString = FileUtils.readJsonFile("example-response-only-one-pull-request.json");

        PullRequest mappedPullRequest = JsonPullRequestMapper.mapFromObject(jsonString);

        assertThat(mappedPullRequest.title()).isEqualTo("Amazing new feature");
        assertThat(mappedPullRequest.htmlUrl()).isEqualTo("https://github.com/octocat/Hello-World/pull/1347");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            // 주의: 여는 중괄호가 없는 경우는 json 표준(single value json)이기 때문에 잘못된 형식이 아니다.
            // 각 라인의 주석에 적힌 <단어>는 "<단어>가 없는 경우"라고 해석하면 된다. 예: "닫는 중괄호가 없는 경우"
            "{\"html_url\": \"https://sample.com\", \"title\": \"sample title\"",  // 닫는 중괄호
            "{\"html_url\" \"https://sample.com\", \"title\" \"sample title\"}",   // key-value 사이의 콜론
            "{\"html_url\": \"https://sample.com\" \"title\": \"sample title\"}",  // 프로퍼티 사이의 콤마
    })
    void json_형식이_잘못된_경우_예외를_발생한다(String invalidJson) {
        assertThatThrownBy(() -> JsonPullRequestMapper.mapFromObject(invalidJson))
                .isInstanceOf(JsonDeserializeException.class)
                .hasCauseInstanceOf(StreamReadException.class);
    }

    @Test
    void PullRequest_객체에_있는_프로퍼티가_json에_없으면_예외를_발생한다() {
        String json = "{\"non existent key\": \"value\"}";
        assertThatThrownBy(() -> JsonPullRequestMapper.mapFromObject(json))
                .isInstanceOf(JsonDeserializeException.class)
                .hasCauseInstanceOf(DatabindException.class);
    }

    @Test
    void json에_있는_프로퍼티가_PullRequest_객체에_없는_경우는_무시하고_진행한다() {
        String json = "{\"html_url\": \"https://sample.com\", \"title\": \"sample title\", \"non existent key\": \"value\"}";

        PullRequest mappedPullRequest = JsonPullRequestMapper.mapFromObject(json);

        assertThat(mappedPullRequest.title()).isEqualTo("sample title");
        assertThat(mappedPullRequest.htmlUrl()).isEqualTo("https://sample.com");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"html_url\": \"https://sample.com\", \"title\": 1234}",  // 정수가 오는 경우
            "{\"html_url\": \"https://sample.com\", \"title\": 12.34}", // 실수가 오는 경우
    })
    void json과_PullRequest_객체의_값_타입이_일치하지_않으면_예외가_발생한다(String json) {
        assertThatThrownBy(() -> JsonPullRequestMapper.mapFromObject(json))
                .isInstanceOf(JsonDeserializeException.class)
                .hasCauseInstanceOf(DatabindException.class);
    }

    @Test
    void 여러_개의_json_객체가_있는_json_배열을_매핑한다() {
        String jsonArray = """
                [
                    {
                        \"html_url\": \"https://sample1.com\",
                        \"title\": \"sample1 title\",
                        \"non existent key\": \"value\"
                    },
                    {
                        \"html_url\": \"https://sample2.com\",
                        \"title\": \"sample2 title\",
                        \"non existent key\": \"value\"
                    }
                ]
                """;

        List<PullRequest> pullRequests = JsonPullRequestMapper.mapFromArray(jsonArray);

        assertThat(pullRequests).hasSize(2);
        assertThat(pullRequests).contains(
                new PullRequest("sample1 title", "https://sample1.com"),
                new PullRequest("sample2 title", "https://sample2.com")
        );
    }
}
