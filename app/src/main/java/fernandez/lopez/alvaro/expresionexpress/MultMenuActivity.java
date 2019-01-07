package fernandez.lopez.alvaro.expresionexpress;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MultMenuActivity extends AppCompatActivity {

    private EditText CodeView;
    private String CreationCode,JoinCode;
    private String R_WrongCode,R_EmptyCode;
    private boolean mult;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference Partida = db.collection("ExpresionExpress");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mult_menu);

        mult = getIntent().getExtras().getBoolean("ModeMult");

        R_WrongCode = getResources().getString(R.string.R_WrongCode);
        R_EmptyCode = getResources().getString(R.string.R_EmptyCode);
        CodeView = findViewById(R.id.CodeView);
    }

    public void OnClickCreate (View view){
        CreationCode = CodeView.getText().toString();
        creaPartida();
        if (CreationCode.equals("")){
            Toast.makeText(this, R_EmptyCode, Toast.LENGTH_SHORT).show();
        }
        else CodeView.setText("");

    }
    public void OnClickJoin (View view){
        JoinCode = CodeView.getText().toString();
        if (JoinCode.equals(CreationCode)){
            Intent intent = new Intent(this, EquipoActivity.class);
            intent.putExtra("ModeMult",mult);
            startActivity(intent);
        }
        else if (JoinCode.equals("")){
            Toast.makeText(this, R_EmptyCode, Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, R_WrongCode, Toast.LENGTH_SHORT).show();
        }

    }

    public void creaPartida(){

        Map <String, Object> Joc = new HashMap<>();
        Map <String, Object> Equip = new HashMap<>();

        Joc.put("Codigo", CreationCode);
        Joc.put("Palabra", "");
        Joc.put("Tiempo", 0);
        Joc.put("Turno", 1);

        Partida.document(CreationCode).set(Joc);

        Equip.put("Casilla", 0);
        Equip.put("Jugadors", "");
        Equip.put("Nom", "");
        Equip.put("Num", 1);

        Partida.document(CreationCode).collection("Equipos").
                document("Equipo1").set(Equip);

        Equip = new HashMap<>();

        Equip.put("Casilla", 0);
        Equip.put("Jugadors", "");
        Equip.put("Nom", "");
        Equip.put("Num", 2);

        Partida.document(CreationCode).collection("Equipos").
                document("Equipo2").set(Equip);
    }
}
