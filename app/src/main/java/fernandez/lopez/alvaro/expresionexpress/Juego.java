package fernandez.lopez.alvaro.expresionexpress;

import java.util.Date;

public class Juego {
    String Codigo, Palabra;
    int Turno;
    Date Tiempo;

    public Juego(String codigo) {
        Codigo = codigo;
    }

    public String getCodigo() {
        return Codigo;
    }

    public void setCodigo(String codigo) {
        Codigo = codigo;
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

    public Date getTiempo() {
        return Tiempo;
    }

    public void setTiempo(Date tiempo) {
        Tiempo = tiempo;
    }
}
