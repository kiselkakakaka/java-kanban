package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Task;
import model.exceptions.IntersectionException;
import model.exceptions.NotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public TaskHandler(TaskManager manager, Gson gson) {
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
                        List<Task> tasks = manager.getTasks();
                        sendText(exchange, gson.toJson(tasks));
                    } else {
                        int id = parseId(query);
                        Task task = manager.getTask(id);
                        sendText(exchange, gson.toJson(task));
                    }
                    break;

                case "POST":
                    InputStream body = exchange.getRequestBody();
                    String json = new String(body.readAllBytes(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(json, Task.class);

                    if (task.getId() != 0) {
                        manager.updateTask(task);
                    } else {
                        manager.addNewTask(task);
                    }
                    sendCreated(exchange);
                    break;

                case "DELETE":
                    if (query == null) {
                        manager.removeAllTasks();
                        sendText(exchange, "All tasks deleted.");
                    } else {
                        int id = parseId(query);
                        manager.removeTask(id);
                        sendText(exchange, "Task with id=" + id + " deleted.");
                    }
                    break;

                default:
                    sendMethodNotAllowed(exchange);
            }

        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (IntersectionException e) {
            sendConflict(exchange);
        } catch (Exception e) {
            sendServerError(exchange, e.getMessage());
            e.printStackTrace();
        }
    }
}
