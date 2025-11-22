// Script para gerenciar a página de produtos
// Armazenar categorias e sizes globalmente para lookup
let categoriesMap = {};
let sizesMap = {};
let allProducts = [];

document.addEventListener('DOMContentLoaded', function() {
    
    // Gerenciar abertura e fechamento de modais
    const modals = document.querySelectorAll('.modal-backdrop');
    const openModalButtons = document.querySelectorAll('[data-open-modal]');
    const closeModalButtons = document.querySelectorAll('[data-close-modal]');
    
    // Função para abrir modal
    openModalButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            const modalName = this.getAttribute('data-open-modal');
            const modal = document.querySelector(`.modal-backdrop[data-modal="${modalName}"]`);
            if (modal) {
                // Se for modal de produto, resetar para modo criação
                if (modalName === 'produto') {
                    const form = document.getElementById('form-produto');
                    const submitButton = form.querySelector('button[type="submit"]');
                    form.reset();
                    delete form.dataset.productId;
                    modal.querySelector('.modal-title').textContent = 'Novo Produto';
                    modal.querySelector('.modal-subtitle').textContent = 'Preencha os dados para criar um novo produto.';
                    submitButton.textContent = 'Criar';
                }
                modal.classList.add('open');
            }
        });
    });
    
    // Função para fechar modal
    closeModalButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            const modal = this.closest('.modal-backdrop');
            if (modal) {
                modal.classList.remove('open');
            }
        });
    });
    
    // Fechar modal clicando fora
    modals.forEach(modal => {
        modal.addEventListener('click', function(e) {
            if (e.target === this) {
                this.classList.remove('open');
            }
        });
    });
    
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
    loadCategories();
    
    // Busca de produtos
    const searchInput = document.getElementById('buscar-produtos');
    if (searchInput) {
        searchInput.addEventListener('input', function(e) {
            const searchTerm = e.target.value.toLowerCase().trim();
            filterProducts(searchTerm);
        });
    }
    
    // Form de produto
    const formProduto = document.getElementById('form-produto');
    if (formProduto) {
        formProduto.addEventListener('submit', async function(e) {
            e.preventDefault();
            const productId = this.dataset.productId;
            if (productId) {
                await updateProduct(this, productId);
            } else {
                await saveProduct(this);
            }
        });
    }
});

// Funções para carregar dados
async function loadProducts() {
    try {
        const data = await API_CONFIG.fetch(API_CONFIG.ENDPOINTS.PRODUCTS);
        allProducts = data.data || [];
        renderProducts(allProducts);
    } catch (error) {
        console.error('Erro ao carregar produtos:', error);
    }
}

async function loadCategories() {
    try {
        const data = await API_CONFIG.fetch(API_CONFIG.ENDPOINTS.CATEGORIES);
        const categories = data.data || [];
        
        // Criar mapa de categorias para lookup rápido
        categoriesMap = {};
        categories.forEach(cat => {
            categoriesMap[cat.id] = cat.name;
        });
        
        updateCategorySelect(categories);
    } catch (error) {
        console.error('Erro ao carregar categorias:', error);
    }
}

async function loadSizes() {
    try {
        const data = await API_CONFIG.fetch(API_CONFIG.ENDPOINTS.SIZES);
        const sizes = data.data || [];
        
        // Criar mapa de sizes para lookup rápido
        sizesMap = {};
        sizes.forEach(size => {
            sizesMap[size.id] = size.name;
        });
        
        updateSizeSelect(sizes);
    } catch (error) {
        console.error('Erro ao carregar tamanhos:', error);
    }
}

// Funções para renderizar dados
function renderProducts(products) {
    const tbody = document.getElementById('produtos-tbody');
    const count = document.getElementById('produtos-count');
    
    if (count) {
        count.textContent = `${products.length} produto(s) encontrado(s)`;
    }
    
    if (products.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="table-empty">Nenhum produto encontrado</td></tr>';
        return;
    }
    
    tbody.innerHTML = products.map(product => {
        // Buscar nome da categoria pelo ID
        const categoryName = product.categoryId ? (categoriesMap[product.categoryId] || '-') : '-';
        // Usar unidade diretamente do produto
        const unit = product.unit || 'un';
        // Calcular máximo (estoque atual * 2, ou definir um valor padrão)
        const maxStock = product.maxStock || (product.stock ? product.stock * 2 : 1000);
        
        return `
        <tr>
            <td>${product.name}</td>
            <td>${categoryName}</td>
            <td>R$ ${parseFloat(product.price || 0).toFixed(2)}</td>
            <td>${unit}</td>
            <td>${product.stock || 0}</td>
            <td>${product.minStock || 0}</td>
            <td>${maxStock}</td>
            <td>
                <button class="btn btn-outline" onclick="editProduct('${product.id}')" style="padding: 6px 12px; font-size: 12px; margin-right: 4px;">Editar</button>
                <button class="btn btn-danger-outline" onclick="deleteProduct('${product.id}', '${product.name}')" style="padding: 6px 12px; font-size: 12px;">Excluir</button>
            </td>
        </tr>
        `;
    }).join('');
}

function filterProducts(searchTerm) {
    if (!searchTerm) {
        renderProducts(allProducts);
        return;
    }
    
    const filtered = allProducts.filter(product => {
        const name = (product.name || '').toLowerCase();
        const categoryName = (categoriesMap[product.categoryId] || '').toLowerCase();
        const price = (product.price || 0).toString();
        
        return name.includes(searchTerm) || 
               categoryName.includes(searchTerm) || 
               price.includes(searchTerm);
    });
    
    renderProducts(filtered);
}

function updateCategorySelect(categories) {
    const select = document.getElementById('produto-categoria');
    if (select) {
        select.innerHTML = '<option value="">Selecione uma categoria</option>' +
            categories.map(cat => `<option value="${cat.id}">${cat.name}</option>`).join('');
    }
}

function updateSizeSelect(sizes) {
    const select = document.getElementById('produto-unidade');
    if (select) {
        select.innerHTML = sizes.map(size => `<option value="${size.id}">${size.name}</option>`).join('');
    }
}

// Funções de ação
async function saveProduct(form) {
    const formData = new FormData(form);
    const nome = formData.get('nome');
    const preco = formData.get('preco');
    const estoque = formData.get('estoque');
    const min = formData.get('min');
    const max = formData.get('max');
    const categoria = formData.get('categoria');
    
    // Validação: todos os campos obrigatórios
    if (!nome || nome.trim() === '') {
        await showWarning('Por favor, preencha o nome do produto', 'Campo Obrigatório');
        return;
    }
    
    if (!preco || parseFloat(preco) <= 0) {
        await showWarning('Por favor, preencha um preço válido', 'Campo Obrigatório');
        return;
    }
    
    if (!estoque || parseInt(estoque) < 0) {
        await showWarning('Por favor, preencha o estoque', 'Campo Obrigatório');
        return;
    }
    
    if (!min || parseInt(min) < 0) {
        await showWarning('Por favor, preencha o estoque mínimo', 'Campo Obrigatório');
        return;
    }
    
    if (!max || parseInt(max) < 0) {
        await showWarning('Por favor, preencha o estoque máximo', 'Campo Obrigatório');
        return;
    }
    
    if (!categoria || categoria === '') {
        await showWarning('Por favor, selecione uma categoria', 'Campo Obrigatório');
        return;
    }
    
    const unidade = formData.get('unidade');
    
    const data = {
        name: nome.trim(),
        price: parseFloat(preco),
        stock: parseInt(estoque),
        minStock: parseInt(min),
        maxStock: parseInt(max),
        categoryId: categoria,
        unit: unidade || 'un'
    };
    
    try {
        await API_CONFIG.fetch(API_CONFIG.ENDPOINTS.PRODUCTS, {
            method: 'POST',
            body: JSON.stringify(data)
        });
        
        await showSuccess('Produto criado com sucesso!');
        form.reset();
        document.querySelector('.modal-backdrop[data-modal="produto"]').classList.remove('open');
        loadProducts();
    } catch (error) {
        console.error('Erro:', error);
        await showError('Erro ao criar produto: ' + (error.message || 'Erro desconhecido'));
    }
}

async function deleteProduct(id, productName) {
    // Atualizar mensagem do modal
    document.getElementById('mensagem-exclusao').textContent = 
        `Tem certeza que deseja excluir o produto "${productName}"? Esta ação não pode ser desfeita.`;
    
    // Abrir modal de confirmação
    const modalBackdrop = document.querySelector('.modal-backdrop[data-modal="confirmar-exclusao"]');
    modalBackdrop.classList.add('open');
    
    // Configurar evento do botão Excluir
    const confirmarBtn = document.getElementById('confirmar-exclusao-btn');
    const novoConfirmarBtn = confirmarBtn.cloneNode(true);
    confirmarBtn.parentNode.replaceChild(novoConfirmarBtn, confirmarBtn);
    
    novoConfirmarBtn.addEventListener('click', async () => {
        try {
            await API_CONFIG.fetch(`${API_CONFIG.ENDPOINTS.PRODUCTS}/${id}`, {
                method: 'DELETE'
            });
            
            modalBackdrop.classList.remove('open');
            await showSuccess('Produto excluído com sucesso!');
            loadProducts();
        } catch (error) {
            console.error('Erro:', error);
            modalBackdrop.classList.remove('open');
            await showError('Erro ao excluir produto: ' + (error.message || 'Erro desconhecido'));
        }
    });
}

async function editProduct(id) {
    try {
        // Buscar dados do produto
        const response = await API_CONFIG.fetch(`${API_CONFIG.ENDPOINTS.PRODUCTS}/${id}`);
        const product = response.data;
        
        // Preencher formulário
        const form = document.getElementById('form-produto');
        const modal = document.querySelector('.modal-backdrop[data-modal="produto"]');
        const modalTitle = modal.querySelector('.modal-title');
        const modalSubtitle = modal.querySelector('.modal-subtitle');
        const submitButton = form.querySelector('button[type="submit"]');
        
        form.dataset.productId = product.id;
        form.querySelector('input[name="nome"]').value = product.name || '';
        form.querySelector('input[name="preco"]').value = product.price || 0;
        form.querySelector('input[name="estoque"]').value = product.stock || 0;
        form.querySelector('input[name="min"]').value = product.minStock || 0;
        form.querySelector('input[name="max"]').value = product.maxStock || (product.stock ? product.stock * 2 : 1000);
        form.querySelector('select[name="categoria"]').value = product.categoryId || '';
        form.querySelector('input[name="unidade"]').value = product.unit || 'un';
        
        // Alterar título do modal e botão
        modalTitle.textContent = 'Editar Produto';
        modalSubtitle.textContent = 'Atualize as informações do produto.';
        submitButton.textContent = 'Atualizar';
        
        // Abrir modal
        modal.classList.add('open');
    } catch (error) {
        console.error('Erro ao carregar produto:', error);
        await showError('Erro ao carregar dados do produto');
    }
}

async function updateProduct(form, productId) {
    const formData = new FormData(form);
    const data = {
        name: formData.get('nome'),
        price: parseFloat(formData.get('preco')) || 0,
        stock: parseInt(formData.get('estoque')) || 0,
        minStock: parseInt(formData.get('min')) || 0,
        maxStock: parseInt(formData.get('max')) || 1000,
        categoryId: formData.get('categoria') || null,
        unit: formData.get('unidade') || 'un'
    };
    
    try {
        await API_CONFIG.fetch(`${API_CONFIG.ENDPOINTS.PRODUCTS}/${productId}`, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
        
        await showSuccess('Produto atualizado com sucesso!');
        form.reset();
        delete form.dataset.productId;
        
        // Restaurar título do modal e botão
        const modal = document.querySelector('.modal-backdrop[data-modal="produto"]');
        const submitButton = form.querySelector('button[type="submit"]');
        modal.querySelector('.modal-title').textContent = 'Novo Produto';
        modal.querySelector('.modal-subtitle').textContent = 'Preencha os dados para criar um novo produto.';
        submitButton.textContent = 'Criar';
        
        modal.classList.remove('open');
        loadProducts();
    } catch (error) {
        console.error('Erro:', error);
        await showError('Erro ao atualizar produto: ' + (error.message || 'Erro desconhecido'));
    }
}
