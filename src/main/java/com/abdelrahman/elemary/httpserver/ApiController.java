package com.abdelrahman.elemary.httpserver;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ApiController {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Map<String, BiConsumer<BufferedReader, OutputStream>>> routes = new HashMap<>();

    public void registerRoute(String method, String path, BiConsumer<BufferedReader, OutputStream> handler) {
        routes.computeIfAbsent(method.toUpperCase(), k -> new HashMap<>()).put(path, handler);
    }

    public void handleApiRequest(String method, String path, BufferedReader reader, OutputStream outputStream) throws IOException {
        Map<String, BiConsumer<BufferedReader, OutputStream>> methodRoutes = routes.get(method.toUpperCase());

        if (methodRoutes != null && methodRoutes.containsKey(path)) {
            methodRoutes.get(path).accept(reader, outputStream);
        } else {
            sendJsonResponse(outputStream, Map.of("error", "Route not found"), 404);
        }
    }

    public void sendJsonResponse(OutputStream outputStream, Map<String, String> data, int statusCode) throws IOException {
        String jsonResponse = objectMapper.writeValueAsString(data);
        final String CRLF = "\r\n";
        String responseHeader = "HTTP/1.1 " + statusCode + (statusCode == 200 ? " OK" : " Error") + CRLF +
                "Content-Type: application/json" + CRLF +
                "Access-Control-Allow-Origin: *" + CRLF + // Allow requests from any origin
                "Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS" + CRLF + // Allowed methods
                "Access-Control-Allow-Headers: Content-Type" + CRLF + // Allowed headers
                "Content-Length: " + jsonResponse.getBytes().length + CRLF +
                CRLF;

        outputStream.write(responseHeader.getBytes());
        outputStream.write(jsonResponse.getBytes());
        outputStream.flush();
    }

    public Map<String, Object> parseRequestBody(BufferedReader reader) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        String line;

        // Skip HTTP headers until an empty line is found
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            // Skip headers
        }

        // Read the actual JSON body after headers
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }

        return objectMapper.readValue(requestBody.toString(), Map.class);
    }

}
