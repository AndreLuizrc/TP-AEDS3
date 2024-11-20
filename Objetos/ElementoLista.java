package Objetos;

import java.lang.annotation.ElementType;

public class ElementoLista implements Comparable<ElementoLista>, Cloneable {
    private int id;
    private float frequencia;

    public ElementoLista(int i, float f) {
        this.id = i;
        this.frequencia = f;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(float frequencia) {
        this.frequencia = frequencia;
    }
    @Override
    public String toString() {
        return "ElementoLista [id=" + id + ", frequencia=" + frequencia + "]";
    }

    @Override
    public ElementoLista clone() {
        try{
            return (ElementoLista) super.clone();
        } catch (CloneNotSupportedException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int compareTo(ElementoLista o) {
        return Integer.compare(this.id, o.id);
    }
}
