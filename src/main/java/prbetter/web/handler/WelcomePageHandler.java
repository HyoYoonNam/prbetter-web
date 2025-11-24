package prbetter.web.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import prbetter.util.FileUtils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 이 클래스는 웰컴 페이지 요청(/)을 처리하는 핸들러다.
 *
 * <p>서버 자원 중 웰컴 페이지를 찾아 응답한다.
 */

@Slf4j
@NoArgsConstructor
public class WelcomePageHandler implements HttpHandler {
    private static final String WELCOME_PAGE = "index.html";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        log.info("Called from URI: {}", exchange.getRequestURI());
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Content-Type", "text/html; charset=utf-8");

        byte[] content = FileUtils.getBytes(WELCOME_PAGE);
        exchange.sendResponseHeaders(200, content.length);

        OutputStream writer = exchange.getResponseBody();
        log.info("Write content");
        writer.write(content);
        writer.flush();
        writer.close();
    }
}
