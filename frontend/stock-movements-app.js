// Script para gerenciar a página de movimentação de estoque
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
        logoutBtn.addEventListener('click', async function() {
            const confirmed = await showConfirm('Deseja realmente sair?', 'Confirmar Saída');
            if (confirmed) {
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                window.location.href = 'login.html';
            }
        });
    }
    
    // Carregar dados iniciais
    loadProducts();
    loadMovements();
    
    // Form de movimentação
    const formMov = document.getElementById('form-mov');
    if (formMov) {
        formMov.addEventListener('submit', async function(e) {
            e.preventDefault();
            await saveMovement(this);
        });
    }
});

// Funções para carregar dados
async function loadProducts() {
    try {
        const data = await API_CONFIG.fetch(API_CONFIG.ENDPOINTS.PRODUCTS);
        updateProductSelect(data.data || []);
    } catch (error) {
        console.error('Erro ao carregar produtos:', error);
    }
}

async function loadMovements() {
    try {
        const data = await API_CONFIG.fetch(API_CONFIG.ENDPOINTS.STOCK_MOVEMENTS);
        renderMovements(data.data || []);
    } catch (error) {
        console.error('Erro ao carregar movimentações:', error);
    }
}

// Funções para renderizar dados
function updateProductSelect(products) {
    const select = document.getElementById('mov-produto');
    if (select) {
        select.innerHTML = '<option value="">Selecione o produto</option>' +
            products.map(product => `<option value="${product.id}">${product.name}</option>`).join('');
    }
}

function renderMovements(movements) {
    const tbody = document.getElementById('mov-tbody');
    
    if (movements.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="table-empty">Nenhuma movimentação registrada</td></tr>';
        return;
    }
    
    // Ordenar por data (mais recente primeiro)
    movements.sort((a, b) => new Date(b.date || b.createdAt) - new Date(a.date || a.createdAt));
    
    tbody.innerHTML = movements.map(mov => {
        const date = new Date(mov.date || mov.createdAt);
        const formattedDate = date.toLocaleDateString('pt-BR');
        const type = mov.type || mov.movementType || 'entrada';
        const typeLabel = type === 'entrada' ? 'Entrada' : 'Saída';
        const typeColor = type === 'entrada' ? 'var(--success)' : 'var(--danger)';
        
        return `
            <tr>
                <td>${mov.productName || mov.product?.name || '-'}</td>
                <td>${formattedDate}</td>
                <td style="color: ${typeColor}; font-weight: 500;">${typeLabel}</td>
                <td>${mov.quantity || 0}</td>
            </tr>
        `;
    }).join('');
}

// Funções de ação
async function saveMovement(form) {
    const formData = new FormData(form);
    const data = {
        productId: formData.get('produto'),
        type: formData.get('tipo'),
        quantity: parseInt(formData.get('quantidade')) || 0,
        reason: formData.get('motivo') || '',
        date: new Date().toISOString()
    };
    
    if (!data.productId || !data.type || data.quantity <= 0) {
        await showWarning('Por favor, preencha todos os campos obrigatórios', 'Campos Obrigatórios');
        return;
    }
    
    try {
        await API_CONFIG.fetch(API_CONFIG.ENDPOINTS.STOCK_MOVEMENTS, {
            method: 'POST',
            body: JSON.stringify(data)
        });
        
        await showSuccess('Movimentação registrada com sucesso!');
        form.reset();
        loadMovements();
        loadProducts(); // Recarregar para atualizar estoque
    } catch (error) {
        console.error('Erro:', error);
        await showError('Erro ao registrar movimentação: ' + (error.message || 'Erro desconhecido'));
    }
}
