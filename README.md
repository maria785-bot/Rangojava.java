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
---

## 💻 Tecnologias Utilizadas

| Tecnologia | Versão | Finalidade |
|------------|--------|------------|
| **Java** | 17 | Linguagem de programação |
| **Maven** | 3.8.1 | Gerenciador de dependências |
| **Gson** | 2.10.1 | Serialização JSON |

---

## 👨‍💻 Autores
Nome	         Função
Maria Eduarda	  Desenvolvedor

---
## 📅 Data de Entrega
05/07/2026
---
---
## 🏆 Dificuldades Enfrentadas e Soluções
1. Serialização de LocalDateTime com Gson
   Problema: O Gson não conseguiu serializar LocalDateTime no Java 17.
   Solução: Criei um TypeAdapter personalizado (LocalDateTimeAdapter.java) que converte LocalDateTime para String ISO

2. Validação de CPF
   Problema: Validar corretamente os dígitos verificadores do CPF.
   Solução: Implementamos o algoritmo de validação de CPF com cálculo dos dígitos verificadores.

---

## 📌 Observação sobre execução

Este projeto não utiliza JavaFX, pois a interface é totalmente via terminal.

---

### Pré-requisitos
- Java 17 ou superior
- Maven 3.8.1 ou superior
---
## 🚀 Como Executar

### Opção 1: Executar pelo Maven
```bash
mvn clean package
java -jar target/rangojava.jar

---

