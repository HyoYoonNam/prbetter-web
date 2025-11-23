package prbetter.web.service;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import prbetter.core.domain.GitHubRepositoryName;
import prbetter.core.domain.PullRequest;
import prbetter.core.service.PullRequestRecommendService;

import java.util.*;

@Slf4j
public class MailSchedulerService {
    private static final int MAILING_TIME_HOUR = 10; // 매일 오전 10시에 메일 발송

    private static final List<ScheduleInformation> scheduledUsers = new ArrayList<>();

    private final PullRequestRecommendService recommendService;
    private final MailSendService mailSendService;

    public MailSchedulerService(PullRequestRecommendService recommendService,
                                MailSendService mailSendService) {
        this.recommendService = recommendService;
        this.mailSendService = mailSendService;
    }

    public void add(String userEmail, String language, String mission, int period) {
        log.info("Scheduler called {} {} {} {}", userEmail, language, mission, period);
        scheduledUsers.add(new ScheduleInformation(userEmail, language, mission, period));
    }

    public void start() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                log.info("스케쥴 시작");
                for (ScheduleInformation scheduledUser : List.copyOf(scheduledUsers)) {
                    PullRequest recommended = recommendService.recommendFrom(scheduledUser.getGitHubRepositoryName());
                    mailSendService.send(scheduledUser.getUserEmail(),
                            "오늘의 추천 PR이 도착했어요!",
                            "제목: " + recommended.title() + System.lineSeparator() +
                                    "링크: " + recommended.htmlUrl());

                    scheduledUser.decreaseDays();
                    if (scheduledUser.isExpired()) {
                        log.info("기간 만료로 메일 서비스 스케쥴에서 제거: {}", scheduledUser);
                        scheduledUsers.remove(scheduledUser);
                    }
                }
                log.info("스케쥴 종료");
            }
        };

        Date firstExecutionTime = getFirstExecutionTime(MAILING_TIME_HOUR);
        timer.scheduleAtFixedRate(timerTask, firstExecutionTime, 1000L * 60L * 60L * 24L);
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

        public String getUserEmail() {
            return userEmail;
        }

        public GitHubRepositoryName getGitHubRepositoryName() {
            return gitHubRepositoryName;
        }
    }
}
