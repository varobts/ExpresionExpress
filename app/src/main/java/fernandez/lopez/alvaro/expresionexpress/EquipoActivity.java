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

import java.util.ArrayList;
import java.util.List;

public class EquipoActivity extends AppCompatActivity {

    private static final int TABLERO = 0;
    private RecyclerView PartListView;
    private EditText NomEquipView, nomParticipantView;
    private TextView EquipoView;
    private Equipo equipo1,equipo2;
    private List<String> jugadors;
    private Adapter adapter;
    private boolean estoy1=true; //Flag que me dice si estoy editando los parametros del equipo 1 o 2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipo);

        PartListView = findViewById(R.id.PartListView);
        NomEquipView = findViewById(R.id.NomEquipView);
        EquipoView = findViewById(R.id.EquipoView);
        EquipoView.setText("Equipo 1:");
        nomParticipantView = findViewById(R.id.nomParticipantView);

        jugadors=new ArrayList<>();
        PartListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter();
        PartListView.setAdapter(adapter);

    }


    public void onClickPlus(View view) {

        jugadors.add(nomParticipantView.getText().toString());
        adapter.notifyItemInserted(jugadors.size() - 1);
        nomParticipantView.setText("");

    }

    public void OnClickNext(View view) {
        if(estoy1) {
            equipo1=new Equipo(NomEquipView.getText().toString(), 1,jugadors);
            estoy1=false;
            //Llama a un metodo que sea NuevoEquipo, este metodo me resetea todos los valores
            NuevoEquipo();
        }
        else {
            equipo2=new Equipo(NomEquipView.getText().toString(), 2,jugadors);
            estoy1=true;
            LlamaTableroActivity();
        }
    }

    public void LlamaTableroActivity(){
        Intent intent = new Intent(this, TableroActivity.class);
        intent.putExtra("Equipo1",equipo1);
        intent.putExtra("Equipo2",equipo2);
        startActivity(intent);

    }

    public void NuevoEquipo(){
        EquipoView.setText("Equipo 2:");
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
