// Script para gerenciar a página de categorias
console.log('categories-app.js loaded successfully');

let allCategories = [];
let categoriesById = {};

document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM loaded, initializing categories page');
    
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
                // Se for modal de categoria, resetar para modo criação
                if (modalName === 'categoria') {
                    const form = document.getElementById('form-categoria');
                    const submitButton = form.querySelector('button[type="submit"]');
                    form.reset();
                    delete form.dataset.categoryId;
                    modal.querySelector('.modal-title').textContent = 'Nova Categoria';
                    modal.querySelector('.modal-subtitle').textContent = 'Preencha os dados para criar uma nova categoria.';
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
    loadCategories();
    loadSizes();
    loadPackagings();
    
    // Busca de categorias
    const searchInput = document.getElementById('buscar-categorias');
    if (searchInput) {
        searchInput.addEventListener('input', function(e) {
            const searchTerm = e.target.value.toLowerCase().trim();
            filterCategories(searchTerm);
        });
    }
    
    // Form de categoria
    const formCategoria = document.getElementById('form-categoria');
    if (formCategoria) {
        formCategoria.addEventListener('submit', async function(e) {
            e.preventDefault();
            const categoryId = this.dataset.categoryId;
            if (categoryId) {
                await updateCategory(this, categoryId);
            } else {
                await saveCategory(this);
            }
        });
    }
    
    // Form de tamanho
    const formTamanho = document.getElementById('form-tamanho');
    if (formTamanho) {
        formTamanho.addEventListener('submit', async function(e) {
            e.preventDefault();
            await saveSize(this);
        });
    }
    
    // Form de embalagem
    const formEmbalagem = document.getElementById('form-embalagem');
    if (formEmbalagem) {
        formEmbalagem.addEventListener('submit', async function(e) {
            e.preventDefault();
            await savePackaging(this);
        });
    }
});

// Funções para carregar dados
async function loadCategories() {
    try {
        const data = await API_CONFIG.fetch(API_CONFIG.ENDPOINTS.CATEGORIES);
        allCategories = data.data || [];
        
        // Criar mapa de categorias por ID
        categoriesById = {};
        allCategories.forEach(cat => {
            categoriesById[cat.id] = cat;
        });
        
        renderCategories(allCategories);
    } catch (error) {
        console.error('Erro ao carregar categorias:', error);
    }
}

async function loadSizes() {
    try {
        const data = await API_CONFIG.fetch(API_CONFIG.ENDPOINTS.SIZES);
        renderSizes(data.data || []);
        updateSizeSelect(data.data || []);
    } catch (error) {
        console.error('Erro ao carregar tamanhos:', error);
    }
}

async function loadPackagings() {
    try {
        const data = await API_CONFIG.fetch(API_CONFIG.ENDPOINTS.PACKAGINGS);
        renderPackagings(data.data || []);
        updatePackagingSelect(data.data || []);
    } catch (error) {
        console.error('Erro ao carregar embalagens:', error);
    }
}

// Funções para renderizar dados
function renderCategories(categories) {
    const tbody = document.getElementById('categorias-tbody');
    const count = document.getElementById('categorias-count');
    
    if (count) {
        count.textContent = `${categories.length} categoria(s) encontrada(s)`;
    }
    
    if (categories.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="table-empty">Nenhuma categoria encontrada</td></tr>';
        return;
    }
    
    tbody.innerHTML = categories.map(cat => {
        // Escapar aspas simples no nome para uso em onclick
        const safeName = (cat.name || '').replace(/'/g, "\\'");
        
        return `
        <tr>
            <td>${cat.name}</td>
            <td>${cat.description || '-'}</td>
            <td>${cat.sizeName || '-'}</td>
            <td>${cat.packagingName || '-'}</td>
            <td>
                <button class="btn btn-outline" onclick="editCategory('${cat.id}')">Editar</button>
                <button class="btn btn-danger-outline" onclick="deleteCategory('${cat.id}', '${safeName}')">Excluir</button>
            </td>
        </tr>
        `;
    }).join('');
}

function filterCategories(searchTerm) {
    if (!searchTerm) {
        renderCategories(allCategories);
        return;
    }
    
    const filtered = allCategories.filter(category => {
        const name = (category.name || '').toLowerCase();
        const description = (category.description || '').toLowerCase();
        const sizeName = (category.sizeName || '').toLowerCase();
        const packagingName = (category.packagingName || '').toLowerCase();
        
        return name.includes(searchTerm) || 
               description.includes(searchTerm) || 
               sizeName.includes(searchTerm) || 
               packagingName.includes(searchTerm);
    });
    
    // Manter o categoriesById atualizado mesmo em filtros
    renderCategories(filtered);
}

function renderSizes(sizes) {
    const tbody = document.getElementById('tamanhos-tbody');
    
    if (sizes.length === 0) {
        tbody.innerHTML = '<tr><td colspan="2" class="table-empty">Nenhum tamanho cadastrado</td></tr>';
        return;
    }
    
    tbody.innerHTML = sizes.map(size => `
        <tr>
            <td>${size.name}</td>
            <td style="text-align: center;">
                <button class="btn btn-danger-outline" data-action="delete-size" data-id="${size.id}" style="padding: 6px 12px; font-size: 12px;">Excluir</button>
            </td>
        </tr>
    `).join('');
    
    // Adicionar event listeners aos botões
    tbody.querySelectorAll('[data-action="delete-size"]').forEach(btn => {
        btn.addEventListener('click', () => deleteSize(btn.dataset.id));
    });
}

function renderPackagings(packagings) {
    const tbody = document.getElementById('embalagens-tbody');
    
    if (packagings.length === 0) {
        tbody.innerHTML = '<tr><td colspan="2" class="table-empty">Nenhuma embalagem cadastrada</td></tr>';
        return;
    }
    
    tbody.innerHTML = packagings.map(pack => `
        <tr>
            <td>${pack.name}</td>
            <td style="text-align: center;">
                <button class="btn btn-danger-outline" data-action="delete-packaging" data-id="${pack.id}" style="padding: 6px 12px; font-size: 12px;">Excluir</button>
            </td>
        </tr>
    `).join('');
    
    // Adicionar event listeners aos botões
    tbody.querySelectorAll('[data-action="delete-packaging"]').forEach(btn => {
        btn.addEventListener('click', () => deletePackaging(btn.dataset.id));
    });
}

function updateSizeSelect(sizes) {
    const select = document.getElementById('categoria-tamanho');
    if (select) {
        select.innerHTML = '<option value="">Selecione o tamanho</option>' +
            sizes.map(size => `<option value="${size.id}">${size.name}</option>`).join('');
    }
}

function updatePackagingSelect(packagings) {
    const select = document.getElementById('categoria-embalagem');
    if (select) {
        select.innerHTML = '<option value="">Selecione a embalagem</option>' +
            packagings.map(pack => `<option value="${pack.id}">${pack.name}</option>`).join('');
    }
}

// Funções de ação
async function saveCategory(form) {
    const formData = new FormData(form);
    const nome = formData.get('nome');
    const tamanho = formData.get('tamanho');
    const embalagem = formData.get('embalagem');
    
    // Validação
    if (!nome || nome.trim() === '') {
        await showWarning('Por favor, preencha o nome da categoria', 'Campo Obrigatório');
        return;
    }
    
    if (!tamanho || tamanho === '') {
        await showWarning('Por favor, selecione um tamanho', 'Campo Obrigatório');
        return;
    }
    
    if (!embalagem || embalagem === '') {
        await showWarning('Por favor, selecione uma embalagem', 'Campo Obrigatório');
        return;
    }
    
    const data = {
        name: nome.trim(),
        description: formData.get('descricao'),
        sizeId: tamanho,
        packagingId: embalagem
    };
    
    try {
        await API_CONFIG.fetch(API_CONFIG.ENDPOINTS.CATEGORIES, {
            method: 'POST',
            body: JSON.stringify(data)
        });
        
        await showSuccess('Categoria criada com sucesso!');
        form.reset();
        document.querySelector('.modal-backdrop[data-modal="categoria"]').classList.remove('open');
        loadCategories();
    } catch (error) {
        console.error('Erro:', error);
        await showError('Erro ao criar categoria: ' + (error.message || 'Erro desconhecido'));
    }
}

async function saveSize(form) {
    const formData = new FormData(form);
    const data = {
        name: formData.get('tamanho-nome')
    };
    
    if (!data.name) return;
    
    try {
        await API_CONFIG.fetch(API_CONFIG.ENDPOINTS.SIZES, {
            method: 'POST',
            body: JSON.stringify(data)
        });
        
        form.reset();
        loadSizes();
    } catch (error) {
        console.error('Erro:', error);
        await showError('Erro ao criar tamanho: ' + (error.message || 'Erro desconhecido'));
    }
}

async function savePackaging(form) {
    const formData = new FormData(form);
    const data = {
        name: formData.get('embalagem-nome')
    };
    
    if (!data.name) return;
    
    try {
        await API_CONFIG.fetch(API_CONFIG.ENDPOINTS.PACKAGINGS, {
            method: 'POST',
            body: JSON.stringify(data)
        });
        
        form.reset();
        loadPackagings();
    } catch (error) {
        console.error('Erro:', error);
        await showError('Erro ao criar embalagem: ' + (error.message || 'Erro desconhecido'));
    }
}

async function deleteCategory(id, categoryName) {
    // Atualizar mensagem do modal
    document.getElementById('mensagem-exclusao-categoria').textContent = 
        `Tem certeza que deseja excluir a categoria "${categoryName}"? Esta ação não pode ser desfeita.`;
    
    // Abrir modal de confirmação
    const modalBackdrop = document.querySelector('.modal-backdrop[data-modal="confirmar-exclusao-categoria"]');
    modalBackdrop.classList.add('open');
    
    // Configurar evento do botão Excluir
    const confirmarBtn = document.getElementById('confirmar-exclusao-categoria-btn');
    const novoConfirmarBtn = confirmarBtn.cloneNode(true);
    confirmarBtn.parentNode.replaceChild(novoConfirmarBtn, confirmarBtn);
    
    novoConfirmarBtn.addEventListener('click', async () => {
        try {
            await API_CONFIG.fetch(`${API_CONFIG.ENDPOINTS.CATEGORIES}/${id}`, {
                method: 'DELETE'
            });
            
            modalBackdrop.classList.remove('open');
            await showSuccess('Categoria excluída com sucesso!');
            loadCategories();
        } catch (error) {
            console.error('Erro:', error);
            modalBackdrop.classList.remove('open');
            await showError('Erro ao excluir categoria: ' + (error.message || 'Erro desconhecido'));
        }
    });
}

async function deleteSize(id) {
    const confirmed = await showConfirm('Deseja realmente excluir este tamanho?', 'Confirmar Exclusão');
    if (!confirmed) return;
    
    try {
        await API_CONFIG.fetch(`${API_CONFIG.ENDPOINTS.SIZES}/${id}`, {
            method: 'DELETE'
        });
        
        loadSizes();
    } catch (error) {
        console.error('Erro:', error);
        await showError('Erro ao excluir tamanho: ' + (error.message || 'Erro desconhecido'));
    }
}

async function deletePackaging(id) {
    const confirmed = await showConfirm('Deseja realmente excluir esta embalagem?', 'Confirmar Exclusão');
    if (!confirmed) return;
    
    try {
        await API_CONFIG.fetch(`${API_CONFIG.ENDPOINTS.PACKAGINGS}/${id}`, {
            method: 'DELETE'
        });
        
        loadPackagings();
    } catch (error) {
        console.error('Erro:', error);
        await showError('Erro ao excluir embalagem: ' + (error.message || 'Erro desconhecido'));
    }
}

async function editCategory(id) {
    try {
        // Buscar dados da categoria
        const response = await API_CONFIG.fetch(`${API_CONFIG.ENDPOINTS.CATEGORIES}/${id}`);
        const category = response.data;
        
        // Preencher formulário
        const form = document.getElementById('form-categoria');
        const modal = document.querySelector('.modal-backdrop[data-modal="categoria"]');
        const modalTitle = modal.querySelector('.modal-title');
        const modalSubtitle = modal.querySelector('.modal-subtitle');
        const submitButton = form.querySelector('button[type="submit"]');
        
        form.dataset.categoryId = category.id;
        form.querySelector('input[name="nome"]').value = category.name || '';
        form.querySelector('textarea[name="descricao"]').value = category.description || '';
        
        // Aguardar um pouco para garantir que os selects foram preenchidos
        setTimeout(() => {
            form.querySelector('select[name="tamanho"]').value = category.sizeId || '';
            form.querySelector('select[name="embalagem"]').value = category.packagingId || '';
        }, 100);
        
        // Alterar título do modal e botão
        modalTitle.textContent = 'Editar Categoria';
        modalSubtitle.textContent = 'Atualize as informações da categoria.';
        submitButton.textContent = 'Atualizar';
        
        // Abrir modal
        modal.classList.add('open');
    } catch (error) {
        console.error('Erro ao carregar categoria:', error);
        await showError('Erro ao carregar dados da categoria');
    }
}

async function updateCategory(form, categoryId) {
    const formData = new FormData(form);
    const nome = formData.get('nome');
    const tamanho = formData.get('tamanho');
    const embalagem = formData.get('embalagem');
    
    // Validação: campos obrigatórios
    if (!nome || nome.trim() === '') {
        await showWarning('Por favor, preencha o nome da categoria', 'Campo Obrigatório');
        return;
    }
    
    if (!tamanho || tamanho === '') {
        await showWarning('Por favor, selecione um tamanho', 'Campo Obrigatório');
        return;
    }
    
    if (!embalagem || embalagem === '') {
        await showWarning('Por favor, selecione uma embalagem', 'Campo Obrigatório');
        return;
    }
    
    const data = {
        name: nome.trim(),
        description: formData.get('descricao'),
        sizeId: tamanho,
        packagingId: embalagem
    };
    
    try {
        await API_CONFIG.fetch(`${API_CONFIG.ENDPOINTS.CATEGORIES}/${categoryId}`, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
        
        await showSuccess('Categoria atualizada com sucesso!');
        form.reset();
        delete form.dataset.categoryId;
        
        // Restaurar título do modal e botão
        const modal = document.querySelector('.modal-backdrop[data-modal="categoria"]');
        const submitButton = form.querySelector('button[type="submit"]');
        modal.querySelector('.modal-title').textContent = 'Nova Categoria';
        modal.querySelector('.modal-subtitle').textContent = 'Preencha os dados para criar uma nova categoria.';
        submitButton.textContent = 'Criar';
        
        modal.classList.remove('open');
        loadCategories();
    } catch (error) {
        console.error('Erro:', error);
        await showError('Erro ao atualizar categoria: ' + (error.message || 'Erro desconhecido'));
    }
}

// Expor funções globalmente para uso com onclick
window.editCategory = editCategory;
window.deleteCategory = deleteCategory;
