package Arquivos;

import Objetos.ParIdId;
import Objetos.Rotulo;

import java.io.IOException;
import java.util.ArrayList;

public class ArquivoRotuloStatus extends Arquivo<Rotulo> {
    ArvoreBMais<ParIdId> arvore;

    public ArquivoRotuloStatus() throws Exception {
        super("rotulo", Rotulo.class.getConstructor());

        arvore = new ArvoreBMais<>(
                ParIdId.class.getConstructor(), 5,
                "dados/arvore.db"
        );

    }

    @Override
    public int create(Rotulo rotulo) throws Exception {
        int id = super.create(rotulo);

        try {
            arvore.create(new ParIdId(rotulo.getCategoriaId(), id));
            arvore.create(new ParIdId(rotulo.getStatusId(), id));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return id;
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
