package fernandez.lopez.alvaro.expresionexpress;

import java.util.List;

public class Equipo {
    String Nom;
    int Num, Casilla;
    List<String> Jugadors;

    public Equipo(String nom, int num, List<String> jugadors) {
        Nom = nom;
        Num = num;
        Jugadors = jugadors;
        Casilla = 0;
    }

    public String getNom() {
        return Nom;
    }

    public void setNom(String nom) {
        Nom = nom;
    }

    public int getNum() {
        return Num;
    }

    public void setNum(int num) {
        Num = num;
    }

    public int getCasilla() {
        return Casilla;
    }

    public void setCasilla(int casilla) {
        Casilla = casilla;
    }

    public List<String> getJugadors() {
        return Jugadors;
    }

    public void setJugadors(List<String> jugadors) {
        Jugadors = jugadors;
    }
}
