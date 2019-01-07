package fernandez.lopez.alvaro.expresionexpress;

import java.io.Serializable;
import java.util.Date;

public class Juego implements Serializable{
    String Codigo, Palabra;
    int Turno;
    int Tiempo;

    public Juego(String codigo) {
        Codigo = codigo;
    }

    public String getCodigo() {
        return Codigo;
    }

    public void setCodigo(String codigo) {
        Codigo = codigo;
    }

    public String getPalabra() {
        return Palabra;
    }

    public void setPalabra(String palabra) {
        Palabra = palabra;
    }

    public int getTurno() {
        return Turno;
    }

    public void setTurno(int turno) {
        Turno = turno;
    }

    public int getTiempo() {
        return Tiempo;
    }

    public void setTiempo(int tiempo) {
        Tiempo = tiempo;
    }
}
