package Objetos;

import Interfaces.Registro;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Arrays;

public class Rotulo implements Registro {
    private int idRelacionamento;
    private int categoriaId;
    private int statusId;

    public Rotulo() {
        this.idRelacionamento = -1;
        this.categoriaId = -1;
        this.statusId = -1;

    }

    public Rotulo(int categoriaId, int statusId) {
        this.idRelacionamento = 0;
        this.categoriaId = categoriaId;
        this.statusId = statusId;
    }

    public Rotulo(int idRelacionamento, int categoriaId, int statusId) {
        this.idRelacionamento = idRelacionamento;
        this.categoriaId = categoriaId;
        this.statusId = statusId;
    }

    public int getStatusId() {
        return statusId;
    }

    public String getStatusName(){
        if (statusId == 0)
            return "PENDENTE";

        if (statusId == 1)
            return "EM ANDAMENTO";

        if (statusId == 2)
            return "CONCLUIDO";

        return "Status nao definido";
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    @Override
    public void setId(int id) {
        this.idRelacionamento = id;
    }

    @Override
    public int getId() {
        return this.idRelacionamento;
    }

    @Override
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.idRelacionamento);
        dos.writeInt(this.categoriaId);
        dos.writeInt(this.statusId);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] b) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        this.idRelacionamento = dis.readInt();
        this.categoriaId = dis.readInt();
        this.statusId = dis.readInt();
    }

    @Override
    public String toString() {
        return  "\nID relacionamento.: " + this.idRelacionamento +
                "\nID categoria.........: " + this.categoriaId +
                "\nID status.........: " + getStatusName();
    }
}
