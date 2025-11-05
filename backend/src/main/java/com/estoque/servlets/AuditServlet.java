package com.estoque.servlets;

import com.estoque.dao.AuditLogDAO;
import com.estoque.models.AuditLog;
import com.estoque.utils.JsonResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/audit-logs/*")
public class AuditServlet extends HttpServlet {
    private AuditLogDAO auditLogDAO = new AuditLogDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        // Parâmetros de filtro
        String entityType = request.getParameter("entityType");
        String entityId = request.getParameter("entityId");
        String userId = request.getParameter("userId");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        
        List<AuditLog> logs = null;
        
        // Aplicar filtros
        if (entityType != null && entityId != null) {
            // Buscar por tipo e ID da entidade
            logs = auditLogDAO.findByEntityId(entityType, entityId);
            
        } else if (entityType != null) {
            // Buscar por tipo de entidade
            logs = auditLogDAO.findByEntityType(entityType);
            
        } else if (userId != null) {
            // Buscar por usuário
            logs = auditLogDAO.findByUserId(userId);
            
        } else if (startDate != null && endDate != null) {
            // Buscar por período
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date start = sdf.parse(startDate);
                Date end = sdf.parse(endDate);
                
                logs = auditLogDAO.findByDateRange(
                    new Timestamp(start.getTime()),
                    new Timestamp(end.getTime())
                );
                
            } catch (ParseException e) {
                JsonResponse.sendBadRequest(response, "Formato de data inválido. Use: yyyy-MM-dd");
                return;
            }
            
        } else {
            // Buscar todos
            logs = auditLogDAO.findAll();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("logs", logs);
        result.put("count", logs.size());
        
        JsonResponse.sendSuccess(response, result);
    }
}
