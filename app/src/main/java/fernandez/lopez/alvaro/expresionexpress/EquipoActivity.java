package fernandez.lopez.alvaro.expresionexpress;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquipoActivity extends AppCompatActivity {

    private static final int TABLERO = 0;
    private RecyclerView PartListView;
    private EditText NomEquipView, nomParticipantView;
    private TextView EquipoView;
    private Equipo equipo,equipo1,equipo2;
    private List<String> jugadors;
    private Adapter adapter;
    private boolean estoy1=true; //Flag que me dice si estoy editando los parametros del equipo 1 o 2
    private boolean mult;
    private String DocID = "";

    private int NumEquipo;      //Aquest valor valdrà 1 si dispositiu ha "Creat" la partida
                                    //i valdrà 2 si dispositiu s'ha "Unit" a la partida
                                    //Per tant, aquest valor ens el dòna l'activitat anterior en
                                    //l'Intent

    private Juego juego;

    private String R_equipo1, R_equipo2, R_participantes, R_faltaEq, R_faltaPart,R_equipo;   //Strings referenciats als recursos

    //FireBase

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference Express = db.collection("ExpresionExpress");
    private DocumentReference Partida;
    private DocumentReference Equip1;
    private DocumentReference Equip2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipo);

        mult = getIntent().getExtras().getBoolean("ModeMult");
        juego = (Juego) getIntent().getExtras().getSerializable("Juego");
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

        R_equipo1 = getResources().getString(R.string.equipo1);
        R_equipo2 = getResources().getString(R.string.equipo2);
        R_participantes = getResources().getString(R.string.participantes);
        R_faltaEq = getResources().getString(R.string.faltaEquipo);
        R_faltaPart = getResources().getString(R.string.faltaParticipantes);
        R_equipo = getResources().getString(R.string.equipo);

        PartListView = findViewById(R.id.PartListView);
        NomEquipView = findViewById(R.id.NomEquipView);
        EquipoView = findViewById(R.id.EquipoView);
        if(mult) EquipoView.setText(R_equipo);
        else EquipoView.setText(R_equipo1);
        nomParticipantView = findViewById(R.id.nomParticipantView);

        jugadors=new ArrayList<>();
        PartListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter();
        PartListView.setAdapter(adapter);

    }


    public void onClickPlus(View view) {

        if(!nomParticipantView.getText().toString().equals("")) {   //Si no s'ha introduit cap nom, no facis res
            jugadors.add(nomParticipantView.getText().toString());
            adapter.notifyItemInserted(jugadors.size() - 1);
            nomParticipantView.setText("");
        }

    }

    public void OnClickNext(View view) {
        if (!NomEquipView.getText().toString().equals("") && jugadors.size() > 1) {
            if (mult) {
                if (NumEquipo == 1) {
                    equipo1 = new Equipo(NomEquipView.getText().toString(), 1, jugadors);
                    guardaEquipo(equipo1);
                } else {
                    equipo2 = new Equipo(NomEquipView.getText().toString(), 2, jugadors);
                    guardaEquipo(equipo2);
                }
                LlamaTableroActivity();
            } else {
                if (estoy1) {
                    equipo1 = new Equipo(NomEquipView.getText().toString(), 1, jugadors);
                    estoy1 = false;
                    guardaEquipo(equipo1);
                    //Llama a un metodo que sea NuevoEquipo, este metodo me resetea todos los valores
                    NuevoEquipo();
                } else {
                    equipo2 = new Equipo(NomEquipView.getText().toString(), 2, jugadors);
                    estoy1 = true;
                    guardaEquipo(equipo2);
                    LlamaTableroActivity();
                }
            }
        } else if (NomEquipView.getText().toString().equals("")) {
            Toast.makeText(this, R_faltaEq, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R_faltaPart, Toast.LENGTH_SHORT).show();
        }
    }


    public void guardaEquipo(Equipo equipo) {

        Map<String, Object> equipoMap = new HashMap<>();

        equipoMap.put("Casilla", equipo.getCasilla());
        equipoMap.put("Jugadors", equipo.getJugadors());
        equipoMap.put("Nom", equipo.getNom());
        equipoMap.put("Num", equipo.getNum());

        if (equipo.getNum() == 1) {
            Equip1.set(equipoMap);
        }
        else {
            Equip2.set(equipoMap);
        }

    }

    public void LlamaTableroActivity(){
        Intent intent = new Intent(this, TableroActivity.class);
        //No fem putExtra dels equips perquè els agafarem des de la firebase
        intent.putExtra("Juego", juego);
        intent.putExtra("ModeMult", mult);
        if (!mult) intent.putExtra("ID", DocID);
        else intent.putExtra("NumEquipo", NumEquipo);
        startActivity(intent);
    }

    public void NuevoEquipo(){
        EquipoView.setText(R_equipo2);
        NomEquipView.setText("");
        nomParticipantView.setText("");
        int i = jugadors.size();
        adapter.notifyItemRangeRemoved(0, i);
        jugadors = new ArrayList<>();
    }


    //El ViewHolder mantiene referencias a las partes del itemView
    //que cambian cuando lo reciclamos.
    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nomview;

        public ViewHolder(View itemView) { //por tanto recibe el itemView en el constructor
            super(itemView);
            //Obtenemos las referencias a objetos de dentro del itemView
            nomview = itemView.findViewById(R.id.nomview);
        }
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //Creamos un item de la pantalla a partir del layout
            View nomview = getLayoutInflater().inflate(R.layout.nom_participant, parent, false);
            //Creamos (y retornamos) el ViewHolder asociado
            return new ViewHolder(nomview);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.nomview.setText(jugadors.get(position));
        }

        @Override
        public int getItemCount() {
            //Puedo acceder a items (que es un campo de la actividad) porque
            //Adapter es una clase interna de la actividad
            return jugadors.size();
        }
    }
}
