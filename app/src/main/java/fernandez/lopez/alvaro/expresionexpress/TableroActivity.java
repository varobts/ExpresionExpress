package fernandez.lopez.alvaro.expresionexpress;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TableroActivity extends AppCompatActivity {

    //MODEL
    private Equipo equipo1, equipo2;
    private Juego juego;


    private List<String> palabras;
    private List<Integer> indicePalabras;   //Lista para saber qué índices hemos cogido de Lista palabras

    private TextView Eq1View, Eq2View, Pos1View, Pos2View, Turno1View, Turno2View, PalabraView;
    private Button Pasa_btn, Tiempo_btn;
    private int jug1=0, jug2=0;

//Variable per a saber quantes paraules portem (NOMÉS en aquesta versió 0.0.2)
    private int FiRonda=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablero);

        equipo1 = (Equipo) getIntent().getExtras().getSerializable("Equipo1");
        equipo2 = (Equipo) getIntent().getExtras().getSerializable("Equipo2");
        juego = new Juego("Codi");

        palabras = new ArrayList<>();
        indicePalabras = new ArrayList<>();
        rellenaPalabras();

//Referenciem als objectes necessaris de la pantalla
        Eq1View = findViewById(R.id.Eq1View);
        Eq2View = findViewById(R.id.Eq2View);
        Pos1View = findViewById(R.id.Pos1View);
        Pos2View = findViewById(R.id.Pos2View);
        Turno1View = findViewById(R.id.Turno1View);
        Turno2View = findViewById(R.id.Turno2View);
        PalabraView = findViewById(R.id.PalabraView);
        Pasa_btn = findViewById(R.id.Pasa_btn);
        Tiempo_btn = findViewById(R.id.Tiempo_btn);

        Eq1View.setText("Equipo 1:\n" + equipo1.getNom());
        Eq2View.setText("Equipo 2:\n" + equipo2.getNom());
        Pos1View.setText("Posición:\n" + equipo1.getCasilla() + "/7");
        Pos2View.setText("Posición:\n" + equipo2.getCasilla() + "/7");
        Turno1View.setText("Turno:\n" + equipo1.getJugadors().get(jug1));
        Turno2View.setText("Siguiente:\n" + equipo2.getJugadors().get(jug2));
        jug1++;
        juego.setTurno(1);


        Pasa_btn.setEnabled(false);
        Tiempo_btn.setEnabled(true);


    }

    public void onClickPasa (View view){
        //Si s'ha acabat de ronda, avança una casella a l'equip corresponent
        /*if(FiRonda==5){
            casellaSeguent();
            if(equipo1.getCasilla()==7 || equipo2.getCasilla()==7) finalJoc();
        }
        else */
        nouTorn();
    }

    public void onClickTiempo (View view){
        Tiempo_btn.setEnabled(false);
        novaParaula();
        Pasa_btn.setEnabled(true);
        Random random = new Random();
        int comp1aPart = random.nextInt(61) + 20;
        final int comp2aPart = (int) (comp1aPart*0.2);
        new CountDownTimer((comp1aPart-comp2aPart)*1000, 1000) {
            ToneGenerator tone = new ToneGenerator(AudioManager.STREAM_ALARM, 50);
            public void onTick(long millisUntilFinished) {
                //Afegir pitidito
                tone.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 100);
            }

            public void onFinish() {
                new CountDownTimer(comp2aPart*1000,500){
                    public void onTick(long millisUntilFinished){
                        //Afegir pitidito
                        tone.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 100);
                    }
                    public void onFinish(){
                        //Pitidito de final
                        tone.startTone(ToneGenerator.TONE_PROP_NACK, 400);
                        casellaSeguent();
                        Tiempo_btn.setEnabled(true);
                        if(equipo1.getCasilla()==7 || equipo2.getCasilla()==7) finalJoc();
                    }
                }.start();

            }
        }.start();
    }

    private void novaParaula() {
        //Només serveix per a incialitzar la ronda
        Random random = new Random();
        int i = random.nextInt(indicePalabras.size());  //Se usa 'indicePalabras' porque es la que indica qué índices quedan aún por usar
        juego.setPalabra(palabras.get(indicePalabras.get(i)));  //Cogemos el índice que corresponde en 'indicePalabras'
        PalabraView.setText(juego.getPalabra());
        //Para que no se repitan palabras
        indicePalabras.remove(i);
        //Cuando nos quedamos sin palabras rellenamos 'palabras' de nuevo
        if(indicePalabras.size()<1) rellenaPalabras();
    }

    private void nouTorn() {
        novaParaula();
        //Canviem el torn de joc
        if (juego.getTurno() == 1) {
            juego.setTurno(2);
            //Canviem els jugadors actual i següent
            Turno1View.setText("Siguiente:\n" + equipo1.getJugadors().get(jug1));
            Turno2View.setText("Turno:\n" + equipo2.getJugadors().get(jug2));
            //Preparem següent jugador
            jug2++;
            if (jug2 == equipo2.getJugadors().size()) jug2 = 0;
        } else {
            juego.setTurno(1);
            //Canviem els jugadors actual i següent
            Turno1View.setText("Turno:\n" + equipo1.getJugadors().get(jug1));
            Turno2View.setText("Siguiente:\n" + equipo2.getJugadors().get(jug2));
            //Preparem següent jugador
            jug1++;
            if (jug1 == equipo1.getJugadors().size()) jug1 = 0;
        }
        FiRonda++;
    }

    private void finalJoc() {
        //Final joc, creem un 'AlertDialog'
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (equipo1.getCasilla() == 7) builder.setMessage("Ha guanyat l'equip " + equipo1.getNom() +"!");
        else builder.setMessage("Ha guanyat l'equip " + equipo2.getNom() +"!");
        builder.setTitle("FINAL DEL JOC")
                .setPositiveButton("Torna a jugar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetLayout();
                    }
                })
                .setNegativeButton("Acaba", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create()
                .show();
    }

    private void casellaSeguent() { //Afegir Toast indicant què passa i què fer
        if(juego.getTurno()==1) {
            equipo2.setCasilla(equipo2.getCasilla()+1);
            Pos2View.setText("Posición:\n" + equipo2.getCasilla() + "/7");
        }
        else {
            equipo1.setCasilla(equipo1.getCasilla()+1);
            Pos1View.setText("Posición:\n" + equipo1.getCasilla() + "/7");
        }
        FiRonda = 1;
        Pasa_btn.setEnabled(false);
    }

    public void rellenaPalabras (){
        //Rellenamos 'palabras' con el recurso 'words'
        String[] words = getResources().getStringArray(R.array.words);
        palabras = Arrays.asList(words);

        //Rellenamos 'indicePalabras' con los índices de 'palabras'
        for (int i = 0; i < palabras.size(); i++){
            indicePalabras.add(i);
        }
    }

    private DialogInterface.OnClickListener resetLayout() {
        equipo1.setCasilla(0);
        equipo2.setCasilla(0);
        juego.setTurno(1);
        Pos1View.setText("Posición:\n" + equipo1.getCasilla() + "/7");
        Pos2View.setText("Posición:\n" + equipo2.getCasilla() + "/7");
        return null;
    }
}
