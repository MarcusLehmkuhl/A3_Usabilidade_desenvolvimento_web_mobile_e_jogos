// Backend em Node.js + Express
const express = require("express");
const fs = require("fs");
const cors = require("cors");

const app = express();
const PORT = 3000;

// Middleware
app.use(cors());
app.use(express.json());

// Simulação de banco (JSON file)
const dbFile = __dirname + "/produtos.json";

// Função auxiliar para ler e salvar produtos
function lerProdutos() {
  return JSON.parse(fs.readFileSync(dbFile, "utf-8"));
}
function salvarProdutos(produtos) {
  fs.writeFileSync(dbFile, JSON.stringify(produtos, null, 2));
}

// CREATE
app.post("/produtos", (req, res) => {
  let produtos = lerProdutos();
  const novo = { id: Date.now(), ...req.body };
  produtos.push(novo);
  salvarProdutos(produtos);
  res.status(201).json(novo);
});

// READ
app.get("/produtos", (req, res) => {
  res.json(lerProdutos());
});

// UPDATE
app.put("/produtos/:id", (req, res) => {
  let produtos = lerProdutos();
  const id = parseInt(req.params.id);
  const index = produtos.findIndex(p => p.id === id);

  if (index === -1) return res.status(404).json({ msg: "Produto não encontrado" });

  produtos[index] = { id, ...req.body };
  salvarProdutos(produtos);
  res.json(produtos[index]);
});

// DELETE
app.delete("/produtos/:id", (req, res) => {
  let produtos = lerProdutos();
  const id = parseInt(req.params.id);
  produtos = produtos.filter(p => p.id !== id);
  salvarProdutos(produtos);
  res.json({ msg: "Produto removido" });
});

// Inicia servidor
app.listen(PORT, () => {
  console.log(`🚀 Servidor rodando em http://localhost:${PORT}`);
});
