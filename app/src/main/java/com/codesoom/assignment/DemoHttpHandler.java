package com.codesoom.assignment;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class DemoHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        URI uri = httpExchange.getRequestURI();
        String path = uri.getPath();

        System.out.println(method + " " + path);

        String content = "[]";

        if(path.equals("/tasks")){
            if(method.equals("GET")){
                httpExchange.sendResponseHeaders(200, content.getBytes().length);

                OutputStream outputStream = httpExchange.getResponseBody();

                outputStream.write(content.getBytes());
                outputStream.flush();
                outputStream.close();
            }
        }
    }
}
