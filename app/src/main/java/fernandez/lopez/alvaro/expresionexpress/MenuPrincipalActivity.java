package fernandez.lopez.alvaro.expresionexpress;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MenuPrincipalActivity extends AppCompatActivity {

    boolean mult=false; //indica si estem en mode multijugador (true)

    private String DocID = "";

    private Juego juego;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference Partida = db.collection("ExpresionExpress");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
    }

    public void OnClickPlay(View view) {
        mult=false;
        creaPartidaLocal();
        juego = new Juego("Local");
        Intent intent = new Intent(this, EquipoActivity.class);
        intent.putExtra("ModeMult",mult);
        intent.putExtra("Juego", juego);
        intent.putExtra("ID", DocID);
        startActivity(intent);
    }
    public void OnClickMult(View view) {
        mult=true;
        Intent intent = new Intent(this, MultMenuActivity.class);
        intent.putExtra("ModeMult",mult);
        startActivity(intent);
    }
    public void OnClickInstru(View view) {
        Intent intent = new Intent(this, InstruActivity.class);
        startActivity(intent);
    }

    public void creaPartidaLocal(){

        Map<String, Object> Joc = new HashMap<>();

        Joc.put("Codigo", "Local");
        Joc.put("Palabra", "");
        Joc.put("Tiempo", 0);
        Joc.put("Turno", 1);

        DocID = Partida.document().getId();
        Partida.document(DocID).set(Joc);
    }
}
