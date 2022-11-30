package iesmm.pmdm.pmdm_t4_login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //Recuperar datos del bundle transmitidos
        Bundle b =this.getIntent().getExtras();

        if (b!=null){
            String nombre = b.getString("nombre");
            String telefono = b.getString("telefono");
            String email = b.getString("email");
            TextView text = this.findViewById(R.id.welcome);
            text.setText("Bienvenido: \n" + "Usuario: " + nombre +"\n Telefono: "
                    + telefono + "\n Email:" + email);
        }
    }
}