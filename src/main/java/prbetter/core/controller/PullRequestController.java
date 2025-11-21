package prbetter.core.controller;

import prbetter.core.domain.GitHubRepositoryName;
import prbetter.core.repository.PullRequestRepository;
import prbetter.core.service.PullRequestLoadService;
import prbetter.core.service.PullRequestRecommendService;

/**
 * 이 클래스는 프로그램의 전체 흐름을 제어하는 책임을 가진다.
 *
 * <p>이 클래스는 {@code final}이므로 상속이 불가하다.
 */

public final class PullRequestController {
    private final PullRequestRepository repository;
    private final PullRequestLoadService loadService;
    private final PullRequestRecommendService recommendService;

    public PullRequestController(PullRequestRepository repository,
                                 PullRequestLoadService loadService,
                                 PullRequestRecommendService recommendService) {
        this.repository = repository;
        this.loadService = loadService;
        this.recommendService = recommendService;
    }

    /** 프로그램을 시작한다. */
    public void run() {
        // 테스트용 임시 로드
        loadService.load(new GitHubRepositoryName("kotlin-lotto-8"));
    }
}
