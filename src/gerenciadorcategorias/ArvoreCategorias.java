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
     * Verifica se o nome informado corresponde à raiz da árvore.
     */
    public boolean isRaiz(String nome) {
        return nome != null && raiz.getNome().equalsIgnoreCase(nome.trim());
    }

    private boolean nomeInvalido(String nome) {
        return nome == null || nome.trim().isEmpty();
    }

    /**
     * Insere uma nova categoria como filha direta da raiz.
     */
    public boolean inserirCategoriaPrincipal(String nome) {
        if (nomeInvalido(nome) || raiz.buscarPorNome(nome.trim()) != null) {
            return false; // nome inválido ou já existe
        }
        raiz.inserirSubcategoria(new Categoria(nome.trim()));
        return true;
    }

    /**
     * Insere uma subcategoria dentro de uma categoria existente.
     */
    public boolean inserirSubcategoria(String nomeCategoriaPai, String nomeSubcategoria) {
        if (nomeInvalido(nomeSubcategoria) || raiz.buscarPorNome(nomeSubcategoria.trim()) != null) {
            return false; // nome inválido ou já existe
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
        if (nomeInvalido(nomeNovo) || raiz.buscarPorNome(nomeNovo.trim()) != null) {
            return false; // nome inválido ou já existe
        }
        Categoria cat = raiz.buscarPorNome(nomeAntigo);
        if (cat != null) {
            cat.setNome(nomeNovo.trim());
            return true;
        }
        return false;
    }

    /**
     * Remove todas as categorias, mantendo apenas a raiz.
     */
    public void limpar() {
        raiz.removerTodasSubcategorias();
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
        if (cat != raiz) {
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
