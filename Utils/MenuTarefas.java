package Utils;

import java.util.ArrayList;
import java.util.Scanner;

import Arquivos.ArquivoRotuloStatus;
import Arquivos.ArquivoTarefa;
import Objetos.*;

import java.time.LocalDate;
import java.text.Normalizer;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import Arquivos.ArquivoCategoria;

public class MenuTarefas {

    ArquivoTarefa arqTarefas;
    ArquivoCategoria arqCategoria;
    ArquivoRotuloStatus arquivoRotuloStatus;
    private Scanner console = new Scanner(new InputStreamReader(System.in, StandardCharsets.UTF_8));

    public MenuTarefas() throws Exception {
        arqTarefas = new ArquivoTarefa();
        arqCategoria = new ArquivoCategoria();
        arquivoRotuloStatus = new ArquivoRotuloStatus();
    }

    public void menu() throws Exception {

        int opcao;
        do {
            System.out.println("AEDsIII");
            System.out.println("-------");
            System.out.println("\n> Início > Tarefas");
            System.out.println("1 - Buscar");
            System.out.println("2 - Incluir");
            System.out.println("3 - Alterar");
            System.out.println("4 - Excluir");
            System.out.println("5 - Listar tarefas de categoria");
            System.out.println("0 - Voltar");

            System.out.print("Opção: ");
            try {
                opcao = Integer.valueOf(console.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    workflowBusca();
                    break;
                case 2:
                    incluirTarefa();
                    break;
                case 3:
                    alterarTarefa();
                    break;
                case 4:
                    excluirTarefa();
                    break;
                case 5:
                    listarTarefasDeCategoria();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }

        } while (opcao != 0);
    }

    public void workflowBusca() throws Exception {
        System.out.println("\nDeseja buscar tarefa por nome, rotulo ou termo?\n");
        String escolhaDoUsuario = console.nextLine();
        String escolhaTratada = escolhaNormalizer(escolhaDoUsuario);

        if (escolhaTratada.equals("nome")) {
            buscarTarefaPorNome();
        }

        if (escolhaTratada.equals("termo")) {
            buscarTarefaPorTermo();
        }

        if(escolhaTratada.equals("rotulo")) {
            buscaPorRotulo();
        }

    }

    public String escolhaNormalizer(String str) {
        return str.toLowerCase().trim();
    }

    public void buscarTarefaPorNome() throws Exception {
        System.out.println("\nPesquisa de tarefa: ");
        System.out.println("\nDigite o nome da Tarefa que deseja pesquisar: ");

        String nome = console.nextLine();
        String nomeLimpo = tratarNome(nome);

        Tarefas obj = arqTarefas.read(nomeLimpo);
        if (obj != null) {
            System.out.println(obj);
        } else {
            System.out.println("Tarefa nao encontrada");
        }
    }

    public void buscaPorRotulo() throws Exception {
        System.out.println("\nPesquisa de tarefa:");
        System.out.println("1 - Buscar por categoria");
        System.out.println("2 - Buscar por status");
        System.out.print("Escolha uma opção: ");

        String escolha = console.nextLine().trim();

        switch(escolha) {
            case "1":
                buscaPorCategoria();
                break;
            case "2":
                buscaPorStatus();
                break;
            default:
                System.out.println("Opção inválida!");
        }
    }

    private void buscaPorCategoria() throws Exception {
        System.out.print("\nDigite o nome da categoria: ");
        String nomeCategoria = console.nextLine();
        String nomeLimpo = tratarNome(nomeCategoria);

        Categoria categoria = arqCategoria.read(nomeLimpo);
        if (categoria == null) {
            System.out.println("Categoria não encontrada!");
            return;
        }

        ArrayList<Rotulo> rotulosEncontrados = arquivoRotuloStatus.buscarPorCategoria(categoria.getId());

        // Exibe os resultados
        if (rotulosEncontrados.isEmpty()) {
            System.out.println("Nenhuma tarefa encontrada para esta categoria!");
        } else {
            System.out.println("\nTarefas encontradas para a categoria '" + categoria.getNome() + "':");
            for (Rotulo r : rotulosEncontrados) {
                System.out.println(r);
            }
        }
    }

    private void buscaPorStatus() throws Exception {
        System.out.println("\nEstados disponíveis:");
        System.out.println("1 - PENDENTE");
        System.out.println("2 - EM ANDAMENTO");
        System.out.println("3 - CONCLUIDO");
        System.out.print("Escolha o status: ");

        String escolha = console.nextLine().trim();
        int statusId;

        switch(escolha) {
            case "1":
                statusId = RotuloStatus.PENDENTE.getValue();
                break;
            case "2":
                statusId = RotuloStatus.EM_ANDAMENTO.getValue();
                break;
            case "3":
                statusId = RotuloStatus.CONCLUIDO.getValue();
                break;
            default:
                System.out.println("Status inválido!");
                return;
        }

        ArrayList<Rotulo> rotulosEncontrados = arquivoRotuloStatus.buscarPorStatus(statusId);

        if (rotulosEncontrados.isEmpty()) {
            System.out.println("Nenhuma tarefa encontrada com este status!");
        } else {
            System.out.println("\nTarefas encontradas com o status '" + RotuloStatus.values()[statusId] + "':");
            for (Rotulo r : rotulosEncontrados) {
                System.out.println(r);
            }
        }
    }

    public void buscarTarefaPorTermo() throws Exception {
        System.out.println("Busca por termo");
    }

    public void incluirTarefa() throws Exception {
        String nome;
        String descricao;
        int categoria;

        System.out.println("\nInclusão de tarefa: ");
        System.out.println("\nDigite o nome da Tarefa que deseja incluir: ");
        nome = filler(console.nextLine());

        System.out.println("\nDigite uma descricao para sua tarefa: ");
        descricao = console.nextLine();

        System.out.println("\nEscolha a categoria a qual esta tarefa pertence: ");
        categoria = getCategoria();

        if(categoria != 0){
            Tarefas novaTarefa = new Tarefas(nome, descricao);
            novaTarefa.setIdCategoria(categoria);
            arqTarefas.create(novaTarefa);

//            Rotulo rotulo = new Rotulo(novaTarefa.getId(), novaTarefa.getStatus().getValue());
//            arquivoRotuloStatus.create(rotulo);

            System.out.println("Tarefa incluída com sucesso!!\n");
        }else{
            System.out.println("ERRO");
        }
        
        
    }

    public void excluirTarefa() throws Exception {

        System.out.println("\nExclusao de tarefa: ");
        System.out.println("\nDigite o nome da tarefa que deseja excluir: ");

        String nome = console.nextLine();
        String nomeLimpo = tratarNome(nome);

        if (arqTarefas.delete(nomeLimpo)) {
            System.out.println("Tarefa excluida com sucesso!!\n");
        } else {
            System.out.println("Tarefa nao encontrada\n");
        }

    }

    public void alterarTarefa() throws Exception {
        System.out.println("\nAlteracao de tarefa: ");
        System.out.println("\nDigite o nome da tarefa que deseja alterar: ");

        String nome = console.nextLine();
        String nomeLimpo = tratarNome(nome);

        Tarefas obj = arqTarefas.read(nomeLimpo);

        if (obj != null) {
            System.out.println(obj);
            System.out.println("\nQual informação gostaria de alterar?");
            System.out.println("1 - Nome");
            System.out.println("2 - Status");
            System.out.println("3 - Prioridade");
            System.out.println("4 - Data de conclusao");
            System.out.println("5 - Categoria");
            System.out.println("0 - Voltar");

            int option;
            System.out.print("Opção: ");
            try {
                option = Integer.valueOf(console.nextLine());
            } catch (NumberFormatException e) {
                option = -1;
            }

            switch (option) {
                case 1:
                    // alterarNomeTarefa();
                    System.out.println("Digite o novo nome da Tarefa");

                    String nomeNovo = console.nextLine();
                    nomeNovo = tratarNome(nomeNovo);

                    obj.setNome(nomeNovo);
                    if (arqTarefas.update(obj, nomeLimpo)) {
                        System.out.println("Atualizacao realizada com sucesso");
                    } else {
                        System.out.println("ERRO");
                    }
                    break;
                case 2:
                    // alterarStatusTarefa();
                    RotuloStatus newStatus = getNewStatus();
                    if (newStatus != null) {
                        obj.setStatus(newStatus);
                        if (newStatus == RotuloStatus.CONCLUIDO) {
                            LocalDate dataConclusao = LocalDate.now();
                            obj.setDoneAt(dataConclusao);
                        }
                        if (arqTarefas.update(obj, nomeLimpo)) {
                            System.out.println("Status atualizado com sucesso!!");
                        } else {
                            System.out.println("ERRO");
                        }
                    } else {
                        System.out.println("ERRO");
                    }
                    break;
                case 3:
                    // alterarStatusTarefa();
                    byte newPriority = getNewPriority();
                    if (newPriority != 0) {
                        obj.setPriority(newPriority);
                        if (arqTarefas.update(obj, nomeLimpo)) {
                            System.out.println("Prioridade atualizada com sucesso!!");
                        } else {
                            System.out.println("ERRO");
                        }
                    } else {
                        System.out.println("ERRO");
                    }
                    break;
                case 4:
                    // alterarDataTarefa();
                    LocalDate newDate = getNewConclusionDate();
                    if (newDate != null) {
                        obj.setDoneAt(newDate);
                        obj.setStatus(RotuloStatus.CONCLUIDO);
                        if (arqTarefas.update(obj, nomeLimpo)) {
                            System.out.println("Data de conclusao atualizada com sucesso!!");
                        } else {
                            System.out.println("ERRO");
                        }
                    }else{
                        System.out.println("ERRO");
                    }
                    break;
                case 5:
                    System.out.println("Escolha a nova categoria");
                    int novaCategoria = getCategoria();
                    if(novaCategoria != 0){
                        int idVelho = obj.getId();
                        obj.setIdCategoria(novaCategoria);
                        arqTarefas.update(obj, nomeLimpo, idVelho);
                    }else{
                        System.out.println("ERRO");
                    }
                    break;
                default:
                    System.out.println("Atualizacao cancelada!");
                    break;
            }
        } else {
            System.out.println("Categoria nao encontrada!");
        }
    }

    public void listarTarefasDeCategoria()throws Exception{
        System.out.println("Escolha uma categoria: ");
        int idCategoria = getCategoria();

        if(idCategoria != 0){
            ArrayList<ParIdId> pii = arqTarefas.getAllStacksFromCategorie(idCategoria);
            if(pii.size() > 0){
                for(ParIdId item : pii){
                    Tarefas tarefa = arqTarefas.read(item.getId2());                    
                    System.out.println(tarefa);
                }
            }else{
                System.out.println("Nao existem tarefas com essa categoria!");
            }
        }else{
            System.out.println("Categoria invalida!");
        }
    }

    public String tratarNome(String nome) {
        // Trata acentos
        String nomeLimpo = Normalizer.normalize(nome, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");

        // Preenche para ficar com tamanho exato
        nomeLimpo = filler(nomeLimpo);

        return nomeLimpo;
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

    public int getCategoria()throws Exception{
        ArrayList<Categoria> categorias = arqCategoria.getAllCategories();
        int option;
        int position = 0;

        if(categorias != null){
            do{
                int i = 1;
                for(Categoria item : categorias){
                    System.out.println(i + "- " + unfiller(item.getNome()));
                    i++;
                }
                System.out.println("opcao:");
                option = console.nextInt();
                console.nextLine();
                if(option <= categorias.size() && option > 0){
                    position = option - 1;
                    option = 0;
                }                
                
            }while(option != 0);
           
            return categorias.get(position).getId();
        }

        return 0;
    }

    public RotuloStatus getNewStatus(){
        RotuloStatus sts = null;
        int option;

        do {
            System.out.println("Escolha um novo status: ");
            System.out.println("1- Pendente");
            System.out.println("2- Em progresso");
            System.out.println("3- Concluido");
            System.out.println("0- Voltar");

            System.out.print("Opção: ");
            try {
                option = Integer.valueOf(console.nextLine());
            } catch (NumberFormatException e) {
                option = -1;
            }
            switch (option) {
                case 1:
                    sts = RotuloStatus.PENDENTE;
                    option = 0;
                    break;
                case 2:
                    sts = RotuloStatus.EM_ANDAMENTO;
                    option = 0;
                    break;
                case 3:
                    sts = RotuloStatus.CONCLUIDO;
                    option = 0;
                    break;
                case 0:
                    break;
                default:
                    System.out.println("opcao invalida");
                    break;
            }
        } while (option != 0);
        return sts;

    }

    public byte getNewPriority() {
        byte prt = 0;
        int option;

        System.out.println("Escolha a nova prioridade da sua tarefa:");
        System.out.println("1- ALTA");
        System.out.println("2- MEDIA");
        System.out.println("3- BAIXA");
        System.out.println("0- Voltar");

        System.out.print("Opção: ");
        try {
            option = Integer.valueOf(console.nextLine());
        } catch (NumberFormatException e) {
            option = -1;
        }

        do {
            switch (option) {
                case 1:
                    prt = 1;
                    option = 0;
                    break;
                case 2:
                    prt = 2;
                    option = 0;
                    break;
                case 3:
                    prt = 3;
                    option = 0;
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opcao invalida");
                    break;
            }
        } while (option != 0);

        return prt;
    }

    public LocalDate getNewConclusionDate() {
        int dia;
        int mes;
        int ano;

        System.out.println("Informe o dia de conclusao:");
        dia = console.nextInt();
        System.out.println("Informe o mes de conclusao:");
        mes = console.nextInt();
        System.out.println("Informe o ano de conclusao:");
        ano = console.nextInt();

        console.nextLine();

        LocalDate novaData = LocalDate.of(ano, mes, dia);

        return novaData;
    }
}
