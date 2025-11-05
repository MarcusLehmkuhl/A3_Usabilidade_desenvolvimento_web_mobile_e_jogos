package com.estoque.servlets;

import com.estoque.dao.AlertDAO;
import com.estoque.dao.ProductDAO;
import com.estoque.dao.StockMovementDAO;
import com.estoque.models.Alert;
import com.estoque.models.Product;
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

@WebServlet("/api/relatorios/*")
public class ReportServlet extends HttpServlet {
    private ProductDAO productDAO = new ProductDAO();
    private StockMovementDAO stockMovementDAO = new StockMovementDAO();
    private AlertDAO alertDAO = new AlertDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            JsonResponse.sendBadRequest(response, "Endpoint não especificado");
            return;
        }
        
        switch (pathInfo) {
            case "/dashboard":
                getDashboardData(response);
                break;
            case "/low-stock":
                getLowStockProducts(response);
                break;
            case "/movements-summary":
                getMovementsSummary(response);
                break;
            case "/stock-value":
                getStockValue(response);
                break;
            default:
                JsonResponse.sendNotFound(response, "Relatório não encontrado");
        }
    }
    
    private void getDashboardData(HttpServletResponse response) throws IOException {
        Map<String, Object> dashboard = new HashMap<>();
        
        // Contagem de produtos
        int totalProducts = productDAO.count();
        int lowStockProducts = productDAO.countLowStock();
        
        // Valor total do estoque
        double totalValue = productDAO.getTotalValue();
        
        // Contagem de movimentações
        int totalEntradas = stockMovementDAO.countByType("entrada");
        int totalSaidas = stockMovementDAO.countByType("saida");
        
        // Quantidade total movimentada
        int quantityEntradas = stockMovementDAO.getTotalQuantityByType("entrada");
        int quantitySaidas = stockMovementDAO.getTotalQuantityByType("saida");
        
        // Alertas não lidos
        int unreadAlerts = alertDAO.countUnread();
        
        dashboard.put("totalProdutos", totalProducts);
        dashboard.put("produtosCriticos", lowStockProducts);
        dashboard.put("valorTotalEstoque", totalValue);
        dashboard.put("totalEntradas", totalEntradas);
        dashboard.put("totalSaidas", totalSaidas);
        dashboard.put("quantidadeEntradas", quantityEntradas);
        dashboard.put("quantidadeSaidas", quantitySaidas);
        dashboard.put("alertasNaoLidos", unreadAlerts);
        dashboard.put("saldoMovimentacoes", quantityEntradas - quantitySaidas);
        
        JsonResponse.sendSuccess(response, dashboard);
    }
    
    private void getLowStockProducts(HttpServletResponse response) throws IOException {
        List<Product> lowStockProducts = productDAO.findLowStock();
        
        Map<String, Object> result = new HashMap<>();
        result.put("products", lowStockProducts);
        result.put("count", lowStockProducts.size());
        
        JsonResponse.sendSuccess(response, result);
    }
    
    private void getMovementsSummary(HttpServletResponse response) throws IOException {
        String startDateParam = request.getParameter("startDate");
        String endDateParam = request.getParameter("endDate");
        
        Map<String, Object> summary = new HashMap<>();
        
        int totalEntradas = stockMovementDAO.countByType("entrada");
        int totalSaidas = stockMovementDAO.countByType("saida");
        int quantityEntradas = stockMovementDAO.getTotalQuantityByType("entrada");
        int quantitySaidas = stockMovementDAO.getTotalQuantityByType("saida");
        
        summary.put("totalEntradas", totalEntradas);
        summary.put("totalSaidas", totalSaidas);
        summary.put("quantidadeEntradas", quantityEntradas);
        summary.put("quantidadeSaidas", quantitySaidas);
        summary.put("saldo", quantityEntradas - quantitySaidas);
        
        JsonResponse.sendSuccess(response, summary);
    }
    
    private void getStockValue(HttpServletResponse response) throws IOException {
        double totalValue = productDAO.getTotalValue();
        List<Product> allProducts = productDAO.findAll();
        
        Map<String, Object> result = new HashMap<>();
        result.put("valorTotal", totalValue);
        result.put("totalProdutos", allProducts.size());
        result.put("valorMedio", allProducts.size() > 0 ? totalValue / allProducts.size() : 0);
        
        JsonResponse.sendSuccess(response, result);
    }
}
