package prbetter.util;

import lombok.extern.slf4j.Slf4j;
import prbetter.core.domain.GitHubRepositoryName;

import java.util.ArrayList;
import java.util.List;

/**
 * 이 유틸리티 클래스는 서비스에서 사용할 깃허브 리포지토리 목록을 생성하는 책임을 가진다.
 *
 * <p>이 클래스는 {@code final}이므로 상속할 수 없다.
 * 또한 인스턴스를 생성할 수 없는 유틸리티 클래스이다.
 */

@Slf4j
public final class TargetRepositoryGenerator {
    private static final String JOIN_DELIMITER = "-";
    private static final List<String> LANGUAGES = List.of("java", "kotlin", "javascript");
    private static final List<String> MISSIONS = List.of("calculator", "racingcar", "lotto");

    private TargetRepositoryGenerator() {
    }

    /**
     * 기수 번호를 입력받아 해당 기수의 모든 미션 리포지토리 목록을 리턴한다.
     *
     * <p>기수에 해당하는 리포지토리 목록은 이 클래스 내부에서 관리된다.
     *
     * @param sessionNumber 우아한테크코스 프리코스의 기수 번호
     * @return 생성된 {@link GitHubRepositoryName} 목록
     */
    public static List<GitHubRepositoryName> generate(int sessionNumber) {
        log.info("=== 우아한테크코스 프리코스 {}기를 위한 리포지토리 이름들을 생성 ===", sessionNumber);
        List<GitHubRepositoryName> repositories = new ArrayList<>();

        for (String language : LANGUAGES) {
            for (String mission : MISSIONS) {
                String name = String.join(JOIN_DELIMITER, language, mission, String.valueOf(sessionNumber));
                log.info("Generated repository name = {}", name);
                repositories.add(new GitHubRepositoryName(name));
            }
        }

        return List.copyOf(repositories);
    }
}
