package prbetter.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import prbetter.core.domain.GitHubRepositoryName;
import prbetter.core.domain.PullRequest;
import prbetter.core.repository.MemoryPullRequestRepository;
import prbetter.core.repository.PullRequestRepository;

import java.util.List;

class PullRequestRecommendServiceTest {
    @Test
    void 리포지토리에_존재하는_PullRequest중_하나를_추천한다() {
        // given
        GitHubRepositoryName name = new GitHubRepositoryName("kotlin-lotto-8");
        PullRequestRepository repository = new MemoryPullRequestRepository();
        repository.save(name, List.of(
                new PullRequest("[로또] 남효윤 미션 제출합니다.", "https://example.com"),
                new PullRequest("[로또] 우테코 미션 제출합니다.", "https://example.com"))
        );

        PullRequestRecommendService recommendService = new PullRequestRecommendService(repository);

        // when
        PullRequest recommended = recommendService.recommendFrom(name);

        // then
        assertThat(repository.findAll(name)).contains(recommended);
    }

    @Test
    void 추천할_PullRequest가_존재하지_않으면_예외를_발생한다() {
        PullRequestRecommendService recommendService =
                new PullRequestRecommendService(new MemoryPullRequestRepository());

        assertThatThrownBy(() -> recommendService.recommendFrom(new GitHubRepositoryName("sample repository")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
