package prbetter.core;

import prbetter.core.controller.PullRequestController;
import prbetter.core.initializer.PullRequestInitializer;
import prbetter.core.repository.MemoryPullRequestRepository;
import prbetter.core.repository.PullRequestRepository;
import prbetter.core.service.PullRequestLoadService;
import prbetter.core.service.PullRequestReadService;
import prbetter.core.service.PullRequestRecommendService;

import java.net.http.HttpClient;

/**
 * 이 클래스는 애플리케이션 전체의 의존성 주입과 객체 생성에 대한 책임을 가진다.
 */

public class AppConfig {
    private static final MemoryPullRequestRepository repository = new MemoryPullRequestRepository();

    public PullRequestController controller() {
        return new PullRequestController(repository(), loadService(), recommendService(), initializer());
    }

    public PullRequestRecommendService recommendService() {
        return new PullRequestRecommendService(repository);
    }

    public PullRequestLoadService loadService() {
        return new PullRequestLoadService(repository, readService());
    }

    public PullRequestReadService readService() {
        return new PullRequestReadService(HttpClient.newHttpClient());
    }

    public PullRequestInitializer initializer() {
        return new PullRequestInitializer(repository(), loadService());
    }

    public PullRequestRepository repository() {
        return repository;
    }
}
