package gerenciadorcategorias;

import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia a árvore de categorias como um todo.
 * Fornece operações de inserção, busca, remoção, renomeio e exportação.
 */
public class ArvoreCategorias {
    private Categoria raiz;

    public ArvoreCategorias() {
        this.raiz = new Categoria("Loja");
    }

    public Categoria getRaiz() {
        return raiz;
    }

    /**
     * Insere uma nova categoria como filha direta da raiz.
     */
    public boolean inserirCategoriaPrincipal(String nome) {
        if (raiz.buscarPorNome(nome) != null) {
            return false; // já existe
        }
        Categoria nova = new Categoria(nome);
        raiz.inserirSubcategoria(nova);
        return true;
    }

    /**
     * Insere uma subcategoria dentro de uma categoria existente.
     */
    public boolean inserirSubcategoria(String nomeCategoriaPai, String nomeSubcategoria) {
        if (raiz.buscarPorNome(nomeSubcategoria) != null) {
            return false; // já existe
        }
        Categoria pai = raiz.buscarPorNome(nomeCategoriaPai);
        if (pai != null) {
            Categoria nova = new Categoria(nomeSubcategoria);
            pai.inserirSubcategoria(nova);
            return true;
        }
        return false;
    }

    /**
     * Remove uma categoria pelo nome (e todas as suas subcategorias).
     */
    public boolean removerCategoria(String nome) {
        if (raiz.getNome().equalsIgnoreCase(nome)) {
            return false; // não pode remover a raiz
        }
        return raiz.removerSubcategoria(nome);
    }

    /**
     * Renomeia uma categoria existente.
     */
    public boolean renomearCategoria(String nomeAntigo, String nomeNovo) {
        if (raiz.buscarPorNome(nomeNovo) != null) {
            return false; // nome novo já existe
        }
        Categoria cat = raiz.buscarPorNome(nomeAntigo);
        if (cat != null) {
            cat.setNome(nomeNovo);
            return true;
        }
        return false;
    }

    /**
     * Busca uma categoria pelo nome.
     */
    public Categoria buscar(String nome) {
        return raiz.buscarPorNome(nome);
    }

    /**
     * Retorna o total de categorias (nós) na árvore.
     */
    public int totalCategorias() {
        return raiz.contarNos() - 1; // exclui a raiz "Loja"
    }

    /**
     * Retorna a profundidade máxima da árvore.
     */
    public int profundidade() {
        return raiz.profundidade() - 1; // exclui a raiz
    }

    /**
     * Exporta a estrutura completa para JSON.
     */
    public String exportarJSON() {
        return raiz.toJSON(0);
    }

    /**
     * Retorna uma lista com todas as categorias (percurso pré-ordem).
     */
    public List<String> listarTodasCategorias() {
        List<String> lista = new ArrayList<>();
        listarRecursivo(raiz, lista);
        return lista;
    }

    private void listarRecursivo(Categoria cat, List<String> lista) {
        if (!cat.getNome().equals("Loja")) {
            lista.add(cat.getNome());
        }
        for (Categoria sub : cat.getSubcategorias()) {
            listarRecursivo(sub, lista);
        }
    }

    /**
     * Retorna uma lista formatada para exibição hierárquica.
     */
    public List<String> exibirHierarquico() {
        List<String> linhas = new ArrayList<>();
        exibirRecursivo(raiz, 0, linhas);
        return linhas;
    }

    private void exibirRecursivo(Categoria categoria, int nivel, List<String> linhas) {
        StringBuilder sb = new StringBuilder();
        if (nivel > 0) {
            for (int i = 1; i < nivel; i++) {
                sb.append("    ");
            }
            sb.append("├── ");
        }
        sb.append(categoria.getNome());
        linhas.add(sb.toString());

        for (Categoria sub : categoria.getSubcategorias()) {
            exibirRecursivo(sub, nivel + 1, linhas);
        }
    }

    /**
     * Carrega categorias de exemplo para demonstração.
     */
    public void carregarExemplos() {
        inserirCategoriaPrincipal("Eletrônicos");
        inserirSubcategoria("Eletrônicos", "Smartphones");
        inserirSubcategoria("Eletrônicos", "Notebooks");
        inserirSubcategoria("Eletrônicos", "Tablets");
        inserirSubcategoria("Smartphones", "Android");
        inserirSubcategoria("Smartphones", "iOS");

        inserirCategoriaPrincipal("Roupas");
        inserirSubcategoria("Roupas", "Masculino");
        inserirSubcategoria("Roupas", "Feminino");
        inserirSubcategoria("Roupas", "Infantil");
        inserirSubcategoria("Masculino", "Camisas");
        inserirSubcategoria("Masculino", "Calças");

        inserirCategoriaPrincipal("Casa e Decoração");
        inserirSubcategoria("Casa e Decoração", "Móveis");
        inserirSubcategoria("Casa e Decoração", "Iluminação");
        inserirSubcategoria("Móveis", "Sofás");
        inserirSubcategoria("Móveis", "Mesas");
        inserirSubcategoria("Móveis", "Estantes");

        inserirCategoriaPrincipal("Alimentos");
        inserirSubcategoria("Alimentos", "Bebidas");
        inserirSubcategoria("Alimentos", "Congelados");
        inserirSubcategoria("Bebidas", "Refrigerantes");
        inserirSubcategoria("Bebidas", "Sucos");
    }
}
