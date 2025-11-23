import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import prbetter.core.AppConfig;
import prbetter.core.domain.GitHubRepositoryName;
import prbetter.util.TargetRepositoryGenerator;
import prbetter.web.service.MailSchedulerService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class WebApplication {
    private static final int PORT = 8080;
    private static final int SYSTEM_DEFAULT_BACKLOG = 0;

    public static void main(String[] args) throws IOException {
        log.info("Application start");
        AppConfig appConfig = new AppConfig();

        List<GitHubRepositoryName> repositoryNames = TargetRepositoryGenerator.generate(8);
        appConfig.initializer().init(repositoryNames);

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), SYSTEM_DEFAULT_BACKLOG);
        server.createContext("/", appConfig.welcomePageHandler());
        server.createContext("/better", appConfig.pullRequestRecommendHandler());
        server.createContext("/email-subscribe", appConfig.emailSubscribeHandler());
        MailSchedulerService mailSchedulerService = appConfig.mailSchedulerService();
        mailSchedulerService.start();

        log.info("Server wakes up: port={}", PORT);
        server.start();
    }
}
