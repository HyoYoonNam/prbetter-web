package prbetter.core;

import com.sun.net.httpserver.HttpHandler;
import prbetter.core.initializer.PullRequestInitializer;
import prbetter.core.repository.MemoryPullRequestRepository;
import prbetter.core.repository.PullRequestRepository;
import prbetter.core.service.PullRequestLoadService;
import prbetter.core.service.PullRequestReadService;
import prbetter.core.service.PullRequestRecommendService;
import prbetter.web.handler.EmailSubscribeHandler;
import prbetter.web.handler.PullRequestRecommendHandler;
import prbetter.web.handler.WelcomePageHandler;
import prbetter.web.service.MailSchedulerService;
import prbetter.web.service.MailSendService;

import java.net.http.HttpClient;

/**
 * 이 클래스는 애플리케이션 전체의 의존성 주입과 객체 생성에 대한 책임을 가진다.
 *
 * <p>이 클래스가 생성하는 모든 인스턴스는 싱글턴임을 보장한다.
 */

public class AppConfig {
    // repository
    private final MemoryPullRequestRepository repository = new MemoryPullRequestRepository();

    // service and initializer
    private final PullRequestReadService readService = new PullRequestReadService(HttpClient.newHttpClient());
    private final PullRequestLoadService loadService = new PullRequestLoadService(repository, readService);
    private final PullRequestInitializer initializer = new PullRequestInitializer(repository, loadService);
    private final PullRequestRecommendService recommendService = new PullRequestRecommendService(repository);
    private final MailSendService mailSendService = new MailSendService();
    private final MailSchedulerService mailSchedulerService =
            new MailSchedulerService(recommendService, mailSendService);

    // handler
    private final WelcomePageHandler welcomePageHandler = new WelcomePageHandler();
    private final PullRequestRecommendHandler pullRequestRecommendHandler =
            new PullRequestRecommendHandler(repository, recommendService);
    private final EmailSubscribeHandler emailSubscribeHandler = new EmailSubscribeHandler(mailSchedulerService);

    public PullRequestRepository repository() {
        return repository;
    }

    public PullRequestReadService readService() {
        return readService;
    }

    public PullRequestLoadService loadService() {
        return loadService;
    }

    public PullRequestInitializer initializer() {
        return initializer;
    }

    public PullRequestRecommendService recommendService() {
        return recommendService;
    }

    public MailSendService mailSendService() {
        return mailSendService;
    }

    public MailSchedulerService mailSchedulerService() {
        return mailSchedulerService;
    }

    public WelcomePageHandler welcomePageHandler() {
        return welcomePageHandler;
    }

    public PullRequestRecommendHandler pullRequestRecommendHandler() {
        return pullRequestRecommendHandler;
    }

    public EmailSubscribeHandler emailSubscribeHandler() {
        return emailSubscribeHandler;
    }
}
