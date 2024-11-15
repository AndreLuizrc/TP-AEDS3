package Arquivos;

import Objetos.ParIdId;
import Objetos.Rotulo;

import java.io.IOException;

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

    public Rotulo read(Rotulo rotulo) throws Exception {
        var rotuloPelaCategoria = new ParIdId(rotulo.getCategoriaId(), rotulo.getId());
        if (arvore.read(rotuloPelaCategoria) != null)
            return rotulo;

        var rotuloPeloStatus = new ParIdId(rotulo.getStatusId(), rotulo.getId());
        while(arvore.read(rotuloPeloStatus) != null)
            return rotulo;

        var emptyEntity = new Rotulo();
        return emptyEntity;
    }
}
