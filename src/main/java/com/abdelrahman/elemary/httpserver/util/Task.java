package com.abdelrahman.elemary.httpserver.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.OutputStream;
import java.io.IOException;

public class Task {
    private int id;
    private String title;
    private String status;
    private Date dueDate;

    public Task(int id, String title, String status, Date dueDate) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.dueDate = dueDate;
    }

    public Task(String title, String status, Date dueDate) {
        this.title = title;
        this.status = status;
        this.dueDate = dueDate;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getStatus() {
        return status;
    }

    public Date getDueDate() {
        return dueDate;
    }

    // Create
    public void createTask() {
        String query = "INSERT INTO tasks (title, status, due_date) VALUES (?, CAST(? AS task_status), ?)";
        try (Connection conn = PostgresJDBCConnector.getInstance().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, title);
            pstmt.setString(2, status);
            pstmt.setDate(3, dueDate);
            pstmt.executeUpdate();
            System.out.println("Task created successfully.");

        } catch (SQLException e) {
            System.err.println("SQL error (createTask): " + e.getMessage());
        }
    }

    // Read
    public static void readTasks(OutputStream outputStream) {
        String query = "SELECT * FROM tasks";
        List<Map<String, Object>> tasksList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> response = new HashMap<>();

        try (Connection conn = PostgresJDBCConnector.getInstance().connect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> taskMap = new HashMap<>();
                taskMap.put("id", rs.getInt("id"));
                taskMap.put("title", rs.getString("title"));
                taskMap.put("status", rs.getString("status"));
                taskMap.put("dueDate", rs.getDate("due_date"));
                tasksList.add(taskMap);
            }

            response.put("tasks", objectMapper.writeValueAsString(tasksList));
            sendJsonResponse(outputStream, response, 200);

        } catch (SQLException | IOException e) {
            response.put("error", "Failed to retrieve tasks: " + e.getMessage());
            try {
                sendJsonResponse(outputStream, response, 500);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    // Update
    public void updateTask() {
        String query = "UPDATE tasks SET title = ?, status = ?, due_date = ? WHERE id = ?";
        try (Connection conn = PostgresJDBCConnector.getInstance().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, title);
            pstmt.setString(2, status);
            pstmt.setDate(3, dueDate);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
            System.out.println("Task updated successfully.");

        } catch (SQLException e) {
            System.err.println("SQL error (updateTask): " + e.getMessage());
        }
    }

    // Delete
    public void deleteTask() {
        String query = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = PostgresJDBCConnector.getInstance().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Task deleted successfully.");

        } catch (SQLException e) {
            System.err.println("SQL error (deleteTask): " + e.getMessage());
        }
    }

    public static void sendJsonResponse(OutputStream outputStream, Map<String, String> data, int statusCode) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
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

    @Override
    public String toString() {
        return "Task{" +
                "dueDate=" + dueDate +
                ", status='" + status + '\'' +
                ", title='" + title + '\'' +
                ", id=" + id +
                '}';
    }
}
