// Script para gerenciar a página de login
document.addEventListener('DOMContentLoaded', function() {
    
    // Verificar se já está logado
    if (localStorage.getItem('token')) {
        window.location.href = 'products.html';
        return;
    }
    
    // Form de login
    const formLogin = document.getElementById('form-login');
    if (formLogin) {
        formLogin.addEventListener('submit', async function(e) {
            e.preventDefault();
            await handleLogin(this);
        });
    }
});

async function handleLogin(form) {
    const formData = new FormData(form);
    const email = formData.get('email') || form.querySelector('input[type="email"]').value;
    const password = formData.get('password') || form.querySelector('input[type="password"]').value;
    
    const data = {
        email: email,
        password: password
    };
    
    try {
        console.log('Enviando login para:', `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.AUTH.LOGIN}`);
        console.log('Dados:', data);
        
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.AUTH.LOGIN}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });
        
        const result = await response.json();
        console.log('Resposta:', result);
        console.log('Status:', response.status);
        
        if (response.ok && result.data) {
            // Salvar token e usuário
            localStorage.setItem('token', result.data.token);
            localStorage.setItem('user', JSON.stringify(result.data.user));
            
            // Redirecionar para produtos
            window.location.href = 'products.html';
        } else {
            console.error('Erro no login:', result);
            alert(result.error?.message || result.message || 'Erro ao fazer login');
        }
    } catch (error) {
        console.error('Erro:', error);
        alert('Erro ao conectar ao servidor');
    }
}
