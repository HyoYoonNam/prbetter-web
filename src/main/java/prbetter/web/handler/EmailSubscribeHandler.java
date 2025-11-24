package prbetter.web.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import prbetter.web.service.MailSchedulerService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 이 클래스는 이메일 정기 발송 서비스 등록 요청(/email-subscribe)을 처리하는 핸들러다.
 *
 * <p> 사용자가 이메일, 추천받을 PR의 언어와 미션, 서비스 등록 기간을 포함한 POST 요청을 보내면
 * 서버 스케쥴러에 해당 사용자를 등록하고, 등록 완료 메시지가 담긴 HTML 페이지를 응답한다.
 */

@Slf4j
@AllArgsConstructor
public class EmailSubscribeHandler implements HttpHandler {
    private static final String NEW_LINE = System.lineSeparator();
    private static final String EMPTY_FAVICON_LINK = "<link rel=\"icon\" href=\"data:,\">";

    private final MailSchedulerService mailSchedulerService;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        log.info("Called from URI: {}", requestURI);

        byte[] bodyBytes = exchange.getRequestBody().readAllBytes();
        String body = new String(bodyBytes, StandardCharsets.UTF_8);
        String decoded = URLDecoder.decode(body, StandardCharsets.UTF_8);
        Map<String, String> queryParamMap = getQueryParamMap(decoded);
        log.info("HTML post 전송 데이터 파싱: {}", queryParamMap.entrySet());

        addToSchedule(queryParamMap);

        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Content-Type", "text/html; charset=utf-8");

        byte[] content = new StringBuffer()
                .append("<!DOCTYPE html>").append(NEW_LINE)
                .append("<html>").append(NEW_LINE)
                .append("<head>").append(NEW_LINE)
                .append("<title>").append("PR 추천 이메일 서비스 등록 완료").append("</title>").append(NEW_LINE)
                .append(EMPTY_FAVICON_LINK).append(NEW_LINE)
                .append("</head>").append(NEW_LINE)
                .append("<body>").append(NEW_LINE)
                .append("정기 발송 스케쥴에 등록 완료. 매일 오전 10시에 만나요!")
                .append("</body>").append(NEW_LINE)
                .append("</html>").append(NEW_LINE)
                .toString().getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, content.length);

        OutputStream writer = exchange.getResponseBody();
        log.info("Write content");
        writer.write(content);
        writer.flush();
        writer.close();
    }

    private static Map<String, String> getQueryParamMap(String body) {
        Map<String, String> paramMap = new HashMap<>();
        String[] params = body.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            paramMap.put(keyValue[0], keyValue[1]);
        }

        return paramMap;
    }

    private void addToSchedule(Map<String, String> queryParamMap) {
        String userEmail = queryParamMap.get("userEmail");
        String language = queryParamMap.get("language");
        String mission = queryParamMap.get("mission");
        int period = Integer.parseInt(queryParamMap.get("period"));

        mailSchedulerService.add(userEmail, language, mission, period);
    }
}
