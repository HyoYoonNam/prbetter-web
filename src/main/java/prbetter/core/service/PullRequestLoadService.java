package prbetter.core.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import prbetter.core.domain.GitHubRepositoryName;
import prbetter.core.domain.PullRequest;
import prbetter.core.mapper.JsonPullRequestMapper;
import prbetter.core.repository.PullRequestRepository;
import prbetter.util.FileUtils;

import java.io.File;
import java.util.List;

/**
 * 이 클래스는 특정 깃허브 리포지토리의 Pull request 목록을 읽어 와서 내부 리포지토리에 로드할 책임을 가진다.
 *
 * <p>이 클래스는 {@code final}이므로 상속이 불가하다.
 */

@Slf4j
@AllArgsConstructor
public final class PullRequestLoadService {
    private final PullRequestRepository pullRequestRepository;
    private final PullRequestReadService readService;

    /**
     * 지정된 깃허브 리포지토리의 모든 Pull request를 조회한 후, 유효한 제목을 가진 PR만 선별하여 저장한다.
     *
     * @param name Pull request를 로드할 대상 깃허브 리포지토리의 이름
     * @throws IllegalStateException API 요청 중 오류가 생긴 경우 발생한다.
     * @throws IllegalArgumentException {@code @param name}에 해당하는 리포지토리가 존재하지 않는 경우 발생한다.
     * @see PullRequest#isValidTitle()
     */
    public void loadFromGitHub(GitHubRepositoryName name) {
        log.info("Load pull requests from GitHub reposiotry: {}", name.value());
        readService.readAllPages(name).stream()
                .filter(PullRequest::isValidTitle)
                .forEach(pr -> pullRequestRepository.save(name, pr));

//        String filePath = "src/main/resources/pullrequest/" + name.value() + ".json";
        File directory = new File("./pullrequest/");
        if (!directory.exists()) {
            directory.mkdir();
        }
        String filePath = "./pullrequest/" + name.value() + ".json";
        log.info("Write to file: path={}", filePath);
        List<PullRequest> founds = pullRequestRepository.findAll(name);
        JsonPullRequestMapper.writeToFile(filePath, founds);
    }

    public void loadFromFile(PullRequestRepository repository,
                             GitHubRepositoryName gitHubRepositoryName,
                             String filePath) {
        List<PullRequest> pullRequests = JsonPullRequestMapper.mapFromArray(FileUtils.readLocalFileToString(filePath));
        repository.save(gitHubRepositoryName, pullRequests);
        log.info("Load {} pull requests on memory for {} repository", pullRequests.size(), gitHubRepositoryName);
    }
}
