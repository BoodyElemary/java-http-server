package com.abdelrahman.elemary.httpserver;

import com.abdelrahman.elemary.httpserver.config.Configuration;
import com.abdelrahman.elemary.httpserver.config.ConfigurationManager;
import com.abdelrahman.elemary.httpserver.util.PostgresJDBCConnector;
import com.abdelrahman.elemary.httpserver.util.Task;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        // Get all tasks
        apiController.registerRoute("GET", "/api/tasks", (reader, outputStream) -> {
            Task.readTasks(outputStream);
        });
        // Create a new task
        apiController.registerRoute("POST", "/api/tasks", (reader, outputStream) -> {
            try {
                Map<String, Object> requestData = apiController.parseRequestBody(reader); // Use instance method
                String title = (String) requestData.get("title");
                String status = (String) requestData.get("status");
                Date dueDate = Date.valueOf((String) requestData.get("dueDate"));

                Task task = new Task(title, status, dueDate);
                task.createTask();
                Task.readTasks(outputStream); // Returning updated list
            } catch (Exception e) {
                System.out.println(e.toString());
                try {
                    apiController.sendJsonResponse(outputStream, Map.of("error", "Invalid request data"), 400);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
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