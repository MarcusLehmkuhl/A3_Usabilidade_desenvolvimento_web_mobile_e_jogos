package com.estoque.services;

import com.estoque.dao.AuditLogDAO;
import com.estoque.models.AuditLog;
import com.google.gson.Gson;

public class AuditService {
    private AuditLogDAO auditLogDAO = new AuditLogDAO();
    private Gson gson = new Gson();
    
    public void logCreate(String userId, String entityType, String entityId, Object newValue, String ipAddress) {
        AuditLog log = new AuditLog(userId, "CREATE", entityType, entityId);
        log.setNewValue(gson.toJson(newValue));
        log.setIpAddress(ipAddress);
        auditLogDAO.create(log);
    }
    
    public void logUpdate(String userId, String entityType, String entityId, Object oldValue, Object newValue, String ipAddress) {
        AuditLog log = new AuditLog(userId, "UPDATE", entityType, entityId);
        log.setOldValue(gson.toJson(oldValue));
        log.setNewValue(gson.toJson(newValue));
        log.setIpAddress(ipAddress);
        auditLogDAO.create(log);
    }
    
    public void logDelete(String userId, String entityType, String entityId, Object oldValue, String ipAddress) {
        AuditLog log = new AuditLog(userId, "DELETE", entityType, entityId);
        log.setOldValue(gson.toJson(oldValue));
        log.setIpAddress(ipAddress);
        auditLogDAO.create(log);
    }
}
