package prbetter.web.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import prbetter.core.domain.GitHubRepositoryName;
import prbetter.core.domain.PullRequest;
import prbetter.core.service.PullRequestRecommendService;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class PullRequestRecommendHandler implements HttpHandler {
    private static final int HTTP_OK = 200;
    private static final String NEW_LINE = System.lineSeparator();
    private static final String EMPTY_FAVICON_LINK = "<link rel=\"icon\" href=\"data:,\">";

    private final PullRequestRecommendService recommendService;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        log.info("Called from URI: {}", requestURI);

        String query = requestURI.getQuery();
        GitHubRepositoryName repositoryName = getRepositoryName(query);

        log.info("{}에 있는 PR 중 하나를 추천합니다.", repositoryName.value());
        PullRequest recommended = recommendService.recommendFrom(repositoryName);
        log.info("Recommended PR is {}", recommended);

        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Content-Type", "text/html; charset=utf-8");

        byte[] content = new StringBuffer()
                .append("<!DOCTYPE html>").append(NEW_LINE)
                .append("<html>").append(NEW_LINE)
                .append("<head>").append(NEW_LINE)
                .append("<title>").append("Recommended").append("</title>").append(NEW_LINE)
                .append(EMPTY_FAVICON_LINK).append(NEW_LINE)
                .append("</head>").append(NEW_LINE)
                .append("<body>").append(NEW_LINE)
                .append("<h1>").append("다음 PR을 리뷰해 보세요!").append("</h1>").append(NEW_LINE)
                .append("<a href=").append("\"").append(recommended.htmlUrl()).append("\"").append(">")
                .append("제목: ").append(recommended.title()).append("</a>").append(NEW_LINE)
                .append("</body>").append(NEW_LINE)
                .append("</html>").append(NEW_LINE)
                .toString().getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(HTTP_OK, content.length);

        OutputStream writer = exchange.getResponseBody();
        log.info("Write content");
        writer.write(content);
        writer.flush();
        writer.close();
    }

    /**
     * @param queryParameter "language=java&mission=racingcar-8" 형식
     * @return java-racingcar-8과 같은 value를 가지는 GitHubRepositoryName
     */
    private static GitHubRepositoryName getRepositoryName(String queryParameter) {
        Map<String, String> paramMap = new HashMap<>();
        String[] params = queryParameter.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            paramMap.put(keyValue[0], keyValue[1]);
        }

        return new GitHubRepositoryName(paramMap.get("language") + "-" + paramMap.get("mission"));
    }
}
