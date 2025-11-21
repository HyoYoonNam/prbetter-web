package prbetter.core.service;

import java.util.Random;
import prbetter.core.domain.GitHubRepositoryName;
import prbetter.core.domain.PullRequest;
import prbetter.core.repository.PullRequestRepository;

/**
 * 이 클래스는 리포지토리에서 하나의 {@code PullRequest}를 무작위로 골라 추천하는 책임을 가진다.
 *
 * <p>이 클래스는 {@code final}이므로 상속이 불가하다.
 */

public final class PullRequestRecommendService {
    private static final String PULL_REQUEST_NO_EXISTS = "추천할 PR이 존재하지 않습니다.";
    private static final Random random = new Random();

    private final PullRequestRepository repository;

    /**
     * {@code PullRequestRecommendService} 인스턴스를 생성한다.
     *
     * @param repository {@code PullRequest}를 고르기 위해 접근하는 리포지토리
     */
    public PullRequestRecommendService(PullRequestRepository repository) {
        this.repository = repository;
    }

    /**
     * 입력받은 리포지토리에 있는 Pull request 중 하나를 골라 추천한다.
     *
     * @param name 추천할 Pull request를 고르기 위한 리포지토리 이름
     * @return 추천할 Pull request
     * @throws IllegalArgumentException 리포지토리에 Pull request가 1개도 존재하지 않으면 발생한다.
     */
    public PullRequest recommendFrom(GitHubRepositoryName name) {
        int size = repository.sizeOf(name);
        if (size == 0) {
            throw new IllegalArgumentException(PULL_REQUEST_NO_EXISTS);
        }

        int randomIndex = random.nextInt(size);

        return repository.findByIndex(name, randomIndex);
    }
}
