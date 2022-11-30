package com.example.pmdm_t4_agenda;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    private ArrayAdapter adaptador; //Adaptador controlador de datos
    ArrayList <Contacto> contactos = new ArrayList();
    Contacto c;
    private final String filename = "contactos.csv";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadData();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadData() {
        InputStreamReader file=null;
        BufferedReader br=null;
        try
        {
            file= new InputStreamReader(openFileInput(filename));
            br= new BufferedReader(file);
            String texto = "";
            while((texto = br.readLine()) !=null) {
                String linea [] = texto.split(";");

                String nombre = linea[0].toString();
                String telefono = linea[1].toString();
                String email = linea[2].toString();
                c = new Contacto(nombre, telefono, email);
                contactos.add(c);

            }
            contactos.sort(Comparator.comparing((Contacto c) ->c.getNombre()));
            addItemInListView(contactos);
        }
        catch (Exception ex)
        {
            Log.e("PMDM", "Error al leer fichero desde memoria interna");
        }
        finally
        {
            try {
                if(br!=null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    //Metodo para añadir items al listView y ventana de dialogo
    private void addItemInListView(ArrayList <Contacto> contactos) {
        //1. Localizar el listView
        ListView listaView = findViewById(R.id.listView1);
        //2. Crear el adaptador de datos y vincular los datos que vamos a presentar a la listaView.
        adaptador = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,contactos);
        listaView.setAdapter(adaptador);
        listaView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int posicion = i;
                AlertDialog.Builder dialogo = new AlertDialog.Builder(MainActivity.this);
                dialogo.setTitle(contactos.get(posicion).getNombre());
                dialogo.setMessage("Tel: " + contactos.get(posicion).getTelefono() + "\n Email: " + contactos.get(posicion).getEmail());
                dialogo.setIcon(R.drawable.contacto);
                dialogo.setCancelable(false);
                //Botón para mandar un whatsapp
                dialogo.setNeutralButton(R.string.enviar_mensaje, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        mandarWhatsapp("hola");
                    }
                //Botón para realizar una llamada al contacto.
                }).setPositiveButton(R.string.llamar, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    public void onClick(DialogInterface dialogo1, int id) {
                        realizarLlamada(contactos.get(posicion).getTelefono());
                    }
                    //botón cancelar.
                }).setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {

                    }
                });
                dialogo.show();
            }
        });
    }

    public void mandarWhatsapp(String mensaje) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND); sendIntent.putExtra(Intent.EXTRA_TEXT, mensaje);
        sendIntent.setType("text/plain"); startActivity(sendIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void realizarLlamada(String telefono) {
        if (confirmarPermisoLlamada()==true){
            Intent i = new Intent(Intent.ACTION_CALL);
            Uri uri = Uri.parse("tel:" + telefono);
            i.setData(uri);
            this.startActivity(i);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean confirmarPermisoLlamada() {
        boolean confirmado = false;

        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {
            this.requestPermissions(new String[]{android.Manifest.permission.CALL_PHONE}, 0);
        }
        else
            confirmado = true;

        return confirmado;
    }
}