package prbetter.web.handler;

import lombok.extern.slf4j.Slf4j;
import prbetter.core.AppConfig;
import prbetter.core.domain.GitHubRepositoryName;
import prbetter.core.domain.PullRequest;
import prbetter.core.repository.PullRequestRepository;
import prbetter.core.service.PullRequestRecommendService;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class PullRequestRecommendHandler implements HttpHandler {
    private static final PullRequestRepository repository = new AppConfig().repository();
    private static final PullRequestRecommendService recommendService = new PullRequestRecommendService(repository);
    private static final String NEW_LINE = System.lineSeparator();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        log.info("Load page from URI: {}", requestURI);

        String query = requestURI.getQuery();
        log.info("query={}", query);

        GitHubRepositoryName repositoryName = getRepositoryName(query);
        log.info("GitHub repository name from query parameter: {}", repositoryName.value());
        PullRequest recommended = recommendService.recommendFrom(repositoryName);

        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Content-Type", "text/html; charset=utf-8");

        byte[] content = new StringBuffer()
                .append("<!DOCTYPE html>").append(NEW_LINE)
                .append("<html>").append(NEW_LINE)
                .append("<head>").append(NEW_LINE)
                .append("<title>").append("Recommended").append("</title>").append(NEW_LINE)
                .append("<link rel=\"icon\" href=\"data:,\">").append(NEW_LINE)
                .append("</head>").append(NEW_LINE)
                .append("<body>").append(NEW_LINE)
                .append("<h1>").append("다음 PR을 리뷰해 보세요!").append("</h1>").append(NEW_LINE)
                .append("<a href=").append("\"").append(recommended.htmlUrl()).append("\"").append(">")
                .append("제목: ").append(recommended.title()).append("</a>").append(NEW_LINE)
                .append("</body>").append(NEW_LINE)
                .append("</html>").append(NEW_LINE)
                .toString().getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(200, content.length);

        OutputStream writer = exchange.getResponseBody();
        writer.write(content);
        writer.flush();
        writer.close();
    }

    /**
     * @param queryParameter "language=java&mission=racingcar-8" 형식
     * @return
     */
    private static GitHubRepositoryName getRepositoryName(String queryParameter) {
        Map<String, String> paramMap = new HashMap<>();
        String[] params = queryParameter.split("&");
        log.info("params={}", Arrays.toString(params));
        for (String param : params) {
            String[] keyValue = param.split("=");
            paramMap.put(keyValue[0], keyValue[1]);
        }
        log.info("{}", paramMap.values());

        return new GitHubRepositoryName(paramMap.get("language") + "-" + paramMap.get("mission"));
    }
}
