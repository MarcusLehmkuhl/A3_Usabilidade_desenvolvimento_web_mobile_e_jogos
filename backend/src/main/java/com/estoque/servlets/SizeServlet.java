package com.estoque.servlets;

import com.estoque.dao.SizeDAO;
import com.estoque.models.Size;
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

@WebServlet("/api/sizes/*")
public class SizeServlet extends HttpServlet {
    private SizeDAO sizeDAO = new SizeDAO();
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            List<Size> sizes = sizeDAO.findAll();
            JsonResponse.sendSuccess(response, sizes);
        } else {
            String id = pathInfo.substring(1);
            Size size = sizeDAO.findById(id);
            
            if (size != null) {
                JsonResponse.sendSuccess(response, size);
            } else {
                JsonResponse.sendNotFound(response, "Tamanho não encontrado");
            }
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        BufferedReader reader = request.getReader();
        Size size = gson.fromJson(reader, Size.class);
        
        if (size == null || size.getName() == null) {
            JsonResponse.sendBadRequest(response, "Dados inválidos");
            return;
        }
        
        Size created = sizeDAO.create(size);
        
        if (created != null) {
            JsonResponse.sendCreated(response, created);
        } else {
            JsonResponse.sendInternalError(response, "Erro ao criar tamanho");
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
        Size size = gson.fromJson(reader, Size.class);
        
        if (size == null) {
            JsonResponse.sendBadRequest(response, "Dados inválidos");
            return;
        }
        
        Size updated = sizeDAO.update(id, size);
        
        if (updated != null) {
            JsonResponse.sendSuccess(response, updated);
        } else {
            JsonResponse.sendNotFound(response, "Tamanho não encontrado");
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
        boolean deleted = sizeDAO.delete(id);
        
        if (deleted) {
            JsonResponse.sendNoContent(response);
        } else {
            JsonResponse.sendNotFound(response, "Tamanho não encontrado");
        }
    }
}
