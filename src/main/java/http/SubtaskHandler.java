package http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Subtask;
import model.exceptions.IntersectionException;
import model.exceptions.NotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public SubtaskHandler(TaskManager manager, Gson gson) {
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
                        List<Subtask> subtasks = manager.getSubtasks();
                        sendText(exchange, gson.toJson(subtasks));
                    } else {
                        int id = parseId(query);
                        Subtask subtask = manager.getSubtask(id); // может выбросить NotFoundException
                        sendText(exchange, gson.toJson(subtask));
                    }
                    break;

                case "POST":
                    InputStream input = exchange.getRequestBody();
                    String json = new String(input.readAllBytes(), StandardCharsets.UTF_8);
                    Subtask subtask = gson.fromJson(json, Subtask.class);

                    if (subtask.getId() != 0) {
                        manager.updateSubtask(subtask);
                    } else {
                        manager.addNewSubtask(subtask);
                    }
                    sendCreated(exchange); // 201
                    break;

                case "DELETE":
                    if (query == null) {
                        manager.removeAllSubtasks();
                        sendText(exchange, "All subtasks deleted");
                    } else {
                        int id = parseId(query);
                        manager.removeSubtask(id);
                        sendText(exchange, "Subtask with id=" + id + " deleted");
                    }
                    break;

                default:
                    sendMethodNotAllowed(exchange);
            }

        } catch (JsonSyntaxException e) {
            sendError(exchange, 400, "Invalid JSON: " + e.getMessage());
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (IntersectionException e) {
            sendConflict(exchange);
        } catch (Exception e) {
            sendError(exchange, 500, "Server error: " + e.getMessage());
        }
    }
}

