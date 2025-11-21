package prbetter.core.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import prbetter.core.domain.GitHubRepositoryName;
import prbetter.core.domain.PullRequest;
import prbetter.core.mapper.JsonPullRequestMapper;

/**
 * 이 클래스는 GitHub API를 호출하여 특정 깃허브 리포지토리의 Pull request 목록을 읽어 오는 책임을 가진다.
 *
 * <p>이 클래스는 {@code final}이므로 상속이 불가하다.
 */

public final class PullRequestReadService {
    private static final String API_URI_PREFIX = "https://api.github.com/repos/woowacourse-precourse/";
    private static final String API_URI_POSTFIX = "/pulls";
    private static final int HTTP_PAGE_NOT_FOUND = 404;
    private static final int HTTP_OK = 200;
    private static final String WOOWACOURSE_PRECOURSE_REPOSITORIES = "https://github.com/orgs/woowacourse-precourse/repositories";
    private static final String RESPONSE_STATUS_CODE_AND_BODY = "응답 코드=%d, 응답 내용=%s";

    private final HttpClient client;

    /**
     * {@code PullRequestReadService} 인스턴스를 생성한다.
     *
     * @param httpClient GitHub API 호출을 위한 http 통신 client
     */
    public PullRequestReadService(HttpClient httpClient) {
        this.client = httpClient;
    }

    /**
     * 리포지토리에 존재하는 모든 Pull request를 읽어 온다.
     *
     * @param name Pull request를 읽어 올 리포지토리의 이름
     * @return Pull request 목록
     * @throws IllegalStateException 다음 경우에 발생한다.
     *         - API 요청 중 통신 오류나 스레드 인터럽트가 생겨 서버가 수신하지 못한 경우
     *         - API 요청을 서버가 수신했으나, 실패한 응답(코드)을 받은 경우
     * @throws IllegalArgumentException API 요청이 잘못된 경우 발생한다.
     */
    public List<PullRequest> readAllPages(GitHubRepositoryName name) {
        List<PullRequest> result = new ArrayList<>();
        int currentPage = 0;

        while (true) {
            HttpResponse<String> httpResponse = read(name, ++currentPage);

            List<PullRequest> pullRequests = JsonPullRequestMapper.mapFromArray(httpResponse.body());
            result.addAll(pullRequests);

            if (isLastPage(httpResponse)) {
                break;
            }
        }

        return result;
    }

    private HttpResponse<String> read(GitHubRepositoryName name, int page) {
        HttpRequest httpRequest = getRequest(name, page);
        return getResponse(httpRequest);
    }

    private HttpRequest getRequest(GitHubRepositoryName name, int page) {
        URI apiUri = URI.create(API_URI_PREFIX + name.value() + API_URI_POSTFIX + "?page=" + page);
        return HttpRequest.newBuilder()
                .GET()
                .uri(apiUri)
                .header("Accept", "application/vnd.github.json")
                .build();
    }

    private HttpResponse<String> getResponse(HttpRequest httpRequest) {
        HttpResponse<String> response;

        try {
            response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("스레드가 인터럽트 되었습니다.", e);
        } catch (IOException e) {
            throw new IllegalStateException("GitHub API 통신 중 오류가 발생했습니다.", e);
        }

        validateHttpStatusCode(response);

        return response;
    }

    private void validateHttpStatusCode(HttpResponse<String> response) {
        if (response.statusCode() == HTTP_PAGE_NOT_FOUND) {
            throw new IllegalArgumentException("존재하지 않는 리포지토리입니다. See: " + WOOWACOURSE_PRECOURSE_REPOSITORIES);
        }

        if (response.statusCode() != HTTP_OK) {
            throwAppropriateException(response);
        }
    }

    private void throwAppropriateException(HttpResponse<String> response) {
        String detailMessage = RESPONSE_STATUS_CODE_AND_BODY.formatted(response.statusCode(), response.body());

        if (isClientError(response)) {
            throw new IllegalArgumentException("잘못된 요청입니다." + detailMessage);
        }

        if (isServerError(response)) {
            throw new IllegalStateException("서버가 요청을 처리하지 못했습니다." + detailMessage);
        }

        throw new IllegalStateException("HTTP 요청이 성공하지 못했습니다." + detailMessage);
    }

    private static boolean isClientError(HttpResponse<String> response) {
        return 400 <= response.statusCode() && response.statusCode() <= 499;
    }

    private static boolean isServerError(HttpResponse<String> response) {
        return 500 <= response.statusCode() && response.statusCode() <= 599;
    }

    private boolean isLastPage(HttpResponse<String> response) {
        return response.headers().firstValue("link")
                .map(header -> !header.contains("rel=\"next\"")) // rel="next"가 없거나
                .orElse(true); // link 헤더가 아예 없거나
    }
}
