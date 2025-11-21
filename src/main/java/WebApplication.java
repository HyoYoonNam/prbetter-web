import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebApplication {
    private static final int PORT = 8080;
    private static final int SYSTEM_DEFAULT_BACKLOG = 0;

    public static void main(String[] args) throws IOException {
        log.info("Application start");
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), SYSTEM_DEFAULT_BACKLOG);
        server.createContext("/", new MyHandler());
        log.info("MyServer wakes up: port={}", PORT);
        server.start();
    }

    private static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            log.info("called from {}", exchange.getRequestURI());

            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.add("Content-Type", "text/plain");

            exchange.sendResponseHeaders(200, 0);
            OutputStream writer = exchange.getResponseBody();
            writer.write("Hello, client!".getBytes());
            writer.flush();
            writer.close();
        }
    }
}
