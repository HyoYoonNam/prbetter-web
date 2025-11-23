package prbetter.web.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import prbetter.util.FileUtils;

import java.io.IOException;
import java.io.OutputStream;

@Slf4j
public class WelcomePageHandler implements HttpHandler {
    private static final String WELCOME_PAGE = "index.html";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        log.info("Load welcome page from URI: {}", exchange.getRequestURI());
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Content-Type", "text/html; charset=utf-8");

        byte[] content = FileUtils.getBytes(WELCOME_PAGE);
        exchange.sendResponseHeaders(200, content.length);

        OutputStream writer = exchange.getResponseBody();
        writer.write(content);
        writer.flush();
        writer.close();
    }
}
