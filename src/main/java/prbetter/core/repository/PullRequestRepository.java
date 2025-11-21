package prbetter.core.repository;

import java.util.List;
import prbetter.core.domain.GitHubRepositoryName;
import prbetter.core.domain.PullRequest;

/**
 * GitHub 리포지토리별 Pull request 데이터를 관리하는 저장소 인터페이스이다.
 *
 * <p>여기서는 설명의 편의를 위해 GitHub 리포지토리는 '리포지토리', {@code PullRequestRepository}는 '저장소'라고 칭한다.
 *
 * <p>이 인터페이스의 모든 메서드는 지정한 리포지토리를 대상으로 이루어지며, 파라미터 중 {@code GitHubRepositoryName name}에 해당한다.
 * 각 메서드에 대한 javadoc에서는 해당 파라미터에 대한 설명을 생략한다.
 *
 * <p>또한 Pull request는 'PR'이라는 약어를 사용한다.
 *
 * @see MemoryPullRequestRepository
 */

public interface PullRequestRepository {
    /**
     * 대상 리포지토리에 하나의 PR을 저장한다.
     *
     * @param pullRequest 저장할 PR
     * @return 저장된 PR 객체
     */
    PullRequest save(GitHubRepositoryName name, PullRequest pullRequest);

    /**
     * 대상 리포지토리에 여러 PR을 일괄 저장한다.
     *
     * @param pullRequests 저장할 PR 목록
     * @return 저장된 PR 목록
     */
    List<PullRequest> save(GitHubRepositoryName name, List<PullRequest> pullRequests);

    /**
     * 대상 리포지토리의 특정 인덱스에 위치한 PR을 조회한다.
     *
     * @param index 조회할 인덱스
     * @return 조회된 PR 객체
     */
    PullRequest findByIndex(GitHubRepositoryName name, int index);

    /**
     * 대상 리포지토리에 저장된 모든 Pull Request를 조회한다.
     *
     * @return 저장된 모든 PR 목록
     */
    List<PullRequest> findAll(GitHubRepositoryName name);

    /**
     * 대상 리포지토리에 저장된 Pull Request의 총 개수를 리턴한다.
     *
     * @return 저장된 PR 개수
     */
    int sizeOf(GitHubRepositoryName name);

    /**
     * 대상 리포지토리의 데이터가 저장소에 존재하는지 확인한다.
     *
     * @return 데이터 존재 여부
     */
    boolean has(GitHubRepositoryName name);
}
