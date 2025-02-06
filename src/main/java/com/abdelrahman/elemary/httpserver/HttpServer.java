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
    private static boolean isRunning = true;

    public static void main(String[] args) {
        System.out.println("The server is now started...");
        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.properties");
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();

        try (ServerSocket serverSocket = new ServerSocket(conf.getPort())) {
            System.out.println("Listening on port " + conf.getPort());

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down server...");
                isRunning = false;
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing server socket: " + e.getMessage());
                }
            }));

            while (isRunning) {
                try {
                    Socket socket = serverSocket.accept();
                    new Thread(new ClientHandler(socket)).start();
                } catch (IOException e) {
                    if (isRunning) {
                        System.err.println("Server error: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}
