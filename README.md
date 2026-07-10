# 🌳 Gerenciador de Categorias

> **Estrutura de Dados — Árvore N-ária**  
> Aplicação Java Swing para gerenciamento hierárquico de categorias de produtos

![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=java)
![Swing](https://img.shields.io/badge/UI-Java%20Swing-blue)
![License](https://img.shields.io/badge/license-MIT-green)
![Status](https://img.shields.io/badge/status-concluído-brightgreen)

---

## 📋 Índice

- [Sobre](#-sobre)
- [Funcionalidades](#-funcionalidades)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Estrutura de Dados](#-estrutura-de-dados)
- [Pré-requisitos](#-pré-requisitos)
- [Como Executar](#-como-executar)
- [Exemplo de Uso](#-exemplo-de-uso)
- [Tecnologias](#-tecnologias)
- [Autores](#-autores)

---

## 📖 Sobre

Sistema para gerenciamento hierárquico de categorias de produtos de uma loja virtual. Utiliza uma **árvore n-ária** implementada em Java com interface gráfica moderna em **Java Swing**.

O projeto foi desenvolvido para a disciplina de **Estrutura de Dados** e demonstra na prática os conceitos de:

- Árvores n-árias (cada nó com N filhos)
- Recursão em operações de inserção, busca e remoção
- Percorrimento em pré-ordem
- Encapsulamento e orientação a objetos

---

## 🚀 Funcionalidades

| Funcionalidade | Descrição |
|----------------|-----------|
| ➕ **Inserir Categoria Principal** | Adiciona uma nova categoria no nível superior |
| 📥 **Inserir Subcategoria** | Adiciona uma subcategoria dentro de uma categoria existente |
| ✏️ **Renomear Categoria** | Altera o nome de uma categoria |
| 🗑️ **Remover Categoria** | Exclui uma categoria e todas as suas subcategorias |
| 🔍 **Buscar por Nome** | Localiza uma categoria em toda a árvore (`Ctrl+F`) |
| 📋 **Exportar JSON** | Exporta a estrutura completa para arquivo JSON (com escape de caracteres) |
| 📊 **Detalhes** | Exibe informações detalhadas da categoria selecionada |
| ⌨️ **Atalhos de Teclado** | `Delete` remove, `F2` renomeia, `Insert` adiciona subcategoria |
| 🌲 **Expandir/Recolher** | Botões para expandir ou recolher toda a árvore de uma vez |

---

## 🧱 Estrutura do Projeto

```
📁 Est/
├── src/
│   └── gerenciadorcategorias/
│       ├── Categoria.java          # Nó da árvore (nome + subcategorias)
│       ├── ArvoreCategorias.java   # Gerencia a árvore (inserção, busca, remoção)
│       └── TelaPrincipal.java      # Interface gráfica Java Swing
├── docs/
│   └── apresentacao.html           # Slides da apresentação
├── nbproject/                       # Configuração do NetBeans
├── build/                           # Classes compiladas
├── gerenciadorDeCategorias.java    # Versão console (CLI)
├── vercel.json                      # Deploy da apresentação (Vercel)
├── LICENSE                          # Licença MIT
├── README.md                        # Documentação principal
└── .gitignore                       # Arquivos ignorados pelo Git
```

---

## 🧠 Estrutura de Dados

### Árvore N-ária

Diferente de uma árvore binária (que tem no máximo 2 filhos por nó), a **árvore n-ária** permite que cada nó tenha **qualquer número de filhos**, sendo ideal para representar hierarquias como categorias de produtos.

```
         🏪 Loja
       ┌───┼───┐
       │   │   │
   📱Eletrônicos  👕Roupas  🏠Casa
       │       │
   ┌───┼───┐   ┌───┼───┐
   │   │   │   │   │   │
Smartph. Not. Tab. Masc. Fem. Inf.
```

### Operações Implementadas

| Operação | Descrição | Complexidade |
|----------|-----------|--------------|
| **Inserir** | Adiciona um nó filho a um pai específico | O(n) |
| **Buscar** | Localiza um nó pelo nome (recursivo) | O(n) |
| **Remover** | Exclui um nó e toda sua subárvore | O(n) |
| **Renomear** | Altera o nome de um nó | O(n) |
| **Contar** | Total de nós na árvore | O(n) |
| **Profundidade** | Maior nível hierárquico | O(n) |
| **Exportar JSON** | Gera representação JSON da estrutura | O(n) |

---

## ✅ Pré-requisitos

- **Java JDK 17** ou superior
- **NetBeans IDE** (recomendado) ou terminal
- Git (para clonar o repositório)

---

## 🖥️ Como Executar

### Opção 1: NetBeans (Recomendado)

```bash
# 1. Clone o repositório
git clone https://github.com/JadsonAlv3s/PROJETO-ARVORES.git

# 2. Abra o NetBeans
# 3. File → Open Project → Selecione a pasta raiz do projeto
# 4. Clique com botão direito no projeto → Run (ou F6)
```

### Opção 2: Terminal — Interface Gráfica (Swing)

```bash
# Clone o repositório
git clone https://github.com/JadsonAlv3s/PROJETO-ARVORES.git
cd PROJETO-ARVORES

# Compilar
javac -encoding UTF-8 -d build/classes src/gerenciadorcategorias/*.java

# Executar (Interface Gráfica)
java -cp build/classes gerenciadorcategorias.TelaPrincipal
```

### Opção 3: Terminal — Versão Console (CLI)

```bash
# Compilar e executar a versão de console
javac -encoding UTF-8 gerenciadorDeCategorias.java
java GerenciadorDeCategorias
```

---

## 📸 Exemplo de Uso

Ao iniciar, o sistema carrega automaticamente categorias de exemplo:

```
🏪 Loja
  ├── Eletrônicos
  │   ├── Smartphones
  │   │   ├── Android
  │   │   └── iOS
  │   ├── Notebooks
  │   └── Tablets
  ├── Roupas
  │   ├── Masculino
  │   │   ├── Camisas
  │   │   └── Calças
  │   ├── Feminino
  │   └── Infantil
  ├── Casa e Decoração
  │   ├── Móveis
  │   │   ├── Sofás
  │   │   ├── Mesas
  │   │   └── Estantes
  │   └── Iluminação
  └── Alimentos
      ├── Bebidas
      │   ├── Refrigerantes
      │   └── Sucos
      └── Congelados
```

---

## 💻 Tecnologias

| Tecnologia | Versão | Finalidade |
|------------|--------|------------|
| Java | 17+ | Linguagem principal |
| Java Swing | — | Interface gráfica |
| JTree | — | Componente de árvore visual |
| NetBeans | 13+ | IDE de desenvolvimento |
| Git | — | Controle de versão |

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 👨‍💻 Autores

Desenvolvido por **Jadson Alves** — 2026  
Disciplina de **Estrutura de Dados**

---

<p align="center">
  <strong>🌳 Gerenciador de Categorias</strong><br>
  <em>Estrutura de Dados — Árvore N-ária</em>
</p>
