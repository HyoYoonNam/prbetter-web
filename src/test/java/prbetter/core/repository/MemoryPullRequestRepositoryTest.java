package prbetter.core.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import prbetter.core.domain.GitHubRepositoryName;
import prbetter.core.domain.PullRequest;

class MemoryPullRequestRepositoryTest {
    PullRequestRepository repository;

    @BeforeEach
    void setUp() {
        repository = new MemoryPullRequestRepository();
    }

    @Test
    void 저장과_조회_정상_흐름() {
        // given
        GitHubRepositoryName name = new GitHubRepositoryName("kotlin-lotto-8");
        PullRequest pullRequest = new PullRequest("[로또] 남효윤 미션 제출합니다.", "https://example.com");

        // when
        repository.save(name, pullRequest);

        // then
        List<PullRequest> foundPullRequests = repository.findAll(name);
        assertThat(foundPullRequests)
                .as("PullRequest 객체 1개가 정상적으로 저장되었는지 검증")
                .hasSize(1)
                .as("조회된 PullRequest 객체가 저장한 것과 같은지 검증")
                .containsExactly(pullRequest);
    }

    @Test
    void 저장된_적이_없는_리포지토리_조회시_빈_리스트를_리턴() {
        List<PullRequest> foundPullRequests = repository.findAll(new GitHubRepositoryName("java-lotto-8"));

        assertThat(foundPullRequests).isEmpty();
    }

    @Nested
    class 인덱스_기반_검색 {
        @Test
        void 검색_성공() {
            GitHubRepositoryName name = new GitHubRepositoryName("kotlin-lotto-8");
            List<PullRequest> pullRequestsToSave = create2PullRequests();

            repository.save(name, pullRequestsToSave);

            assertThat(pullRequestsToSave).contains(
                    repository.findByIndex(name, 0),
                    repository.findByIndex(name, 1)
            );
        }

        @Test
        void 범위를_벗어난_인덱스를_입력하면_예외를_발생한다() {
            GitHubRepositoryName name = new GitHubRepositoryName("kotlin-lotto-8");
            List<PullRequest> pullRequestsToSave = create2PullRequests();

            repository.save(name, pullRequestsToSave);

            assertThatThrownBy(() -> repository.findByIndex(name, 2))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 저장되지_않은_리포지토리에서_검색하면_예외를_발생한다() {
            assertThatThrownBy(() -> repository.findByIndex(new GitHubRepositoryName("sample-repository"), 1))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        private static List<PullRequest> create2PullRequests() {
            return List.of(
                    new PullRequest("[로또] 남효윤 미션 제출합니다.", "https://example.com"),
                    new PullRequest("[로또] 우테코 미션 제출합니다.", "https://example.com")
            );
        }
    }
}
