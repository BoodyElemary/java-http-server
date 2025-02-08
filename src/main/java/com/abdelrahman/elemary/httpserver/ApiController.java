package com.abdelrahman.elemary.httpserver;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ApiController {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Map<RouteInfo, BiConsumer<BufferedReader, OutputStream>>> routes = new HashMap<>();

    private static class RouteInfo {
        private final String path;
        private final boolean hasUrlParam;

        public RouteInfo(String path) {
            this.hasUrlParam = path.endsWith("/");
            this.path = path;
        }

        public boolean matches(String requestPath) {
            if (!hasUrlParam) {
                return path.equals(requestPath);
            }
            return requestPath.startsWith(path);
        }

        public String extractParam(String requestPath) {
            if (!hasUrlParam) {
                return "";
            }
            return requestPath.substring(path.length());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RouteInfo routeInfo = (RouteInfo) o;
            return path.equals(routeInfo.path);
        }

        @Override
        public int hashCode() {
            return path.hashCode();
        }
    }

    public void registerRoute(String method, String path, BiConsumer<BufferedReader, OutputStream> handler) {
        RouteInfo routeInfo = new RouteInfo(path);
        routes.computeIfAbsent(method.toUpperCase(), k -> new HashMap<>()).put(routeInfo, handler);
    }

    public void handleApiRequest(String method, String path, BufferedReader reader, OutputStream outputStream) throws IOException {
        Map<RouteInfo, BiConsumer<BufferedReader, OutputStream>> methodRoutes = routes.get(method.toUpperCase());

        if (methodRoutes != null) {
            for (Map.Entry<RouteInfo, BiConsumer<BufferedReader, OutputStream>> entry : methodRoutes.entrySet()) {
                RouteInfo routeInfo = entry.getKey();
                if (routeInfo.matches(path)) {
                    // Store the URL parameter in a thread-local variable
                    String param = routeInfo.extractParam(path);
                    CurrentRequest.setUrlParameter(param);

                    entry.getValue().accept(reader, outputStream);
                    return;
                }
            }
        }
        sendJsonResponse(outputStream, Map.of("error", "Route not found"), 404);
    }

    public void sendJsonResponse(OutputStream outputStream, Map<String, String> data, int statusCode) throws IOException {
        String jsonResponse = objectMapper.writeValueAsString(data);
        final String CRLF = "\r\n";
        String responseHeader = "HTTP/1.1 " + statusCode + (statusCode == 200 ? " OK" : " Error") + CRLF +
                "Content-Type: application/json" + CRLF +
                "Access-Control-Allow-Origin: *" + CRLF +
                "Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS" + CRLF +
                "Access-Control-Allow-Headers: Content-Type" + CRLF +
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

    // Helper class to store request-specific data
    public static class CurrentRequest {
        private static final ThreadLocal<String> urlParameter = new ThreadLocal<>();

        public static void setUrlParameter(String param) {
            urlParameter.set(param);
        }

        public static String getUrlParameter() {
            return urlParameter.get();
        }

        public static void clear() {
            urlParameter.remove();
        }
    }

    // Helper method to get URL parameter
    public static String getUrlParameter() {
        return CurrentRequest.getUrlParameter();
    }
}