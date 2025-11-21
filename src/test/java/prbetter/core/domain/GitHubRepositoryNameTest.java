package prbetter.core.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class GitHubRepositoryNameTest {
    @Test
    void 리포지토리_이름_규칙을_위반하면_예외를_발생한다() {
        assertThatThrownBy(() -> new GitHubRepositoryName("java lotto 8"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 리포지토리_이름_규칙을_준수하면_객체를_정상_생성한다() {
        String validRepositoryName = "ja.va-lotto_8";

        GitHubRepositoryName name = new GitHubRepositoryName(validRepositoryName);

        assertThat(name.value()).isEqualTo(validRepositoryName);
    }
}
