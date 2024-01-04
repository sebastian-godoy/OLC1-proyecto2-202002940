package Analizadores;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Interpreter {

    public Nodo avl;
    public Nodo comienzo_programa;
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
    if (actual.titulo.equals("declaracion_metodo")) {
        if (actual.hijos.size() == 3) 
        {
            actual.hijos.get(0).entorno = actual.entorno;
            actual.hijos.get(1).entorno = actual.entorno + "_" + actual.hashCode();
            actual.hijos.get(2).entorno = actual.entorno + "_" + actual.hashCode();
        } 
        else 
        {
            actual.hijos.get(0).entorno = actual.entorno;
            actual.hijos.get(1).entorno = actual.entorno + "_" + actual.hashCode();
        }
    } 
    else if (actual.titulo.equals("declaracion_funcion")) 
    {
        if (actual.hijos.size() == 4) {
            actual.hijos.get(1).entorno = actual.entorno;
            actual.hijos.get(2).entorno = actual.entorno + "_" + actual.hashCode();
            actual.hijos.get(3).entorno = actual.entorno + "_" + actual.hashCode();
        } 
        else {
           // En caso de entrar
            actual.hijos.get(1).entorno = actual.entorno;
            actual.hijos.get(2).entorno = actual.entorno + "_" + actual.hashCode();
        }
    } 
    else if (actual.titulo.equals("sentencia_si") || actual.titulo.equals("sentencia_selector")) 
    {
        if (actual.hijos.size() == 3) 
        { 
            actual.hijos.get(0).entorno = actual.entorno;
            actual.hijos.get(1).entorno = actual.entorno + "_" + actual.hashCode();
            actual.hijos.get(2).entorno = actual.entorno + "_" + actual.hashCode();
        } 
        else 
        {
            actual.hijos.get(0).entorno = actual.entorno;
            actual.hijos.get(1).entorno = actual.entorno + "_" + actual.hashCode();
        }
    } 
    else if (actual.titulo.equals("sentencia_mientras")) 
    {
        actual.hijos.get(0).entorno = actual.entorno;
        actual.hijos.get(1).entorno = actual.entorno + "_" + actual.hashCode();
    } else if (actual.titulo.equals("sentencia_para") || actual.titulo.equals("sentencia_hacer")) {
        actual.hijos.forEach((x) -> {
            x.entorno = actual.entorno + "_" + actual.hashCode();
        });
    } else 
    {
        actual.hijos.forEach((x) -> {
            x.entorno = actual.entorno;
        });
    }

    SymTable n_simbolo = new SymTable();
    String tipo;
    String rol;
    String entorno;

    if (actual.titulo.equals("declaracion_metodo")) {
        n_simbolo.identificador = actual.hijos.get(0).dato;
        n_simbolo.rol = "metodo";
        n_simbolo.entorno = actual.entorno;
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
        n_simbolo.entorno = actual.entorno;

        if (actual.hijos.size() == 2) {
            n_simbolo.identificador = actual.hijos.get(1).hijos.get(1).dato;
            n_simbolo.tipo = actual.hijos.get(1).hijos.get(0).dato;
        } else {
            n_simbolo.identificador = actual.hijos.get(0).hijos.get(1).dato;
            n_simbolo.tipo = actual.hijos.get(0).hijos.get(0).dato;
        }

        switch (n_simbolo.tipo) {
            case "entero" -> n_simbolo.valor = 0;
            case "doble" -> n_simbolo.valor = (float) 0.0;
            case "binario" -> n_simbolo.valor = true;
            case "caracter" -> n_simbolo.valor = '\u0000';
            case "cadena" -> n_simbolo.valor = "";
        }
        this.symTSL.add(n_simbolo);
    } else if (actual.titulo.equals("declaracion_var")) {
        tipo = actual.hijos.get(0).dato;
        rol = "variable";
        entorno = actual.entorno;

        ArrayList<String> lista_vars = interpretar_lista_variables(actual.hijos.get(1));

        lista_vars.forEach((identificador) -> {
            SymTable simbolo_variable = new SymTable();
            simbolo_variable.tipo = tipo;
            simbolo_variable.rol = rol;
            simbolo_variable.entorno = entorno;
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
        n_simbolo.entorno = actual.entorno;
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
        System.out.println("|identificador|rol|tipo|entorno|valor|parametros|instrucciones|\n");
        this.symTSL.forEach((s) -> {
            System.out.print("|" + s.identificador + "|" + s.rol + "|" + s.tipo + "|" + s.entorno + "|");
            if (s.valor != null) {
                switch (s.tipo) {
                    case "entero" ->
                        System.out.print((int) s.valor + "|");
                    case "doble" ->
                        System.out.print((float) s.valor + "|");
                    case "binario" ->
                        System.out.print((boolean) s.valor + "|");
                    case "caracter" ->
                        System.out.print((char) s.valor + "|");
                    case "cadena" ->
                        System.out.print("\"" + (String) s.valor + "\"|");
                }
            } else {
                System.out.print(" --- |");
            }
            if (s.parametros != null) {
                s.parametros.forEach((param) -> {
                    System.out.print(param + " ");
                });
                System.out.print("|");
            } else {
                System.out.print(" --- |");
            }

            if (s.instrucciones != null) {
                System.out.print(s.instrucciones.hashCode() + "|\n");
            } else {
                System.out.print(" --- |\n");
            }

        });
    }

    public LogOutput interpretar(ArrayList<SymTable> entorno, Nodo actual) {
        LogOutput retorno = new LogOutput();
        switch (actual.titulo) {
            case "expresion" -> {
                LogOutput r1;
                LogOutput r2;
                switch (actual.dato) {
                    case "+" -> {
                        r1 = interpretar(entorno, actual.hijos.get(0));
                        r2 = interpretar(entorno, actual.hijos.get(1));

                        if (!r1.tipo.equals(r2.tipo)) {
                            this.texto_salida += "Suma de tipos diferentes: " + r1.tipo + " y " + r2.tipo;
                            retorno.sentencia = "ERROR FATAL";
                            return retorno;
                        }

                        switch (r1.tipo) {
                            case "entero" -> {
                                retorno.tipo = "entero";
                                retorno.valor = (int) r1.valor + (int) r2.valor;
                                return retorno;
                            }

                            case "doble" -> {
                                retorno.tipo = "doble";
                                retorno.valor = (float) r1.valor + (float) r2.valor;
                                return retorno;
                            }

                            case "binario" -> {
                                this.texto_salida += "Error de tipos en la suma, se intento sumar binario";
                                retorno.sentencia = "error";
                                return retorno;
                            }

                            case "caracter" -> {
                                retorno.tipo = "cadena";
                                retorno.valor = "" + (char) r1.valor + (char) r2.valor;
                                return retorno;
                            }
                            case "cadena" -> {
                                retorno.tipo = "cadena";
                                retorno.valor = (String) r1.valor + (String) r2.valor;
                                return retorno;
                            }
                        }
                    }

                    case "-" -> {
                        r1 = interpretar(entorno, actual.hijos.get(0));
                        if (actual.hijos.size() == 2) {
                            r2 = interpretar(entorno, actual.hijos.get(1));

                            if (!r1.tipo.equals(r2.tipo)) {
                                this.texto_salida += "Error de tipos en la resta, se intento restar tipos diferentes: " + r1.tipo + " y " + r2.tipo;
                                retorno.sentencia = "error";
                                return retorno;
                            }

                            switch (r1.tipo) {
                                case "entero" -> {
                                    retorno.tipo = "entero";
                                    retorno.valor = (int) r1.valor - (int) r2.valor;
                                    return retorno;
                                }

                                case "doble" -> {
                                    retorno.tipo = "doble";
                                    retorno.valor = (float) r1.valor - (float) r2.valor;
                                    return retorno;
                                }

                                default -> {
                                    this.texto_salida += "Error de tipos en la resta, se intento restar " + r1.tipo;
                                    retorno.sentencia = "error";
                                    return retorno;
                                }
                            }
                        }
                        switch (r1.tipo) {
                            case "entero" -> {
                                retorno.tipo = "entero";
                                retorno.valor = -(int) r1.valor;
                                return retorno;
                            }

                            case "doble" -> {
                                retorno.tipo = "doble";
                                retorno.valor = -(float) r1.valor;
                                return retorno;
                            }

                            default -> {
                                this.texto_salida += "Error de tipos en la resta, se intento restar " + r1.tipo;
                                retorno.sentencia = "error";
                                return retorno;
                            }
                        }
                    }

                    case "*" -> {
                        r1 = interpretar(entorno, actual.hijos.get(0));
                        r2 = interpretar(entorno, actual.hijos.get(1));

                        if (!r1.tipo.equals(r2.tipo)) {
                            this.texto_salida += "Error de tipos en la multiplicacion, se intento multiplicar tipos diferentes: " + r1.tipo + " y " + r2.tipo;
                            retorno.sentencia = "error";
                            return retorno;
                        }

                        switch (r1.tipo) {
                            case "entero" -> {
                                retorno.tipo = "entero";
                                retorno.valor = (int) r1.valor * (int) r2.valor;
                                return retorno;
                            }

                            case "doble" -> {
                                retorno.tipo = "doble";
                                retorno.valor = (float) r1.valor * (float) r2.valor;
                                return retorno;
                            }

                            default -> {
                                this.texto_salida += "Error de tipos en la multiplicacion, se intento multiplicar " + r1.tipo;
                                retorno.sentencia = "error";
                                return retorno;
                            }
                        }
                    }

                    case "/" -> {
                        r1 = interpretar(entorno, actual.hijos.get(0));
                        r2 = interpretar(entorno, actual.hijos.get(1));

                        if (!r1.tipo.equals(r2.tipo)) {
                            this.texto_salida += "Error de tipos en la division, se intento dividir tipos diferentes: " + r1.tipo + " y " + r2.tipo;
                            retorno.sentencia = "error";
                            return retorno;
                        }

                        switch (r1.tipo) {
                            case "entero" -> {
                                retorno.tipo = "entero";
                                retorno.valor = (int) r1.valor / (int) r2.valor;
                                return retorno;
                            }

                            case "doble" -> {
                                retorno.tipo = "doble";
                                retorno.valor = (float) r1.valor / (float) r2.valor;
                                return retorno;
                            }

                            default -> {
                                this.texto_salida += "Error de tipos en la division, se intento dividir " + r1.tipo;
                                retorno.sentencia = "error";
                                return retorno;
                            }
                        }
                    }

                    case "^" -> {
                        r1 = interpretar(entorno, actual.hijos.get(0));
                        r2 = interpretar(entorno, actual.hijos.get(1));

                        if (!r1.tipo.equals(r2.tipo)) {
                            this.texto_salida += "Error de tipos en la potenciacion, se intento potenciar tipos diferentes: " + r1.tipo + " y " + r2.tipo;
                            retorno.sentencia = "error";
                            return retorno;
                        }

                        switch (r1.tipo) {
                            case "entero" -> {
                                retorno.tipo = "entero";
                                retorno.valor = (int) Math.pow((int) r1.valor, (int) r2.valor);
                                return retorno;
                            }

                            case "doble" -> {
                                retorno.tipo = "doble";
                                retorno.valor = (float) Math.pow((float) r1.valor, (float) r2.valor);
                                return retorno;
                            }

                            default -> {
                                this.texto_salida += "Error de tipos en la potenciacion, se intento potenciar " + r1.tipo;
                                retorno.sentencia = "error";
                                return retorno;
                            }
                        }
                    }

                    case "%" -> {
                        r1 = interpretar(entorno, actual.hijos.get(0));
                        r2 = interpretar(entorno, actual.hijos.get(1));

                        if (!r1.tipo.equals(r2.tipo)) {
                            this.texto_salida += "Error de tipos en el modulo, se intento operar tipos diferentes: " + r1.tipo + " y " + r2.tipo;
                            retorno.sentencia = "error";
                            return retorno;
                        }

                        switch (r1.tipo) {
                            case "entero" -> {
                                retorno.tipo = "entero";
                                retorno.valor = (int) r1.valor % (int) r2.valor;
                                return retorno;
                            }

                            case "doble" -> {
                                retorno.tipo = "doble";
                                retorno.valor = (float) r1.valor % (float) r2.valor;
                                return retorno;
                            }

                            default -> {
                                this.texto_salida += "Error de tipos en la modulo, se intento operar " + r1.tipo;
                                retorno.sentencia = "error";
                                return retorno;
                            }
                        }
                    }

                    case "==" -> {
                        r1 = interpretar(entorno, actual.hijos.get(0));
                        r2 = interpretar(entorno, actual.hijos.get(1));

                        retorno.tipo = "binario";
                        retorno.valor = r1.valor.equals(r2.valor);
                        return retorno;
                    }

                    case "!=" -> {
                        r1 = interpretar(entorno, actual.hijos.get(0));
                        r2 = interpretar(entorno, actual.hijos.get(1));

                        retorno.tipo = "binario";
                        retorno.valor = !(r1.valor.equals(r2.valor));
                        return retorno;
                    }

                    case "<" -> {
                        r1 = interpretar(entorno, actual.hijos.get(0));
                        r2 = interpretar(entorno, actual.hijos.get(1));
                        retorno.tipo = "binario";
                        switch (r1.tipo) {
                            case "entero" -> {
                                switch (r2.tipo) {
                                    case "entero" -> {
                                        retorno.valor = (int) r1.valor < (int) r2.valor;
                                        return retorno;
                                    }
                                    case "doble" -> {
                                        retorno.valor = (int) r1.valor < (float) r2.valor;
                                        return retorno;
                                    }
                                    case "caracter" -> {
                                        retorno.valor = (int) r1.valor < (char) r2.valor;
                                        return retorno;
                                    }
                                    default -> {
                                        retorno.tipo = null;
                                        this.texto_salida += "Error de tipos en la operacion relacional, se intento operar " + r1.tipo + " y " + r2.tipo;
                                        retorno.sentencia = "error";
                                        return retorno;
                                    }
                                }
                            }

                            case "doble" -> {
                                switch (r2.tipo) {
                                    case "entero" -> {
                                        retorno.valor = (float) r1.valor < (int) r2.valor;
                                        return retorno;
                                    }
                                    case "doble" -> {
                                        retorno.valor = (float) r1.valor < (float) r2.valor;
                                        return retorno;
                                    }
                                    case "caracter" -> {
                                        retorno.valor = (float) r1.valor < (char) r2.valor;
                                        return retorno;
                                    }
                                    default -> {
                                        retorno.tipo = null;
                                        this.texto_salida += "Error de tipos en la operacion relacional, se intento operar " + r1.tipo + " y " + r2.tipo;
                                        retorno.sentencia = "error";
                                        return retorno;
                                    }
                                }
                            }

                            case "caracter" -> {
                                switch (r2.tipo) {
                                    case "entero" -> {
                                        retorno.valor = (char) r1.valor < (int) r2.valor;
                                        return retorno;
                                    }
                                    case "doble" -> {
                                        retorno.valor = (char) r1.valor < (float) r2.valor;
                                        return retorno;
                                    }
                                    case "caracter" -> {
                                        retorno.valor = (char) r1.valor < (char) r2.valor;
                                        return retorno;
                                    }
                                    default -> {
                                        retorno.tipo = null;
                                        this.texto_salida += "Error de tipos en la operacion relacional, se intento operar " + r1.tipo + " y " + r2.tipo;
                                        retorno.sentencia = "error";
                                        return retorno;
                                    }
                                }
                            }

                            default -> {
                                retorno.tipo = null;
                                this.texto_salida += "Error de tipos en la operacion relacional, se intento operar " + r1.tipo + " y " + r2.tipo;
                                retorno.sentencia = "error";
                                return retorno;
                            }
                        }
                    }

                    case "<=" -> {
                        r1 = interpretar(entorno, actual.hijos.get(0));
                        r2 = interpretar(entorno, actual.hijos.get(1));
                        retorno.tipo = "binario";
                        switch (r1.tipo) {
                            case "entero" -> {
                                switch (r2.tipo) {
                                    case "entero" -> {
                                        retorno.valor = (int) r1.valor <= (int) r2.valor;
                                        return retorno;
                                    }
                                    case "doble" -> {
                                        retorno.valor = (int) r1.valor <= (float) r2.valor;
                                        return retorno;
                                    }
                                    case "caracter" -> {
                                        retorno.valor = (int) r1.valor <= (char) r2.valor;
                                        return retorno;
                                    }
                                    default -> {
                                        retorno.tipo = null;
                                        this.texto_salida += "Error de tipos en la operacion relacional, se intento operar " + r1.tipo + " y " + r2.tipo;
                                        retorno.sentencia = "error";
                                        return retorno;
                                    }
                                }
                            }

                            case "doble" -> {
                                switch (r2.tipo) {
                                    case "entero" -> {
                                        retorno.valor = (float) r1.valor <= (int) r2.valor;
                                        return retorno;
                                    }
                                    case "doble" -> {
                                        retorno.valor = (float) r1.valor <= (float) r2.valor;
                                        return retorno;
                                    }
                                    case "caracter" -> {
                                        retorno.valor = (float) r1.valor <= (char) r2.valor;
                                        return retorno;
                                    }
                                    default -> {
                                        retorno.tipo = null;
                                        this.texto_salida += "Error de tipos en la operacion relacional, se intento operar " + r1.tipo + " y " + r2.tipo;
                                        retorno.sentencia = "error";
                                        return retorno;
                                    }
                                }
                            }

                            case "caracter" -> {
                                switch (r2.tipo) {
                                    case "entero" -> {
                                        retorno.valor = (char) r1.valor <= (int) r2.valor;
                                        return retorno;
                                    }
                                    case "doble" -> {
                                        retorno.valor = (char) r1.valor <= (float) r2.valor;
                                        return retorno;
                                    }
                                    case "caracter" -> {
                                        retorno.valor = (char) r1.valor <= (char) r2.valor;
                                        return retorno;
                                    }
                                    default -> {
                                        retorno.tipo = null;
                                        this.texto_salida += "Error de tipos en la operacion relacional, se intento operar " + r1.tipo + " y " + r2.tipo;
                                        retorno.sentencia = "error";
                                        return retorno;
                                    }
                                }
                            }

                            default -> {
                                retorno.tipo = null;
                                this.texto_salida += "Error de tipos en la operacion relacional, se intento operar " + r1.tipo + " y " + r2.tipo;
                                retorno.sentencia = "error";
                                return retorno;
                            }
                        }
                    }

                    case ">" -> {
                        r1 = interpretar(entorno, actual.hijos.get(0));
                        r2 = interpretar(entorno, actual.hijos.get(1));
                        retorno.tipo = "binario";
                        switch (r1.tipo) {
                            case "entero" -> {
                                switch (r2.tipo) {
                                    case "entero" -> {
                                        retorno.valor = (int) r1.valor > (int) r2.valor;
                                        return retorno;
                                    }
                                    case "doble" -> {
                                        retorno.valor = (int) r1.valor > (float) r2.valor;
                                        return retorno;
                                    }
                                    case "caracter" -> {
                                        retorno.valor = (int) r1.valor > (char) r2.valor;
                                        return retorno;
                                    }
                                    default -> {
                                        retorno.tipo = null;
                                        this.texto_salida += "Error de tipos en la operacion relacional, se intento operar " + r1.tipo + " y " + r2.tipo;
                                        retorno.sentencia = "error";
                                        return retorno;
                                    }
                                }
                            }

                            case "doble" -> {
                                switch (r2.tipo) {
                                    case "entero" -> {
                                        retorno.valor = (float) r1.valor > (int) r2.valor;
                                        return retorno;
                                    }
                                    case "doble" -> {
                                        retorno.valor = (float) r1.valor > (float) r2.valor;
                                        return retorno;
                                    }
                                    case "caracter" -> {
                                        retorno.valor = (float) r1.valor > (char) r2.valor;
                                        return retorno;
                                    }
                                    default -> {
                                        retorno.tipo = null;
                                        this.texto_salida += "Error de tipos en la operacion relacional, se intento operar " + r1.tipo + " y " + r2.tipo;
                                        retorno.sentencia = "error";
                                        return retorno;
                                    }
                                }
                            }

                            case "caracter" -> {
                                switch (r2.tipo) {
                                    case "entero" -> {
                                        retorno.valor = (char) r1.valor > (int) r2.valor;
                                        return retorno;
                                    }
                                    case "doble" -> {
                                        retorno.valor = (char) r1.valor > (float) r2.valor;
                                        return retorno;
                                    }
                                    case "caracter" -> {
                                        retorno.valor = (char) r1.valor > (char) r2.valor;
                                        return retorno;
                                    }
                                    default -> {
                                        retorno.tipo = null;
                                        this.texto_salida += "Error de tipos en la operacion relacional, se intento operar " + r1.tipo + " y " + r2.tipo;
                                        retorno.sentencia = "error";
                                        return retorno;
                                    }
                                }
                            }

                            default -> {
                                retorno.tipo = null;
                                this.texto_salida += "Error de tipos en la operacion relacional, se intento operar " + r1.tipo + " y " + r2.tipo;
                                retorno.sentencia = "error";
                                return retorno;
                            }
                        }
                    }

                    case ">=" -> {
                        r1 = interpretar(entorno, actual.hijos.get(0));
                        r2 = interpretar(entorno, actual.hijos.get(1));
                        retorno.tipo = "binario";
                        switch (r1.tipo) {
                            case "entero" -> {
                                switch (r2.tipo) {
                                    case "entero" -> {
                                        retorno.valor = (int) r1.valor >= (int) r2.valor;
                                        return retorno;
                                    }
                                    case "doble" -> {
                                        retorno.valor = (int) r1.valor >= (float) r2.valor;
                                        return retorno;
                                    }
                                    case "caracter" -> {
                                        retorno.valor = (int) r1.valor >= (char) r2.valor;
                                        return retorno;
                                    }
                                    default -> {
                                        retorno.tipo = null;
                                        this.texto_salida += "Error de tipos en la operacion relacional, se intento operar " + r1.tipo + " y " + r2.tipo;
                                        retorno.sentencia = "error";
                                        return retorno;
                                    }
                                }
                            }

                            case "doble" -> {
                                switch (r2.tipo) {
                                    case "entero" -> {
                                        retorno.valor = (float) r1.valor >= (int) r2.valor;
                                        return retorno;
                                    }
                                    case "doble" -> {
                                        retorno.valor = (float) r1.valor >= (float) r2.valor;
                                        return retorno;
                                    }
                                    case "caracter" -> {
                                        retorno.valor = (float) r1.valor >= (char) r2.valor;
                                        return retorno;
                                    }
                                    default -> {
                                        retorno.tipo = null;
                                        this.texto_salida += "Error de tipos en la operacion relacional, se intento operar " + r1.tipo + " y " + r2.tipo;
                                        retorno.sentencia = "error";
                                        return retorno;
                                    }
                                }
                            }

                            case "caracter" -> {
                                switch (r2.tipo) {
                                    case "entero" -> {
                                        retorno.valor = (char) r1.valor >= (int) r2.valor;
                                        return retorno;
                                    }
                                    case "doble" -> {
                                        retorno.valor = (char) r1.valor >= (float) r2.valor;
                                        return retorno;
                                    }
                                    case "caracter" -> {
                                        retorno.valor = (char) r1.valor >= (char) r2.valor;
                                        return retorno;
                                    }
                                    default -> {
                                        retorno.tipo = null;
                                        this.texto_salida += "Error de tipos en la operacion relacional, se intento operar " + r1.tipo + " y " + r2.tipo;
                                        retorno.sentencia = "error";
                                        return retorno;
                                    }
                                }
                            }

                            default -> {
                                retorno.tipo = null;
                                this.texto_salida += "Error de tipos en la operacion relacional, se intento operar " + r1.tipo + " y " + r2.tipo;
                                retorno.sentencia = "error";
                                return retorno;
                            }
                        }
                    }

                    case "?" -> {
                        r1 = interpretar(entorno, actual.hijos.get(0));

                        if (!r1.tipo.equals("binario")) {
                            this.texto_salida += "Error de tipos en la condicion ternaria, se esperaba binario pero se obtuvo " + r1.tipo;
                            retorno.sentencia = "error";
                            return retorno;
                        }

                        if ((boolean) r1.valor) {
                            return interpretar(entorno, actual.hijos.get(1));
                        }

                        return interpretar(entorno, actual.hijos.get(2));
                    }

                    case "||" -> {
                        r1 = interpretar(entorno, actual.hijos.get(0));
                        r2 = interpretar(entorno, actual.hijos.get(1));

                        if (!r1.tipo.equals("binario")) {
                            retorno.sentencia = "error";
                            this.texto_salida += "Error de tipos en la operacion logica, se esperaba binario pero se obtuvo " + r1.tipo;
                            return retorno;
                        }

                        if (!r1.tipo.equals(r2.tipo)) {
                            retorno.sentencia = "error";
                            this.texto_salida += "Error de tipos en la operacion logica, solo se puede hacer una operacion entre dos binarios";
                            return retorno;
                        }

                        retorno.tipo = "binario";
                        retorno.valor = (boolean) r1.valor || (boolean) r2.valor;
                    }

                    case "&&" -> {
                        r1 = interpretar(entorno, actual.hijos.get(0));
                        r2 = interpretar(entorno, actual.hijos.get(1));

                        if (!r1.tipo.equals("binario")) {
                            retorno.sentencia = "error";
                            this.texto_salida += "Error de tipos en la operacion logica, se esperaba binario pero se obtuvo " + r1.tipo;
                            return retorno;
                        }

                        if (!r1.tipo.equals(r2.tipo)) {
                            retorno.sentencia = "error";
                            this.texto_salida += "Error de tipos en la operacion logica, solo se puede hacer una operacion entre dos binarios";
                            return retorno;
                        }

                        retorno.tipo = "binario";
                        retorno.valor = (boolean) r1.valor && (boolean) r2.valor;
                    }

                    case "!" -> {
                        r1 = interpretar(entorno, actual.hijos.get(0));

                        if (!r1.tipo.equals("binario")) {
                            retorno.sentencia = "error";
                            this.texto_salida += "Error de tipos en la operacion logica, se esperaba binario pero se obtuvo " + r1.tipo;
                            return retorno;
                        }

                        retorno.tipo = "binario";
                        retorno.valor = !(boolean) r1.valor;
                    }

                    default -> {
                        return interpretar(entorno, actual.hijos.get(0));
                    }
                }
            }

            case "instrucciones" -> {
                LogOutput r1;
                if (actual.hijos.size() == 2) {
                    r1 = interpretar(entorno, actual.hijos.get(0));
                    if (r1.sentencia != null) {
                        return r1;
                    }
                    return interpretar(entorno, actual.hijos.get(1));
                } else {
                    return interpretar(entorno, actual.hijos.get(0));
                }
            }

            case "declaracion_var" -> {
                if (actual.hijos.size() == 3) {
                    LogOutput r1 = interpretar(entorno, actual.hijos.get(2));
                    String tipo = actual.hijos.get(0).dato;

                    if (!tipo.equals(r1.tipo)) {
                        retorno.sentencia = "error";
                        this.texto_salida += "Error de tipos en la declaracion de variables, se esperaba " + tipo + " pero se obtuvo " + r1.tipo;
                        return retorno;
                    }

                    ArrayList<String> variables = interpretar_lista_variables(actual.hijos.get(1));

                    variables.forEach((var) -> {
                        SymTable var_actual = encontrar_simbolo(entorno, var, actual.hijos.get(1).entorno);
                        if (var_actual != null) {
                            var_actual.valor = r1.valor;
                        } else {
                            retorno.sentencia = "error";
                            this.texto_salida += "Error de tipos en la declaracion de variables, la variable '" + var + "' no existe en el entorno actual";
                        }
                    });
                }
                return retorno;

            }

            case "asignacion_var" -> {
                LogOutput r1 = interpretar(entorno, actual.hijos.get(1));
                String identificador = actual.hijos.get(0).dato;

                SymTable var_actual = encontrar_simbolo(entorno, identificador, actual.hijos.get(1).entorno);
                if (var_actual == null) {
                    this.texto_salida += "Error en la asignacion de variable, la variable '" + identificador + "' no existe en el entorno actual";
                    retorno.sentencia = "error";
                    return retorno;

                }

                if (!var_actual.tipo.equals(r1.tipo)) {
                    this.texto_salida += "Error de tipos en la asignacion de variable, se esperaba " + var_actual.tipo + " pero se obtuvo " + r1.tipo;
                    retorno.sentencia = "error";
                    return retorno;
                }

                var_actual.valor = r1.valor;
                return retorno;
            }

            case "actualizaciones" -> {
                String identificador = actual.hijos.get(0).dato;

                SymTable var_actual = encontrar_simbolo(entorno, identificador, actual.hijos.get(0).entorno);

                if (var_actual == null) {
                    this.texto_salida += "Error en la actualizacion de variable, la variable '" + identificador + "' no existe en el entorno actual";
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
                                this.texto_salida += "Error en la actualizacion de variable, se esperaba un numero, pero se obtuvo: " + var_actual.tipo;
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
                                this.texto_salida += "Error en la actualizacion de variable, se esperaba un numero, pero se obtuvo: " + var_actual.tipo;
                                retorno.sentencia = "error";
                            }
                        }
                    }
                }

                return retorno;
            }

            case "funcion_imprimir" -> {
                LogOutput r1 = interpretar(entorno, actual.hijos.get(0));
                this.texto_salida += r1.valor + "\n";
                return retorno;
            }

            case "sentencia_selector" -> {
                LogOutput r1 = interpretar(entorno, actual.hijos.get(0));

                if (actual.hijos.size() == 3) {
                    LogOutput r2 = interpretar(entorno, actual.hijos.get(1), r1);
                    if (r2.sentencia == null) {
                        LogOutput r3 = interpretar(entorno, actual.hijos.get(2), r1);
                        switch (r3.sentencia) {
                            case "cortar" -> {
                                return retorno;
                            }
                            default -> {
                                return r3;
                            }
                        }
                    } else {
                        switch (r2.sentencia) {
                            case "cortar" -> {
                                return retorno;
                            }
                            default -> {
                                return r2;
                            }
                        }
                    }
                } else {
                    LogOutput r2 = interpretar(entorno, actual.hijos.get(1), r1);
                    switch (r2.sentencia) {
                        case "continuar" -> {
                            return r2;
                        }
                        case "retorno" -> {
                            return r2;
                        }
                        default -> {
                            return retorno;
                        }
                    }

                }

            }

            case "sentencia_si" -> {
                LogOutput r1 = interpretar(entorno, actual.hijos.get(0));

                if (!r1.tipo.equals("binario")) {
                    this.texto_salida += "Error en la condicion de si, se esperaba un binario, pero se obtuvo: " + r1.tipo;
                    retorno.sentencia = "error";
                    return retorno;
                }

                if ((boolean) r1.valor) {
                    return interpretar(entorno, actual.hijos.get(1));
                } else {
                    if (actual.hijos.size() == 3) {
                        return interpretar(entorno, actual.hijos.get(2));
                    }
                }

                return retorno;
            }

            case "sentencia_mientras" -> {
                LogOutput r1 = interpretar(entorno, actual.hijos.get(0));
                LogOutput r2;
                if (!r1.tipo.equals("binario")) {
                    this.texto_salida += "Error en la condicion de mientras, se esperaba un binario, pero se obtuvo: " + r1.tipo;
                    retorno.sentencia = "error";
                    return retorno;
                }

                while ((boolean) r1.valor) {
                    r2 = interpretar(entorno, actual.hijos.get(1));
                    if (r2.sentencia != null) {
                        switch (r2.sentencia) {
                            case "cortar" -> {
                                break;
                            }
                            case "continuar" -> {
                                continue;
                            }
                            case "retorno" -> {
                                return r2;
                            }
                        }
                    }

                    r1 = interpretar(entorno, actual.hijos.get(0));
                }

                return retorno;
            }

            case "sentencia_para" -> {
                interpretar(entorno, actual.hijos.get(0));
                LogOutput r1 = interpretar(entorno, actual.hijos.get(1));

                if (!r1.tipo.equals("binario")) {
                    this.texto_salida += "Error en la condicion de mientras, se esperaba un binario, pero se obtuvo: " + r1.tipo;
                    retorno.sentencia = "error";
                    return retorno;
                }

                LogOutput r2;
                while ((boolean) r1.valor) {
                    r2 = interpretar(entorno, actual.hijos.get(3));
                    if (r2.sentencia != null) {
                        switch (r2.sentencia) {
                            case "cortar" -> {
                                break;
                            }
                            case "continuar" -> {
                                continue;
                            }
                            case "retorno" -> {
                                return r2;
                            }
                        }
                    }

                    interpretar(entorno, actual.hijos.get(2));
                    r1 = interpretar(entorno, actual.hijos.get(1));
                }
                return retorno;
            }

            case "sentencia_hacer" -> {
                LogOutput r2 = interpretar(entorno, actual.hijos.get(0));
                if (r2.sentencia != null) {
                    switch (r2.sentencia) {
                        case "cortar" -> {
                            return retorno;
                        }
                        case "retorno" -> {
                            return r2;
                        }
                    }
                }

                LogOutput r1 = interpretar(entorno, actual.hijos.get(1));

                if (!r1.tipo.equals("binario")) {
                    this.texto_salida += "Error en la condicion de mientras, se esperaba un binario, pero se obtuvo: " + r1.tipo;
                    return retorno;
                }

                while ((boolean) r1.valor) {
                    r2 = interpretar(entorno, actual.hijos.get(0));
                    if (r2.sentencia != null) {
                        switch (r2.sentencia) {
                            case "cortar" -> {
                                break;
                            }
                            case "continuar" -> {
                                continue;
                            }
                            case "retorno" -> {
                                return r2;
                            }
                        }
                    }

                    r1 = interpretar(entorno, actual.hijos.get(1));
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
                    LogOutput r1 = interpretar(entorno, actual.hijos.get(0));
                    retorno.tipo = r1.tipo;
                    retorno.valor = r1.valor;
                }

                return retorno;
            }

            case "llamada" -> {
                String identificador = actual.hijos.get(0).dato;
                SymTable funcion = encontrar_funcion(entorno, identificador, actual.entorno);
                if (funcion == null) {
                    this.texto_salida += "Error, la funcion/metodo '" + identificador + "' no existe en el entorno actual";
                    retorno.sentencia = "error";
                    return retorno;
                }

                if (actual.hijos.size() == 1) {
                    if (funcion.parametros == null) {
                        return interpretar(crear_nuevo_entorno(entorno, funcion.instrucciones.entorno), funcion.instrucciones);
                    } else {
                        this.texto_salida += "Error en la llamada, la funcion/metodo " + funcion.identificador + " tiene " + funcion.parametros.size() + " parametros, pero se recibio 0";
                        retorno.sentencia = "error";
                        return retorno;
                    }
                } else {
                    ArrayList<LogOutput> valores_parametros = interpretar_parametros(entorno, actual.hijos.get(1));
                    ArrayList<SymTable> nuevo_entorno = crear_nuevo_entorno(entorno, funcion.instrucciones.entorno);
                    
                    if(valores_parametros.size()!=funcion.parametros.size()){
                        this.texto_salida += "Error en la llamada, la funcion/metodo " + funcion.identificador + " tiene " + funcion.parametros.size() + " parametros, pero se recibio"+valores_parametros.size();
                        retorno.sentencia = "error";
                        return retorno;
                    }
                    
                    SymTable sim_actual;
                    
                    for(int i = 0; i<funcion.parametros.size();i++){
                        sim_actual = encontrar_simbolo(nuevo_entorno, funcion.parametros.get(i), funcion.instrucciones.entorno);
                        if(!sim_actual.tipo.equals(valores_parametros.get(i).tipo)){
                           this.texto_salida += "Error en la llamada, el parametro "+sim_actual.identificador+" es de tipo "+sim_actual.tipo+", pero se recibio "+valores_parametros.get(i).tipo;
                           retorno.sentencia = "error";
                           return retorno; 
                        }
                        sim_actual.valor = valores_parametros.get(i).valor;
                    }
                    
                    return interpretar(nuevo_entorno, funcion.instrucciones);
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

                SymTable var_actual = encontrar_simbolo(entorno, identificador, actual.entorno);

                if (var_actual == null) {
                    this.texto_salida += "Error, la variable '" + identificador + "' no existe en el entorno actual";
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

    public LogOutput interpretar(ArrayList<SymTable> entorno, Nodo actual, LogOutput caso) {
        LogOutput retorno = new LogOutput();
        switch (actual.titulo) {
            case "lista_casos" -> {
                LogOutput r1 = interpretar(entorno, actual.hijos.get(0), caso);
                if (actual.hijos.size() == 2) {
                    if (r1.sentencia == null) {
                        return interpretar(entorno, actual.hijos.get(1), caso);
                    }
                }
                return r1;
            }

            case "caso" -> {
                LogOutput r1 = interpretar(entorno, actual.hijos.get(0));
                if (r1.tipo.equals(caso.tipo) && r1.valor.equals(caso.valor)) {
                    return interpretar(entorno, actual.hijos.get(1));
                }

            }

            case "defecto" -> {
                return interpretar(entorno, actual.hijos.get(0));
            }
        }

        return retorno;
    }

    public ArrayList<LogOutput> interpretar_parametros(ArrayList<SymTable> entorno, Nodo actual) {
        ArrayList<LogOutput> n_parametros = new ArrayList<>();
        if (actual.hijos.size() == 2) {
            n_parametros = interpretar_parametros(entorno, actual.hijos.get(0));
            n_parametros.add(interpretar(entorno, actual.hijos.get(1)));
        } else {
            n_parametros.add(interpretar(entorno, actual.hijos.get(0)));
        }
        return n_parametros;
    }

    public SymTable encontrar_simbolo(ArrayList<SymTable> symTSL, String nombre, String ambito) {
        ArrayList<SymTable> variables_posibles = new ArrayList<>();

        symTSL.forEach((simbolo) -> {
            if (simbolo.identificador.equals(nombre) && ambito.contains(simbolo.entorno)) {
                variables_posibles.add(simbolo);
            }
        });

        if (variables_posibles.isEmpty()) {
            return null;
        }

        SymTable retorno = variables_posibles.get(0);

        for (int i = 0; i < variables_posibles.size(); i++) {
            SymTable v = variables_posibles.get(i);
            if (v.entorno.length() > retorno.entorno.length()) {
                retorno = v;
            }
        }

        return retorno;
    }

    public SymTable encontrar_funcion(ArrayList<SymTable> symTSL, String nombre, String ambito) {
        ArrayList<SymTable> variables_posibles = new ArrayList<>();

        symTSL.forEach((simbolo) -> {
            if (simbolo.identificador.equals(nombre) && ambito.contains(simbolo.entorno) && "funcionmetodo".contains(simbolo.rol)) {
                variables_posibles.add(simbolo);
            }
        });

        if (variables_posibles.isEmpty()) {
            return null;
        }

        SymTable retorno = variables_posibles.get(0);

        for (int i = 0; i < variables_posibles.size(); i++) {
            SymTable v = variables_posibles.get(i);
            if (v.entorno.length() > retorno.entorno.length()) {
                retorno = v;
            }
        }

        return retorno;
    }

    public ArrayList<SymTable> crear_nuevo_entorno(ArrayList<SymTable> entorno_actual, String ambito) {
        ArrayList<SymTable> nuevo_entorno = new ArrayList<>();

        entorno_actual.forEach((s) -> {
            if (s.entorno.contains(ambito)) {
                SymTable copiado = new SymTable();
                copiado.entorno = s.entorno;
                copiado.identificador = s.identificador;
                copiado.instrucciones = s.instrucciones;
                copiado.parametros = s.parametros;
                copiado.rol = s.rol;
                copiado.tipo = s.tipo;
                copiado.valor = s.valor;
                nuevo_entorno.add(copiado);
            } else {
                nuevo_entorno.add(s);
            }
        });
        return nuevo_entorno;
    }
}
