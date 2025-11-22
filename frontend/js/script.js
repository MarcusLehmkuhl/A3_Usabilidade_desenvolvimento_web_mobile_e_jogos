const API_URL = "http://localhost:3000/produtos";
const tabela = document.getElementById("tabelaProdutos");
const form = document.getElementById("produtoForm");

const inputId = document.getElementById("id");
const inputNome = document.getElementById("nome");
const inputPreco = document.getElementById("preco");
const inputQuantidade = document.getElementById("quantidade");

// Carregar lista inicial
async function carregarProdutos() {
  const res = await fetch(API_URL);
  const produtos = await res.json();

  tabela.innerHTML = "";
  produtos.forEach(produto => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${produto.id}</td>
      <td>${produto.nome}</td>
      <td>R$ ${produto.preco.toFixed(2)}</td>
      <td>${produto.quantidade}</td>
      <td>
        <button onclick="editar(${produto.id}, '${produto.nome}', ${produto.preco}, ${produto.quantidade})">Editar</button>
        <button onclick="deletar(${produto.id})">Excluir</button>
      </td>
    `;
    tabela.appendChild(tr);
  });
}

// Salvar (Create ou Update)
form.addEventListener("submit", async (e) => {
  e.preventDefault();

  const produto = {
    nome: inputNome.value,
    preco: parseFloat(inputPreco.value),
    quantidade: parseInt(inputQuantidade.value)
  };

  if (inputId.value) {
    // UPDATE
    await fetch(`${API_URL}/${inputId.value}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(produto)
    });
  } else {
    // CREATE
    await fetch(API_URL, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(produto)
    });
  }

  form.reset();
  carregarProdutos();
});

// Editar
function editar(id, nome, preco, quantidade) {
  inputId.value = id;
  inputNome.value = nome;
  inputPreco.value = preco;
  inputQuantidade.value = quantidade;
}

// Deletar
async function deletar(id) {
  await fetch(`${API_URL}/${id}`, { method: "DELETE" });
  carregarProdutos();
}

// Inicializar
carregarProdutos();
