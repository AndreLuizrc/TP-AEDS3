package Utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import ArquivosBackup.BackupUtils;



public class MenuBackups {
    public void menu(){
    try {
        String diretorioOrigem = "dados/";  // Atualizar para o caminho correto
        String diretorioBackup = "backup/";

        System.out.println("1. Criar Backup");
        System.out.println("2. Recuperar Backup");
        System.out.println("3. Listar Backups");
        System.out.print("Escolha uma opção: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int opcao = Integer.parseInt(br.readLine());

        switch (opcao) {
            case 1:
                String nomeBackup = "backup_" + System.currentTimeMillis() + ".lzw";
                BackupUtils.compactarArquivos(diretorioOrigem, diretorioBackup, nomeBackup);
                System.out.println("Backup criado com sucesso: " + nomeBackup);
                break;
            case 2:
                BackupUtils.listarBackups(diretorioBackup);
                System.out.print("Digite o nome do backup a recuperar: ");
                String nomeRecuperar = br.readLine();
                BackupUtils.recuperarArquivos(diretorioBackup + nomeRecuperar, diretorioOrigem);
                System.out.println("Backup recuperado com sucesso.");
                break;
            case 3:
                BackupUtils.listarBackups(diretorioBackup);
                break;
            default:
                System.out.println("Opção inválida.");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

}
