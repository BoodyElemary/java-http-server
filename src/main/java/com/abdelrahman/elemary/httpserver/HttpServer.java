package com.abdelrahman.elemary.httpserver;

import com.abdelrahman.elemary.httpserver.config.Configuration;
import com.abdelrahman.elemary.httpserver.config.ConfigurationManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Simple Java HTTP Server with Multi-threading
 */
public class HttpServer {
    private static final String WEB_ROOT = "src/main/resources/webroot";

    public static void main(String[] args) {
        System.out.println("The server is now started...");
        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.properties");
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();

        try (ServerSocket serverSocket = new ServerSocket(conf.getPort())) {
            System.out.println("Listening on port " + conf.getPort());

            while (true) { // Keep server running to handle multiple requests
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket, WEB_ROOT)).start(); // Handle each client in a new thread
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}