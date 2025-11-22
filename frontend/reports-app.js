// Script para gerenciar a página de relatórios
let categoriesMap = {};
let sizesMap = {};

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
    
    // Gerenciar abas
    const tabs = document.querySelectorAll('.tab');
    const sections = document.querySelectorAll('.report-section');
    
    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            const tabName = this.getAttribute('data-tab');
            
            // Remove active de todas as abas
            tabs.forEach(t => t.classList.remove('active'));
            
            // Adiciona active na aba clicada
            this.classList.add('active');
            
            // Esconde todas as seções
            sections.forEach(section => section.classList.remove('active'));
            
            // Mostra a seção correspondente
            const activeSection = document.querySelector(`[data-section="${tabName}"]`);
            if (activeSection) {
                activeSection.classList.add('active');
            }
        });
    });
    
    // Carregar dados iniciais
    loadCategoriesAndSizes().then(() => {
        loadReports();
    });
});

async function loadCategoriesAndSizes() {
    try {
        const [categoriesData, sizesData] = await Promise.all([
            API_CONFIG.fetch(API_CONFIG.ENDPOINTS.CATEGORIES),
            API_CONFIG.fetch(API_CONFIG.ENDPOINTS.SIZES)
        ]);
        
        const categories = categoriesData.data || [];
        const sizes = sizesData.data || [];
        
        // Criar mapas para lookup rápido
        categoriesMap = {};
        categories.forEach(cat => {
            categoriesMap[cat.id] = cat.name;
        });
        
        sizesMap = {};
        sizes.forEach(size => {
            sizesMap[size.id] = size.name;
        });
    } catch (error) {
        console.error('Erro ao carregar categorias e tamanhos:', error);
    }
}

async function loadReports() {
    await Promise.all([
        loadPriceList(),
        loadBalance(),
        loadBelowMinimum(),
        loadByCategory(),
        loadMovements()
    ]);
}

async function loadPriceList() {
    try {
        const data = await API_CONFIG.fetch(API_CONFIG.ENDPOINTS.PRODUCTS);
        const products = data.data || [];
        
        // Ordenar alfabeticamente
        products.sort((a, b) => a.name.localeCompare(b.name));
        
        renderPriceList(products);
    } catch (error) {
        console.error('Erro ao carregar lista de preços:', error);
    }
}

function renderPriceList(products) {
    const tbody = document.getElementById('lista-precos-tbody');
    
    if (products.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="table-empty">Nenhum produto cadastrado</td></tr>';
        return;
    }
    
    tbody.innerHTML = products.map(product => {
        const categoryName = product.categoryId ? (categoriesMap[product.categoryId] || '-') : '-';
        const unit = product.unit || 'un';
        
        return `
        <tr>
            <td>${product.name}</td>
            <td>R$ ${parseFloat(product.price).toFixed(2)}</td>
            <td>${unit}</td>
            <td>${categoryName}</td>
        </tr>
        `;
    }).join('');
}

async function loadBalance() {
    try {
        const data = await API_CONFIG.fetch(API_CONFIG.ENDPOINTS.PRODUCTS);
        const products = data.data || [];
        
        renderBalance(products);
    } catch (error) {
        console.error('Erro ao carregar balanço:', error);
    }
}

function renderBalance(products) {
    const tbody = document.getElementById('balanco-tbody');
    const totalElement = document.getElementById('balanco-total');
    
    if (products.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="table-empty">Nenhum produto cadastrado</td></tr>';
        if (totalElement) totalElement.textContent = 'R$ 0,00';
        return;
    }
    
    let total = 0;
    
    tbody.innerHTML = products.map(product => {
        const quantity = parseInt(product.stock) || 0;
        const price = parseFloat(product.price) || 0;
        const totalValue = quantity * price;
        total += totalValue;
        
        return `
            <tr>
                <td>${product.name}</td>
                <td>${quantity}</td>
                <td>R$ ${price.toFixed(2)}</td>
                <td>R$ ${totalValue.toFixed(2)}</td>
            </tr>
        `;
    }).join('');
    
    if (totalElement) {
        totalElement.textContent = `R$ ${total.toFixed(2)}`;
    }
}

async function loadBelowMinimum() {
    try {
        const data = await API_CONFIG.fetch(API_CONFIG.ENDPOINTS.PRODUCTS);
        const products = data.data || [];
        
        // Filtrar produtos abaixo do mínimo
        const belowMin = products.filter(p => {
            const stock = parseInt(p.stock) || 0;
            const minStock = parseInt(p.minStock) || 0;
            return minStock > 0 && stock < minStock;
        });
        
        renderBelowMinimum(belowMin);
    } catch (error) {
        console.error('Erro ao carregar produtos abaixo do mínimo:', error);
    }
}

function renderBelowMinimum(products) {
    const tbody = document.getElementById('minimo-tbody');
    
    if (products.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="table-empty">Todos os produtos estão com estoque adequado</td></tr>';
        return;
    }
    
    tbody.innerHTML = products.map(product => {
        const stock = parseInt(product.stock) || 0;
        const minStock = parseInt(product.minStock) || 0;
        const difference = minStock - stock;
        
        return `
            <tr>
                <td>${product.name}</td>
                <td>${minStock}</td>
                <td>${stock}</td>
                <td style="color: var(--danger);">-${difference}</td>
                <td><span style="background: #fef2f2; color: var(--danger); padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: 500;">Crítico</span></td>
            </tr>
        `;
    }).join('');
}

async function loadByCategory() {
    try {
        const [productsData, categoriesData] = await Promise.all([
            API_CONFIG.fetch(API_CONFIG.ENDPOINTS.PRODUCTS),
            API_CONFIG.fetch(API_CONFIG.ENDPOINTS.CATEGORIES)
        ]);
        
        const products = productsData.data || [];
        const categories = categoriesData.data || [];
        
        renderByCategory(products, categories);
    } catch (error) {
        console.error('Erro ao carregar produtos por categoria:', error);
    }
}

function renderByCategory(products, categories) {
    const tbody = document.getElementById('categoria-tbody');
    
    if (categories.length === 0) {
        tbody.innerHTML = '<tr><td colspan="3" class="table-empty">Nenhuma categoria cadastrada</td></tr>';
        return;
    }
    
    const total = products.length;
    
    const categoryCount = categories.map(cat => {
        const count = products.filter(p => p.categoryId === cat.id).length;
        const percentage = total > 0 ? ((count / total) * 100).toFixed(1) : 0;
        
        return {
            name: cat.name,
            count,
            percentage
        };
    });
    
    tbody.innerHTML = categoryCount.map(cat => `
        <tr>
            <td>${cat.name}</td>
            <td>${cat.count}</td>
            <td>${cat.percentage}%</td>
        </tr>
    `).join('');
}

async function loadMovements() {
    try {
        const data = await API_CONFIG.fetch(API_CONFIG.ENDPOINTS.STOCK_MOVEMENTS);
        const movements = data.data || [];
        
        renderMovements(movements);
    } catch (error) {
        console.error('Erro ao carregar movimentações:', error);
    }
}

function renderMovements(movements) {
    const container = document.getElementById('rel-mov-container');
    
    if (movements.length === 0) {
        container.innerHTML = 'Nenhuma movimentação registrada ainda';
        return;
    }
    
    // Agrupar por produto
    const byProduct = {};
    
    movements.forEach(mov => {
        if (!byProduct[mov.productId]) {
            byProduct[mov.productId] = {
                productName: mov.productName || 'Produto',
                entrada: 0,
                saida: 0
            };
        }
        
        if (mov.type === 'entrada') {
            byProduct[mov.productId].entrada += mov.quantity;
        } else if (mov.type === 'saida') {
            byProduct[mov.productId].saida += mov.quantity;
        }
    });
    
    // Converter para array e ordenar
    const sorted = Object.values(byProduct)
        .sort((a, b) => (b.entrada + b.saida) - (a.entrada + a.saida))
        .slice(0, 10); // Top 10
    
    container.innerHTML = `
        <div class="table-wrapper">
            <table class="table">
                <thead>
                    <tr>
                        <th>Produto</th>
                        <th>Entradas</th>
                        <th>Saídas</th>
                        <th>Total</th>
                    </tr>
                </thead>
                <tbody>
                    ${sorted.map(item => `
                        <tr>
                            <td>${item.productName}</td>
                            <td style="color: #10b981;">${item.entrada}</td>
                            <td style="color: #ef4444;">${item.saida}</td>
                            <td><strong>${item.entrada + item.saida}</strong></td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        </div>
    `;
}
