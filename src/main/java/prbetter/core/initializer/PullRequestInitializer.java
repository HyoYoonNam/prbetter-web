package prbetter.core.initializer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import prbetter.core.domain.GitHubRepositoryName;
import prbetter.core.repository.PullRequestRepository;
import prbetter.core.service.PullRequestLoadService;
import prbetter.util.FileUtils;

import java.util.List;

/**
 * 이 클래스는 애플리케이션 구동 시점에 Pull request 목록 데이터를 초기화하는 책임을 가진다.
 */

@Slf4j
@AllArgsConstructor
public final class PullRequestInitializer {
    private final PullRequestRepository repository;
    private final PullRequestLoadService loadService;

    /**
     * 입력받은 깃허브 리포지토리의 pull request 목록이 서버에 파일로 캐싱되어 있으면 그걸 사용하고, 없으면 API 요청을 수행하여 초기화한다.
     *
     * <p>API 요청을 해서 초기화 하는 경우, 이후에 재사용할 수 있도록 서버에 파일로 캐싱한다.
     *
     * @param repositoryNames Pull request 목록을 가져올 깃허브 리포지토리들의 이름
     */
    public void init(List<GitHubRepositoryName> repositoryNames) {
        log.info("리포지토리의 pull request 목록 초기화");
        for (GitHubRepositoryName gitHubRepositoryName : repositoryNames) {
//            String filePath = "src/main/resources/pullrequest/" + gitHubRepositoryName.value() + ".json";
            String filePath = "./pullrequest/" + gitHubRepositoryName.value() + ".json";

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
