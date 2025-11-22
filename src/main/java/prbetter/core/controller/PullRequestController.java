package prbetter.core.controller;

import prbetter.core.domain.GitHubRepositoryName;
import prbetter.core.initializer.PullRequestInitializer;
import prbetter.core.repository.PullRequestRepository;
import prbetter.core.service.PullRequestLoadService;
import prbetter.core.service.PullRequestRecommendService;
import prbetter.util.TargetRepositoryGenerator;

import java.util.List;

/**
 * 이 클래스는 프로그램의 전체 흐름을 제어하는 책임을 가진다.
 *
 * <p>이 클래스는 {@code final}이므로 상속이 불가하다.
 */

public final class PullRequestController {
    private final PullRequestRepository repository;
    private final PullRequestLoadService loadService;
    private final PullRequestRecommendService recommendService;
    private final PullRequestInitializer initializer;

    public PullRequestController(PullRequestRepository repository,
                                 PullRequestLoadService loadService,
                                 PullRequestRecommendService recommendService,
                                 PullRequestInitializer initializer) {
        this.repository = repository;
        this.loadService = loadService;
        this.recommendService = recommendService;
        this.initializer = initializer;
    }

    /** 프로그램을 시작한다. */
    public void run() {
        List<GitHubRepositoryName> repositoryNames = TargetRepositoryGenerator.generate(8);
        initializer.init(repositoryNames);
    }
}
