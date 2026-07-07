# 🍽️ RangoJava - Sistema de Gerenciamento de Restaurante

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://adoptopenjdk.net/)
[![Maven](https://img.shields.io/badge/Maven-3.8.1-red.svg)](https://maven.apache.org/)

---

## 📋 Descrição do Projeto

O **RangoJava** é um sistema de gerenciamento de restaurante desenvolvido em **Java** com arquitetura **MVC + Repository**.

## 🚀 Funcionalidades

### Para Clientes
- 🔐 Login com e-mail e senha
- 📝 Cadastro com validação de CPF
- 🍽️ Fazer pedidos
- 📜 Ver histórico de pedidos
- 👤 Visualizar e atualizar dados pessoais

### Para Gerentes
- 📋 Gerenciar cardápio (CRUD)
- 🔄 Alternar disponibilidade de itens
- 📁 Gerenciar categorias
- 📦 Gerenciar pedidos
- 📥 Importar cardápio via JSON
- 👥 Gerenciar clientes

### Regras de Negócio
- ❌ Não é possível cadastrar CPF duplicado
- ❌ Senha deve ter no mínimo 8 caracteres
- ❌ Não é possível cancelar pedido entregue
- ❌ Não é possível remover categoria com itens
- ❌ Não é possível adicionar item com preço negativo

---

## 🏗️ Arquitetura do Sistema

### Estrutura de Pacotes

src/main/java/main/java/
├── controller/
│ └── LoginController.java
├── model/
│ ├── CategoriaItem.java
│ ├── Cliente.java
│ ├── Gerente.java
│ ├── ItemCardapio.java
│ ├── Pedido.java
│ ├── Restaurante.java
│ ├── StatusPedido.java
│ └── Usuario.java
├── repository/
│ ├── CategoriaItemRepository.java
│ ├── ClienteRepository.java
│ ├── ItemCardapioRepository.java
│ ├── PedidoRepository.java
│ ├── Repository.java
│ └── RestauranteRepository.java
├── service/
│ └── PedidoService.java
├── util/
│ ├── HashSenha.java
│ ├── LocalDateTimeAdapter.java
│ └── ValidadorDocumentos.java
└── view/
└── Main.java

