package Objetos;

import Interfaces.Registro;
import java.io.*;

public class Categoria implements Registro {

    public int id;
    public String nome;

    public Categoria() {
        this(-1, "");
    }
    public Categoria(String n) {
        this(-1, n);
    }

    public Categoria(int i, String n) {
        this.id = i;
        this.nome = n;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return this.nome;
    }

    public String toString() {

        return "\nID..: " + this.id +
               "\nNome: " + unfiller(this.getNome());
    }

    public String unfiller(String nome) {
        if (nome == null) {
            return "";
        }

        char[] tmp = new char[20];
        int j = 0;
        for (int i = 0; i < 20 && i < nome.length(); i++) {
            if (nome.charAt(i) != '|') {
                tmp[j] = nome.charAt(i);
                j++;
            }
        }
        return new String(tmp, 0, j);
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeUTF(this.nome);
        return baos.toByteArray();
    }


    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.nome = dis.readUTF();
    }
}
