package prbetter.core.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import prbetter.core.domain.GitHubRepositoryName;
import prbetter.core.domain.PullRequest;
import prbetter.core.repository.PullRequestRepository;

import java.util.List;

class PullRequestLoadServiceTest {
    @Test
    void PullRequest들을_리포지토리에_로드한다() {
        // given
        PullRequestReadService mockReadService = mock();
        when(mockReadService.readAllPages(any(GitHubRepositoryName.class))).thenReturn(createPullRequests());
        PullRequestRepository mockRepository = mock();

        PullRequestLoadService lodeService = new PullRequestLoadService(mockRepository, mockReadService);

        GitHubRepositoryName name = new GitHubRepositoryName("java-lotto-8");

        // when
        lodeService.load(name);

        // then
        verify(mockRepository, times(2)).save(eq(name), any(PullRequest.class));
    }

    private static List<PullRequest> createPullRequests() {
        String sampleHtmlUrl = "https://example.com";
        return List.of(
                // Valid pull requests
                new PullRequest("[로또] 남효윤 미션 제출합니다.", sampleHtmlUrl),
                new PullRequest("[로또] 우테코 미션 제출합니다.", sampleHtmlUrl),
                // Invalid pull requests
                new PullRequest("남효윤 미션 제출합니다.", sampleHtmlUrl),        // [<미션명>] 없음
                new PullRequest("[로또] 남효윤 미션 제출합니다!!", sampleHtmlUrl)  // '.'으로 끝나지 않음
        );
    }
}
