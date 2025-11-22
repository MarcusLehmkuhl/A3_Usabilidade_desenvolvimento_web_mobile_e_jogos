// Configuração da API
const API_CONFIG = {
    // URL base do backend (Railway Production)
    BASE_URL: 'https://a3-usabilidade-deploy-backend-production.up.railway.app/api',
    
    // Endpoints
    ENDPOINTS: {
        AUTH: {
            LOGIN: '/auth/login',
            REGISTER: '/auth/register',
            LOGOUT: '/auth/logout'
        },
        PRODUCTS: '/products',
        CATEGORIES: '/categories',
        SIZES: '/sizes',
        PACKAGINGS: '/packagings',
        STOCK_MOVEMENTS: '/stock-movements',
        REPORTS: '/relatorios',
        ALERTS: '/alertas',
        AUDIT: '/audit-logs'
    },
    
    // Headers padrão
    getHeaders: function(includeAuth = true) {
        const headers = {
            'Content-Type': 'application/json'
        };
        
        if (includeAuth) {
            const token = localStorage.getItem('token');
            if (token) {
                headers['Authorization'] = `Bearer ${token}`;
            }
        }
        
        return headers;
    },
    
    // Função auxiliar para fazer requisições
    fetch: async function(endpoint, options = {}) {
        const url = `${this.BASE_URL}${endpoint}`;
        const config = {
            ...options,
            headers: {
                ...this.getHeaders(options.auth !== false),
                ...options.headers
            }
        };
        
        try {
            const response = await fetch(url, config);
            
            // Se não autorizado, redirecionar para login
            if (response.status === 401) {
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                window.location.href = '/login.html';
                throw new Error('Não autorizado');
            }
            
            // Se resposta vazia (204 No Content), retornar sucesso
            if (response.status === 204 || response.headers.get('content-length') === '0') {
                return { success: true };
            }
            
            const data = await response.json();
            
            if (!response.ok) {
                // Backend retorna erro em data.error.message
                const errorMessage = data.error?.message || data.message || 'Erro na requisição';
                throw new Error(errorMessage);
            }
            
            return data;
        } catch (error) {
            // Se for erro de parsing JSON em resposta vazia, ignorar
            if (error instanceof SyntaxError && error.message.includes('JSON')) {
                return { success: true };
            }
            console.error('Erro na requisição:', error);
            throw error;
        }
    }
};

// Tornar disponível globalmente
window.API_CONFIG = API_CONFIG;

