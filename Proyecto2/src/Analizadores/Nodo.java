package Analizadores;
import java.util.ArrayList;

public class Nodo {
    public String titulo;
    public String dato;
    public String entorno = "";
    public ArrayList<Nodo> hijos = new ArrayList<>();
    
    
    public Nodo(String titulo){  
        this.titulo = titulo;
        this.dato = "";
    }
    
    public Nodo(String titulo, String dato){  
        this.titulo = titulo;
        this.dato = dato;
    }
}
