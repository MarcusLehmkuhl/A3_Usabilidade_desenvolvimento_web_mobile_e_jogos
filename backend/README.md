# Sistema de Gestão de Estoque - Backend

> API REST completa para gestão de estoque com autenticação JWT, alertas automáticos e sistema de auditoria.

[![Java](https://img.shields.io/badge/Java-11-orange)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.8-red)](https://maven.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

## Características

- ✅ **42 Endpoints REST** - API completa para gestão de estoque
- ✅ **Autenticação JWT** - Segurança com tokens e BCrypt
- ✅ **Sistema de Auditoria** - Rastreamento completo de operações
- ✅ **Alertas Automáticos** - Notificações de estoque baixo
- ✅ **Relatórios** - Dashboard e relatórios consolidados
- ✅ **Arquitetura em Camadas** - MVC com separation of concerns

---

## Quick Start

### 1. Clone e Configure

```bash
# Clonar repositório
git clone https://github.com/SEU-USUARIO/sistema-estoque-backend.git
cd sistema-estoque-backend

# Copiar configuração
cp src/main/resources/database.properties.example src/main/resources/database.properties
```

### 2. Configurar Banco de Dados

```bash
# Criar banco
mysql -u root -p < database.sql

# Editar credenciais em database.properties
notepad src\main\resources\database.properties
```

### 3. Compilar e Rodar

```bash
# Compilar
mvn clean package

# Deploy no Tomcat
copy target\estoque-api.war %CATALINA_HOME%\webapps\

# Acessar
# http://localhost:8080/estoque-api/api/
```

---

## API Endpoints

### Autenticação
```http
POST   /api/auth/login       # Login
POST   /api/auth/register    # Registro
GET    /api/auth/me          # Usuário atual
```

### Produtos
```http
GET    /api/products         # Listar todos
GET    /api/products/{id}    # Buscar por ID
POST   /api/products         # Criar
PUT    /api/products/{id}    # Atualizar
DELETE /api/products/{id}    # Deletar
```

### Relatórios
```http
GET    /api/relatorios/dashboard          # Dashboard consolidado
GET    /api/relatorios/low-stock          # Produtos críticos
GET    /api/relatorios/movements-summary  # Resumo movimentações
GET    /api/relatorios/stock-value        # Valor do estoque
```

### Alertas
```http
GET    /api/alertas                    # Todos os alertas
GET    /api/alertas?unread=true        # Apenas não lidos
PUT    /api/alertas/{id}               # Marcar como lido
DELETE /api/alertas/{id}               # Deletar alerta
```

### Auditoria
```http
GET    /api/audit-logs                      # Histórico completo
GET    /api/audit-logs?entityType=PRODUCT   # Filtrar por tipo
GET    /api/audit-logs?userId=1             # Filtrar por usuário
```

---

## Estrutura do Projeto

```
backend/
├── src/main/java/com/estoque/
│   ├── models/          # 7 Models (User, Product, Category, Alert, AuditLog, etc)
│   ├── dao/             # 8 DAOs (Acesso a dados)
│   ├── servlets/        # 9 Servlets (42 endpoints REST)
│   ├── services/        # 2 Services (Auditoria, Alertas)
│   ├── filters/         # 1 Filter (CORS)
│   └── utils/           # 3 Utils (Database, JWT, JSON)
├── src/main/resources/
│   └── database.properties.example
├── src/main/webapp/WEB-INF/
│   └── web.xml
├── database.sql         # Schema do banco (8 tabelas)
└── pom.xml             # Dependências Maven
```

---

## Segurança

⚠️ **IMPORTANTE - Antes de fazer deploy:**

```bash
# 1. Copiar arquivo de configuração
cp src/main/resources/database.properties.example src/main/resources/database.properties

# 2. Editar com credenciais reais (já está no .gitignore)
notepad src\main\resources\database.properties

# 3. NUNCA commitar:
#    ❌ database.properties com senhas reais
#    ❌ Chaves JWT de produção
#    ❌ Credenciais de banco de dados
```

---

## Tecnologias

| Categoria | Tecnologia | Versão |
|-----------|------------|--------|
| **Linguagem** | Java | 11 |
| **Build** | Maven | 3.8 |
| **Servidor** | Apache Tomcat | 9.0 |
| **Banco de Dados** | MySQL | 8.0 |
| **Autenticação** | JWT (jjwt) | 0.11.5 |
| **Criptografia** | BCrypt (jbcrypt) | 0.4 |
| **JSON** | Gson | 2.10.1 |
| **JDBC** | MySQL Connector | 8.0.33 |

---

## Contribuindo

Este é um projeto acadêmico (A3 - Usabilidade desenvolvimento web mobile e jogos).

**Equipe:**
- **Backend:** Flavio da Silva Vargas, Marcus Filipi Lehmkuhl Ventura
- **Frontend:** Felipe Goularte, João Vitor Cardoso de Jesus, Nycolle Viera
- **Gerente de Projeto:** Carolina Pinto

---

## Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---


<div align="center">

**Desenvolvido com ❤️ para A3 - UDW 2025**

[⬆ Voltar ao topo](backend)

</div>


