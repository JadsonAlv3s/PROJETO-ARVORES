package gerenciadorcategorias;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa um nó da árvore de categorias.
 * Cada categoria possui um nome e uma lista de subcategorias (filhos).
 */
public class Categoria {
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

    /**
     * Retorna uma visão somente-leitura das subcategorias.
     * Modificações na árvore devem passar pelos métodos de inserção/remoção.
     */
    public List<Categoria> getSubcategorias() {
        return Collections.unmodifiableList(subcategorias);
    }

    /**
     * Insere uma subcategoria nesta categoria.
     */
    public void inserirSubcategoria(Categoria subcategoria) {
        this.subcategorias.add(subcategoria);
    }

    /**
     * Remove todas as subcategorias deste nó.
     */
    public void removerTodasSubcategorias() {
        this.subcategorias.clear();
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
     * Busca recursivamente uma categoria pelo nome.
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
     * Gera JSON formatado desta categoria e suas subcategorias.
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
