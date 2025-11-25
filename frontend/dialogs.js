// Sistema de diálogos customizados para substituir alert() e confirm()

// Criar HTML dos modais customizados se não existir
function initDialogs() {
    if (!document.getElementById('custom-alert-modal')) {
        const dialogsHTML = `
            <!-- Modal de Alerta -->
            <div class="modal-backdrop" id="custom-alert-modal">
                <div class="modal" style="max-width: 450px; width: 100%;">
                    <div class="modal-header">
                        <div>
                            <h2 class="modal-title" id="alert-title">Aviso</h2>
                        </div>
                    </div>
                    <div class="modal-body">
                        <p id="alert-message" style="margin: 0; color: #4a5568; line-height: 1.6;"></p>
                    </div>
                    <div class="form-footer">
                        <button type="button" class="btn btn-primary" id="alert-ok-btn" style="width: 100%;">OK</button>
                    </div>
                </div>
            </div>

            <!-- Modal de Confirmação -->
            <div class="modal-backdrop" id="custom-confirm-modal">
                <div class="modal" style="max-width: 450px; width: 100%;">
                    <div class="modal-header">
                        <div>
                            <h2 class="modal-title" id="confirm-title">Confirmar</h2>
                        </div>
                    </div>
                    <div class="modal-body">
                        <p id="confirm-message" style="margin: 0; color: #4a5568; line-height: 1.6;"></p>
                    </div>
                    <div class="form-footer">
                        <button type="button" class="btn btn-outline" id="confirm-cancel-btn">Cancelar</button>
                        <button type="button" class="btn btn-primary" id="confirm-ok-btn">Confirmar</button>
                    </div>
                </div>
            </div>
        `;
        
        document.body.insertAdjacentHTML('beforeend', dialogsHTML);
    }
}

// Função customizada para substituir alert()
function showAlert(message, title = 'Aviso') {
    return new Promise((resolve) => {
        initDialogs();
        
        const modal = document.getElementById('custom-alert-modal');
        const titleEl = document.getElementById('alert-title');
        const messageEl = document.getElementById('alert-message');
        const okBtn = document.getElementById('alert-ok-btn');
        
        titleEl.textContent = title;
        messageEl.textContent = message;
        
        // Remover listeners antigos
        const newOkBtn = okBtn.cloneNode(true);
        okBtn.parentNode.replaceChild(newOkBtn, okBtn);
        
        newOkBtn.addEventListener('click', () => {
            modal.classList.remove('open');
            resolve();
        });
        
        // Fechar com ESC
        const handleEsc = (e) => {
            if (e.key === 'Escape') {
                modal.classList.remove('open');
                document.removeEventListener('keydown', handleEsc);
                resolve();
            }
        };
        document.addEventListener('keydown', handleEsc);
        
        modal.classList.add('open');
    });
}

// Função customizada para substituir confirm()
function showConfirm(message, title = 'Confirmar') {
    return new Promise((resolve) => {
        initDialogs();
        
        const modal = document.getElementById('custom-confirm-modal');
        const titleEl = document.getElementById('confirm-title');
        const messageEl = document.getElementById('confirm-message');
        const okBtn = document.getElementById('confirm-ok-btn');
        const cancelBtn = document.getElementById('confirm-cancel-btn');
        
        titleEl.textContent = title;
        messageEl.textContent = message;
        
        // Remover listeners antigos
        const newOkBtn = okBtn.cloneNode(true);
        const newCancelBtn = cancelBtn.cloneNode(true);
        okBtn.parentNode.replaceChild(newOkBtn, okBtn);
        cancelBtn.parentNode.replaceChild(newCancelBtn, cancelBtn);
        
        newOkBtn.addEventListener('click', () => {
            modal.classList.remove('open');
            resolve(true);
        });
        
        newCancelBtn.addEventListener('click', () => {
            modal.classList.remove('open');
            resolve(false);
        });
        
        // Fechar com ESC (cancelar)
        const handleEsc = (e) => {
            if (e.key === 'Escape') {
                modal.classList.remove('open');
                document.removeEventListener('keydown', handleEsc);
                resolve(false);
            }
        };
        document.addEventListener('keydown', handleEsc);
        
        modal.classList.add('open');
    });
}

// Função de alerta que fecha automaticamente
function showToast(message, title = 'Aviso', duration = 1800) {
    return new Promise((resolve) => {
        initDialogs();
        
        const modal = document.getElementById('custom-alert-modal');
        const titleEl = document.getElementById('alert-title');
        const messageEl = document.getElementById('alert-message');
        const okBtn = document.getElementById('alert-ok-btn');
        
        titleEl.textContent = title;
        messageEl.textContent = message;
        
        // Esconder o botão OK
        okBtn.style.display = 'none';
        
        modal.classList.add('open');
        
        // Fechar automaticamente após o tempo especificado
        setTimeout(() => {
            modal.classList.remove('open');
            okBtn.style.display = 'block'; // Restaurar botão para próximos usos
            resolve();
        }, duration);
    });
}

// Variantes com ícones e cores
function showSuccess(message, title = 'Sucesso') {
    return showToast(message, '✓ ' + title);
}

function showError(message, title = 'Erro') {
    return showToast(message, '✗ ' + title);
}

function showWarning(message, title = 'Atenção') {
    return showToast(message, '⚠ ' + title);
}

// Exportar para uso global
window.showAlert = showAlert;
window.showConfirm = showConfirm;
window.showSuccess = showSuccess;
window.showError = showError;
window.showWarning = showWarning;
window.showToast = showToast;



