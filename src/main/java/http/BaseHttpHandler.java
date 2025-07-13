package http;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    protected void sendText(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendCreated(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(201, 0);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
        exchange.getResponseBody().write("Not found".getBytes(StandardCharsets.UTF_8));
        exchange.close();
    }

    protected void sendConflict(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(406, 0);
        exchange.getResponseBody().write("Task conflict".getBytes(StandardCharsets.UTF_8));
        exchange.close();
    }

    protected void sendServerError(HttpExchange exchange, String message) throws IOException {
        exchange.sendResponseHeaders(500, 0);
        exchange.getResponseBody().write(("Server error: " + message).getBytes(StandardCharsets.UTF_8));
        exchange.close();
    }

    protected void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(405, 0);
        exchange.getResponseBody().write("Method Not Allowed".getBytes(StandardCharsets.UTF_8));
        exchange.close();
    }

    protected void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        exchange.sendResponseHeaders(statusCode, 0);
        exchange.getResponseBody().write(message.getBytes(StandardCharsets.UTF_8));
        exchange.close();
    }

    protected int parseId(String query) {
        if (query == null || !query.contains("=")) {
            throw new IllegalArgumentException("Query string is invalid: " + query);
        }
        String[] parts = query.split("=");
        return Integer.parseInt(parts[1]);
    }
}
