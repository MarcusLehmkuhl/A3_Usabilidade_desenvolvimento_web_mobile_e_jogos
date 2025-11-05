package com.estoque.servlets;

import com.estoque.dao.UserDAO;
import com.estoque.models.User;
import com.estoque.utils.JWTUtil;
import com.estoque.utils.JsonResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();
    private Gson gson = new Gson();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            JsonResponse.sendBadRequest(response, "Endpoint não especificado");
            return;
        }
        
        switch (pathInfo) {
            case "/login":
                handleLogin(request, response);
                break;
            case "/register":
                handleRegister(request, response);
                break;
            default:
                JsonResponse.sendNotFound(response, "Endpoint não encontrado");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if ("/me".equals(pathInfo)) {
            handleGetCurrentUser(request, response);
        } else {
            JsonResponse.sendNotFound(response, "Endpoint não encontrado");
        }
    }
    
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        BufferedReader reader = request.getReader();
        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
        
        if (jsonObject == null || !jsonObject.has("email") || !jsonObject.has("password")) {
            JsonResponse.sendBadRequest(response, "Email e senha são obrigatórios");
            return;
        }
        
        String email = jsonObject.get("email").getAsString();
        String password = jsonObject.get("password").getAsString();
        
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            JsonResponse.sendBadRequest(response, "Email e senha são obrigatórios");
            return;
        }
        
        User user = userDAO.findByEmail(email);
        
        if (user == null) {
            JsonResponse.sendUnauthorized(response, "Credenciais inválidas");
            return;
        }
        
        if (!userDAO.checkPassword(password, user.getPassword())) {
            JsonResponse.sendUnauthorized(response, "Credenciais inválidas");
            return;
        }
        
        String token = JWTUtil.generateToken(user.getId(), user.getEmail());
        
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("email", user.getEmail());
        userData.put("name", user.getName());
        
        result.put("user", userData);
        result.put("token", token);
        
        JsonResponse.sendSuccess(response, result);
    }
    
    private void handleRegister(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        BufferedReader reader = request.getReader();
        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
        
        if (jsonObject == null || !jsonObject.has("email") || !jsonObject.has("password")) {
            JsonResponse.sendBadRequest(response, "Email e senha são obrigatórios");
            return;
        }
        
        String email = jsonObject.get("email").getAsString();
        String password = jsonObject.get("password").getAsString();
        String name = jsonObject.has("name") ? jsonObject.get("name").getAsString() : null;
        
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            JsonResponse.sendBadRequest(response, "Email e senha são obrigatórios");
            return;
        }
        
        if (userDAO.findByEmail(email) != null) {
            JsonResponse.sendBadRequest(response, "Email já cadastrado");
            return;
        }
        
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setName(name);
        
        User createdUser = userDAO.create(newUser);
        
        if (createdUser == null) {
            JsonResponse.sendInternalError(response, "Erro ao criar usuário");
            return;
        }
        
        String token = JWTUtil.generateToken(createdUser.getId(), createdUser.getEmail());
        
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", createdUser.getId());
        userData.put("email", createdUser.getEmail());
        userData.put("name", createdUser.getName());
        
        result.put("user", userData);
        result.put("token", token);
        
        JsonResponse.sendCreated(response, result);
    }
    
    private void handleGetCurrentUser(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            JsonResponse.sendUnauthorized(response, "Token não fornecido");
            return;
        }
        
        String token = authHeader.substring(7);
        String userId = JWTUtil.getUserIdFromToken(token);
        
        if (userId == null) {
            JsonResponse.sendUnauthorized(response, "Token inválido");
            return;
        }
        
        User user = userDAO.findById(userId);
        
        if (user == null) {
            JsonResponse.sendNotFound(response, "Usuário não encontrado");
            return;
        }
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("email", user.getEmail());
        userData.put("name", user.getName());
        
        JsonResponse.sendSuccess(response, userData);
    }
}
