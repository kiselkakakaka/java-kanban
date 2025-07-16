package http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Epic;
import model.exceptions.IntersectionException;
import model.exceptions.NotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public EpicHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();

            switch (method) {
                case "GET":
                    if (query == null) {
                        sendText(exchange, gson.toJson(manager.getEpics()));
                    } else {
                        int id = parseId(query);
                        Epic epic = manager.getEpic(id); // NotFoundException, если нет
                        sendText(exchange, gson.toJson(epic));
                    }
                    break;

                case "POST":
                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Epic epic = gson.fromJson(body, Epic.class);

                    if (epic.getId() == 0) {
                        manager.addNewEpic(epic);
                    } else {
                        manager.updateEpic(epic);
                    }
                    sendCreated(exchange); // 201 вместо 200
                    break;

                case "DELETE":
                    if (query == null) {
                        manager.removeAllEpics();
                        sendText(exchange, "All epics deleted");
                    } else {
                        int id = parseId(query);
                        manager.removeEpic(id);
                        sendText(exchange, "Epic deleted");
                    }
                    break;

                default:
                    sendMethodNotAllowed(exchange);
            }
        } catch (JsonSyntaxException e) {
            sendError(exchange, 400, "Invalid JSON format");
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (IntersectionException e) {
            sendConflict(exchange);
        } catch (Exception e) {
            sendError(exchange, 500, "Server error: " + e.getMessage());
        }
    }
}
