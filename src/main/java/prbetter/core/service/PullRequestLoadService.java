package prbetter.core.service;

import lombok.extern.slf4j.Slf4j;
import prbetter.core.domain.GitHubRepositoryName;
import prbetter.core.domain.PullRequest;
import prbetter.core.mapper.JsonPullRequestMapper;
import prbetter.core.repository.PullRequestRepository;

import java.util.List;

/**
 * 이 클래스는 특정 깃허브 리포지토리의 Pull request 목록을 읽어 와서 내부 리포지토리에 로드할 책임을 가진다.
 *
 * <p>이 클래스는 {@code final}이므로 상속이 불가하다.
 */

@Slf4j
public final class PullRequestLoadService {
    private final PullRequestRepository pullRequestRepository;
    private final PullRequestReadService readService;

    /**
     * {@code PullRequestLoadService} 인스턴스를 생성한다.
     *
     * @param pullRequestRepository 읽어 온 Pull request 목록을 로드할 내부 리포지토리
     * @param readService Pull request 목록을 읽어 올 수 있는 객체
     */
    public PullRequestLoadService(PullRequestRepository pullRequestRepository,
                                  PullRequestReadService readService) {
        this.pullRequestRepository = pullRequestRepository;
        this.readService = readService;
    }


    /**
     * 지정된 깃허브 리포지토리의 모든 Pull request를 조회한 후, 유효한 제목을 가진 PR만 선별하여 저장한다.
     *
     * @param name Pull request를 로드할 대상 깃허브 리포지토리의 이름
     * @throws IllegalStateException API 요청 중 오류가 생긴 경우 발생한다.
     * @throws IllegalArgumentException {@code @param name}에 해당하는 리포지토리가 존재하지 않는 경우 발생한다.
     * @see PullRequest#isValidTitle()
     */
    public void load(GitHubRepositoryName name) {
        log.info("load from {}", name.value());
        readService.readAllPages(name).stream()
                .filter(PullRequest::isValidTitle)
                .forEach(pr -> pullRequestRepository.save(name, pr));

        String filePath = "src/main/resources/" + name.value() + ".json";
        log.info("write to file: path={}", filePath);
        List<PullRequest> founds = pullRequestRepository.findAll(name);
        JsonPullRequestMapper.writeToFile(filePath, founds);
    }
}
