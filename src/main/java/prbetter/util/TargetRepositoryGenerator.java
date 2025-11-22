package prbetter.util;

import lombok.extern.slf4j.Slf4j;
import prbetter.core.domain.GitHubRepositoryName;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public final class TargetRepositoryGenerator {
    private static final String JOIN_DELIMITER = "-";
    private static final List<String> LANGUAGES = List.of("java", "kotlin", "javascript");
    private static final List<String> MISSIONS = List.of("calculator", "racingcar", "lotto");

    private TargetRepositoryGenerator() {
    }

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
