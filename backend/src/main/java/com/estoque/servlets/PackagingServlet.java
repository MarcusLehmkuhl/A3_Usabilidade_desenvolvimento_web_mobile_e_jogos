package com.estoque.servlets;

import com.estoque.dao.PackagingDAO;
import com.estoque.models.Packaging;
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

@WebServlet("/api/packagings/*")
public class PackagingServlet extends HttpServlet {
    private PackagingDAO packagingDAO = new PackagingDAO();
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            List<Packaging> packagings = packagingDAO.findAll();
            JsonResponse.sendSuccess(response, packagings);
        } else {
            String id = pathInfo.substring(1);
            Packaging packaging = packagingDAO.findById(id);
            
            if (packaging != null) {
                JsonResponse.sendSuccess(response, packaging);
            } else {
                JsonResponse.sendNotFound(response, "Embalagem não encontrada");
            }
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        BufferedReader reader = request.getReader();
        Packaging packaging = gson.fromJson(reader, Packaging.class);
        
        if (packaging == null || packaging.getName() == null) {
            JsonResponse.sendBadRequest(response, "Dados inválidos");
            return;
        }
        
        Packaging created = packagingDAO.create(packaging);
        
        if (created != null) {
            JsonResponse.sendCreated(response, created);
        } else {
            JsonResponse.sendInternalError(response, "Erro ao criar embalagem");
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
        Packaging packaging = gson.fromJson(reader, Packaging.class);
        
        if (packaging == null) {
            JsonResponse.sendBadRequest(response, "Dados inválidos");
            return;
        }
        
        Packaging updated = packagingDAO.update(id, packaging);
        
        if (updated != null) {
            JsonResponse.sendSuccess(response, updated);
        } else {
            JsonResponse.sendNotFound(response, "Embalagem não encontrada");
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
        boolean deleted = packagingDAO.delete(id);
        
        if (deleted) {
            JsonResponse.sendNoContent(response);
        } else {
            JsonResponse.sendNotFound(response, "Embalagem não encontrada");
        }
    }
}
