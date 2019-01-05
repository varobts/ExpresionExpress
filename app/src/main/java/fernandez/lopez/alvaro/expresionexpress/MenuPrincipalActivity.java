package fernandez.lopez.alvaro.expresionexpress;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MenuPrincipalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
    }

    public void OnClickPlay(View view) {
        Intent intent = new Intent(this, EquipoActivity.class);
        startActivity(intent);
    }
    public void OnClickMult(View view) {
        Intent intent = new Intent(this, EquipoActivity.class);
        startActivity(intent);
    }
    public void OnClickInstru(View view) {
        Intent intent = new Intent(this, InstruActivity.class);
        startActivity(intent);
    }
}
