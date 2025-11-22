// Script para gerenciar a página de reajuste de preços
document.addEventListener('DOMContentLoaded', function() {
    
    // Marcar link ativo na navegação
    const currentPage = document.body.getAttribute('data-page');
    const navLinks = document.querySelectorAll('.nav-link');
    navLinks.forEach(link => {
        if (link.getAttribute('data-page') === currentPage) {
            link.classList.add('active');
        }
    });
    
    // Botão de logout
    const logoutBtn = document.querySelector('.logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function() {
            if (confirm('Deseja realmente sair?')) {
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                window.location.href = 'login.html';
            }
        });
    }
    
    // Form de reajuste
    const formReajuste = document.getElementById('form-reajuste');
    if (formReajuste) {
        formReajuste.addEventListener('submit', async function(e) {
            e.preventDefault();
            await applyPriceAdjustment(this);
        });
    }
});

// Função para aplicar reajuste
async function applyPriceAdjustment(form) {
    const formData = new FormData(form);
    const percentage = parseFloat(formData.get('percentual')) || 0;
    
    if (percentage === 0) {
        alert('Por favor, informe um percentual válido');
        return;
    }
    
    const confirmMessage = percentage > 0 
        ? `Confirma o aumento de ${percentage}% em todos os produtos?`
        : `Confirma a redução de ${Math.abs(percentage)}% em todos os produtos?`;
    
    if (!confirm(confirmMessage)) {
        return;
    }
    
    try {
        // Buscar todos os produtos
        const data = await API_CONFIG.fetch(API_CONFIG.ENDPOINTS.PRODUCTS);
        const products = data.data || [];
        
        if (products.length === 0) {
            alert('Nenhum produto cadastrado para reajuste');
            return;
        }
        
        // Aplicar reajuste em cada produto
        const updates = products.map(product => {
            const currentPrice = parseFloat(product.price) || 0;
            const newPrice = currentPrice * (1 + percentage / 100);
            
            return API_CONFIG.fetch(`${API_CONFIG.ENDPOINTS.PRODUCTS}/${product.id}`, {
                method: 'PUT',
                body: JSON.stringify({
                    id: product.id,
                    name: product.name,
                    description: product.description,
                    categoryId: product.categoryId,
                    sizeId: product.sizeId,
                    packagingId: product.packagingId,
                    unit: product.unit || 'un',
                    price: newPrice,
                    stock: product.stock,
                    minStock: product.minStock,
                    maxStock: product.maxStock
                })
            });
        });
        
        await Promise.all(updates);
        
        alert(`Reajuste de ${percentage}% aplicado com sucesso em ${products.length} produto(s)!`);
        form.reset();
        
    } catch (error) {
        console.error('Erro:', error);
        alert('Erro ao aplicar reajuste: ' + (error.message || 'Erro desconhecido'));
    }
}
