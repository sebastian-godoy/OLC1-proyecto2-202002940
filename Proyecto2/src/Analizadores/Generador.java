package Analizadores;

public class Generador {
    public static void main(String[] args){
        try{
        String ruta = "src/Analizadores/";
        String opcFlex[] = {ruta+"AnalizadorLexico","-d",ruta};
        jflex.Main.generate(opcFlex);
        String opcCUP[] = {"-destdir",ruta,"-parser","AnalizadorSintactico",ruta+"AnalizadorSintactico"};
        java_cup.Main.main(opcCUP);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}