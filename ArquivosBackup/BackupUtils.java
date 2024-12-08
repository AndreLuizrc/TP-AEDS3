package ArquivosBackup;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import ArquivosBackup.LZW.*;

public class BackupUtils{

    public static void listarBackups(String diretorioBackup) {
        File pastaBackup = new File(diretorioBackup);
        if (pastaBackup.exists() && pastaBackup.isDirectory()) {
            File[] backups = pastaBackup.listFiles();
            if (backups != null) {
                for (File backup : backups) {
                    if (backup.isFile()) {
                        System.out.println(backup.getName());
                    }
                }
            }
        } else {
            System.out.println("Nenhuma pasta de backup encontrada.");
        }
    }

    public static void recuperarArquivos(String arquivoBackup, String diretorioDestino) throws Exception {
        File pastaDestino = new File(diretorioDestino);
    
        // Verifica se o diretório existe
        if (pastaDestino.exists()) {
            // Exclui recursivamente os arquivos antigos no diretório de destino
            deletarArquivosAntigos(pastaDestino);
        } else {
            // Cria o diretório, caso não exista
            pastaDestino.mkdirs();
        }
    
        // Restaura os arquivos do backup
        try (DataInputStream dis = new DataInputStream(new FileInputStream(arquivoBackup))) {
            while (dis.available() > 0) {
                // Nome do arquivo
                int tamanhoNome = dis.readInt();
                byte[] nomeBytes = new byte[tamanhoNome];
                dis.readFully(nomeBytes);
                String nomeArquivo = new String(nomeBytes);
    
                // Tamanho do vetor compactado
                int tamanhoCompactado = dis.readInt();
                byte[] conteudoCompactado = new byte[tamanhoCompactado];
                dis.readFully(conteudoCompactado);
    
                // Descompactação
                byte[] conteudoOriginal = LZW.decodifica(conteudoCompactado);
    
                // Escreve o arquivo recuperado
                File arquivoRecuperado = new File(pastaDestino, nomeArquivo);
                try (FileOutputStream fos = new FileOutputStream(arquivoRecuperado)) {
                    fos.write(conteudoOriginal);
                }
            }
        }
    }
    
    // Método recursivo para deletar arquivos e subdiretórios
    private static void deletarArquivosAntigos(File diretorio) {
        File[] arquivos = diretorio.listFiles();
        if (arquivos != null) {
            for (File arquivo : arquivos) {
                if (arquivo.isDirectory()) {
                    // Deleta recursivamente subdiretórios
                    deletarArquivosAntigos(arquivo);
                }
                // Deleta arquivos e diretórios vazios
                arquivo.delete();
            }
        }
    }
    


    private static final int BITS_POR_INDICE = 12; // 12 bits para o dicionário do LZW

    public static void compactarArquivos(String diretorioOrigem, String diretorioBackup, String nomeBackup) throws Exception {
        File pastaOrigem = new File(diretorioOrigem);
        File pastaBackup = new File(diretorioBackup);

        if (!pastaBackup.exists()) {
            pastaBackup.mkdirs();
        }

        File arquivoBackup = new File(pastaBackup, nomeBackup);
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(arquivoBackup))) {
            File[] arquivos = pastaOrigem.listFiles();
            if (arquivos != null) {
                for (File arquivo : arquivos) {
                    if (arquivo.isFile()) {
                        // Nome do arquivo
                        byte[] nomeBytes = arquivo.getName().getBytes();
                        dos.writeInt(nomeBytes.length);
                        dos.write(nomeBytes);

                        // Conteúdo compactado
                        byte[] conteudo = Files.readAllBytes(arquivo.toPath());
                        byte[] conteudoCompactado = LZW.codifica(conteudo);

                        // Tamanho do vetor compactado
                        dos.writeInt(conteudoCompactado.length);
                        dos.write(conteudoCompactado);
                    }
                }
            }
        }
    }


    
}
