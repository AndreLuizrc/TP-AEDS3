package Arquivos;

import Objetos.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ArquivoRotuloStatus extends Arquivo<Rotulo> {
    ArvoreBMais<ParIdId> arvore;
    private int proximoId;

    public ArquivoRotuloStatus() throws Exception {
        super("rotulo", Rotulo.class.getConstructor());

        arvore = new ArvoreBMais<>(
                ParIdId.class.getConstructor(), 5,
                "dados/arvore.db"
        );
        this.proximoId = 1;
    }

    public int criarRelacionamento(int categoriaId, int statusId) throws Exception {
        // Cria novo objeto Rotulo
        Rotulo novoRotulo = new Rotulo(categoriaId, statusId);

        // Cria o registro no arquivo e obtém o ID
        int id = super.create(novoRotulo);

        // Cria as entradas na árvore B+
        arvore.create(new ParIdId(categoriaId, id));
        arvore.create(new ParIdId(statusId, id));

        return id;
    }

    public int criarRelacionamentoParaTarefa(int categoriaId, int statusId) throws Exception { return criarRelacionamento(categoriaId, statusId); }


    public ArrayList<Rotulo> buscarPorCategoria(int categoriaId) throws Exception {
        ArrayList<Rotulo> rotulos = new ArrayList<>();

        ParIdId chaveBusca = new ParIdId(categoriaId, -1);
        ArrayList<ParIdId> resultados = arvore.read(chaveBusca);

        if (resultados != null) {
            for (ParIdId resultado : resultados) {
                try {
                    Rotulo rotulo = super.read(resultado.getId2());
                    if (rotulo != null) {
                        rotulos.add(rotulo);
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao ler rótulo com ID " + resultado.getId2() + ": " + e.getMessage());
                    throw e;
                }
            }
        }

        return rotulos;
    }


    public ArrayList<Rotulo> buscarPorStatus(int statusId) throws Exception {
        ArrayList<Rotulo> rotulos = new ArrayList<>();

        ParIdId chaveBusca = new ParIdId(statusId, -1);
        ArrayList<ParIdId> resultados = arvore.read(chaveBusca);

        if (!resultados.isEmpty()) {
            for (ParIdId resultado : resultados) {
                try {
                    Rotulo rotulo = super.read(resultado.getId2());
                    rotulos.add(rotulo);
                } catch (Exception e) {
                    System.err.println("Erro ao ler rótulo com ID " + resultado.getId2() + ": " + e.getMessage());
                    throw e;
                }
            }
        }

        return rotulos;
    }

    public void imprimirRelacionamentos() throws Exception {
        ArrayList<Rotulo> todosRotulos = super.readAll();

        if (todosRotulos.isEmpty()) {
            System.out.println("Não há relacionamentos cadastrados.");
            return;
        }

        System.out.println("\n=== RELACIONAMENTOS TAREFA-CATEGORIA-STATUS ===");
        for (Rotulo rotulo : todosRotulos) {
            // Busca os detalhes da categoria
            ArquivoCategoria arquivoCategoria = new ArquivoCategoria();
            Categoria categoria = arquivoCategoria.read(rotulo.getCategoriaId());

            // Busca as tarefas associadas a esta categoria
            ArquivoTarefa arquivoTarefa = new ArquivoTarefa();
            ArrayList<ParIdId> tarefasCategoria = arquivoTarefa.getAllStacksFromCategorie(rotulo.getCategoriaId());

            System.out.println("\nRelacionamento ID: " + rotulo.getId());
            System.out.println("Categoria: " + (categoria != null ? categoria.getNome() : "Não encontrada"));
            System.out.println("Status: " + rotulo.getStatusName());

            System.out.println("Tarefas associadas:");
            if (tarefasCategoria.isEmpty()) {
                System.out.println("- Nenhuma tarefa associada");
            } else {
                for (ParIdId par : tarefasCategoria) {
                    Tarefas tarefa = arquivoTarefa.read(par.getId2());
                    if (tarefa != null) {
                        System.out.println("- " + tarefa.getNome() +
                                " (ID: " + tarefa.getId() +
                                ", Status: " + rotulo.getStatusName() + ")");
                    }
                }
            }
            System.out.println("----------------------------------------");
        }
    }

    public void imprimirTarefasPorStatus(ArrayList<Rotulo> rotulos) throws Exception {
        ArquivoTarefa arquivoTarefa = new ArquivoTarefa();
        // Usar Set para evitar duplicatas de tarefas
        Set<Integer> tarefasJaImpressas = new HashSet<>();

        System.out.println("\n=== TAREFAS COM STATUS: " +
                (rotulos.isEmpty() ? "NENHUM" : rotulos.get(0).getStatusName()) + " ===");

        for (Rotulo rotulo : rotulos) {
            ArrayList<ParIdId> tarefasCategoria = arquivoTarefa.getAllStacksFromCategorie(rotulo.getCategoriaId());
            for (ParIdId par : tarefasCategoria) {
                // Verifica se já imprimiu esta tarefa
                if (!tarefasJaImpressas.contains(par.getId2())) {
                    Tarefas tarefa = arquivoTarefa.read(par.getId2());
                    if (tarefa != null) {
                        System.out.println(tarefa);
                        tarefasJaImpressas.add(par.getId2());
                    }
                }
            }
        }

        if (tarefasJaImpressas.isEmpty()) {
            System.out.println("Nenhuma tarefa encontrada com este status!");
        }
    }
    public void imprimirTarefasPorCategoria(ArrayList<Rotulo> rotulos, int categoriaId) throws Exception {
        ArquivoTarefa arquivoTarefa = new ArquivoTarefa();
        ArquivoCategoria arquivoCategoria = new ArquivoCategoria();
        Set<Integer> tarefasJaImpressas = new HashSet<>();

        Categoria categoria = arquivoCategoria.read(categoriaId);
        System.out.println("\n=== TAREFAS DA CATEGORIA: " +
                (categoria != null ? categoria.unfiller(categoria.getNome()) : "Não encontrada") +
                " ===");

        if (rotulos.isEmpty()) {
            System.out.println("Não há tarefas nesta categoria.");
            return;
        }

        ArrayList<ParIdId> tarefasCategoria = arquivoTarefa.getAllStacksFromCategorie(categoriaId);
        for (ParIdId par : tarefasCategoria) {
            if (!tarefasJaImpressas.contains(par.getId2())) {
                Tarefas tarefa = arquivoTarefa.read(par.getId2());
                if (tarefa != null) {
                    System.out.println("\nTarefa: " + tarefa.unfiller(tarefa.getNome()));
                    System.out.println("ID: " + tarefa.getId());
                    String status = "Não definido";
                    for (Rotulo r : rotulos) {
                        if (r.getCategoriaId() == categoriaId) {
                            status = r.getStatusName();
                            break;
                        }
                    }
                    System.out.println("Status: " + status);
                    System.out.println("Descrição: " + tarefa.getDescricao());
                    tarefasJaImpressas.add(par.getId2());
                }
            }
        }
    }
}
