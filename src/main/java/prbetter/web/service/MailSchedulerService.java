package prbetter.web.service;

import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import prbetter.core.domain.GitHubRepositoryName;
import prbetter.core.domain.PullRequest;
import prbetter.core.service.PullRequestRecommendService;

import java.time.ZoneId;
import java.util.*;

/**
 * 이 클래스는 사용자에게 정기적으로 추천 PR을 메일로 발송하는 책임을 가진다.
 *
 * <p>매일 오전 10시(KST)에 스케쥴러에 등록된 사용자들을 대상으로 메일을 발송한다.
 */

@Slf4j
@AllArgsConstructor
public class MailSchedulerService {
    private static final int FIXED_MAILING_TIME_HOUR = 10; // 매일 오전 10시에 메일 발송
    private static final long ONE_DAY_MILLI_SECONDS = 1000L * 60L * 60L * 24L;

    private static final List<ScheduleInformation> scheduledUsers = new ArrayList<>();
    private final PullRequestRecommendService recommendService;
    private final MailSendService mailSendService;

    /**
     * 스케쥴러에 사용자를 등록한다.
     *
     * @param userEmail 사용자가 메일을 수신할 주소
     * @param language  추천받을 PR의 언어; 예. java
     * @param mission   추천받을 PR의 미션명; 예. racingcar
     *                  {@code @param language}와 결합하여 java-racingcar-8과 같은 깃허브 리포지토리 이름이 된다.
     * @param period    메일로 발송받을 기간(단위: 일)
     */
    public void add(String userEmail, String language, String mission, int period) {
        scheduledUsers.add(new ScheduleInformation(userEmail, language, mission, period));
        log.info("[스케쥴러 등록 완료] {}, 현재 등록 수={}", scheduledUsers.getLast(), scheduledUsers.size());
    }

    /**
     * 스케쥴러를 시작한다.
     *
     * <p>애플리케이션 로딩 시각을 기준으로 최초 실행 시점을 계산하여 정기 작업을 예약한다.
     * 정기 작업의 시각은 매일 오전 10시이며, 최초 실행 시점은 애플리케이션 로딩 시각이 오전 10시 이전이라면 당일 오전 10시, 아니면 다음 날 오전 10시다.
     *
     * <p>매 정기작업마다 사용자의 남은 {@code period}를 계산하여 모두 소모되면 스케쥴에서 제거한다.
     *
     * @see MailSchedulerService#add(String, String, String, int)
     */
    public void start() {
        log.info("Scheduler start");
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                log.info("[메일 정기 발송 스케쥴] 시작");
                int count = 0;
                for (ScheduleInformation scheduledUser : List.copyOf(scheduledUsers)) {
                    PullRequest recommended = recommendService.recommendFrom(scheduledUser.gitHubRepositoryName);
                    mailSendService.send(scheduledUser.userEmail,
                            "오늘의 추천 PR이 도착했어요!",
                            "제목: " + recommended.title() + System.lineSeparator() +
                                    "링크: " + recommended.htmlUrl());
                    log.info("발송완료({}): user email={}, pr={}", ++count, scheduledUser.userEmail, recommended);

                    scheduledUser.decreaseDays();
                    if (scheduledUser.isExpired()) {
                        log.info("기간 만료로 메일 서비스 스케쥴에서 제거: {}", scheduledUser);
                        scheduledUsers.remove(scheduledUser);
                    }
                }
                log.info("[메일 정기 발송 스케쥴] 종료");
            }
        };

        Date firstExecutionTime = getFirstExecutionTime(FIXED_MAILING_TIME_HOUR);
        log.info("First execution time is {}", firstExecutionTime);
        timer.scheduleAtFixedRate(timerTask, firstExecutionTime, ONE_DAY_MILLI_SECONDS);
    }

    private static Date getFirstExecutionTime(int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Seoul")));

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date firstTime = calendar.getTime();
        Date now = new Date();

        if (firstTime.before(now)) {
            calendar.add(Calendar.DATE, 1);
            firstTime = calendar.getTime();
        }

        return firstTime;
    }

    @ToString
    private static class ScheduleInformation {
        final String userEmail;
        final GitHubRepositoryName gitHubRepositoryName;
        int remainingDays;

        public ScheduleInformation(String userEmail, String language, String mission, int period) {
            this.userEmail = userEmail;
            this.gitHubRepositoryName = new GitHubRepositoryName(language + "-" + mission);
            this.remainingDays = period;
        }

        public void decreaseDays() {
            this.remainingDays--;
        }

        public boolean isExpired() {
            return this.remainingDays <= 0;
        }
    }
}
