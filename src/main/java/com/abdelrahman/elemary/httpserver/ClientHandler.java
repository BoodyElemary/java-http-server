package com.abdelrahman.elemary.httpserver;
import java.io.*;
import java.net.Socket;
public class ClientHandler implements Runnable {
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            // Read HTTP Request
            String line;
            while (!(line = reader.readLine()).isEmpty()) {
                System.out.println(line); // Display request headers
            }

            // Prepare HTTP Response
                String html = ResponseBuilder.buildHtmlResponse();

            final String CRLF = "\r\n"; // Correct CRLF
            String response = "HTTP/1.1 200 OK" + CRLF +
                    "Content-Type: text/html" + CRLF +
                    "Content-Length: " + html.getBytes().length + CRLF +
                    CRLF +
                    html + CRLF;

            outputStream.write(response.getBytes());
            outputStream.flush();

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
}
