package com.abdelrahman.elemary.httpserver;

public class ResponseBuilder {
    public static String buildHtmlResponse() {
        return """
                <!DOCTYPE html>
                <html lang="en">
                  <head>
                    <meta charset="UTF-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                    <title>Simple Java HTTP Server</title>
                  </head>
                  <body>
                    <h1>This page is served using HTTP Server</h1>
                  </body>
                </html>
                """;
    }
}
