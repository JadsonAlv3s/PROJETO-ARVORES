// Projeto 1 - Gerenciador de Categorias de Produtos
// Estrutura de dados em árvore para organização hierárquica de categorias

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Classe que representa um nó da árvore: uma categoria com suas subcategorias.
 */
class Categoria {
    private String nome;
    private List<Categoria> subcategorias;

    public Categoria(String nome) {
        this.nome = nome;
        this.subcategorias = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Categoria> getSubcategorias() {
        return subcategorias;
    }

    /**
     * Insere uma subcategoria na lista de subcategorias desta categoria.
     */
    public void inserirSubcategoria(Categoria subcategoria) {
        this.subcategorias.add(subcategoria);
    }

    /**
     * Busca recursivamente uma categoria pelo nome dentro desta subárvore.
     * @param nome Nome da categoria a ser buscada
     * @return A categoria encontrada ou null se não existir
     */
    public Categoria buscarPorNome(String nome) {
        if (this.nome.equalsIgnoreCase(nome)) {
            return this;
        }
        for (Categoria sub : subcategorias) {
            Categoria resultado = sub.buscarPorNome(nome);
            if (resultado != null) {
                return resultado;
            }
        }
        return null;
    }

    /**
     * Retorna a representação em JSON desta categoria e suas subcategorias.
     */
    public String toJSON(int indentacao) {
        StringBuilder sb = new StringBuilder();
        String espaco = "  ".repeat(indentacao);
        String espacoInterno = "  ".repeat(indentacao + 1);

        sb.append("{\n");
        sb.append(espacoInterno).append("\"nome\": \"").append(nome).append("\"");
        
        if (!subcategorias.isEmpty()) {
            sb.append(",\n");
            sb.append(espacoInterno).append("\"subcategorias\": [\n");
            for (int i = 0; i < subcategorias.size(); i++) {
                sb.append(espacoInterno).append("  ");
                sb.append(subcategorias.get(i).toJSON(indentacao + 2));
                if (i < subcategorias.size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append(espacoInterno).append("]");
        }
        
        sb.append("\n").append(espaco).append("}");
        return sb.toString();
    }
}

/**
 * Classe que gerencia a árvore de categorias como um todo.
 */
class ArvoreCategorias {
    private Categoria raiz;

    public ArvoreCategorias() {
        // Cria uma raiz genérica que representa a loja
        this.raiz = new Categoria("Loja");
    }

    public Categoria getRaiz() {
        return raiz;
    }

    /**
     * Insere uma nova categoria como filha da raiz (categoria principal).
     */
    public void inserirCategoriaPrincipal(String nome) {
        Categoria nova = new Categoria(nome);
        raiz.inserirSubcategoria(nova);
        System.out.println("Categoria '" + nome + "' adicionada com sucesso!");
    }

    /**
     * Insere uma subcategoria dentro de uma categoria existente.
     * @param nomeCategoriaPai Nome da categoria pai
     * @param nomeSubcategoria Nome da nova subcategoria
     * @return true se inseriu com sucesso, false se a categoria pai não foi encontrada
     */
    public boolean inserirSubcategoria(String nomeCategoriaPai, String nomeSubcategoria) {
        Categoria pai = raiz.buscarPorNome(nomeCategoriaPai);
        if (pai != null) {
            Categoria nova = new Categoria(nomeSubcategoria);
            pai.inserirSubcategoria(nova);
            System.out.println("Subcategoria '" + nomeSubcategoria + "' adicionada em '" + nomeCategoriaPai + "'!");
            return true;
        } else {
            System.out.println("Erro: Categoria '" + nomeCategoriaPai + "' não encontrada.");
            return false;
        }
    }

    /**
     * Busca uma categoria pelo nome em toda a árvore.
     */
    public Categoria buscar(String nome) {
        return raiz.buscarPorNome(nome);
    }

    /**
     * Exibe a árvore hierarquicamente no console.
     */
    public void exibirArvore() {
        System.out.println("\n========== ESTRUTURA DE CATEGORIAS ==========");
        exibirRecursivo(raiz, 0);
        System.out.println("=============================================\n");
    }

    private void exibirRecursivo(Categoria categoria, int nivel) {
        String prefixo = "  ".repeat(nivel);
        if (nivel == 0) {
            System.out.println(prefixo + "🏪 " + categoria.getNome());
        } else {
            System.out.println(prefixo + "📂 " + categoria.getNome());
        }
        for (Categoria sub : categoria.getSubcategorias()) {
            exibirRecursivo(sub, nivel + 1);
        }
    }

    /**
     * Exporta a estrutura completa para JSON.
     */
    public void exportarJSON() {
        System.out.println("\n========== EXPORTAÇÃO JSON ==========");
        System.out.println(raiz.toJSON(0));
        System.out.println("=====================================\n");
    }
}

/**
 * Classe principal com o menu interativo para o usuário.
 */
class GerenciadorDeCategorias {
    private static ArvoreCategorias arvore = new ArvoreCategorias();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int opcao;
        
        // Já adiciona algumas categorias de exemplo
        adicionarCategoriasExemplo();

        do {
            exibirMenu();
            System.out.print("Escolha uma opção: ");
            
            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                opcao = 0;
            }

            switch (opcao) {
                case 1:
                    inserirCategoriaPrincipal();
                    break;
                case 2:
                    inserirSubcategoria();
                    break;
                case 3:
                    arvore.exibirArvore();
                    break;
                case 4:
                    buscarCategoria();
                    break;
                case 5:
                    arvore.exportarJSON();
                    break;
                case 6:
                    System.out.println("Encerrando o programa...");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        } while (opcao != 6);

        scanner.close();
    }

    private static void exibirMenu() {
        System.out.println("\n===== GERENCIADOR DE CATEGORIAS =====");
        System.out.println("1. Inserir categoria principal");
        System.out.println("2. Inserir subcategoria");
        System.out.println("3. Visualizar árvore de categorias");
        System.out.println("4. Buscar categoria por nome");
        System.out.println("5. Exportar estrutura como JSON");
        System.out.println("6. Sair");
        System.out.println("=====================================");
    }

    private static void inserirCategoriaPrincipal() {
        System.out.print("Digite o nome da nova categoria: ");
        String nome = scanner.nextLine().trim();
        if (!nome.isEmpty()) {
            arvore.inserirCategoriaPrincipal(nome);
        } else {
            System.out.println("Nome inválido!");
        }
    }

    private static void inserirSubcategoria() {
        System.out.print("Digite o nome da categoria pai: ");
        String pai = scanner.nextLine().trim();
        System.out.print("Digite o nome da nova subcategoria: ");
        String sub = scanner.nextLine().trim();
        
        if (!pai.isEmpty() && !sub.isEmpty()) {
            arvore.inserirSubcategoria(pai, sub);
        } else {
            System.out.println("Nomes inválidos!");
        }
    }

    private static void buscarCategoria() {
        System.out.print("Digite o nome da categoria para buscar: ");
        String nome = scanner.nextLine().trim();
        
        Categoria resultado = arvore.buscar(nome);
        if (resultado != null) {
            System.out.println("✅ Categoria encontrada: " + resultado.getNome());
            int qtdSub = resultado.getSubcategorias().size();
            System.out.println("   Possui " + qtdSub + " subcategoria(s)");
        } else {
            System.out.println("❌ Categoria '" + nome + "' não encontrada.");
        }
    }

    /**
     * Adiciona categorias de exemplo para demonstração inicial.
     */
    private static void adicionarCategoriasExemplo() {
        arvore.inserirCategoriaPrincipal("Eletrônicos");
        arvore.inserirSubcategoria("Eletrônicos", "Smartphones");
        arvore.inserirSubcategoria("Eletrônicos", "Notebooks");
        arvore.inserirSubcategoria("Eletrônicos", "Tablets");

        arvore.inserirCategoriaPrincipal("Roupas");
        arvore.inserirSubcategoria("Roupas", "Masculino");
        arvore.inserirSubcategoria("Roupas", "Feminino");
        arvore.inserirSubcategoria("Roupas", "Infantil");

        arvore.inserirCategoriaPrincipal("Casa e Decoração");
        arvore.inserirSubcategoria("Casa e Decoração", "Móveis");
        arvore.inserirSubcategoria("Casa e Decoração", "Iluminação");
        arvore.inserirSubcategoria("Móveis", "Sofás");
        arvore.inserirSubcategoria("Móveis", "Mesas");
    }
}
