package prbetter.core.initializer;

import lombok.extern.slf4j.Slf4j;
import prbetter.core.domain.GitHubRepositoryName;
import prbetter.core.domain.PullRequest;
import prbetter.core.mapper.JsonPullRequestMapper;
import prbetter.core.repository.PullRequestRepository;
import prbetter.core.service.PullRequestLoadService;
import prbetter.util.FileUtils;

import java.util.List;

@Slf4j
public final class PullRequestInitializer {
    private final PullRequestRepository repository;
    private final PullRequestLoadService loadService;

    public PullRequestInitializer(PullRequestRepository repository, PullRequestLoadService loadService) {
        this.repository = repository;
        this.loadService = loadService;
    }

    public void init(List<GitHubRepositoryName> repositoryNames) {
        log.info("리포지토리의 Pull request 목록 초기화");
        for (GitHubRepositoryName repositoryName : repositoryNames) {
            String fileName = "src/main/resources/" + repositoryName.value() + ".json";
            // 파일로 이미 관리되고 있으면 파일에서 메모리 리포지토리로 불러 옴
            if (FileUtils.exists(fileName)) {
                log.info("{}에 대한 파일이 존재함. 파일 읽기.", repositoryName);
                List<PullRequest> pullRequests = JsonPullRequestMapper.mapFromArray(FileUtils.readString(fileName));
                repository.save(repositoryName, pullRequests);
                continue;
            }

            // 파일로 관리되고 있지 않으면 API를 호출해서 로드
            log.info("{}에 대한 파일이 존재하지 않음. API 호출.", repositoryName);
            loadService.load(repositoryName);
        }
    }
}
