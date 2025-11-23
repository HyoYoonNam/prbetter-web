package prbetter.core.initializer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import prbetter.core.domain.GitHubRepositoryName;
import prbetter.core.repository.PullRequestRepository;
import prbetter.core.service.PullRequestLoadService;
import prbetter.util.FileUtils;

import java.util.List;

@Slf4j
@AllArgsConstructor
public final class PullRequestInitializer {
    private final PullRequestRepository repository;
    private final PullRequestLoadService loadService;

    public void init(List<GitHubRepositoryName> repositoryNames) {
        log.info("리포지토리의 pull request 목록 초기화");
        for (GitHubRepositoryName gitHubRepositoryName : repositoryNames) {
            String filePath = "src/main/resources/" + gitHubRepositoryName.value() + ".json";

            // 파일로 이미 관리되고 있으면 파일에서 메모리 리포지토리로 불러 옴
            if (FileUtils.exists(filePath)) {
                log.info("{}에 대한 파일이 존재함. 파일 읽기: ", gitHubRepositoryName);
                loadService.loadFromFile(repository, gitHubRepositoryName, filePath);
                continue;
            }

            // 파일로 관리되고 있지 않으면 API를 호출해서 로드
            log.info("{}에 대한 파일이 존재하지 않음. API 호출: ", gitHubRepositoryName);
            loadService.loadFromGitHub(gitHubRepositoryName);
        }
    }
}
