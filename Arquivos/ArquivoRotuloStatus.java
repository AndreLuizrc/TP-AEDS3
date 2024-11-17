package Arquivos;

import Objetos.ParIdId;
import Objetos.Rotulo;

import java.io.IOException;
import java.util.ArrayList;

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

    public int criarRelacionamentoParaTarefa(int tarefaId, int categoriaId, int statusId) throws Exception {
        // Cria o relacionamento
        int relId = criarRelacionamento(categoriaId, statusId);

        // Aqui você pode adicionar lógica adicional para vincular à tarefa
        // Por exemplo, criar uma entrada em outra árvore que relacione tarefa->relacionamento

        return relId;
    }


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

        if (resultados != null) {
            for (ParIdId resultado : resultados) {
                System.out.println(resultado);
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

}
