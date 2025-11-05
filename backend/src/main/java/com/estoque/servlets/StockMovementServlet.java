package com.estoque.servlets;

import com.estoque.dao.ProductDAO;
import com.estoque.dao.StockMovementDAO;
import com.estoque.models.Product;
import com.estoque.models.StockMovement;
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

@WebServlet("/api/stock-movements/*")
public class StockMovementServlet extends HttpServlet {
    private StockMovementDAO stockMovementDAO = new StockMovementDAO();
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
            List<StockMovement> movements = stockMovementDAO.findAll();
            JsonResponse.sendSuccess(response, movements);
        } else {
            String id = pathInfo.substring(1);
            
            if (id.startsWith("product/")) {
                String productId = id.substring(8);
                List<StockMovement> movements = stockMovementDAO.findByProductId(productId);
                JsonResponse.sendSuccess(response, movements);
            } else {
                StockMovement movement = stockMovementDAO.findById(id);
                
                if (movement != null) {
                    JsonResponse.sendSuccess(response, movement);
                } else {
                    JsonResponse.sendNotFound(response, "Movimentação não encontrada");
                }
            }
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        BufferedReader reader = request.getReader();
        StockMovement movement = gson.fromJson(reader, StockMovement.class);
        
        if (movement == null || movement.getProductId() == null || 
            movement.getType() == null || movement.getQuantity() == null) {
            JsonResponse.sendBadRequest(response, "Dados inválidos");
            return;
        }
        
        if (!movement.getType().equals("entrada") && !movement.getType().equals("saida")) {
            JsonResponse.sendBadRequest(response, "Tipo deve ser 'entrada' ou 'saida'");
            return;
        }
        
        StockMovement created = stockMovementDAO.create(movement);
        
        if (created == null) {
            JsonResponse.sendInternalError(response, "Erro ao criar movimentação");
            return;
        }
        
        int quantityChange = movement.getType().equals("entrada") ? 
                            movement.getQuantity() : -movement.getQuantity();
        
        boolean stockUpdated = productDAO.updateStock(movement.getProductId(), quantityChange);
        
        if (!stockUpdated) {
            JsonResponse.sendInternalError(response, "Erro ao atualizar estoque do produto");
            return;
        }
        
        // Registrar auditoria
        String userId = getUserIdFromRequest(request);
        auditService.logCreate(userId, "STOCK_MOVEMENT", created.getId(), created, request.getRemoteAddr());
        
        // Buscar produto atualizado e verificar alertas
        Product updatedProduct = productDAO.findById(movement.getProductId());
        if (updatedProduct != null) {
            alertService.checkAndCreateLowStockAlert(updatedProduct);
            alertService.checkAndCreateOutOfStockAlert(updatedProduct);
        }
        
        JsonResponse.sendCreated(response, created);
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
        
        // Buscar movimentação antes de deletar para auditoria
        StockMovement movement = stockMovementDAO.findById(id);
        
        boolean deleted = stockMovementDAO.delete(id);
        
        if (deleted) {
            // Registrar auditoria
            String userId = getUserIdFromRequest(request);
            auditService.logDelete(userId, "STOCK_MOVEMENT", id, movement, request.getRemoteAddr());
            
            JsonResponse.sendNoContent(response);
        } else {
            JsonResponse.sendNotFound(response, "Movimentação não encontrada");
        }
    }
}
