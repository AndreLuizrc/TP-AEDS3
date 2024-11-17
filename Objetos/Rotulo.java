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

    public static final int TAMANHO = 12;

    public Rotulo() {
        this.idRelacionamento = -1;
        this.categoriaId = -1;
        this.statusId = -1;
    }

    public Rotulo(int categoriaId, int statusId) {
        this.idRelacionamento = -1; // será definido pelo arquivo no create
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
        if (b.length < TAMANHO) {
            throw new Exception("Byte array com tamanho inválido. Esperado: " + TAMANHO + ", Recebido: " + b.length);
        }

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
                "\nId status.........: " + this.getStatusId() +
                "\n";
    }

    public String getStatusName() {
        return switch (statusId) {
            case 0 -> "PENDENTE";
            case 1 -> "EM ANDAMENTO";
            case 2 -> "CONCLUIDO";
            default -> "Status não definido";
        };
    }
}
