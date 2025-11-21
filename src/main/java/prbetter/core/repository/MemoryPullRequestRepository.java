package prbetter.core.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import prbetter.core.domain.GitHubRepositoryName;
import prbetter.core.domain.PullRequest;

/**
 * {@link PullRequestRepository}의 메모리 기반 구현체이다.
 *
 * <p>데이터를 메모리에 저장하므로, 프로그램 종료 시 데이터는 초기화된다.
 *
 * <p>이 클래스는 {@code final}이므로 상속이 불가하다.
 */

public final class MemoryPullRequestRepository implements PullRequestRepository {
    private static final List<PullRequest> EMPTY_LIST = Collections.emptyList();
    private static final String REPOSITORY_NO_EXISTS = "저장되지 않은 리포지토리입니다.";
    private static final String INDEX_OUT_OF_BOUNDS = "리포지토리의 최대 인덱스(%d)를 벗어났습니다.";

    private final Map<GitHubRepositoryName, List<PullRequest>> store = new HashMap<>();

    @Override
    public PullRequest save(GitHubRepositoryName name, PullRequest pullRequest) {
        if (!store.containsKey(name)) {
            store.put(name, new ArrayList<>());
        }

        store.get(name).add(pullRequest);

        return pullRequest;
    }

    @Override
    public List<PullRequest> save(GitHubRepositoryName name, List<PullRequest> pullRequests) {
        if (!store.containsKey(name)) {
            store.put(name, new ArrayList<>());
        }

        store.get(name).addAll(pullRequests);

        return pullRequests;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException 저장되지 않은 리포지토리거나, 인덱스 범위를 벗어난 경우 발생한다.
     */
    @Override
    public PullRequest findByIndex(GitHubRepositoryName name, int index) {
        if (!has(name)) {
            throw new IllegalArgumentException(REPOSITORY_NO_EXISTS);
        }

        List<PullRequest> pullRequests = store.get(name);

        int maxIndex = pullRequests.size() - 1;
        if (index < 0 || index > maxIndex) {
            throw new IllegalArgumentException(String.format(INDEX_OUT_OF_BOUNDS, maxIndex));
        }

        return pullRequests.get(index);
    }

    @Override
    public List<PullRequest> findAll(GitHubRepositoryName name) {
        return List.copyOf(store.getOrDefault(name, EMPTY_LIST));
    }

    @Override
    public int sizeOf(GitHubRepositoryName name) {
        return store.getOrDefault(name, EMPTY_LIST).size();
    }

    @Override
    public boolean has(GitHubRepositoryName name) {
        return store.containsKey(name);
    }
}
