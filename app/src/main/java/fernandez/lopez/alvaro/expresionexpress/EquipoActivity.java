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

    private RecyclerView PartListView;
    private EditText NomEquipView;
    private EditText nomParticipantView;
    private Equipo equipo1,equipo2;
    private List<String> jugadors;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipo);

        PartListView = findViewById(R.id.PartListView);
        NomEquipView = findViewById(R.id.NomEquipView);
        nomParticipantView = findViewById(R.id.nomParticipantView);

        jugadors=new ArrayList<>();
        PartListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter();
        PartListView.setAdapter(adapter);

    }


    public void onClickPlus(View view) {

    jugadors.add(nomParticipantView.getText().toString());
        adapter.notifyItemInserted(jugadors.size() - 1);


    }

    public void OnClickNext(View view) {
        equipo1=new Equipo(NomEquipView.getText().toString(), 1,jugadors);
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
