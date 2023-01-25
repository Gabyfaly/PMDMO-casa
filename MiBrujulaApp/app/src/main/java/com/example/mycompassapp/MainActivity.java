package com.example.mycompassapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private ImageView imageView;
    private TextView grados;
    private TextView orientacion;

    private SensorManager sensorManager;
    private Sensor acelerometro, magnetometro, vectorRotacion;
    private float[] valorAcelerometro = new float[3];
    private float[] valorMagnetometro = new float[3];
    private float[] matrizRotacion = new float[9];
    private float[] valorOrientacion= new float[3];

    private boolean tieneSensor=false, tieneSensor2=false;
    boolean arrayAcelerometroCopiado = false;
    boolean arrayMagnetometroCopiado= false;

    long tiempoActualizacion=0;
    int gradoActual=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.brujula);
        grados= findViewById(R.id.grados);
        orientacion= findViewById(R.id.orientacion);


        sensorManager =(SensorManager) getSystemService(SENSOR_SERVICE);
        acelerometro= sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometro= sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent){
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(matrizRotacion, sensorEvent.values);
            gradoActual = (int) (Math.toDegrees(SensorManager.getOrientation(matrizRotacion, valorOrientacion)[0]) + 360) % 360;
        }
        if (sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            System.arraycopy(sensorEvent.values,0,valorAcelerometro,0,sensorEvent.values.length);
            arrayAcelerometroCopiado=true;

        }else if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            System.arraycopy(sensorEvent.values,0,valorMagnetometro,0,sensorEvent.values.length);
            arrayMagnetometroCopiado=true;
        }
        if(arrayAcelerometroCopiado && arrayMagnetometroCopiado) {
            sensorManager.getRotationMatrix(matrizRotacion, null, valorAcelerometro, valorMagnetometro);
            sensorManager.getOrientation(matrizRotacion, valorOrientacion);
            gradoActual = (int) (Math.toDegrees(SensorManager.getOrientation(matrizRotacion, valorOrientacion)[0]) + 360) % 360;
        }
        gradoActual=Math.round(gradoActual);
        imageView.setRotation(-gradoActual);

        /*
            RotateAnimation rotateAnimation= new RotateAnimation(gradoActual,cambioGrados, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            rotateAnimation.setDuration(600);
            rotateAnimation.setFillAfter(true);
            imageView.startAnimation(rotateAnimation);

            gradoActual= -cambioGrados;
            tiempoActualizacion=System.currentTimeMillis();
*/
            if (gradoActual==0){
                orientacion.setText("N");
            }else if (gradoActual<90 && gradoActual>0){
                orientacion.setText("NE");
            }else if (gradoActual==90){
                orientacion.setText("E");
            }else if (gradoActual>90 && gradoActual<180){
                orientacion.setText("SE");
            }else if (gradoActual==180){
                orientacion.setText("S");
            }else if(gradoActual>180 && gradoActual<270){
                orientacion.setText("SW");
            }else if(gradoActual==-270){
                orientacion.setText("W");
            }else if (gradoActual>270 && gradoActual<360){
                orientacion.setText("NW");
            }

            grados.setText(gradoActual+"º");
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this,acelerometro);
        sensorManager.unregisterListener(this,magnetometro);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,acelerometro,SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this,magnetometro,SensorManager.SENSOR_DELAY_GAME);
    }
    //Método star para garantizar el acceso de los sensores, muestra mensaje de error si el dispositivo no lo incorporase.
    public void start() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            if ((sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) || (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null)) {
                noSensorsAlert();
            }
            else {
                acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                magnetometro = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                tieneSensor = sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_UI);
                tieneSensor2 = sensorManager.registerListener(this, magnetometro, SensorManager.SENSOR_DELAY_UI);
            }
        }
        else{
            vectorRotacion = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            tieneSensor = sensorManager.registerListener(this, vectorRotacion, SensorManager.SENSOR_DELAY_UI);
        }
    }

    private void noSensorsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Tu dispositivo no soporta la Brújula.")
                .setCancelable(false)
                .setNegativeButton("Cerrar" +
                        "",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        alertDialog.show();
    }
}