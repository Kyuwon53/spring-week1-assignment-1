package com.codesoom.assignment;

import com.codesoom.assignment.models.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.checkerframework.checker.units.qual.A;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DemoHttpHandler implements HttpHandler {
    private ObjectMapper objectMapper = new ObjectMapper();
    private List<Task> tasks = new ArrayList<>();
    private Long newId = 0L;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        URI uri = httpExchange.getRequestURI();
        String path = uri.getPath();

        System.out.println(method + " " + path);

        String content = "[]";

        if(path.equals("/tasks")) {
            if (method.equals("GET")) {
                content = "GET list";
            }
            if(method.equals("POST")){
                InputStream inputStream = httpExchange.getRequestBody();
                String body = new BufferedReader(new InputStreamReader(inputStream))
                        .lines()
                        .collect(Collectors.joining("\n"));



                Task task = objectMapper.readValue(body, Task.class);

                newId += 1;
                task.setId(newId);
                tasks.add(task);

                content = toJSON(tasks);

            }
        }

        httpExchange.sendResponseHeaders(200, content.getBytes().length);

        OutputStream outputStream = httpExchange.getResponseBody();

        outputStream.write(content.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    private String toJSON(Object object) throws IOException {
        OutputStream outputStream = new ByteArrayOutputStream();
        objectMapper.writeValue(outputStream, object);

        return outputStream.toString();
    }
}
