package Utils;

import java.lang.annotation.ElementType;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Objetos.ElementoLista;

public class AuxFunctions {
    
    Set<String> stopWords;

    public AuxFunctions() throws Exception{
        stopWords = new HashSet<>(Files.readAllLines(Paths.get("Utils/stopwords.txt")));
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

    public void QuickSort(List<ElementoLista> lista , int esq, int dir) {
        int i = esq;
        int j = dir;

        ElementoLista pivo = lista.get((esq+dir)/2);

        while( i <= j){
            while(lista.get(i).getFrequencia() > pivo.getFrequencia()){
                i++;
            }
    
            while(lista.get(j).getFrequencia() < pivo.getFrequencia()){
                j--;
            }
    
            if(i <= j){
                ElementoLista tmp = lista.get(i);
                lista.set(i, lista.get(j));
                lista.set(j, tmp);
                i++;
                j--;
            }
        }

        if( i < dir){
            QuickSort(lista,i,dir);
        }

        if(j > esq){
            QuickSort(lista, esq, j);
        }
    }
}
