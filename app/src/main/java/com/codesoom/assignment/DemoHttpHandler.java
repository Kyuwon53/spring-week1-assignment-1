package com.codesoom.assignment;

import com.codesoom.assignment.models.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DemoHttpHandler implements HttpHandler {

    private List<Task> tasks = new ArrayList<>();

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        System.out.println(method + " " + path);

        InputStream inputStream = exchange.getRequestBody();
        String body = new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.joining("\n"));

        // path가 아이디를 가지는지 여부
        boolean hasTaskId = Pattern.matches("/tasks/[0-9]+$", path);

        // 콘텐츠 초기화
        String content = null;

        // GET /tasks
        if (method.equals("GET") && path.equals("/tasks")) {
            content = tasksToJson(tasks);
        }

        // GET /tasks/{id}
        if (method.equals("GET") && hasTaskId) {
            Long id = extractIdFromPath(path);
            Task findTask = findTaskById(id);
            content = taskToJson(findTask);
        }

        // POST /tasks
        if (method.equals("POST") && path.equals("/tasks")) {
            Task task = createTaskWithId(body);
            content = taskToJson(task);
            tasks.add(task);
        }

        // Delete /tasks/{id}
        if (method.equals("DELETE") && hasTaskId) {
            Long id = extractIdFromPath(path);
            Task findTask = findTaskById(id);
            tasks.remove(findTask);
        }

        // PUT,PATCH /tasks/{id}
        // 여기서는 Task 객체의 변경 가능한 필드가 title 뿐이므로 put, patch가 동일하게 동작한다.
        if ((method.equals("PATCH") || method.equals("PUT")) && hasTaskId) {
            Long id = extractIdFromPath(path);
            Task findTask = findTaskById(id);
            Task inputTask = toTask(body);
            findTask.setTitle(inputTask.getTitle());

            content = taskToJson(findTask);
        }

        exchange.sendResponseHeaders(200, content.getBytes().length);
        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(content.getBytes());
        responseBody.flush();
        responseBody.close();
    }

    /**
     * Task 객체 생성
     * @param body
     * @return
     * @throws JsonProcessingException
     */
    private Task createTaskWithId(String body) throws JsonProcessingException {
        Task task;
        task = toTask(body);
        if (tasks.size() == 0) {
            task.setId(1L);
        } else {
            long maxId = tasks
                    .stream()
                    .mapToLong(Task::getId)
                    .max()
                    .orElseThrow(NoSuchElementException::new);
            task.setId(maxId + 1L);
        }
        System.out.println("task = " + task);

        return task;
    }

    /**
     * Id로 task 찾기
     *
     * @param id 아이디
     * @return Task
     */
    private Task findTaskById(Long id) {
        return tasks
                .stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    /**
     * Path에서 Id 추출하기
     *
     * @param path url 경로
     * @return Id
     */
    private Long extractIdFromPath(String path) {
        String[] pathSplit = path.split("/");
        String id = pathSplit[pathSplit.length - 1];
        return Long.parseLong(id);
    }

    /**
     * Json을 Task 객체로 변환
     *
     * @param content 콘텐츠
     * @return Task 객체
     * @throws JsonProcessingException
     */
    private Task toTask(String content) throws JsonProcessingException {
        return mapper.readValue(content, Task.class);
    }

    /**
     * Task 객체 리스트를 Json으로 변환
     *
     * @param tasks task 리스트
     * @return String/Json으로 변환된 객체
     * @throws IOException
     */
    private String tasksToJson(List<Task> tasks) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mapper.writeValue(outputStream, tasks);

        return outputStream.toString();
    }

    /**
     * Task 객체를 Json으로 변환
     *
     * @param task task 리스트
     * @return String/Json으로 변환된 객체
     * @throws IOException
     */
    private String taskToJson(Task task) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mapper.writeValue(outputStream, task);

        return outputStream.toString();
    }
}
