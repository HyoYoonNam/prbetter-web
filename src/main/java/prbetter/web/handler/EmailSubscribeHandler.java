package prbetter.web.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import prbetter.web.service.MailSchedulerService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class EmailSubscribeHandler implements HttpHandler {
    private static final String NEW_LINE = System.lineSeparator();

    private final MailSchedulerService mailSchedulerService;

    public EmailSubscribeHandler(MailSchedulerService mailSchedulerService) {
        this.mailSchedulerService = mailSchedulerService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        log.info("Load page from URI: {}", requestURI);

        byte[] bodyBytes = exchange.getRequestBody().readAllBytes();
        String body = new String(bodyBytes, StandardCharsets.UTF_8);
        String decoded = URLDecoder.decode(body, StandardCharsets.UTF_8);
        Map<String, String> queryParamMap = getQueryParamMap(decoded);

        String userEmail = queryParamMap.get("userEmail");
        String language = queryParamMap.get("language");
        String mission = queryParamMap.get("mission");
        int period = Integer.parseInt(queryParamMap.get("period"));
        mailSchedulerService.add(userEmail, language, mission, period);

        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Content-Type", "text/html; charset=utf-8");

        byte[] content = new StringBuffer()
                .append("<!DOCTYPE html>").append(NEW_LINE)
                .append("<html>").append(NEW_LINE)
                .append("<head>").append(NEW_LINE)
                .append("<title>").append("PR 추천 이메일 서비스 등록 완료").append("</title>").append(NEW_LINE)
                .append("<link rel=\"icon\" href=\"data:,\">").append(NEW_LINE)
                .append("</head>").append(NEW_LINE)
                .append("<body>").append(NEW_LINE)
                .append("매일 오전 10시에 만나요!")
                .append("</body>").append(NEW_LINE)
                .append("</html>").append(NEW_LINE)
                .toString().getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(200, content.length);

        OutputStream writer = exchange.getResponseBody();
        writer.write(content);
        writer.flush();
        writer.close();
    }

    private static Map<String, String> getQueryParamMap(String body) {
        Map<String, String> paramMap = new HashMap<>();
        String[] params = body.split("&");
        log.info("params={}", Arrays.toString(params));
        for (String param : params) {
            String[] keyValue = param.split("=");
            paramMap.put(keyValue[0], keyValue[1]);
        }
        log.info("{}", paramMap.values());

        return paramMap;
    }
}
