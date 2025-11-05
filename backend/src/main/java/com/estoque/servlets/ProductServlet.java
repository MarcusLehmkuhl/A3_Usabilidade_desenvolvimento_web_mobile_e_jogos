package com.estoque.servlets;

import com.estoque.dao.ProductDAO;
import com.estoque.models.Product;
import com.estoque.services.AuditService;
import com.estoque.services.AlertService;
import com.estoque.utils.JsonResponse;
import com.estoque.utils.JWTUtil;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/products/*")
public class ProductServlet extends HttpServlet {
    private ProductDAO productDAO = new ProductDAO();
    private AuditService auditService = new AuditService();
    private AlertService alertService = new AlertService();
    private Gson gson = new Gson();
    
    private String getUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return JWTUtil.getUserIdFromToken(token);
        }
        return "anonymous";
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            List<Product> products = productDAO.findAll();
            JsonResponse.sendSuccess(response, products);
        } else {
            String id = pathInfo.substring(1);
            Product product = productDAO.findById(id);
            
            if (product != null) {
                JsonResponse.sendSuccess(response, product);
            } else {
                JsonResponse.sendNotFound(response, "Produto não encontrado");
            }
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        BufferedReader reader = request.getReader();
        Product product = gson.fromJson(reader, Product.class);
        
        if (product == null || product.getName() == null || product.getPrice() == null) {
            JsonResponse.sendBadRequest(response, "Dados inválidos");
            return;
        }
        
        Product created = productDAO.create(product);
        
        if (created != null) {
            // Registrar auditoria
            String userId = getUserIdFromRequest(request);
            auditService.logCreate(userId, "PRODUCT", created.getId(), created, request.getRemoteAddr());
            
            // Verificar se precisa criar alerta de estoque baixo
            alertService.checkAndCreateLowStockAlert(created);
            
            JsonResponse.sendCreated(response, created);
        } else {
            JsonResponse.sendInternalError(response, "Erro ao criar produto");
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
        
        // Buscar valor antigo para auditoria
        Product oldProduct = productDAO.findById(id);
        
        BufferedReader reader = request.getReader();
        Product product = gson.fromJson(reader, Product.class);
        
        if (product == null) {
            JsonResponse.sendBadRequest(response, "Dados inválidos");
            return;
        }
        
        Product updated = productDAO.update(id, product);
        
        if (updated != null) {
            // Registrar auditoria
            String userId = getUserIdFromRequest(request);
            auditService.logUpdate(userId, "PRODUCT", id, oldProduct, updated, request.getRemoteAddr());
            
            // Verificar alertas de estoque
            alertService.checkAndCreateLowStockAlert(updated);
            alertService.checkAndCreateOutOfStockAlert(updated);
            
            JsonResponse.sendSuccess(response, updated);
        } else {
            JsonResponse.sendNotFound(response, "Produto não encontrado");
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
        
        // Buscar produto antes de deletar para auditoria
        Product product = productDAO.findById(id);
        
        boolean deleted = productDAO.delete(id);
        
        if (deleted) {
            // Registrar auditoria
            String userId = getUserIdFromRequest(request);
            auditService.logDelete(userId, "PRODUCT", id, product, request.getRemoteAddr());
            
            JsonResponse.sendNoContent(response);
        } else {
            JsonResponse.sendNotFound(response, "Produto não encontrado");
        }
    }
}
