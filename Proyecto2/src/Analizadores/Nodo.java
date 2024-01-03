package Analizadores;
import java.util.ArrayList;
/**
 *
 * @author Sebas
 */
public class Nodo {
    public String titulo;
    public String dato;
    public ArrayList<Nodo> hijos = new ArrayList<>();
    
    public Nodo(String titulo){  
        this.titulo = titulo;
        this.dato = "";
    }
    
    public Nodo(String titulo, String dato){  
        this.titulo = titulo;
        this.dato = dato;
    }
    
    public String imprimir_(){
        String s ="";
        s = this.imprimir(this, 0);
        return s;
    }
    
    private String imprimir(Nodo actual, int padre){
        String s = "N_"+actual.hashCode()+"[label=\""+actual.titulo;
        if(!actual.dato.equals("")){
            s+="\\n"+actual.dato.replaceAll("\"", "")+"\"];\n";
        }else{
            s+="\"];\n";
        }
        
        if(padre != 0){
            s+= "N_"+padre+"->N_"+actual.hashCode()+";\n";
        }
        
        for (int i = 0; i < actual.hijos.size(); i++){
            s+=imprimir(actual.hijos.get(i), actual.hashCode());      
        }
    
        return s;
             
    }
    
}
