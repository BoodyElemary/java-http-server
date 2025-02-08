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
        StringBuilder headerBuilder = new StringBuilder();
        String line;
        int contentLength = -1;

        // Read and store headers
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            headerBuilder.append(line).append("\n");
            if (line.toLowerCase().startsWith("content-length:")) {
                contentLength = Integer.parseInt(line.substring(15).trim());
            }
        }

        // Debug print headers
        System.out.println("Headers received:");
        System.out.println(headerBuilder.toString());
        System.out.println("Content-Length: " + contentLength);

        // If no content length or it's 0, return empty map
        if (contentLength <= 0) {
            System.out.println("No content length specified or content length is 0");
            return new HashMap<>();
        }

        // Read the body
        char[] charBuffer = new char[contentLength];
        int totalCharsRead = 0;
        while (totalCharsRead < contentLength) {
            int charsRead = reader.read(charBuffer, totalCharsRead, contentLength - totalCharsRead);
            if (charsRead == -1) {
                break;
            }
            totalCharsRead += charsRead;
        }

        String requestBody = new String(charBuffer, 0, totalCharsRead);

        // Debug print body
        System.out.println("Body received:");
        System.out.println(requestBody);

        // If body is empty, return empty map
        if (requestBody.trim().isEmpty()) {
            System.out.println("Empty request body");
            return new HashMap<>();
        }

        try {
            return objectMapper.readValue(requestBody, Map.class);
        } catch (Exception e) {
            System.out.println("Error parsing JSON: " + e.getMessage());
            System.out.println("Received body: '" + requestBody + "'");
            throw e;
        }
    }

}
