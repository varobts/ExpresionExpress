package fernandez.lopez.alvaro.expresionexpress;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultMenuActivity extends AppCompatActivity {

    private EditText CodeView;
    private String CreationCode,JoinCode;
    private String R_WrongCode,R_EmptyCode, R_codi_repe;
    private boolean mult;

    private int NumEquipo;  //Aquest valor valdrà 1 si dispositiu "Crea" la partida
                            //i valdrà 2 si dispositiu s'"Uneix" a la partida
    private Juego juego;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference Partida = db.collection("ExpresionExpress");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mult_menu);

        mult = getIntent().getExtras().getBoolean("ModeMult");

        R_WrongCode = getResources().getString(R.string.R_WrongCode);
        R_EmptyCode = getResources().getString(R.string.R_EmptyCode);
        R_codi_repe = getResources().getString(R.string.codi_repe);
        CodeView = findViewById(R.id.CodeView);
    }

    public void OnClickCreate (View view){
        CreationCode = CodeView.getText().toString();

        if (CreationCode.equals("")){
            Toast.makeText(this, R_EmptyCode, Toast.LENGTH_SHORT).show();
        }
        else {
            CodeView.setText("");
            creaPartida();
        }
    }

    public void OnClickJoin (View view){
        JoinCode = CodeView.getText().toString();

        if (JoinCode.equals("")){
            Toast.makeText(this, R_EmptyCode, Toast.LENGTH_SHORT).show();
        }
        else {
            buscaPartida();
        }
    }

    public void creaPartida(){
        Partida.document(CreationCode).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()){
                    Map <String, Object> Joc = new HashMap<>();
                    Map <String, Object> Equip = new HashMap<>();

                    Joc.put("Codigo", CreationCode);
                    Joc.put("Palabra", "");
                    Joc.put("Tiempo", 0);
                    Joc.put("Turno", 1);

                    Partida.document(CreationCode).set(Joc);

                    //Creem una llista de jugadors per a què si un dels dos equips triga massa en omplir-la
                    //no es produeixi error en passar a TableroActivity el primer equip que passi
                    List<String> jugadors = new ArrayList<>();
                    jugadors.add("");
                    jugadors.add("");

                    Equip.put("Casilla", 0);
                    Equip.put("Jugadors", jugadors);
                    Equip.put("Nom", "");
                    Equip.put("Num", 1);

                    Partida.document(CreationCode).collection("Equipos").
                            document("Equipo1").set(Equip);

                    Equip = new HashMap<>();

                    Equip.put("Casilla", 0);
                    Equip.put("Jugadors", jugadors);
                    Equip.put("Nom", "");
                    Equip.put("Num", 2);

                    Partida.document(CreationCode).collection("Equipos").
                            document("Equipo2").set(Equip);
                    juego = new Juego(CreationCode);
                    NumEquipo = 1;
                    llamaEquipoActivity();
                }
                else {
                    Toast.makeText(MultMenuActivity.this, R_codi_repe, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void buscaPartida(){
        Partida.document(JoinCode).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                CreationCode = documentSnapshot.getString("Codigo");

                juego = new Juego(CreationCode);

                if (JoinCode.equals(CreationCode)) {
                    NumEquipo = 2;
                    llamaEquipoActivity();
                }
                else{
                    Toast.makeText(MultMenuActivity.this, R_WrongCode, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void llamaEquipoActivity(){
        Intent intent = new Intent(this, EquipoActivity.class);
        intent.putExtra("ModeMult",mult);
        intent.putExtra("Juego", juego);
        intent.putExtra("NumEquipo", NumEquipo);
        startActivity(intent);
    }
}
