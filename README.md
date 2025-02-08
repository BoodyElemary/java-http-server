# HTTP Server Application

## Overview
This HTTP Server is a simple, multi-threaded Java server capable of handling both API requests and serving static content. It is designed to be lightweight, flexible, and easily configurable using a properties file.

## Features
- **Multi-threaded:** Handles multiple client requests concurrently.
- **API Support:** Allows custom API routes with dynamic responses.
- **Static Content Serving:** Serves HTML, CSS, JavaScript, and other static files.
- **CORS Support:** Enables cross-origin requests for frontend applications.

## Project Structure
```
├── src/main/java/com/abdelrahman/elemary/httpserver
│   ├── HttpServer.java
│   ├── ClientHandler.java
│   └── ApiController.java
├── src/main/resources
│   ├── webroot/            # Directory for static files
│   └── http.properties     # Configuration file
└── pom.xml                 # Maven configuration
```

## Getting Started

### Prerequisites
- Java Development Kit (JDK 8 or higher)
- Maven

### Build and Run
```bash
# Compile the project
mvn clean install

# Run the server
java -cp target/your-jar-file.jar com.abdelrahman.elemary.httpserver.HttpServer
```

## Configuration
The server is configured using the `http.properties` file located in `src/main/resources/`.

### Example `http.properties` File
```properties
webroot=src/main/resources/webroot
db_url=jdbc:postgresql://localhost:5432/Todo
db_username=postgres
db_password=password


```
- **server.port:** Defines the port on which the server listens.
- **server.webroot:** Specifies the directory for serving static files.

## API Usage

### Defining Custom Routes
Custom API routes can be registered in the `ApiController.java`:
```java
apiController.registerRoute("/api/hello", "GET", (method, path, reader, outputStream) -> {
    Map<String, String> response = new HashMap<>();
    response.put("message", "Hello, API!");
    HttpServer.sendJsonResponse(outputStream, new ObjectMapper().writeValueAsString(response));
});
```

### Example API Request
```bash
# GET Request
curl http://localhost:8080/api/hello
```
Response:
```json
{
  "message": "Hello, API!"
}
```

## Serving Static Files
Place your static files (HTML, CSS, JS, images) inside the `webroot` directory.

### Example
```
webroot/
├── index.html
└── styles.css
```
Access via:
```
http://localhost:8080/index.html
```

## CORS Support
The server includes CORS headers to allow cross-origin requests:
```http
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
Access-Control-Allow-Headers: Content-Type
```

## Contributing
Feel free to fork the repository, make changes, and submit pull requests.

## License
This project is open-source and available under the MIT License.

