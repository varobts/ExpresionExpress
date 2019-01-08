package fernandez.lopez.alvaro.expresionexpress;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

public class TableroActivity extends AppCompatActivity {

    //MODEL
    private Equipo equipo1, equipo2;
    private Juego juego;


    private CountDownTimer DownTimer1;
    private CountDownTimer DownTimer2;
    private boolean DT1 = false;
    private boolean DT2 = false;

    private boolean PrimerCop = true;

    private List<String> palabras;
    private List<Integer> indicePalabras;   //Lista para saber qué índices hemos cogido de Lista palabras

    private TextView Eq1View, Eq2View, Pos1View, Pos2View, Turno1View, Turno2View, PalabraView;
    private Button Pasa_btn, Tiempo_btn;
    private int jug1=0, jug2=0;

    private boolean mult;
    private String DocID;
    private int NumEquipo;      //Aquest valor valdrà 1 si dispositiu ha "Creat" la partida
                                //i valdrà 2 si dispositiu s'ha "Unit" a la partida
                                //Per tant, aquest valor ens el dòna l'activitat anterior en
                                //l'Intent

    //Strings referenciats als recursos
    private String R_equipo1, R_equipo2, R_pos, R_turno, R_sig, R_gana, R_fin, R_repite, R_acaba;
    private String R_not_exist, R_tiempo, R_error_lectura, R_error_escritura, R_partida, R_casilla;
    private String R_creaEquips;

    //FireBase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference Express = db.collection("ExpresionExpress");
    private DocumentReference Partida;
    private DocumentReference Equip1;
    private DocumentReference Equip2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablero);

        NumEquipo = getIntent().getExtras().getInt("NumEquipo");
        juego = (Juego) getIntent().getExtras().getSerializable("Juego");
        mult = getIntent().getExtras().getBoolean("ModeMult");
        if (!mult) {
            DocID = getIntent().getExtras().getString("ID");
            Partida = Express.document(DocID);
        }
        else {
            NumEquipo = getIntent().getExtras().getInt("NumEquipo");
            Partida = Express.document(juego.getCodigo());
        }

        Equip1 = Partida.collection("Equipos").document("Equipo1");
        Equip2 = Partida.collection("Equipos").document("Equipo2");

        equipo1 = new Equipo();
        equipo2 = new Equipo();



        palabras = new ArrayList<>();
        indicePalabras = new ArrayList<>();
        rellenaPalabras();

        //Donem valor als strings dels recursos
        R_equipo1 = getResources().getString(R.string.equipo1);
        R_equipo2 = getResources().getString(R.string.equipo2);
        R_pos = getResources().getString(R.string.posicion);
        R_turno = getResources().getString(R.string.turno);
        R_sig = getResources().getString(R.string.siguiente);
        R_gana = getResources().getString(R.string.gana);
        R_fin = getResources().getString(R.string.fin);
        R_repite = getResources().getString(R.string.repite);
        R_acaba = getResources().getString(R.string.acaba);
        R_not_exist = getResources().getString(R.string.not_exist);
        R_tiempo = getResources().getString(R.string.tiempo);
        R_error_lectura = getResources().getString(R.string.error_lectura);
        R_error_escritura = getResources().getString(R.string.error_escritura);
        R_partida = getResources().getString(R.string.partida);
        R_casilla = getResources().getString(R.string.casilla);
        R_creaEquips = getResources().getString(R.string.creaEquips);

        //Sólo rellenamos los equipos automáticamente si estamos en local
        if (!mult) {
            rellenaEquipos();
        }
        //Si estamos en multijugadors, el equipo tendrá que clicar en Pasa y se lo indicamos en un Toast
        else {
            Toast.makeText(this, R_creaEquips, Toast.LENGTH_LONG).show();
        }

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

        if (mult) {
            Pasa_btn.setEnabled(true);
            Tiempo_btn.setEnabled(false);
        }
        else {
            Pasa_btn.setEnabled(false);
            Tiempo_btn.setEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Partida.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null){ //Evitem que deixi de funcionar la app si hi ha hagut algun error
                    return;
                }

                juego.setPalabra(documentSnapshot.getString("Palabra"));
                juego.setTiempo(Math.toIntExact((Long) documentSnapshot.get("Tiempo")));
                juego.setTurno(Math.toIntExact((Long) documentSnapshot.get("Turno")));

                PalabraView.setText(juego.getPalabra());

                if (juego.getTiempo() == -1) {
                    casellaSeguent();
                    Tiempo_btn.setEnabled(true);
                    if(equipo1.getCasilla()==7 || equipo2.getCasilla()==7) finalJoc();
                }
                else if (juego.getTurno() == NumEquipo && !juego.getCodigo().equals("Local")){
                    if (equipo1.getJugadors() != null && equipo2.getJugadors() != null) {
                        if (NumEquipo == 1) {
                            Turno1View.setText(R_turno + ":\n" + equipo1.getJugadors().get(jug1));
                            Turno2View.setText(R_sig + ":\n" + equipo2.getJugadors().get(jug2));
                            jug1++;
                            if (jug1 == equipo1.getJugadors().size()) jug1 = 0;
                        } else {
                            Turno1View.setText(R_sig + ":\n" + equipo1.getJugadors().get(jug1));
                            Turno2View.setText(R_turno + ":\n" + equipo2.getJugadors().get(jug2));
                            jug2++;
                            if (jug2 == equipo2.getJugadors().size()) jug2 = 0;
                        }
                    }
                    Pasa_btn.setEnabled(true);
                }
                if (juego.getTiempo() > 0) {
                    Tiempo_btn.setEnabled(false);
                }
            }
        });

        Equip1.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    return;
                }

                equipo1.setCasilla(Math.toIntExact((Long) documentSnapshot.get("Casilla")));
                Pos1View.setText(R_pos + ":\n" + equipo1.getCasilla() + "/7");
            }
        });

        Equip2.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    return;
                }

                equipo2.setCasilla(Math.toIntExact((Long) documentSnapshot.get("Casilla")));
                Pos2View.setText(R_pos + ":\n" + equipo2.getCasilla() + "/7");
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (DT1) DownTimer1.cancel();
        if (DT2) DownTimer2.cancel();
    }

    private void rellenaEquipos() {
        Equip1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    String NomEquip = documentSnapshot.getString("Nom");
                    Long Casilla = (Long) documentSnapshot.get("Casilla");
                    Long Numero = (Long) documentSnapshot.get("Num");

                    List<String> jugadors = new ArrayList<>();
                    jugadors = (List<String>) documentSnapshot.get("Jugadors");

                    /*while (jugadors.size() < 2){
                        NomEquip = documentSnapshot.getString("Nom");
                        Casilla = (Long) documentSnapshot.get("Casilla");
                        Numero = (Long) documentSnapshot.get("Num");
                        jugadors = (List<String>) documentSnapshot.get("Jugadors");
                    }*/

                    int Cas = Math.toIntExact(Casilla);
                    int Nume = Math.toIntExact(Numero);

                    equipo1.setNom(NomEquip);
                    equipo1.setCasilla(Cas);
                    equipo1.setNum(Nume);
                    equipo1.setJugadors(jugadors);

                    Eq1View.setText(R_equipo1 + ":\n" + equipo1.getNom());
                    Pos1View.setText(R_pos + ":\n" + equipo1.getCasilla() + "/7");
                    Turno1View.setText(R_turno + ":\n" + equipo1.getJugadors().get(jug1));
                    jug1++;
                    juego.setTurno(1);
                }
                else {
                    Toast.makeText(TableroActivity.this, R_not_exist + " 1: ", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("EXpress",e.toString());
                Toast.makeText(TableroActivity.this, R_error_lectura + "1: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        Equip2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    String NomEquip = documentSnapshot.getString("Nom");
                    Long Casilla = (Long) documentSnapshot.get("Casilla");
                    Long Numero = (Long) documentSnapshot.get("Num");

                    List<String> jugadors = new ArrayList<>();
                    jugadors = (List<String>) documentSnapshot.get("Jugadors");

                    /*while (jugadors.get(1).equals("")){
                        NomEquip = documentSnapshot.getString("Nom");
                        Casilla = (Long) documentSnapshot.get("Casilla");
                        Numero = (Long) documentSnapshot.get("Num");
                        jugadors = (List<String>) documentSnapshot.get("Jugadors");
                        Toast.makeText(TableroActivity.this, "hola!", Toast.LENGTH_SHORT).show();
                    }*/

                    int Cas = Math.toIntExact(Casilla);
                    int Nume = Math.toIntExact(Numero);

                    equipo2.setNom(NomEquip);
                    equipo2.setCasilla(Cas);
                    equipo2.setNum(Nume);
                    equipo2.setJugadors(jugadors);

                    Eq2View.setText(R_equipo2 + ":\n" + equipo2.getNom());
                    Pos2View.setText(R_pos + ":\n" + equipo2.getCasilla() + "/7");
                    Turno2View.setText(R_sig + ":\n" + equipo2.getJugadors().get(jug2));
                }
                else {
                    Toast.makeText(TableroActivity.this, R_not_exist + " 2: ", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("EXpress",e.toString());
                Toast.makeText(TableroActivity.this, R_error_lectura + "2: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void actualitzaPartida() {

        Map<String, Object> partida = new HashMap<>();

        partida.put("Codigo", juego.getCodigo());
        partida.put("Palabra", juego.getPalabra());
        partida.put("Tiempo", juego.getTiempo());
        partida.put("Turno", juego.getTurno());

        Partida.update(partida).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("EXpress",e.toString());
                Toast.makeText(TableroActivity.this, R_error_escritura + R_partida + ": " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualitzaTiempo() {

        Map <String, Object> partida = new HashMap<>();

        partida.put("Tiempo", juego.getTiempo());

        Partida.update(partida).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("EXpress",e.toString());
                Toast.makeText(TableroActivity.this, R_error_escritura + R_tiempo + ": " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualitzaCasella1() {
        Map <String, Object> equipo = new HashMap<>();

        equipo.put("Casilla", equipo1.getCasilla());

        Equip1.update(equipo).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("EXpress",e.toString());
                Toast.makeText(TableroActivity.this, R_error_escritura + R_casilla + "1: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualitzaCasella2() {
        Map <String, Object> equipo = new HashMap<>();

        equipo.put("Casilla", equipo2.getCasilla());

        Equip2.update(equipo).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("EXpress",e.toString());
                Toast.makeText(TableroActivity.this, R_error_escritura + R_casilla + "2: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClickPasa (View view){
        if (PrimerCop && mult){
            PrimerCop = false;
            rellenaEquipos();
            Tiempo_btn.setEnabled(true);
            Pasa_btn.setEnabled(false);
        }
        else {
            nouTorn();
            novaParaula();
            if (!juego.getCodigo().equals("Local")) {   //Si estem en multijugador
                Pasa_btn.setEnabled(false);
            }
        }
    }

    public void onClickTiempo (View view){
        if(equipo1.getCasilla()==7 || equipo2.getCasilla()==7) {
            finalJoc();
            return;
        }
//Per a evitar que actualitzi el nom quan cliquem aquest botó
        if (NumEquipo == 1 && juego.getTurno() == 1) {
            jug1--;
            if (jug1 == -1) jug1 = equipo1.getJugadors().size()-1;
        }
        else if (NumEquipo == 2 && juego.getTurno() == 2){
            jug2--;
            if (jug2 == -1) jug2 = equipo2.getJugadors().size()-1;
        }
        Tiempo_btn.setEnabled(false);
        Random random = new Random();
        int comp1aPart = random.nextInt(51) + 30;
        juego.setTiempo(comp1aPart);
        novaParaula();
        if ((!juego.getCodigo().equals("Local") && juego.getTurno() == NumEquipo) || juego.getCodigo().equals("Local")) {
            Pasa_btn.setEnabled(true);
        }
        final int comp2aPart = (int) (comp1aPart*0.2);
        DownTimer1 = new CountDownTimer((comp1aPart-comp2aPart)*1000, 1000) {
            ToneGenerator tone = new ToneGenerator(AudioManager.STREAM_ALARM, 75);
            public void onTick(long millisUntilFinished) {
                tone.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 100);
                DT1 = true;
            }

            public void onFinish() {
                DT1 = false;
                DownTimer2 = new CountDownTimer(comp2aPart*1000,500){
                    public void onTick(long millisUntilFinished){
                        tone.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 100);
                        DT2 = true;
                    }
                    public void onFinish(){
                        DT2 = false;
                        tone = new ToneGenerator(AudioManager.STREAM_ALARM, 150);
                        tone.startTone(ToneGenerator.TONE_PROP_NACK, 400);
                        juego.setTiempo(-1);
                        actualitzaTiempo();
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
        //PalabraView.setText(juego.getPalabra());
        //Para que no se repitan palabras
        indicePalabras.remove(i);
        //Cuando nos quedamos sin palabras rellenamos 'palabras' de nuevo
        if(indicePalabras.size()<1) rellenaPalabras();
        actualitzaPartida();
    }

    private void nouTorn() {
        //Canviem el torn de joc
        if (juego.getTurno() == 1) {
            juego.setTurno(2);
            //Canviem els jugadors actual i següent
            Turno1View.setText(R_sig + ":\n" + equipo1.getJugadors().get(jug1));
            Turno2View.setText(R_turno + ":\n" + equipo2.getJugadors().get(jug2));
            //Preparem següent jugador
            jug2++;
            if (jug2 == equipo2.getJugadors().size()) jug2 = 0;
        } else {
            juego.setTurno(1);
            //Canviem els jugadors actual i següent
            Turno1View.setText(R_turno + ":\n" + equipo1.getJugadors().get(jug1));
            Turno2View.setText(R_sig + ":\n" + equipo2.getJugadors().get(jug2));
            //Preparem següent jugador
            jug1++;
            if (jug1 == equipo1.getJugadors().size()) jug1 = 0;
        }
    }

    private void finalJoc() {
        //Final joc, creem un 'AlertDialog'
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (equipo1.getCasilla() == 7) builder.setMessage(R_gana + equipo1.getNom() +"!");
        else builder.setMessage(R_gana + equipo2.getNom() +"!");
        builder.setTitle(R_fin)
                .setPositiveButton(R_repite, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetLayout();
                    }
                })
                .setNegativeButton(R_acaba, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create()
                .show();
    }

    private void casellaSeguent() { //Afegir Toast indicant què passa i què fer
        if(juego.getTurno()==1) {
            if (equipo2.getCasilla() >= 7) {
                equipo2.setCasilla(7);
            }
            else {
                equipo2.setCasilla(equipo2.getCasilla()+1);
            }
            actualitzaCasella2();
        }
        else {
            if (equipo1.getCasilla() >= 7) {
                equipo1.setCasilla(7);
            }
            else {
                equipo1.setCasilla(equipo1.getCasilla() + 1);
            }
            actualitzaCasella1();
        }
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
        actualitzaPartida();
        actualitzaCasella1();
        actualitzaCasella2();
        return null;
    }
}
