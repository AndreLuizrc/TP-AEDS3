package Arquivos;
import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

import Interfaces.Registro;
import Objetos.ParEnderecoId;

public class Arquivo<T extends Registro> {
    final int TAM_CABECALHO = 4;
    RandomAccessFile arquivo;
    String nomeArquivo;
    Constructor<T> construtor;
    HashExtensivel<ParEnderecoId> indiceDireto;

    public Arquivo(String na, Constructor<T> c) throws Exception {
        File d = new File(".\\dados");
        if(!d.exists())
            d.mkdir();

        this.nomeArquivo = ".\\dados\\"+na+".db";
        this.construtor = c;
        arquivo = new RandomAccessFile(this.nomeArquivo, "rw");
        if(arquivo.length()<TAM_CABECALHO) {
            // inicializa o arquivo, criando seu cabecalho
            arquivo.writeInt(0);
        }

        //Inicialização tabela Hash 
        indiceDireto = new HashExtensivel<>(
            ParEnderecoId.class.getConstructor(), 
            4, 
            ".\\dados\\"+na+".hash_d.db",
            ".\\dados\\"+na+".hash_c.db"
        );
    }

    public int create(T obj) throws Exception {
        arquivo.seek(0);
        int proximoID = arquivo.readInt()+1;
        arquivo.seek(0);
        arquivo.writeInt(proximoID);
        obj.setId(proximoID);
        arquivo.seek(arquivo.length());
        long endereco = arquivo.getFilePointer();
        
        byte[] b = obj.toByteArray();
        arquivo.writeByte(' ');
        arquivo.writeShort(b.length);
        arquivo.write(b);
        
        indiceDireto.create(new ParEnderecoId(proximoID,endereco));
        //System.out.println(response);

        return obj.getId();
    }
    
    public T read(int id) throws Exception {
        T obj;
        short tam;
        byte[] b;
        byte lapide;
        
        ParEnderecoId pid = indiceDireto.read(id);
        if(pid != null){
            //System.out.println(pid.getId());
            arquivo.seek(pid.getEndereco());
            obj = construtor.newInstance();
            lapide = arquivo.readByte();
            if(lapide==' ') {
                tam = arquivo.readShort();
                b = new byte[tam];
                arquivo.read(b);
                obj.fromByteArray(b);
                if(obj.getId()==id)
                    return obj;
            }
        }
        return null;
    }

    public ArrayList<T> readAll() throws Exception {
        ArrayList<T> lista = new ArrayList<>();
        T obj;
        byte lapide;
        short tam;
        byte[] b;

        arquivo.seek(TAM_CABECALHO);

        while(arquivo.getFilePointer() < arquivo.length()) {
            obj = construtor.newInstance();
            lapide = arquivo.readByte();
            if(lapide == ' ') {
                tam = arquivo.readShort();

                b = new byte[tam];

                arquivo.read(b);

                obj.fromByteArray(b);
                lista.add(obj);
            } else {
                tam = arquivo.readShort();
                arquivo.skipBytes(tam);
            }
        }

        return lista;
    }

    public boolean delete(int id) throws Exception {
        T obj;
        short tam;
        byte[] b;
        byte lapide;
        
        ParEnderecoId pid = indiceDireto.read(id);
        
        if(pid!=null) {
            arquivo.seek(pid.getEndereco());
            obj = construtor.newInstance();
            lapide = arquivo.readByte();
            if(lapide==' ') {
                tam = arquivo.readShort();
                b = new byte[tam];
                arquivo.read(b);
                obj.fromByteArray(b);
                if(obj.getId()==id) {
                    if(indiceDireto.delete(id)){
                        arquivo.seek(pid.getEndereco());
                        arquivo.write('*');
                        return true;
                    }else{
                        return false;
                    }
                }
            }
        }
        return false;
    }

    public boolean update(T novoObj) throws Exception {
        T obj;
        short tam;
        byte[] b;
        byte lapide;

        ParEnderecoId pid = indiceDireto.read(novoObj.getId());
        
        if(pid != null){
            arquivo.seek(pid.getEndereco());
            obj = construtor.newInstance();
            lapide = arquivo.readByte();
            if(lapide==' ') {
                tam = arquivo.readShort();
                b = new byte[tam];
                arquivo.read(b);
                obj.fromByteArray(b);
                if(obj.getId()==novoObj.getId()) {

                    byte[] b2 = novoObj.toByteArray();
                    short tam2 = (short)b2.length;

                    // sobrescreve o registro
                    if(tam2 <= tam) {
                        arquivo.seek(pid.getEndereco()+3);
                        arquivo.write(b2);
                    }

                    // move o novo registro para o fim
                    else {
                        arquivo.seek(pid.getEndereco());
                        arquivo.write('*');
                        arquivo.seek(arquivo.length());
                        indiceDireto.update(new ParEnderecoId(novoObj.getId(),arquivo.getFilePointer()));
                        arquivo.writeByte(' ');
                        arquivo.writeShort(tam2);
                        arquivo.write(b2);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public void close() throws Exception {
        arquivo.close();
        indiceDireto.close();
    }

    public RandomAccessFile getPointer(){
        return this.arquivo;
    }


}
