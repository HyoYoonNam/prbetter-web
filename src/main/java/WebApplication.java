import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import lombok.extern.slf4j.Slf4j;
import prbetter.core.AppConfig;
import prbetter.core.controller.PullRequestController;
import prbetter.web.handler.PullRequestRecommendHandler;
import prbetter.web.handler.WelcomePageHandler;

@Slf4j
public class WebApplication {
    private static final int PORT = 8080;
    private static final int SYSTEM_DEFAULT_BACKLOG = 0;

    public static void main(String[] args) throws IOException {
        log.info("Application start");
        AppConfig appConfig = new AppConfig();
        PullRequestController controller = new PullRequestController(
                appConfig.repository(),
                appConfig.loadService(),
                appConfig.recommendService());
        controller.run();

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), SYSTEM_DEFAULT_BACKLOG);
        server.createContext("/", new WelcomePageHandler());
        server.createContext("/better", new PullRequestRecommendHandler());
        log.info("Server wakes up: port={}", PORT);
        server.start();
    }
}
