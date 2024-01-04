package Analizadores;
import java.util.ArrayList;

public class SymTable {
    public String identificador;
    public String rol;
    public String tipo;
    public String nivel;
    public Object valor = null;
    public ArrayList<String> parametros = null;
    public Nodo instrucciones = null;
    public SymTable(){
    }
    
}