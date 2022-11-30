package iesmm.pmdm.pmdm_t4_login;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final String filename = "users.csv";
    private String [] linea;
    private final ArrayList <Usuario> usuarios = new ArrayList();
    public Usuario user;
    private int cont=0;
    private int posicion=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b = this.findViewById(R.id.boton_iniciar_sesion);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //Comprobamos lo que se ha escrito en los campos email y password
                String email = ((TextView) findViewById(R.id.input_usuario)).getText().toString();
                String pass =((TextView) findViewById(R.id.input_contrasena)).getText().toString();

                if (getAccess(email,pass)){
                    //Construccion de conjunto de datos a transmitir
                    Bundle bundle = new Bundle();
                    bundle.putString("email", email);
                    bundle.putString("telefono", usuarios.get(posicion).getTelefono());
                    bundle.putString("nombre", usuarios.get(posicion).getNombre());
                    //Llamada de intent
                    Intent i = new Intent(getApplicationContext(),DetailActivity.class);
                    i.putExtras(bundle);
                    startActivity(i);
                }else {
                    Snackbar.make(view,"!Error de acceso¡",Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Devuelve cierto si se confirma que el email y el password son correctos
     *  y se localizan en el fichero
     * @param email
     * @param pass
     * @return devuelve cierto si es correcto y falso en caso contrario
     */
    private boolean getAccess(String email, String pass){
        boolean res = false;
        try
        {
            //leemos el fichero
            BufferedReader br = new BufferedReader(new InputStreamReader (openFileInput(filename)));
            String linea = br.readLine();
            //Separamos con split(":"), por :,
            while (linea!=null){
                String [] lista=linea.split(":");
                String nombre = lista[0];
                String contrasenia = lista[1];
                String user_email = lista[2];
                String telefono = lista[3];
                //creamos el nuevo usuario con los valores de la linea separada del archivo csv
                user = new Usuario(nombre,contrasenia,user_email,telefono);
                usuarios.add(user);
                if (!email.equals("") && !pass.equals("") && email.equals(user_email) && pass.equals(contrasenia)){
                    res = true;
                    posicion=cont;

                }else{
                    res = false;
                }
                linea = br.readLine();
                cont++;
            }

        }
        catch (Exception ex)
        {
            Log.e("PMDM", "Error al leer fichero desde memoria interna");
            mensajeError();
        }
        return res;
    }

    //método para lanzar una diálogo de alerta de que el fichero no se ha leído correctamente.
    private void mensajeError(){
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle("Error");
        alert.setMessage("Error al leer fichero desde memoria interna!");
        alert.setButton(Dialog.BUTTON_POSITIVE,"OK",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        alert.show();
    }

}
