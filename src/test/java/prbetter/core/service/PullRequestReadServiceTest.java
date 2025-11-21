package prbetter.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import prbetter.core.FileUtils;
import prbetter.core.domain.GitHubRepositoryName;
import prbetter.core.domain.PullRequest;

class PullRequestReadServiceTest {
    private static final int HTTP_OK = 200;

    @Test
    void 한_페이지만_있는_경우_한_번만_읽는다() throws IOException, InterruptedException {
        // given
        HttpClient mockClient = createMockClientResponse1Page();
        PullRequestReadService readService = new PullRequestReadService(mockClient);

        // when
        List<PullRequest> pullRequests = readService.readAllPages(new GitHubRepositoryName("kotlin-lotto-8"));

        // then
        // 1번 읽고, link 헤더가 없을 인지하고 종료
        verify(mockClient, times(1)).send(any(), any());
        // 20개가 전부
        assertThat(pullRequests).hasSize(20);
    }

    @Test
    void 다음_페이지가_있는_경우_계속_읽는다() throws IOException, InterruptedException {
        // given
        HttpClient mockClient = createMockClientResponse2Pages();
        PullRequestReadService readService = new PullRequestReadService(mockClient);

        // when
        List<PullRequest> pullRequests = readService.readAllPages(new GitHubRepositoryName("kotlin-lotto-8"));

        // then
        // 1번 읽고, 마지막 페이지가 아니니까(rel="next" 존재) 1번 더 읽고, 마지막 페이지니까 그만 읽음
        verify(mockClient, times(2)).send(any(), any());
        // 1페이지에서 30개, 2페이지에서 20개 읽음
        assertThat(pullRequests).hasSize(50);
    }

    private static HttpClient createMockClientResponse1Page() throws IOException, InterruptedException {
        HttpClient mockClient = mock();
        HttpResponse<String> pageResponse = mock();

        // pageResponse setting
        Map<String, List<String>> linkHeaderExcludedHeaderMap = Map.of(
                "content-type", List.of("application/json; charset=utf-8")
        );
        when(pageResponse.statusCode()).thenReturn(HTTP_OK);
        when(pageResponse.headers()).thenReturn(HttpHeaders.of(linkHeaderExcludedHeaderMap, (k, v) -> true));
        when(pageResponse.body()).thenReturn(FileUtils.readJsonFile("example-response-20-pull-requests.json"));

        // mockClient setting
        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(pageResponse);

        return mockClient;
    }

    private static HttpClient createMockClientResponse2Pages() throws IOException, InterruptedException {
        HttpClient mockClient = mock();
        HttpResponse<String> page1Response = mock();
        HttpResponse<String> page2Response = mock();

        // page1Response setting
        Map<String, List<String>> nextIncludedHeaderMap = Map.of(
                "content-type", List.of("application/json; charset=utf-8"),
                "link", List.of("<https://example.com/...>; rel=\"next\"")
        );
        when(page1Response.statusCode()).thenReturn(HTTP_OK);
        when(page1Response.headers()).thenReturn(HttpHeaders.of(nextIncludedHeaderMap, (k, v) -> true));
        when(page1Response.body()).thenReturn(FileUtils.readJsonFile("example-response-30-pull-requests.json"));

        // page2Response setting
        Map<String, List<String>> nextExcludedHeaderMap = Map.of(
                "content-type", List.of("application/json; charset=utf-8"),
                "link", List.of("")
        );
        when(page2Response.statusCode()).thenReturn(HTTP_OK);
        when(page2Response.headers()).thenReturn(HttpHeaders.of(nextExcludedHeaderMap, (k, v) -> true));
        when(page2Response.body()).thenReturn(FileUtils.readJsonFile("example-response-20-pull-requests.json"));

        // mockClient setting
        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(page1Response)
                .thenReturn(page2Response);

        return mockClient;
    }
}
