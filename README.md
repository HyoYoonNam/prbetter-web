# prbetter-web
### Environment
![OS](https://img.shields.io/badge/OS-Windows_%7C_macOS_%7C_Linux-8A2BE2?style=flat-square)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![GCP](https://img.shields.io/badge/Cloud-Google_Cloud-4285F4?style=flat-square&logo=google-cloud&logoColor=white)](https://cloud.google.com/)
[![Frontend](https://img.shields.io/badge/Frontend-Pure_HTML5-E34F26?style=flat-square&logo=html5&logoColor=white)](https://developer.mozilla.org/en-US/docs/Web/Guide/HTML/HTML5)
[![Backend](https://img.shields.io/badge/Back--end-JDK_HttpServer-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.httpserver/com/sun/net/httpserver/HttpServer.html)
[![Gradle](https://img.shields.io/badge/Build-Gradle-02303A?style=flat-square&logo=gradle&logoColor=white)](https://gradle.org/)

### Development
[![Jakarta Mail](https://img.shields.io/badge/Jakarta_Mail-2.1.3-F37029?style=flat-square&logo=jakartaee&logoColor=white)](https://jakarta.ee/specifications/mail/)
[![Lombok](https://img.shields.io/badge/Tools-Lombok-BC0230?style=flat-square&logo=lombok&logoColor=white)](https://projectlombok.org/)
[![Jackson](https://img.shields.io/badge/JSON-Jackson-007ec6?style=flat-square&logo=json&logoColor=white)](https://github.com/FasterXML/jackson)
[![SLF4J](https://img.shields.io/badge/Logging-SLF4J-005571?style=flat-square)](https://www.slf4j.org/)

### Testing
[![JUnit5](https://img.shields.io/badge/Test-JUnit5-25A162?style=flat-square&logo=junit5&logoColor=white)](https://junit.org/junit5/)
[![Mockito](https://img.shields.io/badge/Test-Mockito-81B518?style=flat-square&logo=mockito&logoColor=white)](https://site.mockito.org/)
[![AssertJ](https://img.shields.io/badge/Test-AssertJ-2C2255?style=flat-square)](https://assertj.github.io/doc/)
[![Coverage](https://img.shields.io/badge/Coverage-80%25-green?style=flat-square)](https://htmlpreview.github.io/?https://github.com/HyoYoonNam/prbetter-web/blob/main/htmlReport/index.html)

### Meta
[![WoowaCourse](https://img.shields.io/badge/Project_For-WoowaCourse_Pre--course-00C854?style=flat-square&logo=woowabrothers&logoColor=white)](https://woowacourse.github.io/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)](https://opensource.org/licenses/MIT)
[![Docusaurus](https://img.shields.io/badge/Docs-Docusaurus-3ECC5F?style=flat-square&logo=docusaurus&logoColor=white)](여기에_도큐사우루스_사이트_URL_입력)
[![DuckDNS](https://img.shields.io/badge/DNS-DuckDNS-c0d33e?style=flat-square)](https://www.duckdns.org/)
![Last Commit](https://img.shields.io/github/last-commit/hyoyoonnam/prbetter-web?style=flat-square&color=important)

---
prbetter-web은 [prbetter-console](https://github.com/HyoYoonNam/prbetter-console)의 기능을 기반으로 제작한 초경량 웹서비스입니다.

## 📃 목차
[개발 배경](#-개발-배경)  
[설치와 사용 방법](#-설치와-사용-방법)
[콘솔 프로그램과 비교해서 무엇이 달라졌나요?](#-콘솔-프로그램과-비교해서-무엇이-달라졌나요)

## 💡 개발 배경
우아한테크코스 프리코스(이하 '우테코')에 참가하면서 느낀 상호 코드 리뷰 문화의 활성화를 위해 위해 리뷰할 [prbetter-console](https://github.com/HyoYoonNam/prbetter-console)을 구현했었어요.

하지만 콘솔 프로그램에서는 사용자가 직접 jar 파일을 구동하고, 리뷰할 PR을 추천 받으려고 할 때마다 프로그램에 명령해야 하는 불편함이 있었어요.

이를 해결하기 위해 웹서비스로 구현함과 함께 메일 정기 발송 기능을 추가했어요!

## ⚙️ 설치와 사용 방법
먼저, http://prbetter.duckdns.org:8080/ 에 접속합니다.

### PR 즉시 추천받기
![usage-example-immediately](assets/usage-example-immediately.png)

표시된 영역에서 언어와 미션을 선택하고, **Better!** 버튼을 누릅니다.

<br>

![usage-example-immediately-result](assets/usage-example-immediately-result.png)

추천이 완료된 것을 확인할 수 있습니다.

### 매일 오전 10시에 메일로 추천받기
![usage-example-mail-service](assets/usage-example-mail-service.png)

표시된 영역에서 이메일을 입력하고 추천받을 기간, 언어, 미션을 선택한 뒤에 **이메일 서비스 등록하기** 버튼을 누릅니다.

<br>

![usage-example-mail-service-result](assets/usage-example-mail-service-result.png)

정기 발송 스케쥴에 등록된 것을 확인할 수 있습니다.

## ❓ 콘솔 프로그램과 비교해서 무엇이 달라졌나요?
### 웹 서비스
![compare-using-sequence-between-console-and-web](assets/compare-using-sequence-between-console-and-web.png)
(좌측 사진) Java를 설치하고, jar 파일을 다운로드 하고, 로컬 환경에서 직접 실행시켜야 하는 콘솔 프로그램과 다르게,  
(우측 사진) 단순히 [웹사이트](prbetter.duckdns.org:8080)에 방문하는 것만으로 사용할 수 있습니다.

### GitHub API 호출 최소화
```java
public final class PullRequestInitializer {
    public void init(List<GitHubRepositoryName> repositoryNames) {
        log.info("리포지토리의 pull request 목록 초기화");
        for (GitHubRepositoryName gitHubRepositoryName : repositoryNames) {
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

public final class PullRequestLoadService {
    public void loadFromGitHub(GitHubRepositoryName name) {
        log.info("Load pull requests from GitHub reposiotry: {}", name.value());
        readService.readAllPages(name).stream()
                .filter(PullRequest::isValidTitle)
                .forEach(pr -> pullRequestRepository.save(name, pr));

        File directory = new File("./pullrequest/");
        if (!directory.exists()) {
            directory.mkdir();
        }
        String filePath = "./pullrequest/" + name.value() + ".json";
        log.info("Write to file: path={}", filePath);
        List<PullRequest> founds = pullRequestRepository.findAll(name);
        JsonPullRequestMapper.writeToFile(filePath, founds);
    }
}
```
한번 GitHub API를 호출해서 로드한 PR 데이터들은 서버에서 파일로 저장하여 관리합니다.

덕분에 서버측에서 애플리케이션을 다시 실행할 때, 이미 파일로 저장되어 있는 경우에는 API 호출을 하지 않습니다.

### GitHUB API 호출시 서버측 인증 토큰 사용
```java
public final class PullRequestReadService {
    private HttpRequest getRequest(GitHubRepositoryName name, int page) {
        URI apiUri = URI.create(API_URI_PREFIX + name.value() + API_URI_POSTFIX + "?per_page=30" + "&page=" + page);
        return HttpRequest.newBuilder()
                .GET()
                .uri(apiUri)
                .header("Accept", "application/vnd.github.json")
                .header("Authorization", "Bearer " + FileUtils.readString(GITHUB_AUTHORIZATION_TOKEN_PATH).strip())
                .build();
    }
}
```
prbetter-console에서는 `Authorization` 헤더를 요청에 포함하지 않았고, 로컬에서 실행되기 때문에 프로그램 실행자 IP 기반으로 적은 횟수(시간당 60회)의 API 호출만이 가능했습니다.

prbetter-web에서는 요청 헤더에 인증 정보를 포함하여 API 호출 제한을 크게 늘리고(시간당 5,000회), API 호출 자체도 서버측에서 담당하므로 사용자는 신경쓰지 않아도 됩니다. 

### 메일 정기 발송 서비스
![email-recommended-example](assets/email-recommended-example.png)

정기 발송 스케쥴에 등록하면, 매일 10시 정각에 PR을 자동으로 추천받을 수 있습니다.
