package prbetter.web.service;

import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import prbetter.core.domain.GitHubRepositoryName;
import prbetter.core.domain.PullRequest;
import prbetter.core.service.PullRequestRecommendService;

import java.util.*;

@Slf4j
@AllArgsConstructor
public class MailSchedulerService {
    private static final int FIXED_MAILING_TIME_HOUR = 10; // 매일 오전 10시에 메일 발송
    private static final long ONE_DAY_MILLI_SECONDS = 1000L * 60L * 60L * 24L;

    private static final List<ScheduleInformation> scheduledUsers = new ArrayList<>();
    private final PullRequestRecommendService recommendService;
    private final MailSendService mailSendService;

    public void add(String userEmail, String language, String mission, int period) {
        scheduledUsers.add(new ScheduleInformation(userEmail, language, mission, period));
        log.info("[스케쥴러 등록 완료] {}, 현재 등록 수={}", scheduledUsers.getLast(), scheduledUsers.size());
    }

    public void start() {
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
        timer.scheduleAtFixedRate(timerTask, firstExecutionTime, ONE_DAY_MILLI_SECONDS);
    }

    private static Date getFirstExecutionTime(int hour) {
        Calendar calendar = Calendar.getInstance();

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
