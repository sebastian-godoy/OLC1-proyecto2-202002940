package Analizadores;

public class Generador {
    public static void main(String[] args){
        try{
        String ruta = "src/Analizadores/";
        String opcFlex[] = {ruta+"Lexer","-d",ruta};
        jflex.Main.generate(opcFlex);
        String opcCUP[] = {"-destdir",ruta,"-parser","Parser",ruta+"Parser"};
        java_cup.Main.main(opcCUP);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}