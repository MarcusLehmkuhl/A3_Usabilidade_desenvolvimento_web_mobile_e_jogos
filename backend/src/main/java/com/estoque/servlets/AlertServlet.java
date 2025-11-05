package com.estoque.servlets;

import com.estoque.dao.AlertDAO;
import com.estoque.models.Alert;
import com.estoque.utils.JsonResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/alertas/*")
public class AlertServlet extends HttpServlet {
    private AlertDAO alertDAO = new AlertDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // Lista todos os alertas ou apenas não lidos
            String unreadOnly = request.getParameter("unread");
            
            List<Alert> alerts;
            if ("true".equals(unreadOnly)) {
                alerts = alertDAO.findUnread();
            } else {
                alerts = alertDAO.findAll();
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("alerts", alerts);
            result.put("count", alerts.size());
            result.put("unreadCount", alertDAO.countUnread());
            
            JsonResponse.sendSuccess(response, result);
            
        } else if (pathInfo.startsWith("/product/")) {
            // Alertas de um produto específico
            String productId = pathInfo.substring(9);
            List<Alert> alerts = alertDAO.findByProductId(productId);
            JsonResponse.sendSuccess(response, alerts);
            
        } else if (pathInfo.equals("/count")) {
            // Contagem de alertas não lidos
            int count = alertDAO.countUnread();
            Map<String, Object> result = new HashMap<>();
            result.put("unreadCount", count);
            JsonResponse.sendSuccess(response, result);
            
        } else {
            JsonResponse.sendNotFound(response, "Endpoint não encontrado");
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
        
        if (pathInfo.equals("/mark-all-read")) {
            // Marcar todos como lidos
            boolean success = alertDAO.markAllAsRead();
            
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("message", "Todos os alertas foram marcados como lidos");
                JsonResponse.sendSuccess(response, result);
            } else {
                JsonResponse.sendInternalError(response, "Erro ao marcar alertas como lidos");
            }
            
        } else {
            // Marcar alerta específico como lido
            String id = pathInfo.substring(1);
            boolean success = alertDAO.markAsRead(id);
            
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("message", "Alerta marcado como lido");
                JsonResponse.sendSuccess(response, result);
            } else {
                JsonResponse.sendNotFound(response, "Alerta não encontrado");
            }
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
        boolean deleted = alertDAO.delete(id);
        
        if (deleted) {
            JsonResponse.sendNoContent(response);
        } else {
            JsonResponse.sendNotFound(response, "Alerta não encontrado");
        }
    }
}
