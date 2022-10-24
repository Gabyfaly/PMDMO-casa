package iesmm.pmdm.tresenrayas;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;

public class MainActivity extends AppCompatActivity {

    //atributos MediaPlayer
    private MediaPlayer mJugadorMediaPlayer;
    private MediaPlayer mfondoMediaPlayer;
    private MediaPlayer mganadorMediaPlayer;

    //Representa el estado del juego
    private JuegoTresEnRaya mJuego;

    //Botones del layout
    private Button mbotonesTablero[];

    //Texto informativo del estado del juego
    private TextView mInfoTexto;

    //Determina quién será primer turno
    private char mturno=JuegoTresEnRaya.JUGADOR;

    //Determina si ha acabado el juego
    private boolean gameOver= false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //referencia de los botones del tablero
        mbotonesTablero= new Button[JuegoTresEnRaya.DIM_TABLERO];
        mbotonesTablero[0]=(Button) findViewById(R.id.one);
        mbotonesTablero[1]=(Button) findViewById(R.id.two);
        mbotonesTablero[2]=(Button) findViewById(R.id.three);
        mbotonesTablero[3]=(Button) findViewById(R.id.four);
        mbotonesTablero[4]=(Button) findViewById(R.id.five);
        mbotonesTablero[5]=(Button) findViewById(R.id.six);
        mbotonesTablero[6]=(Button) findViewById(R.id.seven);
        mbotonesTablero[7]=(Button) findViewById(R.id.eight);
        mbotonesTablero[8] =(Button) findViewById(R.id.nine);

        //Referencia de los textos informativos del estado del juego
        mInfoTexto=(TextView) findViewById(R.id.information);

        //Ejecución inicial de la lógica del videojuego
        mJuego= new JuegoTresEnRaya();
        comenzarJuego();



        //Ejecución incial de la lógica del videojuego
        mJuego= new JuegoTresEnRaya();
    }

    private void comenzarJuego() {
        //Reincio de la lógica del tablero
        mJuego.limpiarTablero();

        //Reinicio de los botones del layout
        for (int i= 0;i < mbotonesTablero.length;i++){
            mbotonesTablero[i].setText("");
            mbotonesTablero[i].setEnabled(true);
            //El evento está asociado desde la interfaz del layout
            //FUNCION/OBJETO CALLBACK : Asocia evento al botón para JUGADOR
        }
        //TURNRO INCIAL DEL JUEGO: JUGADOR O MAQUINA
        controlarTurno();
    }



//Evento para el control del jugador para el button del tablero
    public void onclick(View boton){
        //1.Localizamos cuál  es el botón pulsado y su número de casilla correcta
        int id= boton.getId();
        String descripcionBoton= ((Button) findViewById(id)).getContentDescription().toString();
        int casilla= Integer.parseInt(descripcionBoton)-1;

        //2.Determinamos si es posible colocar la ficga en la casilla
        if(mbotonesTablero[casilla].isEnabled()){
            //Mueve y representa la ficha JUGADOR en la casilla
            colocarFichaEnTablero(JuegoTresEnRaya.JUGADOR, casilla);

        }

    }
    private void controlarTurno() {
        if(mturno==JuegoTresEnRaya.JUGADOR)
            mInfoTexto.setText(R.string.primero_jugador);
        else if(mturno== JuegoTresEnRaya.MAQUINA){
            //1. Determinamos la posicion según nivel
            int casilla= mJuego.getMovimientoMaquina();

            //2. Actualización en el layout
            colocarFichaEnTablero(JuegoTresEnRaya.MAQUINA,casilla);

            //3. Actualización del turno: JUGADOR
            if (!gameOver){
                mturno =JuegoTresEnRaya.JUGADOR;
                mInfoTexto.setText(R.string.turno_jugador);
            }
        }
    }

    private void colocarFichaEnTablero(char jugador, int casilla) {
        //1. Mueve la ficha según lógica
        mJuego.moverFicha(jugador,casilla);

        //2. Actualización y representación en el layout
        //Se desactiva el control del botón predeterminado
        mbotonesTablero[casilla].setEnabled(false);
        mbotonesTablero[casilla].setText(String.valueOf(jugador));

        //3. Se representa  la ficha
        if(jugador == JuegoTresEnRaya.JUGADOR) {
            mbotonesTablero[casilla].setBackgroundResource(R.drawable.balon);
            mJugadorMediaPlayer.start();
        }else
            mbotonesTablero[casilla].setBackgroundResource(R.drawable.guantes);

        //4. Se comprueba : ESTADO DEL JUEGO (SI AUN NO SE HA ACABADO CONTINUA)
        int estadoJuego= comprobarEstadoJuego();

        if(estadoJuego == 1 || estadoJuego ==2)
            gameOver();
        else if(estadoJuego == 0){
            if (jugador == JuegoTresEnRaya.JUGADOR)
                mturno = JuegoTresEnRaya.MAQUINA;
            else if (jugador==JuegoTresEnRaya.MAQUINA)
                mturno=JuegoTresEnRaya.JUGADOR;

            //5. siguiente turno
            controlarTurno();
        }
    }

    private void gameOver() {
        //Actualizo la variable del control de finalización del juego
        gameOver=true;

        //Reinicio de los botones del layout a desactivados
        for (int i =0; i < mbotonesTablero.length;i++){
            mbotonesTablero[i].setEnabled(false);
        }

    }

    private int comprobarEstadoJuego() {
        //1. Comprobar el estado principal del tablero
        int estado = mJuego.comprobarGanador();

        //2. Representar estado del juego
        if (estado == 1){
            mInfoTexto.setText(R.string.result_human_wins);
        mganadorMediaPlayer.start();
        }else if (estado==2)
            mInfoTexto.setText(R.string.result_computer_wins);

        return estado;
    }
    @Override
    protected void onResume(){
        super.onResume();

        //Activación del efecto de sonido
        mJugadorMediaPlayer =MediaPlayer.create(this, R.raw.arbitro);

        //Activación de la música de fondo
        mfondoMediaPlayer= MediaPlayer.create(this,R.raw.fondo);

        //Activación de la música de win
        mganadorMediaPlayer= MediaPlayer.create(this, R.raw.aplausos);

        //Comienzo de la música de fondo en bucle
        mfondoMediaPlayer.setLooping(true);
        mfondoMediaPlayer.start();
    }

    protected void onPause(){
        super.onPause();

        //Liberar los recursos de sonido y música de fondo
        mJugadorMediaPlayer.release();
        mfondoMediaPlayer.release();
    }
}