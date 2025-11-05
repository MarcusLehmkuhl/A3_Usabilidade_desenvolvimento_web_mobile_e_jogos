package com.estoque.utils;

import com.google.gson.Gson;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonResponse {
    private static final Gson gson = new Gson();

    public static void send(HttpServletResponse response, int status, Object data) throws IOException {
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("data", data);
        
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(jsonResponse));
    }

    public static void sendError(HttpServletResponse response, int status, String message) throws IOException {
        Map<String, Object> jsonResponse = new HashMap<>();
        Map<String, String> error = new HashMap<>();
        error.put("message", message);
        jsonResponse.put("error", error);
        
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(jsonResponse));
    }

    public static void sendSuccess(HttpServletResponse response, Object data) throws IOException {
        send(response, HttpServletResponse.SC_OK, data);
    }

    public static void sendCreated(HttpServletResponse response, Object data) throws IOException {
        send(response, HttpServletResponse.SC_CREATED, data);
    }

    public static void sendNoContent(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    public static void sendBadRequest(HttpServletResponse response, String message) throws IOException {
        sendError(response, HttpServletResponse.SC_BAD_REQUEST, message);
    }

    public static void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        sendError(response, HttpServletResponse.SC_UNAUTHORIZED, message);
    }

    public static void sendNotFound(HttpServletResponse response, String message) throws IOException {
        sendError(response, HttpServletResponse.SC_NOT_FOUND, message);
    }

    public static void sendInternalError(HttpServletResponse response, String message) throws IOException {
        sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
    }
}
