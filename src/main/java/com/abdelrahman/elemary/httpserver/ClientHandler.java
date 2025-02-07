package com.abdelrahman.elemary.httpserver;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final String webRoot;
    private final ApiController apiController;

    public ClientHandler(Socket socket, String webRoot, ApiController apiController) {
        this.socket = socket;
        this.webRoot = webRoot;
        this.apiController = apiController;
    }

    @Override
    public void run() {
        try (
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            // Read HTTP Request
            String line = reader.readLine();
            if (line == null || line.isEmpty()) return;

            String[] requestParts = line.split(" ");
            String method = requestParts[0];
            String requestedPath = requestParts[1];

            if (requestedPath.startsWith("/api")) {
                apiController.handleApiRequest(method, requestedPath, reader, outputStream);
            } else {
                serveStaticFile(outputStream, requestedPath);
            }

        } catch (IOException e) {
            System.err.println("Client handling error: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    private void serveStaticFile(OutputStream outputStream, String requestedFile) throws IOException {
        if (requestedFile.equals("/")) {
            requestedFile = "/index.html";
        }

        File file = new File(webRoot, requestedFile);
        if (file.exists() && !file.isDirectory()) {
            String contentType = getContentType(requestedFile);
            byte[] fileContent = new byte[(int) file.length()];
            try (FileInputStream fis = new FileInputStream(file)) {
                fis.read(fileContent);
            }

            final String CRLF = "\r\n";
            String responseHeader = "HTTP/1.1 200 OK" + CRLF +
                    "Content-Type: " + contentType + CRLF +
                    "Content-Length: " + fileContent.length + CRLF +
                    CRLF;

            outputStream.write(responseHeader.getBytes());
            outputStream.write(fileContent);
        } else {
            String response = "HTTP/1.1 404 Not Found\r\n\r\nFile Not Found";
            outputStream.write(response.getBytes());
        }
        outputStream.flush();
    }

    private String getContentType(String fileName) {
        if (fileName.endsWith(".html")) return "text/html";
        if (fileName.endsWith(".css")) return "text/css";
        if (fileName.endsWith(".js")) return "application/javascript";
        if (fileName.endsWith(".json")) return "application/json";
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".svg")) return "image/svg+xml";
        if (fileName.endsWith(".ico")) return "image/x-icon";
        return "text/plain";
    }

}
