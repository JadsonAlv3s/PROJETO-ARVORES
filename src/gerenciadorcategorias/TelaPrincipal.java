package gerenciadorcategorias;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Interface gráfica moderna do Gerenciador de Categorias.
 * Design profissional com Java Swing.
 */
public class TelaPrincipal extends JFrame {

    private final ArvoreCategorias arvore;
    private JTree tree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private JLabel lblStatus;
    private JLabel lblTotalCategorias;
    private JLabel lblProfundidade;
    private JTextField txtBusca;
    private JPanel painelDetalhes;

    // Cores do tema - todas sólidas, sem alpha para evitar renderização borrada
    private static final Color COR_PRIMARIA = new Color(41, 128, 185);
    private static final Color COR_PRIMARIA_HOVER = new Color(33, 107, 155);
    private static final Color COR_FUNDO = new Color(245, 247, 250);
    private static final Color COR_CARD = new Color(255, 255, 255);
    private static final Color COR_TEXTO = new Color(52, 73, 94);
    private static final Color COR_TEXTO_CLARO = new Color(149, 165, 166);
    private static final Color COR_SUCESSO = new Color(46, 204, 113);
    private static final Color COR_ERRO = new Color(231, 76, 60);
    private static final Color COR_BORDA = new Color(220, 225, 232);
    private static final Color COR_BOTAO_HOVER = new Color(236, 245, 252);
    private static final Color COR_BOTAO_CLICK = new Color(214, 234, 248);

    public TelaPrincipal() {
        this.arvore = new ArvoreCategorias();
        initUI();
        carregarExemplos();
    }

    private void initUI() {
        setTitle("Gerenciador de Categorias - Loja Virtual");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COR_FUNDO);

        mainPanel.add(criarTopBar(), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBorder(null);
        splitPane.setDividerSize(4);
        splitPane.setResizeWeight(0.6);
        splitPane.setBackground(COR_FUNDO);

        splitPane.setLeftComponent(criarPainelArvore());
        splitPane.setRightComponent(criarPainelDireito());

        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(criarBarraStatus(), BorderLayout.SOUTH);

        add(mainPanel);
        atualizarStatus();
    }

    // ======================================================================
    // COMPONENTES DA INTERFACE
    // ======================================================================

    private JPanel criarTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(COR_PRIMARIA);
        topBar.setPreferredSize(new Dimension(0, 60));
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JLabel titulo = new JLabel("Gerenciador de Categorias");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        topBar.add(titulo, BorderLayout.WEST);

        JPanel buscaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        buscaPanel.setOpaque(false);

        txtBusca = new JTextField(18);
        txtBusca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBusca.setPreferredSize(new Dimension(200, 32));
        txtBusca.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 215, 230), 1),
                BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));
        txtBusca.setBackground(Color.WHITE);
        txtBusca.setForeground(COR_TEXTO);
        txtBusca.setOpaque(true);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setBackground(new Color(55, 145, 200));
        btnBuscar.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btnBuscar.setFocusPainted(false);
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBuscar.setOpaque(true);
        btnBuscar.setContentAreaFilled(true);
        btnBuscar.addActionListener(e -> buscarCategoria());
        btnBuscar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnBuscar.setBackground(new Color(45, 125, 180));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnBuscar.setBackground(new Color(55, 145, 200));
            }
        });

        txtBusca.addActionListener(e -> buscarCategoria());

        buscaPanel.add(txtBusca);
        buscaPanel.add(btnBuscar);

        topBar.add(buscaPanel, BorderLayout.EAST);
        return topBar;
    }

    private JPanel criarPainelArvore() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COR_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, COR_BORDA),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel header = new JLabel("Estrutura de Categorias");
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setForeground(COR_TEXTO);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(header, BorderLayout.NORTH);

        rootNode = new DefaultMutableTreeNode("Loja");
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tree.setBackground(COR_CARD);
        tree.setRowHeight(28);
        tree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        tree.setShowsRootHandles(true);
        tree.setRootVisible(true);

        // Renderer personalizado com ícones
        tree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value,
                    boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                
                String texto = value.toString();
                if (texto.equals("Loja")) {
                    setText("Loja");
                }
                
                if (sel) {
                    setBackground(new Color(41, 128, 185, 30));
                    setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185, 60), 1));
                } else {
                    setBorder(null);
                }
                return this;
            }
        });

        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node != null) {
                mostrarDetalhesCategoria(node);
            }
        });

        // Menu de contexto (clique direito)
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tree.getClosestRowForLocation(e.getX(), e.getY());
                    tree.setSelectionRow(row);
                    mostrarMenuContexto(e);
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tree.getClosestRowForLocation(e.getX(), e.getY());
                    tree.setSelectionRow(row);
                    mostrarMenuContexto(e);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tree);
        scroll.setBorder(null);
        scroll.setBackground(COR_CARD);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel criarPainelDireito() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COR_FUNDO);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // === PAINEL DE AÇÕES ===
        JPanel acoesPanel = new JPanel(new GridBagLayout());
        acoesPanel.setBackground(COR_CARD);
        acoesPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.gridx = 0;
        gbc.weightx = 1;

        JLabel lblAcoes = new JLabel("Ações Rápidas");
        lblAcoes.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblAcoes.setForeground(COR_TEXTO);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        acoesPanel.add(lblAcoes, gbc);
        gbc.insets = new Insets(4, 0, 4, 0);

        // Botão 1: Inserir Categoria Principal
        JButton btnInserirPrincipal = criarBotaoAcao("Inserir Categoria Principal",
                "Adicionar uma nova categoria no nível principal");
        btnInserirPrincipal.addActionListener(e -> inserirCategoriaPrincipal());
        gbc.gridy = 1;
        acoesPanel.add(btnInserirPrincipal, gbc);

        // Botão 2: Inserir Subcategoria
        JButton btnInserirSub = criarBotaoAcao("Inserir Subcategoria",
                "Adicionar uma subcategoria dentro de uma categoria existente");
        btnInserirSub.addActionListener(e -> inserirSubcategoria());
        gbc.gridy = 2;
        acoesPanel.add(btnInserirSub, gbc);

        // Botão 3: Renomear Categoria
        JButton btnRenomear = criarBotaoAcao("Renomear Categoria",
                "Alterar o nome de uma categoria existente");
        btnRenomear.addActionListener(e -> renomearCategoria());
        gbc.gridy = 3;
        acoesPanel.add(btnRenomear, gbc);

        // Botão 4: Remover Categoria
        JButton btnRemover = criarBotaoAcao("Remover Categoria",
                "Excluir uma categoria e todas as suas subcategorias");
        btnRemover.addActionListener(e -> removerCategoria());
        gbc.gridy = 4;
        acoesPanel.add(btnRemover, gbc);

        // Botão 5: Exportar JSON
        JButton btnExportar = criarBotaoAcao("Exportar JSON",
                "Exportar a estrutura completa para o formato JSON");
        btnExportar.addActionListener(e -> exportarJSON());
        gbc.gridy = 5;
        acoesPanel.add(btnExportar, gbc);

        // Botão 6: Recarregar Exemplos
        JButton btnRecarregar = criarBotaoAcao("Recarregar Exemplos",
                "Recarregar as categorias de exemplo (substitui as atuais)");
        btnRecarregar.addActionListener(e -> recarregarExemplos());
        gbc.gridy = 6;
        acoesPanel.add(btnRecarregar, gbc);

        // === PAINEL DE DETALHES ===
        painelDetalhes = new JPanel();
        painelDetalhes.setBackground(COR_CARD);
        painelDetalhes.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        painelDetalhes.setLayout(new BoxLayout(painelDetalhes, BoxLayout.Y_AXIS));

        JLabel lblDetalhes = new JLabel("Detalhes da Categoria");
        lblDetalhes.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDetalhes.setForeground(COR_TEXTO);
        lblDetalhes.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelDetalhes.add(lblDetalhes);
        painelDetalhes.add(Box.createVerticalStrut(10));

        JLabel lblSelecione = new JLabel("Selecione uma categoria na árvore ao lado");
        lblSelecione.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSelecione.setForeground(COR_TEXTO_CLARO);
        lblSelecione.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelDetalhes.add(lblSelecione);

        // Container para ações + detalhes
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(COR_FUNDO);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 15, 0);
        container.add(acoesPanel, c);

        c.weighty = 1;
        c.gridy = 1;
        c.insets = new Insets(0, 0, 0, 0);
        container.add(painelDetalhes, c);

        panel.add(container, BorderLayout.CENTER);
        return panel;
    }

    private JPanel criarBarraStatus() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(COR_CARD);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, COR_BORDA),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        lblStatus = new JLabel("Pronto");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(COR_TEXTO_CLARO);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        statsPanel.setOpaque(false);

        lblTotalCategorias = new JLabel("0 categorias");
        lblTotalCategorias.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTotalCategorias.setForeground(COR_TEXTO_CLARO);

        lblProfundidade = new JLabel("Profundidade: 0");
        lblProfundidade.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblProfundidade.setForeground(COR_TEXTO_CLARO);

        statsPanel.add(lblTotalCategorias);
        statsPanel.add(lblProfundidade);

        statusBar.add(lblStatus, BorderLayout.WEST);
        statusBar.add(statsPanel, BorderLayout.EAST);

        return statusBar;
    }

    // ======================================================================
    // MÉTODOS AUXILIARES
    // ======================================================================

    private JButton criarBotaoAcao(String texto, String tooltip) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(COR_TEXTO);
        btn.setBackground(COR_CARD);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        btn.setToolTipText(tooltip);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(COR_BOTAO_HOVER);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(COR_PRIMARIA, 1, true),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(COR_CARD);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(COR_BORDA, 1, true),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
            @Override
            public void mousePressed(MouseEvent e) {
                btn.setBackground(COR_BOTAO_CLICK);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                btn.setBackground(COR_BOTAO_HOVER);
            }
        });

        return btn;
    }

    private void mostrarMenuContexto(MouseEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node == null) return;

        String nome = node.getUserObject().toString();
        boolean isRaiz = nome.equals("Loja");

        JPopupMenu menu = new JPopupMenu();
        menu.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JMenuItem itemInserirSub = new JMenuItem("Inserir Subcategoria em \"" + nome + "\"");
        itemInserirSub.addActionListener(ev -> {
            String nomeSub = JOptionPane.showInputDialog(this,
                    "Digite o nome da nova subcategoria em \"" + nome + "\":",
                    "Inserir Subcategoria", JOptionPane.PLAIN_MESSAGE);
            if (nomeSub != null && !nomeSub.trim().isEmpty()) {
                processarInsercaoSubcategoria(nome, nomeSub.trim());
            }
        });
        menu.add(itemInserirSub);

        if (!isRaiz) {
            menu.addSeparator();

            JMenuItem itemRenomear = new JMenuItem("Renomear \"" + nome + "\"");
            itemRenomear.addActionListener(ev -> {
                String novoNome = JOptionPane.showInputDialog(this,
                        "Novo nome para \"" + nome + "\":",
                        "Renomear Categoria", JOptionPane.PLAIN_MESSAGE);
                if (novoNome != null && !novoNome.trim().isEmpty()) {
                    processarRenomeio(nome, novoNome.trim());
                }
            });
            menu.add(itemRenomear);

            JMenuItem itemRemover = new JMenuItem("Remover \"" + nome + "\"");
            itemRemover.addActionListener(ev -> processarRemocao(nome));
            menu.add(itemRemover);
        }

        menu.show(tree, e.getX(), e.getY());
    }

    private void mostrarDetalhesCategoria(DefaultMutableTreeNode node) {
        painelDetalhes.removeAll();

        JLabel lblDetalhes = new JLabel("Detalhes da Categoria");
        lblDetalhes.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDetalhes.setForeground(COR_TEXTO);
        lblDetalhes.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelDetalhes.add(lblDetalhes);
        painelDetalhes.add(Box.createVerticalStrut(15));

        String nome = node.getUserObject().toString();
        Categoria cat = arvore.buscar(nome);

        if (cat != null) {
            adicionarLinhaDetalhe("Nome", cat.getNome());
            adicionarLinhaDetalhe("Subcategorias", String.valueOf(cat.getSubcategorias().size()));
            adicionarLinhaDetalhe("Total de sub-nós", String.valueOf(cat.contarNos() - 1));
            adicionarLinhaDetalhe("Profundidade", String.valueOf(cat.profundidade()));

            if (!cat.getSubcategorias().isEmpty()) {
                painelDetalhes.add(Box.createVerticalStrut(10));
                JLabel lblFilhos = new JLabel("Subcategorias:");
                lblFilhos.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblFilhos.setForeground(COR_TEXTO);
                lblFilhos.setAlignmentX(Component.LEFT_ALIGNMENT);
                painelDetalhes.add(lblFilhos);
                painelDetalhes.add(Box.createVerticalStrut(5));

                for (Categoria sub : cat.getSubcategorias()) {
                    JLabel lblSub = new JLabel("  - " + sub.getNome());
                    lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    lblSub.setForeground(COR_TEXTO_CLARO);
                    lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
                    painelDetalhes.add(lblSub);
                }
            }
        } else {
            JLabel lblInfo = new JLabel("Categoria raiz - Loja");
            lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lblInfo.setForeground(COR_TEXTO_CLARO);
            lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
            painelDetalhes.add(lblInfo);
        }

        painelDetalhes.revalidate();
        painelDetalhes.repaint();
    }

    private void adicionarLinhaDetalhe(String label, String valor) {
        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        linha.setOpaque(false);
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblLabel = new JLabel(label + ":");
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLabel.setForeground(COR_TEXTO);
        linha.add(lblLabel);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblValor.setForeground(COR_PRIMARIA);
        linha.add(lblValor);

        painelDetalhes.add(linha);
    }

    private void atualizarStatus() {
        int total = arvore.totalCategorias();
        int prof = arvore.profundidade();
        lblTotalCategorias.setText(total + " categorias");
        lblProfundidade.setText("Profundidade: " + prof);
    }

    private void atualizarArvore() {
        rootNode.removeAllChildren();
        carregarNosRecursivo(arvore.getRaiz(), rootNode);
        treeModel.reload();
        expandirTodos(tree);
        atualizarStatus();
    }

    private void carregarNosRecursivo(Categoria cat, DefaultMutableTreeNode node) {
        for (Categoria sub : cat.getSubcategorias()) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(sub.getNome());
            node.add(child);
            carregarNosRecursivo(sub, child);
        }
    }

    private void expandirTodos(JTree tree) {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    private void carregarExemplos() {
        arvore.carregarExemplos();
        atualizarArvore();
        lblStatus.setText("Exemplos carregados com sucesso!");
        lblStatus.setForeground(COR_SUCESSO);
    }

    private void setStatusSucesso(String msg) {
        lblStatus.setText(msg);
        lblStatus.setForeground(COR_SUCESSO);
    }

    private void setStatusErro(String msg) {
        lblStatus.setText(msg);
        lblStatus.setForeground(COR_ERRO);
    }

    // ======================================================================
    // AÇÕES DOS BOTÕES
    // ======================================================================

    private void inserirCategoriaPrincipal() {
        String nome = JOptionPane.showInputDialog(this,
                "Digite o nome da nova categoria principal:",
                "Inserir Categoria Principal", JOptionPane.PLAIN_MESSAGE);

        if (nome == null) return; // usuário cancelou
        nome = nome.trim();
        if (nome.isEmpty()) {
            setStatusErro("Nome inválido!");
            return;
        }

        if (arvore.inserirCategoriaPrincipal(nome)) {
            atualizarArvore();
            setStatusSucesso("Categoria \"" + nome + "\" adicionada com sucesso!");
        } else {
            setStatusErro("Já existe uma categoria com o nome \"" + nome + "\"!");
            JOptionPane.showMessageDialog(this,
                    "Já existe uma categoria com o nome \"" + nome + "\".",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void inserirSubcategoria() {
        // Pega o nó selecionado na árvore
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        String pai;

        if (node != null) {
            pai = node.getUserObject().toString();
        } else {
            pai = JOptionPane.showInputDialog(this,
                    "Digite o nome da categoria pai:",
                    "Inserir Subcategoria", JOptionPane.PLAIN_MESSAGE);
            if (pai == null) return;
            pai = pai.trim();
            if (pai.isEmpty()) {
                setStatusErro("Nome da categoria pai inválido!");
                return;
            }
        }

        // Verifica se a categoria pai existe
        if (arvore.buscar(pai) == null) {
            setStatusErro("Categoria \"" + pai + "\" não encontrada!");
            JOptionPane.showMessageDialog(this,
                    "Categoria \"" + pai + "\" não encontrada.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nomeSub = JOptionPane.showInputDialog(this,
                "Digite o nome da nova subcategoria em \"" + pai + "\":",
                "Inserir Subcategoria", JOptionPane.PLAIN_MESSAGE);

        if (nomeSub == null) return;
        nomeSub = nomeSub.trim();
        if (nomeSub.isEmpty()) {
            setStatusErro("Nome inválido!");
            return;
        }

        processarInsercaoSubcategoria(pai, nomeSub);
    }

    private void processarInsercaoSubcategoria(String pai, String nomeSub) {
        if (arvore.inserirSubcategoria(pai, nomeSub)) {
            atualizarArvore();
            setStatusSucesso("Subcategoria \"" + nomeSub + "\" adicionada em \"" + pai + "\"!");
        } else {
            String erro;
            if (arvore.buscar(pai) == null) {
                erro = "Categoria pai \"" + pai + "\" não encontrada.";
            } else {
                erro = "Já existe uma categoria com o nome \"" + nomeSub + "\".";
            }
            setStatusErro(erro);
            JOptionPane.showMessageDialog(this, erro, "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void renomearCategoria() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma categoria na árvore primeiro.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nomeAntigo = node.getUserObject().toString();
        if (nomeAntigo.equals("Loja")) {
            JOptionPane.showMessageDialog(this,
                    "Não é possível renomear a raiz \"Loja\".",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nomeNovo = JOptionPane.showInputDialog(this,
                "Novo nome para \"" + nomeAntigo + "\":",
                "Renomear Categoria", JOptionPane.PLAIN_MESSAGE);

        if (nomeNovo == null) return;
        nomeNovo = nomeNovo.trim();
        if (nomeNovo.isEmpty()) {
            setStatusErro("Nome inválido!");
            return;
        }

        processarRenomeio(nomeAntigo, nomeNovo);
    }

    private void processarRenomeio(String nomeAntigo, String nomeNovo) {
        if (arvore.renomearCategoria(nomeAntigo, nomeNovo)) {
            atualizarArvore();
            setStatusSucesso("Categoria renomeada: \"" + nomeAntigo + "\" -> \"" + nomeNovo + "\"");
        } else {
            String erro;
            if (arvore.buscar(nomeAntigo) == null) {
                erro = "Categoria \"" + nomeAntigo + "\" não encontrada.";
            } else {
                erro = "Já existe uma categoria com o nome \"" + nomeNovo + "\".";
            }
            setStatusErro(erro);
            JOptionPane.showMessageDialog(this, erro, "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removerCategoria() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma categoria na árvore primeiro.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nome = node.getUserObject().toString();
        if (nome.equals("Loja")) {
            JOptionPane.showMessageDialog(this,
                    "Não é possível remover a raiz \"Loja\".",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        processarRemocao(nome);
    }

    private void processarRemocao(String nome) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja remover \"" + nome
                + "\" e todas as suas subcategorias?",
                "Confirmar Remoção",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (arvore.removerCategoria(nome)) {
                atualizarArvore();
                setStatusSucesso("Categoria \"" + nome + "\" removida com sucesso!");
            } else {
                setStatusErro("Erro ao remover \"" + nome + "\".");
            }
        }
    }

    private void buscarCategoria() {
        String nome = txtBusca.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Digite um nome para buscar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Categoria resultado = arvore.buscar(nome);
        if (resultado != null) {
            setStatusSucesso("Categoria \"" + nome + "\" encontrada! (" + resultado.getSubcategorias().size() + " subcategorias)");

            // Seleciona o nó na árvore
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
            DefaultMutableTreeNode no = encontrarNo(root, nome);
            if (no != null) {
                tree.setSelectionPath(new TreePath(no.getPath()));
                tree.scrollPathToVisible(new TreePath(no.getPath()));
            }

            JOptionPane.showMessageDialog(this,
                    "Categoria encontrada: \"" + resultado.getNome() + "\"\n"
                    + "Subcategorias: " + resultado.getSubcategorias().size(),
                    "Resultado da Busca",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            setStatusErro("Categoria \"" + nome + "\" não encontrada.");
            JOptionPane.showMessageDialog(this,
                    "Categoria \"" + nome + "\" não encontrada.",
                    "Resultado da Busca",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private DefaultMutableTreeNode encontrarNo(DefaultMutableTreeNode node, String nome) {
        if (node.getUserObject().toString().equalsIgnoreCase(nome)) {
            return node;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
            DefaultMutableTreeNode result = encontrarNo(child, nome);
            if (result != null) return result;
        }
        return null;
    }

    private void exportarJSON() {
        String json = arvore.exportarJSON();

        JTextArea textArea = new JTextArea(json);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setBackground(new Color(40, 44, 52));
        textArea.setForeground(new Color(171, 178, 191));
        textArea.setCaretColor(Color.WHITE);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(600, 400));

        Object[] options = {"Salvar como...", "Fechar"};
        int result = JOptionPane.showOptionDialog(this, scroll,
                "Exportação JSON",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null, options, options[1]);

        if (result == 0) {
            salvarJSON(json);
        }
    }

    private void salvarJSON(String json) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("categorias.json"));
        fileChooser.setDialogTitle("Salvar JSON");

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".json")) {
                file = new File(file.getAbsolutePath() + ".json");
            }
            try (PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8)) {
                writer.write(json);
                setStatusSucesso("JSON exportado para: " + file.getName());
                JOptionPane.showMessageDialog(this,
                        "Arquivo salvo em:\n" + file.getAbsolutePath(),
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                setStatusErro("Erro ao salvar arquivo!");
                JOptionPane.showMessageDialog(this,
                        "Erro ao salvar: " + e.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void recarregarExemplos() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Isso irá substituir todas as categorias atuais pelos exemplos padrão.\nContinuar?",
                "Recarregar Exemplos",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            arvore.getRaiz().getSubcategorias().clear();
            carregarExemplos();
        }
    }

    // ========== MAIN ==========

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            TelaPrincipal tela = new TelaPrincipal();
            tela.setVisible(true);
        });
    }
}
