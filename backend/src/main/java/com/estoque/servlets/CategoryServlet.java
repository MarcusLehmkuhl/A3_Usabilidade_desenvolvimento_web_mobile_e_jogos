package com.estoque.servlets;

import com.estoque.dao.CategoryDAO;
import com.estoque.models.Category;
import com.estoque.utils.JsonResponse;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/categories/*")
public class CategoryServlet extends HttpServlet {
    private CategoryDAO categoryDAO = new CategoryDAO();
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            List<Category> categories = categoryDAO.findAll();
            JsonResponse.sendSuccess(response, categories);
        } else {
            String id = pathInfo.substring(1);
            Category category = categoryDAO.findById(id);
            
            if (category != null) {
                JsonResponse.sendSuccess(response, category);
            } else {
                JsonResponse.sendNotFound(response, "Categoria não encontrada");
            }
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        BufferedReader reader = request.getReader();
        Category category = gson.fromJson(reader, Category.class);
        
        if (category == null || category.getName() == null) {
            JsonResponse.sendBadRequest(response, "Dados inválidos");
            return;
        }
        
        Category created = categoryDAO.create(category);
        
        if (created != null) {
            JsonResponse.sendCreated(response, created);
        } else {
            JsonResponse.sendInternalError(response, "Erro ao criar categoria");
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            JsonResponse.sendBadRequest(response, "ID não fornecido");
            return;
        }
        
        String id = pathInfo.substring(1);
        BufferedReader reader = request.getReader();
        Category category = gson.fromJson(reader, Category.class);
        
        if (category == null) {
            JsonResponse.sendBadRequest(response, "Dados inválidos");
            return;
        }
        
        Category updated = categoryDAO.update(id, category);
        
        if (updated != null) {
            JsonResponse.sendSuccess(response, updated);
        } else {
            JsonResponse.sendNotFound(response, "Categoria não encontrada");
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            JsonResponse.sendBadRequest(response, "ID não fornecido");
            return;
        }
        
        String id = pathInfo.substring(1);
        boolean deleted = categoryDAO.delete(id);
        
        if (deleted) {
            JsonResponse.sendNoContent(response);
        } else {
            JsonResponse.sendNotFound(response, "Categoria não encontrada");
        }
    }
}
