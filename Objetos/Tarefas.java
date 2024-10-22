package Objetos;
//envio

import Interfaces.Registro;
import Utils.Status;

import java.io.*;
import java.time.LocalDate;
import Arquivos.ArquivoCategoria;

public class Tarefas implements Registro {
    private int id;
    private String nome;
    private LocalDate createdAt;
    private LocalDate doneAt;
    private Status status;
    private byte priority;
    private int idCategoria;

    public Tarefas() {
        this.id = 0;
        this.nome = "ABCDEFGHIJKLMNQRST";
        this.createdAt = LocalDate.now();
        this.doneAt = null;
        this.status = Status.PENDENTE;
        this.priority = 1;
        this.idCategoria = 0;
    }

    public Tarefas(int id, String nome, LocalDate createdAt) {
        this.id = id;
        this.nome = nome;
        this.createdAt = LocalDate.now();
        this.doneAt = null;
        this.status = Status.PENDENTE;
        this.priority = 1;
        this.idCategoria = 0;
    }

    public Tarefas(String nome) {
        this.id = 0;
        this.nome = nome;
        this.createdAt = LocalDate.now();
        this.doneAt = null;
        this.status = Status.PENDENTE;
        this.priority = 1;
        this.idCategoria = 0;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setDoneAt(LocalDate doneAt) {
        this.doneAt = doneAt;
    }

    public LocalDate getDoneAt() {
        return this.doneAt;
    }

    public byte getPriority() {
        return this.priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public String getPriorityType(byte priority) {
        switch (priority) {
            case 1:
                return "ALTA";

            case 2:
                return "MEDIA";

            case 3:
                return "BAIXA";

            default:
                return "";
        }
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return this.nome;
    }

    @Override
    public void setId(int i) {
        this.id = i;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public int getIdCategoria(){
        return this.idCategoria;
    }

    public void setIdCategoria(int idCategoria){
        this.idCategoria = idCategoria;
    }

    @Override
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeUTF(this.nome);
        dos.writeInt((int) this.createdAt.toEpochDay());

        if (this.doneAt != null) {
            dos.writeBoolean(true);
            dos.writeInt((int) this.doneAt.toEpochDay());
        } else {
            dos.writeBoolean(false);
        }

        dos.writeByte(this.status.getValue());
        dos.write(this.priority);
        dos.writeInt(idCategoria);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] b) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);

        this.id = dis.readInt();
        this.nome = dis.readUTF();
        this.createdAt = LocalDate.ofEpochDay(dis.readInt());

        boolean hasDoneAt = dis.readBoolean();
        if (hasDoneAt) {
            this.doneAt = LocalDate.ofEpochDay(dis.readInt());
        } else {
            this.doneAt = null;
        }

        this.status = Status.fromByte(dis.readByte());
        this.priority = (byte) dis.read();
        this.idCategoria = dis.readInt();
    }

    @Override
    public String toString(){
        try {
            return "\nID........: " + this.id +
                    "\nName......: " + unfiller(this.nome) +
                    "\nCreated At: " + this.createdAt.toString() +
                    "\nStatus....: " + this.status +
                    (this.status == Status.CONCLUIDO ? "\nDone at...: " + this.doneAt.toString() : "") +
                    "\nPriority..: " + getPriorityType(this.priority) +
                    "\nCategoria.: " + getNomeCategoria(this.idCategoria);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return " ";
    }

    public String getNomeCategoria(int id) throws Exception{
        ArquivoCategoria arqCategoria = new ArquivoCategoria();
        
        return unfiller(arqCategoria.read(id).getNome());
    }

    public String filler(String nome) {
        if (nome.length() > 20) {
            throw new IllegalArgumentException("O nome excede o tamanho máximo permitido.");
        }
        char[] filler = new char[20];
        for (int i = 0; i < nome.length(); i++) {
            filler[i] = nome.charAt(i);
        }
        for (int i = nome.length(); i < filler.length; i++) {
            filler[i] = '|';
        }
        String tmp = new String(filler);
        nome = tmp;
        return nome;
    }

    public String unfiller(String nome){
        char[] tmp = new char[20];
        int j = 0;
        for (int i = 0; i < 20; i++) {
            if (nome.charAt(i) != '|') {
                tmp[j] = nome.charAt(i);
                j++;
            }
        }
        String fixed = new String(tmp);
        return fixed;
    }
}
