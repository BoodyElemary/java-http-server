package com.abdelrahman.elemary.httpserver;

import com.abdelrahman.elemary.httpserver.config.Configuration;
import com.abdelrahman.elemary.httpserver.config.ConfigurationManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple Java HTTP Server with Multi-threading
 */
public class HttpServer {
    private static final String WEB_ROOT = "src/main/resources/webroot";

    public static void main(String[] args) {
        System.out.println("The server is now started...");
        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.properties");
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();

        ApiController apiController = new ApiController(); // Initialize ApiController
        apiController.registerRoute("GET", "/api/hello", (reader, outputStream) -> {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Hello, World!");
            try {
                apiController.sendJsonResponse(outputStream,response,200);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        try (ServerSocket serverSocket = new ServerSocket(conf.getPort())) {
            System.out.println("Listening on port " + conf.getPort());

            while (true) { // Keep server running to handle multiple requests
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket, WEB_ROOT, apiController)).start(); // Pass ApiController instance
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}