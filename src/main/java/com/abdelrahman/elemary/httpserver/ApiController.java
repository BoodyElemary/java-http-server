package com.abdelrahman.elemary.httpserver;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class ApiController {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void handleApiRequest(String method, String path, BufferedReader reader, OutputStream outputStream) throws IOException {
        Map<String, String> responseMap = new HashMap<>();

        switch (method) {
            case "GET":
                responseMap.put("message", "This is a GET request");
                break;
            case "POST":
                responseMap.put("message", "This is a POST request");
                break;
            case "PUT":
                responseMap.put("message", "This is a PUT request");
                break;
            case "DELETE":
                responseMap.put("message", "This is a DELETE request");
                break;
            default:
                responseMap.put("error", "Unsupported HTTP method");
        }

        sendJsonResponse(outputStream, responseMap);
    }

    private void sendJsonResponse(OutputStream outputStream, Map<String, String> data) throws IOException {
        String jsonResponse = objectMapper.writeValueAsString(data);
        final String CRLF = "\r\n";
        String responseHeader = "HTTP/1.1 200 OK" + CRLF +
                "Content-Type: application/json" + CRLF +
                "Content-Length: " + jsonResponse.getBytes().length + CRLF +
                CRLF;

        outputStream.write(responseHeader.getBytes());
        outputStream.write(jsonResponse.getBytes());
        outputStream.flush();
    }
}