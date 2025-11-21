package prbetter.core.domain;

import java.util.regex.Pattern;

/**
 * 이 클래스는 GitHub 리포지토리의 이름을 가지는 Value object이다.
 *
 * <p> 이 클래스는 {@code record}로 선언되었으므로 불변임을 보장한다.
 *
 * @param value GitHub 리포지토리 이름
 */

public record GitHubRepositoryName(String value) {
    /*
     * GitHub에서 Create a new repository를 선택하고, Repository name에 'ㄱ' '@' 등의 규칙을 벗어나는 문자를 입력하면 다음 메시지가 보인다.
     * The repository name can only ASCII letters, digits, and the characters ., -, and _.
     * 이를 위반하는 이름의 리포지토리는 존재할 수 없으므로 (api 호출 전에) 생성되지 못하게 예외를 발생시킬 수 있도록 한다.
     */
    private static final Pattern NAMING_RULE_PATTERN = Pattern.compile("[A-Za-z0-9.\\-_]+");

    /** @throws IllegalArgumentException GitHub 리포지토리 명명 규칙을 위반하면 발생한다. */
    public GitHubRepositoryName {
        if (!NAMING_RULE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    value + ": 깃허브 리포지토리 이름은 ASCII 문자, 숫자, '.', '-', '_' 중 하나여야 합니다.");
        }
    }
}
