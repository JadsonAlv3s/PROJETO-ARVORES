// Projeto 1 - Gerenciador de Categorias de Produtos (versão console)
// Estrutura de dados em árvore n-ária para organização hierárquica de categorias.
// Mantida em paridade de funcionalidades com a versão gráfica (src/gerenciadorcategorias).

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
     * Remove uma subcategoria pelo nome (recursivamente).
     * @return true se removeu, false se não encontrou
     */
    public boolean removerSubcategoria(String nome) {
        for (int i = 0; i < subcategorias.size(); i++) {
            Categoria sub = subcategorias.get(i);
            if (sub.getNome().equalsIgnoreCase(nome)) {
                subcategorias.remove(i);
                return true;
            }
            if (sub.removerSubcategoria(nome)) {
                return true;
            }
        }
        return false;
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
     * Conta o total de nós nesta subárvore (incluindo este nó).
     */
    public int contarNos() {
        int count = 1;
        for (Categoria sub : subcategorias) {
            count += sub.contarNos();
        }
        return count;
    }

    /**
     * Retorna a profundidade máxima desta subárvore.
     */
    public int profundidade() {
        int maxProf = 0;
        for (Categoria sub : subcategorias) {
            int prof = sub.profundidade();
            if (prof > maxProf) {
                maxProf = prof;
            }
        }
        return maxProf + 1;
    }

    /**
     * Retorna a representação em JSON desta categoria e suas subcategorias.
     */
    public String toJSON(int indentacao) {
        StringBuilder sb = new StringBuilder();
        String espaco = "  ".repeat(indentacao);
        String espacoInterno = "  ".repeat(indentacao + 1);

        sb.append("{\n");
        sb.append(espacoInterno).append("\"nome\": \"").append(escapeJSON(nome)).append("\"");

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

    private String escapeJSON(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
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

    private boolean nomeInvalido(String nome) {
        return nome == null || nome.trim().isEmpty();
    }

    /**
     * Insere uma nova categoria como filha da raiz (categoria principal).
     * @return true se inseriu, false se o nome é inválido ou já existe
     */
    public boolean inserirCategoriaPrincipal(String nome) {
        if (nomeInvalido(nome) || raiz.buscarPorNome(nome.trim()) != null) {
            return false;
        }
        raiz.inserirSubcategoria(new Categoria(nome.trim()));
        return true;
    }

    /**
     * Insere uma subcategoria dentro de uma categoria existente.
     * @return true se inseriu, false se o nome já existe ou o pai não foi encontrado
     */
    public boolean inserirSubcategoria(String nomeCategoriaPai, String nomeSubcategoria) {
        if (nomeInvalido(nomeSubcategoria) || raiz.buscarPorNome(nomeSubcategoria.trim()) != null) {
            return false;
        }
        Categoria pai = raiz.buscarPorNome(nomeCategoriaPai);
        if (pai != null) {
            pai.inserirSubcategoria(new Categoria(nomeSubcategoria.trim()));
            return true;
        }
        return false;
    }

    /**
     * Remove uma categoria pelo nome (e todas as suas subcategorias).
     * A raiz não pode ser removida.
     */
    public boolean removerCategoria(String nome) {
        if (raiz.getNome().equalsIgnoreCase(nome)) {
            return false;
        }
        return raiz.removerSubcategoria(nome);
    }

    /**
     * Renomeia uma categoria existente.
     */
    public boolean renomearCategoria(String nomeAntigo, String nomeNovo) {
        if (nomeInvalido(nomeNovo) || raiz.buscarPorNome(nomeNovo.trim()) != null) {
            return false;
        }
        Categoria cat = raiz.buscarPorNome(nomeAntigo);
        if (cat != null) {
            cat.setNome(nomeNovo.trim());
            return true;
        }
        return false;
    }

    /**
     * Busca uma categoria pelo nome em toda a árvore.
     */
    public Categoria buscar(String nome) {
        return raiz.buscarPorNome(nome);
    }

    /**
     * Retorna o total de categorias (nós) na árvore, excluindo a raiz.
     */
    public int totalCategorias() {
        return raiz.contarNos() - 1;
    }

    /**
     * Retorna a profundidade máxima da árvore, excluindo a raiz.
     */
    public int profundidade() {
        return raiz.profundidade() - 1;
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
                    renomearCategoria();
                    break;
                case 6:
                    removerCategoria();
                    break;
                case 7:
                    exibirEstatisticas();
                    break;
                case 8:
                    arvore.exportarJSON();
                    break;
                case 9:
                    System.out.println("Encerrando o programa...");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        } while (opcao != 9);

        scanner.close();
    }

    private static void exibirMenu() {
        System.out.println("\n===== GERENCIADOR DE CATEGORIAS =====");
        System.out.println("1. Inserir categoria principal");
        System.out.println("2. Inserir subcategoria");
        System.out.println("3. Visualizar árvore de categorias");
        System.out.println("4. Buscar categoria por nome");
        System.out.println("5. Renomear categoria");
        System.out.println("6. Remover categoria");
        System.out.println("7. Exibir estatísticas da árvore");
        System.out.println("8. Exportar estrutura como JSON");
        System.out.println("9. Sair");
        System.out.println("=====================================");
    }

    private static void inserirCategoriaPrincipal() {
        System.out.print("Digite o nome da nova categoria: ");
        String nome = scanner.nextLine().trim();
        if (nome.isEmpty()) {
            System.out.println("❌ Nome inválido!");
            return;
        }
        if (arvore.inserirCategoriaPrincipal(nome)) {
            System.out.println("✅ Categoria '" + nome + "' adicionada com sucesso!");
        } else {
            System.out.println("❌ Já existe uma categoria com o nome '" + nome + "'.");
        }
    }

    private static void inserirSubcategoria() {
        System.out.print("Digite o nome da categoria pai: ");
        String pai = scanner.nextLine().trim();
        System.out.print("Digite o nome da nova subcategoria: ");
        String sub = scanner.nextLine().trim();

        if (pai.isEmpty() || sub.isEmpty()) {
            System.out.println("❌ Nomes inválidos!");
            return;
        }
        if (arvore.buscar(pai) == null) {
            System.out.println("❌ Categoria pai '" + pai + "' não encontrada.");
            return;
        }
        if (arvore.inserirSubcategoria(pai, sub)) {
            System.out.println("✅ Subcategoria '" + sub + "' adicionada em '" + pai + "'!");
        } else {
            System.out.println("❌ Já existe uma categoria com o nome '" + sub + "'.");
        }
    }

    private static void buscarCategoria() {
        System.out.print("Digite o nome da categoria para buscar: ");
        String nome = scanner.nextLine().trim();

        Categoria resultado = arvore.buscar(nome);
        if (resultado != null) {
            System.out.println("✅ Categoria encontrada: " + resultado.getNome());
            int qtdSub = resultado.getSubcategorias().size();
            System.out.println("   Possui " + qtdSub + " subcategoria(s) direta(s)");
            System.out.println("   Total de sub-nós: " + (resultado.contarNos() - 1));
        } else {
            System.out.println("❌ Categoria '" + nome + "' não encontrada.");
        }
    }

    private static void renomearCategoria() {
        System.out.print("Digite o nome da categoria a renomear: ");
        String antigo = scanner.nextLine().trim();
        System.out.print("Digite o novo nome: ");
        String novo = scanner.nextLine().trim();

        if (antigo.isEmpty() || novo.isEmpty()) {
            System.out.println("❌ Nomes inválidos!");
            return;
        }
        if (arvore.buscar(antigo) == null) {
            System.out.println("❌ Categoria '" + antigo + "' não encontrada.");
            return;
        }
        if (arvore.renomearCategoria(antigo, novo)) {
            System.out.println("✅ Categoria renomeada: '" + antigo + "' -> '" + novo + "'");
        } else {
            System.out.println("❌ Já existe uma categoria com o nome '" + novo + "'.");
        }
    }

    private static void removerCategoria() {
        System.out.print("Digite o nome da categoria a remover: ");
        String nome = scanner.nextLine().trim();

        if (nome.isEmpty()) {
            System.out.println("❌ Nome inválido!");
            return;
        }
        System.out.print("Isso removerá '" + nome + "' e todas as suas subcategorias. Confirmar? (s/n): ");
        String confirma = scanner.nextLine().trim();
        if (!confirma.equalsIgnoreCase("s")) {
            System.out.println("Operação cancelada.");
            return;
        }
        if (arvore.removerCategoria(nome)) {
            System.out.println("✅ Categoria '" + nome + "' removida com sucesso!");
        } else {
            System.out.println("❌ Categoria '" + nome + "' não encontrada (ou é a raiz, que não pode ser removida).");
        }
    }

    private static void exibirEstatisticas() {
        System.out.println("\n========== ESTATÍSTICAS ==========");
        System.out.println("Total de categorias: " + arvore.totalCategorias());
        System.out.println("Profundidade máxima: " + arvore.profundidade());
        System.out.println("==================================\n");
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
