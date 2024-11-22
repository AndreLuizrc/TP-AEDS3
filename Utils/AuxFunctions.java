package Utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AuxFunctions {
    
    Set<String> stopWords;

    public AuxFunctions() throws Exception{
        stopWords = new HashSet<>(Files.readAllLines(Paths.get("dados/stopwords.txt")));
    }
    
    //Função para realizar tratamentos na descrição da tarefa e retornar um vetor com as strings tratadas e separadas
    public List<String> splitTextToList(String texto) {
        String stringFormatada = Normalizer.normalize(texto, Normalizer.Form.NFD);
        stringFormatada.replaceAll("[^\\p{ASCII}]", "");
        stringFormatada = stringFormatada.toLowerCase();
        String[] listaPalavras = stringFormatada.split(" ");

        int qtdPalavras = listaPalavras.length;
        List<String> listChaves = new ArrayList<>();

        //Retirando as StopWords da lista de termos da descrição
        for(int i = 0; i < qtdPalavras; i++){
            if(!this.stopWords.contains(listaPalavras[i])){
                listChaves.add(listaPalavras[i]);
            }
        }

        return listChaves;
    }
}
