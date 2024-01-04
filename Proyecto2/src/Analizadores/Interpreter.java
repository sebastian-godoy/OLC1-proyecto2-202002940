package Analizadores;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Interpreter {

    public Nodo avl;
    public Nodo comienzo_programa;
    // Lista de simbolos
    public ArrayList<SymTable> symTSL = new ArrayList<>();
    public String texto_salida = "";

    public Interpreter(Nodo avl) {
        this.avl = avl;
        get_env(this.avl);
        
        
        //SENTENCIAS DE PRIORIDAD
        print_TSM();
        ejecutar();

    }

    

    public void get_env(Nodo actual) {
    if (actual.titulo.equals("declaracion_metodo")) 
    {
        if (actual.hijos.size() == 3) 
        {
            actual.hijos.get(0).nivel = actual.nivel;
            actual.hijos.get(1).nivel = actual.nivel + "_" + actual.hashCode();
            
            //Subnivel detectado
            actual.hijos.get(2).nivel = actual.nivel + "_" + actual.hashCode();
        } 
        else 
        {
            actual.hijos.get(0).nivel = actual.nivel;
            
            
            actual.hijos.get(1).nivel = actual.nivel + "_" + actual.hashCode();
        }
    } 
    else if (actual.titulo.equals("declaracion_funcion")) 
    {
        if (actual.hijos.size() == 4) {
            actual.hijos.get(1).nivel = actual.nivel;
            
            actual.hijos.get(2).nivel = actual.nivel + "_" + actual.hashCode();
            
            
            actual.hijos.get(3).nivel = actual.nivel + "_" + actual.hashCode();
        } 
        else 
        {
           // En caso de entrar
            actual.hijos.get(1).nivel = actual.nivel;
            
            
            actual.hijos.get(2).nivel = actual.nivel + "_" + actual.hashCode();
        }
    } 
    else if (actual.titulo.equals("sentencia_si") || actual.titulo.equals("sentencia_selector")) 
    {
        if (actual.hijos.size() == 3) 
        { 
            actual.hijos.get(0).nivel = actual.nivel;
            
            
            actual.hijos.get(1).nivel = actual.nivel + "_" + actual.hashCode();
            
            
            actual.hijos.get(2).nivel = actual.nivel + "_" + actual.hashCode();
        } 
        else 
        {
            actual.hijos.get(0).nivel = actual.nivel;
            
            actual.hijos.get(1).nivel = actual.nivel + "_" + actual.hashCode();
        }
    } 
    else if (actual.titulo.equals("sentencia_mientras")) 
    {
        actual.hijos.get(0).nivel = actual.nivel;
        
        
        actual.hijos.get(1).nivel = actual.nivel + "_" + actual.hashCode();
        
        
    } else if (actual.titulo.equals("sentencia_para") || actual.titulo.equals("sentencia_hacer")) {
        actual.hijos.forEach((x) -> {
            
            
            x.nivel = actual.nivel + "_" + actual.hashCode();
            
        });
    } else 
    {
        actual.hijos.forEach((x) -> {
            
            x.nivel = actual.nivel;
        });
    }

    SymTable n_simbolo = new SymTable();
    String tipo;
    
    
    String rol;
    
    String nivel;
    

    if (actual.titulo.equals("declaracion_metodo")) 
    {
        
        n_simbolo.identificador = actual.hijos.get(0).dato;
        n_simbolo.rol = "metodo";
        
        
        n_simbolo.nivel = actual.nivel;
        
        n_simbolo.tipo = "void";

        if (actual.hijos.size() == 2) {
            n_simbolo.instrucciones = actual.hijos.get(1);
        } else {
            n_simbolo.instrucciones = actual.hijos.get(2);
            
            
            n_simbolo.parametros = interpretar_parametros(actual.hijos.get(1));
        }
        this.symTSL.add(n_simbolo);
        
    } else if (actual.titulo.equals("parametros")) {
        
        n_simbolo.rol = "parametro";
        
        
        
        
        
        
        n_simbolo.nivel = actual.nivel;

        if (actual.hijos.size() == 2) 
        {
            
            
            
            n_simbolo.identificador = actual.hijos.get(1).hijos.get(1).dato;
            
            
            
            n_simbolo.tipo = actual.hijos.get(1).hijos.get(0).dato;
            
            
        } else 
        {
            n_simbolo.identificador = actual.hijos.get(0).hijos.get(1).dato;
            
            
            
            n_simbolo.tipo = actual.hijos.get(0).hijos.get(0).dato;
        }

        switch (n_simbolo.tipo) 
        {
            case "entero" -> n_simbolo.valor = 0;
            
            
            
            case "doble" -> n_simbolo.valor = (float) 0.0;
            
            case "binario" -> n_simbolo.valor = true;
            
            
            
            case "caracter" -> n_simbolo.valor = '\u0000';
            
            case "cadena" -> n_simbolo.valor = "";
        }
        this.symTSL.add(n_simbolo);
    } else if (actual.titulo.equals("declaracion_var")) 
    {
        
        tipo = actual.hijos.get(0).dato;
        rol = "variable";
        nivel = actual.nivel;

        ArrayList<String> lista_vars = interpretar_lista_variables(actual.hijos.get(1));

        lista_vars.forEach((identificador) -> {
            
            SymTable simbolo_variable = new SymTable();
            
            simbolo_variable.tipo = tipo;
            
            simbolo_variable.rol = rol;
   
            
            simbolo_variable.nivel = nivel;
            
            simbolo_variable.identificador = identificador;
            switch (simbolo_variable.tipo) {
                case "entero" -> simbolo_variable.valor = 0;
                case "doble" -> simbolo_variable.valor = (float) 0.0;
                case "binario" -> simbolo_variable.valor = true;
                case "caracter" -> simbolo_variable.valor = '\u0000';
                case "cadena" -> simbolo_variable.valor = "";
            }
            this.symTSL.add(simbolo_variable);
        });
    } else if (actual.titulo.equals("declaracion_funcion")) {
        n_simbolo.identificador = actual.hijos.get(1).dato;
        n_simbolo.rol = "funcion";
        n_simbolo.nivel = actual.nivel;
        n_simbolo.tipo = actual.hijos.get(0).dato;

        if (actual.hijos.size() == 3) {
            n_simbolo.instrucciones = actual.hijos.get(2);
        } else {
            n_simbolo.parametros = interpretar_parametros(actual.hijos.get(2));
            n_simbolo.instrucciones = actual.hijos.get(3);
        }

        this.symTSL.add(n_simbolo);
    }

    if (actual.titulo.equals("sentencia_ejecutar")) {
        comienzo_programa = actual.hijos.get(0);
    }

    actual.hijos.forEach((x) -> {
        get_env(x);
    });
}

    private ArrayList<String> interpretar_parametros(Nodo actual) {
        ArrayList<String> lista_actual;

        if (actual.hijos.size() == 2) {
            lista_actual = interpretar_parametros(actual.hijos.get(0));
            lista_actual.add(actual.hijos.get(1).hijos.get(1).dato);
        } else {
            lista_actual = new ArrayList<>();
            lista_actual.add(actual.hijos.get(0).hijos.get(1).dato);
        }
        return lista_actual;
    }

    private ArrayList<String> interpretar_lista_variables(Nodo actual) {
        ArrayList<String> lista_actual;

        if (actual.hijos.size() == 2) {
            lista_actual = interpretar_lista_variables(actual.hijos.get(0));
            lista_actual.add(actual.hijos.get(1).dato);
        } else {
            lista_actual = new ArrayList<>();
            lista_actual.add(actual.hijos.get(0).dato);
        }
        return lista_actual;
    }

    public void print_TSM() {
        System.out.println("+ID-tk-type-lvl-value-params-realizando+\n");
        this.symTSL.forEach((s) -> {
            System.out.print("+" + s.identificador + "-" + s.rol + "-" + s.tipo + "-" + s.nivel + "+");
            if (s.valor != null) {
                switch (s.tipo) {
                    case "entero" ->
                        System.out.print((int) s.valor + "-");
                    case "doble" ->
                        System.out.print((float) s.valor + "-");
                    case "binario" ->
                        System.out.print((boolean) s.valor + "-");
                    case "caracter" ->
                        System.out.print((char) s.valor + "-");
                    case "cadena" ->
                        System.out.print("\"" + (String) s.valor + "\"-");
                }
            } else {
                System.out.print(" ");
            }
            if (s.parametros != null) {
                s.parametros.forEach((param) -> {
                    System.out.print(param + " ");
                });
                System.out.print("+");
            } else {
                System.out.print("  ");
            }

            if (s.instrucciones != null) {
                System.out.print(s.instrucciones.hashCode() + "\n");
            } else {
                System.out.print("\n");
            }

        });
    }

    public LogOutput interpretar(ArrayList<SymTable> nivel, Nodo actual) {
        LogOutput retorno = new LogOutput();
        switch (actual.titulo) {
            case "expresion" -> {
                LogOutput REGISTRO_A;
                LogOutput REGISTRO_B_HIJO;
                switch (actual.dato) {
                    case "+" -> {
                        REGISTRO_A = interpretar(nivel, actual.hijos.get(0));
                        REGISTRO_B_HIJO = interpretar(nivel, actual.hijos.get(1));

                        if (!REGISTRO_A.tipo.equals(REGISTRO_B_HIJO.tipo)) {
                            this.texto_salida += "Suma de tipos diferentes: " + REGISTRO_A.tipo + " y " + REGISTRO_B_HIJO.tipo;
                            retorno.sentencia = "ERROR FATAL";
                            return retorno;
                        }

                        switch (REGISTRO_A.tipo) {
                            case "entero" -> {
                                retorno.tipo = "entero";
                                retorno.valor = (int) REGISTRO_A.valor + (int) REGISTRO_B_HIJO.valor;
                                return retorno;
                            }

                            case "doble" -> {
                                retorno.tipo = "doble";
                                retorno.valor = (float) REGISTRO_A.valor + (float) REGISTRO_B_HIJO.valor;
                                return retorno;
                            }

                            case "binario" -> {
                                this.texto_salida += "Error de tipos en la suma";
                                retorno.sentencia = "error";
                                return retorno;
                            }

                            case "caracter" -> {
                                retorno.tipo = "cadena";
                                retorno.valor = "" + (char) REGISTRO_A.valor + (char) REGISTRO_B_HIJO.valor;
                                return retorno;
                            }
                            case "cadena" -> {
                                retorno.tipo = "cadena";
                                retorno.valor = (String) REGISTRO_A.valor + (String) REGISTRO_B_HIJO.valor;
                                return retorno;
                            }
                        }
                    }

                    case "-" -> {
                        REGISTRO_A = interpretar(nivel, actual.hijos.get(0));
                        if (actual.hijos.size() == 2) {
                            REGISTRO_B_HIJO = interpretar(nivel, actual.hijos.get(1));

                            if (!REGISTRO_A.tipo.equals(REGISTRO_B_HIJO.tipo)) {
                                this.texto_salida += "Error en la resta, se intento restar " + REGISTRO_A.tipo + " y " + REGISTRO_B_HIJO.tipo;
                                retorno.sentencia = "error";
                                return retorno;
                            }

                            switch (REGISTRO_A.tipo) {
                                case "entero" -> {
                                    retorno.tipo = "entero";
                                    retorno.valor = (int) REGISTRO_A.valor - (int) REGISTRO_B_HIJO.valor;
                                    return retorno;
                                }

                                case "doble" -> {
                                    retorno.tipo = "doble";
                                    retorno.valor = (float) REGISTRO_A.valor - (float) REGISTRO_B_HIJO.valor;
                                    return retorno;
                                }

                                default -> {
                                    this.texto_salida += "Error se intento restar " + REGISTRO_A.tipo;
                                    retorno.sentencia = "error";
                                    return retorno;
                                }
                            }
                        }
                        switch (REGISTRO_A.tipo) 
                        {
                            case "entero" -> {
                                retorno.tipo = "entero";
                                retorno.valor = -(int) REGISTRO_A.valor;
                                return retorno;
                            }

                            case "doble" -> {
                                retorno.tipo = "doble";
                                retorno.valor = -(float) REGISTRO_A.valor;
                                return retorno;
                            }

                            default -> {
                                this.texto_salida += "Error en la resta, se intento restar " + REGISTRO_A.tipo;
                                retorno.sentencia = "error";
                                return retorno;
                            }
                        }
                    }

                    case "*" -> {
                        REGISTRO_A = interpretar(nivel, actual.hijos.get(0));
                        REGISTRO_B_HIJO = interpretar(nivel, actual.hijos.get(1));

                        if (!REGISTRO_A.tipo.equals(REGISTRO_B_HIJO.tipo)) {
                            this.texto_salida += "Error en la multiplicacion, se intento operar: " + REGISTRO_A.tipo + " y " + REGISTRO_B_HIJO.tipo;
                            retorno.sentencia = "error";
                            return retorno;
                        }

                        switch (REGISTRO_A.tipo) {
                            case "entero" -> {
                                retorno.tipo = "entero";
                                retorno.valor = (int) REGISTRO_A.valor * (int) REGISTRO_B_HIJO.valor;
                                return retorno;
                            }

                            case "doble" -> {
                                retorno.tipo = "doble";
                                retorno.valor = (float) REGISTRO_A.valor * (float) REGISTRO_B_HIJO.valor;
                                return retorno;
                            }

                            default -> {
                                this.texto_salida += "Error en la multiplicacion: " + REGISTRO_A.tipo;
                                retorno.sentencia = "error";
                                return retorno;
                            }
                        }
                    }

                    case "/" -> {
                        REGISTRO_A = interpretar(nivel, actual.hijos.get(0));
                        REGISTRO_B_HIJO = interpretar(nivel, actual.hijos.get(1));

                        if (!REGISTRO_A.tipo.equals(REGISTRO_B_HIJO.tipo)) {
                            this.texto_salida += "Error en la division, se intento dividir: " + REGISTRO_A.tipo + " y " + REGISTRO_B_HIJO.tipo;
                            retorno.sentencia = "error";
                            return retorno;
                        }

                        switch (REGISTRO_A.tipo) {
                            case "entero" -> {
                                retorno.tipo = "entero";
                                retorno.valor = (int) REGISTRO_A.valor / (int) REGISTRO_B_HIJO.valor;
                                return retorno;
                            }

                            case "doble" -> {
                                retorno.tipo = "doble";
                                retorno.valor = (float) REGISTRO_A.valor / (float) REGISTRO_B_HIJO.valor;
                                return retorno;
                            }

                            default -> {
                                this.texto_salida += "Error de tipos en la division: " + REGISTRO_A.tipo;
                                retorno.sentencia = "error";
                                return retorno;
                            }
                        }
                    }

                    case "^" -> {
                        REGISTRO_A = interpretar(nivel, actual.hijos.get(0));
                        REGISTRO_B_HIJO = interpretar(nivel, actual.hijos.get(1));

                        if (!REGISTRO_A.tipo.equals(REGISTRO_B_HIJO.tipo)) {
                            this.texto_salida += "Error en la potencia debido a: " + REGISTRO_A.tipo + " y " + REGISTRO_B_HIJO.tipo;
                            retorno.sentencia = "error";
                            return retorno;
                        }

                        switch (REGISTRO_A.tipo) {
                            case "entero" -> {
                                retorno.tipo = "entero";
                                retorno.valor = (int) Math.pow((int) REGISTRO_A.valor, (int) REGISTRO_B_HIJO.valor);
                                return retorno;
                            }

                            case "doble" -> {
                                retorno.tipo = "doble";
                                retorno.valor = (float) Math.pow((float) REGISTRO_A.valor, (float) REGISTRO_B_HIJO.valor);
                                return retorno;
                            }

                            default -> {
                                this.texto_salida += "Error de potenciacion: " + REGISTRO_A.tipo;
                                retorno.sentencia = "error";
                                return retorno;
                            }
                        }
                    }

                    case "%" -> {
                        REGISTRO_A = interpretar(nivel, actual.hijos.get(0));
                        REGISTRO_B_HIJO = interpretar(nivel, actual.hijos.get(1));

                        if (!REGISTRO_A.tipo.equals(REGISTRO_B_HIJO.tipo)) {
                            this.texto_salida += "Error de div modular entre: " + REGISTRO_A.tipo + " y " + REGISTRO_B_HIJO.tipo;
                            retorno.sentencia = "error";
                            return retorno;
                        }

                        switch (REGISTRO_A.tipo) {
                            case "entero" -> {
                                retorno.tipo = "entero";
                                retorno.valor = (int) REGISTRO_A.valor % (int) REGISTRO_B_HIJO.valor;
                                return retorno;
                            }

                            case "doble" -> {
                                retorno.tipo = "doble";
                                retorno.valor = (float) REGISTRO_A.valor % (float) REGISTRO_B_HIJO.valor;
                                return retorno;
                            }

                            default -> {
                                this.texto_salida += "Error modular: " + REGISTRO_A.tipo;
                                retorno.sentencia = "error";
                                return retorno;
                            }
                        }
                    }

                    case "==" -> {
                        REGISTRO_A = interpretar(nivel, actual.hijos.get(0));
                        REGISTRO_B_HIJO = interpretar(nivel, actual.hijos.get(1));

                        retorno.tipo = "binario";
                        retorno.valor = REGISTRO_A.valor.equals(REGISTRO_B_HIJO.valor);
                        return retorno;
                    }

                    case "!=" -> {
                        REGISTRO_A = interpretar(nivel, actual.hijos.get(0));
                        REGISTRO_B_HIJO = interpretar(nivel, actual.hijos.get(1));

                        retorno.tipo = "binario";
                        retorno.valor = !(REGISTRO_A.valor.equals(REGISTRO_B_HIJO.valor));
                        return retorno;
                    }

                    case "<" -> {
                        REGISTRO_A = interpretar(nivel, actual.hijos.get(0));
                        REGISTRO_B_HIJO = interpretar(nivel, actual.hijos.get(1));
                        retorno.tipo = "binario";
                        switch (REGISTRO_A.tipo) {
                            case "entero" -> {
                                switch (REGISTRO_B_HIJO.tipo) {
                                    case "entero" -> {
                                        retorno.valor = (int) REGISTRO_A.valor < (int) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    case "doble" -> {
                                        retorno.valor = (int) REGISTRO_A.valor < (float) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    case "caracter" -> {
                                        retorno.valor = (int) REGISTRO_A.valor < (char) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    default -> {
                                        retorno.tipo = null;
                                        this.texto_salida += "Error en la operacion" + REGISTRO_A.tipo + " y " + REGISTRO_B_HIJO.tipo;
                                        retorno.sentencia = "error";
                                        return retorno;
                                    }
                                }
                            }

                            case "doble" -> {
                                switch (REGISTRO_B_HIJO.tipo) {
                                    case "entero" -> {
                                        retorno.valor = (float) REGISTRO_A.valor < (int) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    case "doble" -> {
                                        retorno.valor = (float) REGISTRO_A.valor < (float) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    case "caracter" -> {
                                        retorno.valor = (float) REGISTRO_A.valor < (char) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    default -> {
                                        retorno.tipo = null;
                                        this.texto_salida += "Error operativo debido a  " + REGISTRO_A.tipo + " y " + REGISTRO_B_HIJO.tipo;
                                        retorno.sentencia = "error";
                                        return retorno;
                                    }
                                }
                            }

                            case "caracter" -> {
                                switch (REGISTRO_B_HIJO.tipo) {
                                    case "entero" -> {
                                        retorno.valor = (char) REGISTRO_A.valor < (int) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    case "doble" -> {
                                        retorno.valor = (char) REGISTRO_A.valor < (float) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    case "caracter" -> {
                                        retorno.valor = (char) REGISTRO_A.valor < (char) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    default -> {
                                        retorno.tipo = null;
                                        this.texto_salida += "Error operativo: " + REGISTRO_A.tipo + " con " + REGISTRO_B_HIJO.tipo;
                                        retorno.sentencia = "error";
                                        return retorno;
                                    }
                                }
                            }

                            default -> {
                                retorno.tipo = null;
                                this.texto_salida += "Error operativo" + REGISTRO_A.tipo + " con " + REGISTRO_B_HIJO.tipo;
                                retorno.sentencia = "error";
                                return retorno;
                            }
                        }
                    }

                    case "<=" -> {
                        REGISTRO_A = interpretar(nivel, actual.hijos.get(0));
                        REGISTRO_B_HIJO = interpretar(nivel, actual.hijos.get(1));
                        retorno.tipo = "binario";
                        switch (REGISTRO_A.tipo) {
                            case "entero" -> {
                                switch (REGISTRO_B_HIJO.tipo) {
                                    case "entero" -> {
                                        retorno.valor = (int) REGISTRO_A.valor <= (int) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    case "doble" -> {
                                        retorno.valor = (int) REGISTRO_A.valor <= (float) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    case "caracter" -> {
                                        retorno.valor = (int) REGISTRO_A.valor <= (char) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    default -> {
                                        retorno.tipo = null;
                                        this.texto_salida += "Error de tipos" + REGISTRO_A.tipo + " y " + REGISTRO_B_HIJO.tipo;
                                        retorno.sentencia = "error";
                                        return retorno;
                                    }
                                }
                            }

                            case "doble" -> {
                                switch (REGISTRO_B_HIJO.tipo) {
                                    case "entero" -> {
                                        retorno.valor = (float) REGISTRO_A.valor <= (int) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    case "doble" -> {
                                        retorno.valor = (float) REGISTRO_A.valor <= (float) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    case "caracter" -> {
                                        retorno.valor = (float) REGISTRO_A.valor <= (char) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    default -> {
                                        retorno.tipo = null;
                                        this.texto_salida += "Error de tipos" + REGISTRO_A.tipo + " y " + REGISTRO_B_HIJO.tipo;
                                        retorno.sentencia = "error";
                                        return retorno;
                                    }
                                }
                            }

                            case "caracter" -> {
                                switch (REGISTRO_B_HIJO.tipo) {
                                    case "entero" -> {
                                        retorno.valor = (char) REGISTRO_A.valor <= (int) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    case "doble" -> {
                                        retorno.valor = (char) REGISTRO_A.valor <= (float) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    case "caracter" -> {
                                        retorno.valor = (char) REGISTRO_A.valor <= (char) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    default -> {
                                        retorno.tipo = null;
                                        this.texto_salida += "Error de tipos" + REGISTRO_A.tipo + " y " + REGISTRO_B_HIJO.tipo;
                                        retorno.sentencia = "error";
                                        return retorno;
                                    }
                                }
                            }

                            default -> {
                                retorno.tipo = null;
                                this.texto_salida += "Error de tipos" + REGISTRO_A.tipo + " y " + REGISTRO_B_HIJO.tipo;
                                retorno.sentencia = "error";
                                return retorno;
                            }
                        }
                    }

                    case ">" -> {
                        REGISTRO_A = interpretar(nivel, actual.hijos.get(0));
                        REGISTRO_B_HIJO = interpretar(nivel, actual.hijos.get(1));
                        retorno.tipo = "binario";
                        switch (REGISTRO_A.tipo) {
                            case "entero" -> {
                                switch (REGISTRO_B_HIJO.tipo) {
                                    case "entero" -> {
                                        retorno.valor = (int) REGISTRO_A.valor > (int) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    case "doble" -> {
                                        retorno.valor = (int) REGISTRO_A.valor > (float) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    case "caracter" -> {
                                        retorno.valor = (int) REGISTRO_A.valor > (char) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    default -> {
                                        retorno.tipo = null;
                                        this.texto_salida += "Error de tipos " + REGISTRO_A.tipo + " y " + REGISTRO_B_HIJO.tipo;
                                        retorno.sentencia = "error";
                                        return retorno;
                                    }
                                }
                            }

                            case "doble" -> {
                                switch (REGISTRO_B_HIJO.tipo) {
                                    case "entero" -> {
                                        retorno.valor = (float) REGISTRO_A.valor > (int) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    case "doble" -> {
                                        retorno.valor = (float) REGISTRO_A.valor > (float) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    case "caracter" -> {
                                        retorno.valor = (float) REGISTRO_A.valor > (char) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    default -> {
                                        retorno.tipo = null;
                                        this.texto_salida += "Error de tipos" + REGISTRO_A.tipo + " y " + REGISTRO_B_HIJO.tipo;
                                        retorno.sentencia = "error";
                                        return retorno;
                                    }
                                }
                            }

                            case "caracter" -> {
                                switch (REGISTRO_B_HIJO.tipo) {
                                    case "entero" -> {
                                        retorno.valor = (char) REGISTRO_A.valor > (int) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    case "doble" -> {
                                        retorno.valor = (char) REGISTRO_A.valor > (float) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    case "caracter" -> {
                                        retorno.valor = (char) REGISTRO_A.valor > (char) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    default -> {
                                        retorno.tipo = null;
                                        this.texto_salida += "Error de tipos" + REGISTRO_A.tipo + " y " + REGISTRO_B_HIJO.tipo;
                                        retorno.sentencia = "error";
                                        return retorno;
                                    }
                                }
                            }

                            default -> {
                                retorno.tipo = null;
                                this.texto_salida += "Error de tipos" + REGISTRO_A.tipo + " y " + REGISTRO_B_HIJO.tipo;
                                retorno.sentencia = "error";
                                return retorno;
                            }
                        }
                    }

                    case ">=" -> {
                        REGISTRO_A = interpretar(nivel, actual.hijos.get(0));
                        REGISTRO_B_HIJO = interpretar(nivel, actual.hijos.get(1));
                        retorno.tipo = "binario";
                        switch (REGISTRO_A.tipo) {
                            case "entero" -> {
                                switch (REGISTRO_B_HIJO.tipo) {
                                    case "entero" -> {
                                        retorno.valor = (int) REGISTRO_A.valor >= (int) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    case "doble" -> {
                                        retorno.valor = (int) REGISTRO_A.valor >= (float) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    case "caracter" -> {
                                        retorno.valor = (int) REGISTRO_A.valor >= (char) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    default -> {
                                        retorno.tipo = null;
                                        this.texto_salida += "Error de tipos" + REGISTRO_A.tipo + " y " + REGISTRO_B_HIJO.tipo;
                                        retorno.sentencia = "error";
                                        return retorno;
                                    }
                                }
                            }

                            case "doble" -> {
                                switch (REGISTRO_B_HIJO.tipo) {
                                    case "entero" -> {
                                        retorno.valor = (float) REGISTRO_A.valor >= (int) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    case "doble" -> {
                                        retorno.valor = (float) REGISTRO_A.valor >= (float) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    case "caracter" -> {
                                        retorno.valor = (float) REGISTRO_A.valor >= (char) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    default -> {
                                        retorno.tipo = null;
                                        this.texto_salida += "Error de tipos" + REGISTRO_A.tipo + " y " + REGISTRO_B_HIJO.tipo;
                                        retorno.sentencia = "error";
                                        return retorno;
                                    }
                                }
                            }

                            case "caracter" -> {
                                switch (REGISTRO_B_HIJO.tipo) {
                                    case "entero" -> {
                                        retorno.valor = (char) REGISTRO_A.valor >= (int) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    case "doble" -> {
                                        retorno.valor = (char) REGISTRO_A.valor >= (float) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    case "caracter" -> {
                                        retorno.valor = (char) REGISTRO_A.valor >= (char) REGISTRO_B_HIJO.valor;
                                        return retorno;
                                    }
                                    default -> {
                                        retorno.tipo = null;
                                        this.texto_salida += "Error de tipos " + REGISTRO_A.tipo + " y " + REGISTRO_B_HIJO.tipo;
                                        retorno.sentencia = "error";
                                        return retorno;
                                    }
                                }
                            }

                            default -> {
                                retorno.tipo = null;
                                this.texto_salida += "Error de tipos" + REGISTRO_A.tipo + " y " + REGISTRO_B_HIJO.tipo;
                                retorno.sentencia = "error";
                                return retorno;
                            }
                        }
                    }

                    case "?" -> {
                        REGISTRO_A = interpretar(nivel, actual.hijos.get(0));

                        if (!REGISTRO_A.tipo.equals("binario")) {
                            this.texto_salida += "Error de tipos" + REGISTRO_A.tipo;
                            retorno.sentencia = "error";
                            return retorno;
                        }

                        if ((boolean) REGISTRO_A.valor) {
                            return interpretar(nivel, actual.hijos.get(1));
                        }

                        return interpretar(nivel, actual.hijos.get(2));
                    }

                    case "||" -> {
                        REGISTRO_A = interpretar(nivel, actual.hijos.get(0));
                        REGISTRO_B_HIJO = interpretar(nivel, actual.hijos.get(1));

                        if (!REGISTRO_A.tipo.equals("binario")) {
                            retorno.sentencia = "error";
                            this.texto_salida += "Error de tipos" + REGISTRO_A.tipo;
                            return retorno;
                        }

                        if (!REGISTRO_A.tipo.equals(REGISTRO_B_HIJO.tipo)) {
                            retorno.sentencia = "error";
                            this.texto_salida += "Error de tipos";
                            return retorno;
                        }

                        retorno.tipo = "binario";
                        retorno.valor = (boolean) REGISTRO_A.valor || (boolean) REGISTRO_B_HIJO.valor;
                    }

                    case "&&" -> {
                        REGISTRO_A = interpretar(nivel, actual.hijos.get(0));
                        REGISTRO_B_HIJO = interpretar(nivel, actual.hijos.get(1));

                        if (!REGISTRO_A.tipo.equals("binario")) {
                            retorno.sentencia = "error";
                            this.texto_salida += "Error de tipos " + REGISTRO_A.tipo;
                            return retorno;
                        }

                        if (!REGISTRO_A.tipo.equals(REGISTRO_B_HIJO.tipo)) {
                            retorno.sentencia = "error";
                            this.texto_salida += "Error de tipos";
                            return retorno;
                        }

                        retorno.tipo = "binario";
                        retorno.valor = (boolean) REGISTRO_A.valor && (boolean) REGISTRO_B_HIJO.valor;
                    }

                    case "!" -> {
                        REGISTRO_A = interpretar(nivel, actual.hijos.get(0));

                        if (!REGISTRO_A.tipo.equals("binario")) {
                            retorno.sentencia = "error";
                            this.texto_salida += "Error de tipos" + REGISTRO_A.tipo;
                            return retorno;
                        }

                        retorno.tipo = "binario";
                        retorno.valor = !(boolean) REGISTRO_A.valor;
                    }

                    default -> {
                        return interpretar(nivel, actual.hijos.get(0));
                    }
                }
            }

            case "instrucciones" -> {
                LogOutput REGISTRO_A;
                if (actual.hijos.size() == 2) {
                    REGISTRO_A = interpretar(nivel, actual.hijos.get(0));
                    if (REGISTRO_A.sentencia != null) {
                        return REGISTRO_A;
                    }
                    return interpretar(nivel, actual.hijos.get(1));
                } else {
                    return interpretar(nivel, actual.hijos.get(0));
                }
            }

            case "declaracion_var" -> {
                if (actual.hijos.size() == 3) {
                    LogOutput REGISTRO_A = interpretar(nivel, actual.hijos.get(2));
                    String tipo = actual.hijos.get(0).dato;

                    if (!tipo.equals(REGISTRO_A.tipo)) {
                        retorno.sentencia = "error";
                        this.texto_salida += "Error de tipos " + tipo + " pero se obtuvo " + REGISTRO_A.tipo;
                        return retorno;
                    }

                    ArrayList<String> variables = interpretar_lista_variables(actual.hijos.get(1));

                    variables.forEach((var) -> {
                        SymTable var_actual = encontrar_simbolo(nivel, var, actual.hijos.get(1).nivel);
                        if (var_actual != null) {
                            var_actual.valor = REGISTRO_A.valor;
                        } else {
                            retorno.sentencia = "error";
                            this.texto_salida += "Error de tipos" + var + "' no existe en el nivel actual";
                        }
                    });
                }
                return retorno;

            }

            case "asignacion_var" -> {
                LogOutput REGISTRO_A = interpretar(nivel, actual.hijos.get(1));
                String identificador = actual.hijos.get(0).dato;

                SymTable var_actual = encontrar_simbolo(nivel, identificador, actual.hijos.get(1).nivel);
                if (var_actual == null) {
                    this.texto_salida += "Error: '" + identificador + "' no existe en este nvl";
                    retorno.sentencia = "error";
                    return retorno;

                }

                if (!var_actual.tipo.equals(REGISTRO_A.tipo)) {
                    this.texto_salida += "Error " + var_actual.tipo + "recuperado: " + REGISTRO_A.tipo;
                    retorno.sentencia = "error";
                    return retorno;
                }

                var_actual.valor = REGISTRO_A.valor;
                return retorno;
            }

            case "actualizaciones" -> {
                String identificador = actual.hijos.get(0).dato;

                SymTable var_actual = encontrar_simbolo(nivel, identificador, actual.hijos.get(0).nivel);

                if (var_actual == null) {
                    this.texto_salida += "Error: " + identificador + "";
                    retorno.sentencia = "error";
                    return retorno;

                }

                switch (actual.dato) {
                    case "++" -> {
                        switch (var_actual.tipo) {
                            case "entero" -> {
                                var_actual.valor = (int) var_actual.valor + 1;
                            }
                            case "doble" -> {
                                var_actual.valor = (float) var_actual.valor + 1;
                            }
                            default -> {
                                retorno.sentencia = "error";
                                this.texto_salida += "Error: " + var_actual.tipo;
                            }
                        }
                    }
                    case "--" -> {
                        switch (var_actual.tipo) {
                            case "entero" -> {
                                var_actual.valor = (int) var_actual.valor - 1;
                            }
                            case "doble" -> {
                                var_actual.valor = (float) var_actual.valor - 1;
                            }
                            default -> {
                                this.texto_salida += "Error: " + var_actual.tipo;
                                retorno.sentencia = "error";
                            }
                        }
                    }
                }

                return retorno;
            }

            case "funcion_imprimir" -> {
                LogOutput REGISTRO_A = interpretar(nivel, actual.hijos.get(0));
                this.texto_salida += REGISTRO_A.valor + "\n";
                return retorno;
            }

            case "sentencia_selector" -> {
                LogOutput REGISTRO_A = interpretar(nivel, actual.hijos.get(0));

                if (actual.hijos.size() == 3) {
                    LogOutput REGISTRO_B_HIJO = interpretar(nivel, actual.hijos.get(1), REGISTRO_A);
                    if (REGISTRO_B_HIJO.sentencia == null) {
                        LogOutput r3 = interpretar(nivel, actual.hijos.get(2), REGISTRO_A);
                        switch (r3.sentencia) {
                            case "cortar" -> {
                                return retorno;
                            }
                            default -> {
                                return r3;
                            }
                        }
                    } else {
                        switch (REGISTRO_B_HIJO.sentencia) {
                            case "cortar" -> {
                                return retorno;
                            }
                            default -> {
                                return REGISTRO_B_HIJO;
                            }
                        }
                    }
                } else {
                    LogOutput REGISTRO_B_HIJO = interpretar(nivel, actual.hijos.get(1), REGISTRO_A);
                    switch (REGISTRO_B_HIJO.sentencia) {
                        case "continuar" -> {
                            return REGISTRO_B_HIJO;
                        }
                        case "retorno" -> {
                            return REGISTRO_B_HIJO;
                        }
                        default -> {
                            return retorno;
                        }
                    }

                }

            }

            case "sentencia_si" -> {
                LogOutput REGISTRO_A = interpretar(nivel, actual.hijos.get(0));

                if (!REGISTRO_A.tipo.equals("binario")) {
                    this.texto_salida += "Error: " + REGISTRO_A.tipo;
                    retorno.sentencia = "error";
                    return retorno;
                }

                if ((boolean) REGISTRO_A.valor) {
                    return interpretar(nivel, actual.hijos.get(1));
                } else {
                    if (actual.hijos.size() == 3) {
                        return interpretar(nivel, actual.hijos.get(2));
                    }
                }

                return retorno;
            }

            case "sentencia_mientras" -> {
                LogOutput REGISTRO_A = interpretar(nivel, actual.hijos.get(0));
                LogOutput REGISTRO_B_HIJO;
                if (!REGISTRO_A.tipo.equals("binario")) {
                    this.texto_salida += "Error: " + REGISTRO_A.tipo;
                    retorno.sentencia = "error";
                    return retorno;
                }

                while ((boolean) REGISTRO_A.valor) {
                    REGISTRO_B_HIJO = interpretar(nivel, actual.hijos.get(1));
                    if (REGISTRO_B_HIJO.sentencia != null) {
                        switch (REGISTRO_B_HIJO.sentencia) {
                            case "cortar" -> {
                                break;
                            }
                            case "continuar" -> {
                                continue;
                            }
                            case "retorno" -> {
                                return REGISTRO_B_HIJO;
                            }
                        }
                    }

                    REGISTRO_A = interpretar(nivel, actual.hijos.get(0));
                }

                return retorno;
            }

            case "sentencia_para" -> {
                interpretar(nivel, actual.hijos.get(0));
                LogOutput REGISTRO_A = interpretar(nivel, actual.hijos.get(1));

                if (!REGISTRO_A.tipo.equals("binario")) {
                    this.texto_salida += "Error : " + REGISTRO_A.tipo;
                    retorno.sentencia = "error";
                    return retorno;
                }

                LogOutput REGISTRO_B_HIJO;
                while ((boolean) REGISTRO_A.valor) {
                    REGISTRO_B_HIJO = interpretar(nivel, actual.hijos.get(3));
                    if (REGISTRO_B_HIJO.sentencia != null) {
                        switch (REGISTRO_B_HIJO.sentencia) {
                            case "cortar" -> {
                                break;
                            }
                            case "continuar" -> {
                                continue;
                            }
                            case "retorno" -> {
                                return REGISTRO_B_HIJO;
                            }
                        }
                    }

                    interpretar(nivel, actual.hijos.get(2));
                    REGISTRO_A = interpretar(nivel, actual.hijos.get(1));
                }
                return retorno;
            }

            case "sentencia_hacer" -> {
                LogOutput REGISTRO_B_HIJO = interpretar(nivel, actual.hijos.get(0));
                if (REGISTRO_B_HIJO.sentencia != null) {
                    switch (REGISTRO_B_HIJO.sentencia) {
                        case "cortar" -> {
                            return retorno;
                        }
                        case "retorno" -> {
                            return REGISTRO_B_HIJO;
                        }
                    }
                }

                LogOutput REGISTRO_A = interpretar(nivel, actual.hijos.get(1));

                if (!REGISTRO_A.tipo.equals("binario")) {
                    this.texto_salida += "Error: " + REGISTRO_A.tipo;
                    return retorno;
                }

                while ((boolean) REGISTRO_A.valor) {
                    REGISTRO_B_HIJO = interpretar(nivel, actual.hijos.get(0));
                    if (REGISTRO_B_HIJO.sentencia != null) {
                        switch (REGISTRO_B_HIJO.sentencia) {
                            case "cortar" -> {
                                break;
                            }
                            case "continuar" -> {
                                continue;
                            }
                            case "retorno" -> {
                                return REGISTRO_B_HIJO;
                            }
                        }
                    }

                    REGISTRO_A = interpretar(nivel, actual.hijos.get(1));
                }

                return retorno;
            }

            case "sentencia_cortar" -> {
                retorno.sentencia = "cortar";
                return retorno;
            }

            case "sentencia_continuar" -> {
                retorno.sentencia = "continuar";
                return retorno;
            }

            case "sentencia_retorno" -> {
                retorno.sentencia = "retorno";

                if (!actual.hijos.isEmpty()) {
                    LogOutput REGISTRO_A = interpretar(nivel, actual.hijos.get(0));
                    retorno.tipo = REGISTRO_A.tipo;
                    retorno.valor = REGISTRO_A.valor;
                }

                return retorno;
            }

            case "llamada" -> {
                String identificador = actual.hijos.get(0).dato;
                SymTable funcion = encontrar_funcion(nivel, identificador, actual.nivel);
                if (funcion == null) {
                    this.texto_salida += "Error " + identificador + "";
                    retorno.sentencia = "error";
                    return retorno;
                }

                if (actual.hijos.size() == 1) {
                    if (funcion.parametros == null) {
                        return interpretar(crear_nuevo_nivel(nivel, funcion.instrucciones.nivel), funcion.instrucciones);
                    } else {
                        this.texto_salida += "Error " + funcion.identificador + "  " + funcion.parametros.size() + " ";
                        retorno.sentencia = "error";
                        return retorno;
                    }
                } else {
                    ArrayList<LogOutput> valores_parametros = interpretar_parametros(nivel, actual.hijos.get(1));
                    ArrayList<SymTable> nuevo_nivel = crear_nuevo_nivel(nivel, funcion.instrucciones.nivel);
                    
                    if(valores_parametros.size()!=funcion.parametros.size()){
                        this.texto_salida += "Error  " + funcion.identificador + " tiene " + funcion.parametros.size() + " "+valores_parametros.size();
                        retorno.sentencia = "error";
                        return retorno;
                    }
                    
                    SymTable sim_actual;
                    
                    for(int i = 0; i<funcion.parametros.size();i++){
                        sim_actual = encontrar_simbolo(nuevo_nivel, funcion.parametros.get(i), funcion.instrucciones.nivel);
                        if(!sim_actual.tipo.equals(valores_parametros.get(i).tipo)){
                           this.texto_salida += "Error "+sim_actual.identificador+" "+sim_actual.tipo+",  "+valores_parametros.get(i).tipo;
                           retorno.sentencia = "error";
                           return retorno; 
                        }
                        sim_actual.valor = valores_parametros.get(i).valor;
                    }
                    
                    return interpretar(nuevo_nivel, funcion.instrucciones);
                }
            }

            case "ENTERO" -> {
                retorno.tipo = "entero";
                retorno.valor = Integer.parseInt(actual.dato);
                return retorno;
            }

            case "CADENA" -> {
                retorno.tipo = "cadena";
                retorno.valor = actual.dato;
                return retorno;
            }

            case "CHARR" -> {
                retorno.tipo = "caracter";
                retorno.valor = actual.dato.charAt(1);
                return retorno;
            }

            case "DECIMALES" -> {
                retorno.tipo = "doble";
                retorno.valor = Float.parseFloat(actual.dato);
                return retorno;
            }

            case "RESERVADA_VERDADERO" -> {
                retorno.tipo = "binario";
                retorno.valor = true;
                return retorno;
            }

            case "RESERVADA_FALSO" -> {
                retorno.tipo = "binario";
                retorno.valor = false;
                return retorno;
            }

            case "ID" -> {
                String identificador = actual.dato;

                SymTable var_actual = encontrar_simbolo(nivel, identificador, actual.nivel);

                if (var_actual == null) {
                    this.texto_salida += "Error, la variable '" + identificador + "' no existe en el nivel actual";
                    retorno.sentencia = "error";
                    return retorno;
                }

                retorno.tipo = var_actual.tipo;
                retorno.valor = var_actual.valor;
                return retorno;
            }

        }

        return retorno;
    }
    
    public void ejecutar() {
        this.texto_salida = "";
        interpretar(symTSL, comienzo_programa);
        System.out.println(this.texto_salida);
    }

    public LogOutput interpretar(ArrayList<SymTable> nivel, Nodo actual, LogOutput caso) {
        LogOutput retorno = new LogOutput();
        switch (actual.titulo) {
            case "lista_casos" -> {
                LogOutput REGISTRO_A = interpretar(nivel, actual.hijos.get(0), caso);
                if (actual.hijos.size() == 2) {
                    if (REGISTRO_A.sentencia == null) {
                        return interpretar(nivel, actual.hijos.get(1), caso);
                    }
                }
                return REGISTRO_A;
            }

            case "caso" -> {
                LogOutput REGISTRO_A = interpretar(nivel, actual.hijos.get(0));
                if (REGISTRO_A.tipo.equals(caso.tipo) && REGISTRO_A.valor.equals(caso.valor)) {
                    return interpretar(nivel, actual.hijos.get(1));
                }

            }

            case "defecto" -> {
                return interpretar(nivel, actual.hijos.get(0));
            }
        }

        return retorno;
    }

    public ArrayList<LogOutput> interpretar_parametros(ArrayList<SymTable> nivel, Nodo actual) {
        ArrayList<LogOutput> n_parametros = new ArrayList<>();
        if (actual.hijos.size() == 2) {
            n_parametros = interpretar_parametros(nivel, actual.hijos.get(0));
            n_parametros.add(interpretar(nivel, actual.hijos.get(1)));
        } else {
            n_parametros.add(interpretar(nivel, actual.hijos.get(0)));
        }
        return n_parametros;
    }

    public SymTable encontrar_simbolo(ArrayList<SymTable> symTSL, String nombre, String ambito) {
        ArrayList<SymTable> variables_posibles = new ArrayList<>();

        symTSL.forEach((simbolo) -> {
            if (simbolo.identificador.equals(nombre) && ambito.contains(simbolo.nivel)) {
                variables_posibles.add(simbolo);
            }
        });

        if (variables_posibles.isEmpty()) {
            return null;
        }

        SymTable retorno = variables_posibles.get(0);

        for (int i = 0; i < variables_posibles.size(); i++) {
            SymTable v = variables_posibles.get(i);
            if (v.nivel.length() > retorno.nivel.length()) {
                retorno = v;
            }
        }

        return retorno;
    }

    public SymTable encontrar_funcion(ArrayList<SymTable> symTSL, String nombre, String ambito) {
        ArrayList<SymTable> variables_posibles = new ArrayList<>();

        symTSL.forEach((simbolo) -> {
            if (simbolo.identificador.equals(nombre) && ambito.contains(simbolo.nivel) && "funcionmetodo".contains(simbolo.rol)) {
                variables_posibles.add(simbolo);
            }
        });

        if (variables_posibles.isEmpty()) {
            return null;
        }

        SymTable retorno = variables_posibles.get(0);

        for (int i = 0; i < variables_posibles.size(); i++) {
            SymTable v = variables_posibles.get(i);
            if (v.nivel.length() > retorno.nivel.length()) {
                retorno = v;
            }
        }

        return retorno;
    }

    public ArrayList<SymTable> crear_nuevo_nivel(ArrayList<SymTable> nivel_actual, String ambito) {
        ArrayList<SymTable> nuevo_nivel = new ArrayList<>();

        nivel_actual.forEach((s) -> {
            if (s.nivel.contains(ambito)) {
                SymTable copiado = new SymTable();
                copiado.nivel = s.nivel;
                copiado.identificador = s.identificador;
                copiado.instrucciones = s.instrucciones;
                copiado.parametros = s.parametros;
                copiado.rol = s.rol;
                copiado.tipo = s.tipo;
                copiado.valor = s.valor;
                nuevo_nivel.add(copiado);
            } else {
                nuevo_nivel.add(s);
            }
        });
        return nuevo_nivel;
    }
}
