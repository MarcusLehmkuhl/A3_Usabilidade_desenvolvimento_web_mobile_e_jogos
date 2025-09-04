# 📦 Sistema de Controle de Estoque Web

![Badge de Status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow)
![Badge de Licença](https://img.shields.io/badge/license-MIT-blue)
![JavaScript](https://img.shields.io/badge/JavaScript-ES6-yellow)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)

Projeto acadêmico da Unidade Curricular de *[Nome da Matéria]*, desenvolvido para a Unisul. O sistema consiste em uma aplicação web completa para gerenciamento de estoque, permitindo o controle de produtos, categorias e movimentações (entradas e saídas).

---

## 📝 Descrição do Projeto

Toda empresa de comércio precisa de um controle de estoque eficiente para planejar suas compras e atender bem a demanda de seus clientes. Este sistema web foi projetado para resolver esse problema, oferecendo uma plataforma intuitiva para cadastrar produtos, classificá-los em categorias e registrar todas as movimentações.

A aplicação foi desenvolvida seguindo os princípios de *usabilidade, acessibilidade e inclusão*, com foco em uma interface limpa e responsiva.

---

## ✨ Funcionalidades Principais

O sistema conta com as seguintes funcionalidades:

* *Gerenciamento de Produtos:*
    * [✔️] Inclusão, alteração, consulta e exclusão (CRUD) de produtos.
    * [✔️] Campos detalhados: nome, preço, unidade, quantidade em estoque (mínima e máxima), categoria.
    * [✔️] Funcionalidade para reajustar preços de todos os produtos com base em um percentual.

* *Gerenciamento de Categorias:*
    * [✔️] CRUD completo de categorias para organizar os produtos.
    * [✔️] Campos: nome, tamanho e tipo de embalagem.

* *Movimentação de Estoque:*
    * [✔️] Registro de entradas e saídas de produtos.
    * [✔️] Atualização automática do saldo em estoque.
    * [❗] *Alertas Visuais (JavaScript):* O sistema avisa o usuário em tempo real se uma saída deixará o estoque abaixo do mínimo ou se uma entrada ultrapassará o máximo permitido.

* *Relatórios Gerenciais:*
    * [✔️] *Lista de Preços:* Relação completa de produtos com preços e categorias.
    * [✔️] *Balanço Físico/Financeiro:* Visão geral da quantidade e do valor total do estoque.
    * [✔️] *Estoque Mínimo:* Lista produtos que precisam de reposição.
    * [✔️] *Produtos por Categoria:* Quantitativo de itens em cada categoria.
    * [✔️] *Ranking de Movimentação:* Identifica os produtos com maior número de entradas e saídas.

---

## 🛠️ Tecnologias Utilizadas

Este projeto foi construído com uma arquitetura robusta, utilizando as seguintes tecnologias:

* *Backend:* Java 8+, Servlets & JSP
* *Frontend:* HTML5, CSS3, JavaScript (ES6)
* *Banco de Dados:* MySQL 8.0
* *Servidor de Aplicação:* Apache Tomcat
* *Gerenciamento de Dependências:* Maven
* *Controle de Versão:* Git & GitHub

---

## 🚀 Como Executar o Projeto

Siga os passos abaixo para configurar e executar o projeto em seu ambiente local.

1.  *Clone o repositório:*
    bash
    git clone [https://github.com/seu-usuario/nome-do-repositorio.git](https://github.com/seu-usuario/nome-do-repositorio.git)
    cd nome-do-repositorio
    

2.  *Configure o Banco de Dados:*
    * Execute o script database/script.sql no seu servidor MySQL para criar as tabelas necessárias.
    * Configure as credenciais de acesso ao banco no arquivo src/main/java/seu/pacote/dao/ConnectionFactory.java.

3.  *Execute a Aplicação:*
    * Importe o projeto em sua IDE (Eclipse, IntelliJ).
    * Compile e implante a aplicação em um servidor Apache Tomcat.
    * Acesse http://localhost:8080/nome-do-seu-projeto em seu navegador.

---

## 👥 Equipe Responsável

Este projeto foi desenvolvido de forma colaborativa pelos seguintes alunos:

| Nome Completo        | Função Principal no Projeto          |
| -------------------- | ------------------------------------ |
| *[Nome do Aluno 1]* | Gerente de Projeto / Arquiteto Backend |
| *[Nome do Aluno 2]* | Desenvolvedor Full-Stack (CRUD Produto) |
| *[Nome do Aluno 3]* | Desenvolvedor Full-Stack (CRUD Categoria) |
| *[Nome do Aluno 4]* | Desenvolvedor Full-Stack (Movimentação) |
| *[Nome do Aluno 5]* | Desenvolvedor Full-Stack (Relatórios) / UX/UI |

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.