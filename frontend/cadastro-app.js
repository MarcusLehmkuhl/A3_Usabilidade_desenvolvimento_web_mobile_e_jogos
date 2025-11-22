// Script para gerenciar a página de cadastro
document.addEventListener('DOMContentLoaded', function() {
    
    // Form de cadastro
    const formCadastro = document.getElementById('form-cadastro');
    if (formCadastro) {
        formCadastro.addEventListener('submit', async function(e) {
            e.preventDefault();
            await handleRegister(this);
        });
    }
});

// Função para registrar usuário
async function handleRegister(form) {
    const formData = new FormData(form);
    const email = formData.get('email') || form.querySelector('input[type="email"]').value;
    const password = formData.get('password') || form.querySelector('input[type="password"]').value;
    
    if (!email || !password) {
        alert('Por favor, preencha todos os campos');
        return;
    }
    
    try {
        await API_CONFIG.fetch(API_CONFIG.ENDPOINTS.AUTH.REGISTER, {
            method: 'POST',
            body: JSON.stringify({
                email: email,
                password: password,
                name: email.split('@')[0] // Usar parte do email como nome
            }),
            auth: false // Não precisa de autenticação para registro
        });
        
        alert('Conta criada com sucesso! Faça login para continuar.');
        window.location.href = 'login.html';
        
    } catch (error) {
        console.error('Erro ao criar conta:', error);
        // Mostrar mensagem amigável
        const message = error.message || 'Erro desconhecido';
        alert(message);
    }
}
