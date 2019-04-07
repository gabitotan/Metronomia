package com.example.metronomia;

import android.annotation.SuppressLint;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final long START_TIMI_MILIS = 600000;

    int MS_MIN = 60000;
    int bpm = 100;
    int division = 1;
    int accent = 3;
    int mod_accent = 4;
    boolean bool_countdown = false;
    boolean bool_accent = false;
    boolean countdownRunning;
    long timeLeftMilis = START_TIMI_MILIS;
    CountDownTimer countdownTimer;
    Timer mainTimer;
    MyTimerTask timerTask;
    TextView bpmTextView;
    TextView countdownTextView;
    ImageButton playButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //declarare
        playButton = (ImageButton) findViewById(R.id.playButton);
        ImageButton upButton = (ImageButton) findViewById(R.id.upImageButton);
        ImageButton downButton = (ImageButton) findViewById(R.id.downImageButton);

        bpmTextView = (TextView) findViewById(R.id.bpmTextView);
        bpmTextView.setText("100");

        countdownTextView = (TextView) findViewById(R.id.countdownTextView);
        final Switch countdownSwitch = (Switch) findViewById(R.id.countdownSwitch);
        final Switch accentSwitch = (Switch) findViewById(R.id.accentSwitch);

        final Button butonPlus = (Button) findViewById(R.id.buttonPlusCount);
        final Button butonMinus = (Button)findViewById(R.id.buttonMinusCount);
        final Button butonReset = (Button) findViewById(R.id.buttonReset);

        Spinner subdivisionSpinner = (Spinner) findViewById(R.id.subdivisionSpinner);
        Spinner masuraSpinner = (Spinner) findViewById(R.id.timeMeasureSpinner);

        ArrayList<String> listaSubdivisions = new ArrayList<>(); listaSubdivisions.add("1");listaSubdivisions.add("2"); listaSubdivisions.add("3"); listaSubdivisions.add("4");
        ArrayList<String> listaMasura = new ArrayList<>(); listaMasura.add("4/4"); listaMasura.add("2/4"); listaMasura.add("3/4"); listaMasura.add("5/4");

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.spinner_items, listaSubdivisions);
        adapter1.setDropDownViewResource(R.layout.spinner_dropdown);
        subdivisionSpinner.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,  R.layout.spinner_items, listaMasura);
        adapter2.setDropDownViewResource(R.layout.spinner_dropdown);
        masuraSpinner.setAdapter(adapter2);

        //spinner item listeners
        subdivisionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String aux = parent.getItemAtPosition(position).toString();
                division = Integer.parseInt(aux);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        masuraSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                String cif = String.valueOf(text.charAt(0));
                mod_accent = Integer.parseInt(cif);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //button click listeners
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String b = (String) bpmTextView.getText();
                bpm = Integer.parseInt(b);
                bpm++;
                if(bpm >= 300)
                    bpm = 300;
                bpmTextView.setText(Integer.toString(bpm));
            }
        });

        upButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String b = (String) bpmTextView.getText();
                bpm = Integer.parseInt(b);
                bpm+=10;
                if(bpm >= 300)
                    bpm = 300;
                bpmTextView.setText(Integer.toString(bpm));
                return true;
            }
        });

        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String b = (String) bpmTextView.getText();
                bpm = Integer.parseInt(b);
                bpm--;
                if(bpm <= 20)
                    bpm = 20;
                bpmTextView.setText(Integer.toString(bpm));
            }
        });

        downButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String b = (String) bpmTextView.getText();
                bpm = Integer.parseInt(b);
                bpm-=10;
                if(bpm <= 20)
                    bpm = 20;
                bpmTextView.setText(Integer.toString(bpm));
                return true;
            }
        });

        butonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeLeftMilis += 60000;
                updateCountdown();
                int min = (int) (timeLeftMilis / 1000) / 60;
                if(min == 99) {
                    resetTimer();
                }

                if(countdownRunning) {
                    pauseTimer();
                    startTimer();
                }
            }
        });

        butonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeLeftMilis -= 60000;
                updateCountdown();
                int sec = (int) (timeLeftMilis / 1000) % 60;
                int min = (int) (timeLeftMilis / 1000) / 60;

                if(min < 0){
                    resetTimer();
                }
                if(countdownRunning) {
                    pauseTimer();
                    startTimer();
                }
            }
        });

        butonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countdownRunning) {
                    pauseTimer();
                    resetTimer();
                    startTimer();
                }
                resetTimer();
            }
        });

        countdownSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(countdownSwitch.isChecked()){
                    updateCountdown();
                    bool_countdown = true;
                    countdownTextView.setVisibility(View.VISIBLE);
                    butonPlus.setVisibility(View.VISIBLE);
                    butonMinus.setVisibility(View.VISIBLE);
                    butonReset.setVisibility(View.VISIBLE);
                }
                else{
                    bool_countdown = false;
                    countdownTextView.setVisibility(View.INVISIBLE);
                    butonPlus.setVisibility(View.INVISIBLE);
                    butonMinus.setVisibility(View.INVISIBLE);
                    butonReset.setVisibility(View.INVISIBLE);
                }
            }
        });

        accentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(accentSwitch.isChecked()){
                    bool_accent = true;
                }
                else
                    bool_accent = false;
            }
        });

        //buton principal
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mainTimer != null){
                    stopClick();
                }
                else{
                    if(bool_countdown){
                        startTimer();
                    }

                    accent = mod_accent*division - 1;
                    //TIMER PRINCIPAL
                    mainTimer = new Timer();
                    timerTask = new MyTimerTask();
                    mainTimer.scheduleAtFixedRate(timerTask, 0, MS_MIN / (bpm*division));
                    playButton.setImageResource(android.R.drawable.ic_media_pause);
                }

            }
        });
    }

    private void playSound(){
         MediaPlayer mp = MediaPlayer.create(this, R.raw.metronome_sound);

        if(bool_accent){
            if(accent % (mod_accent*division) == 0) {
                mp = MediaPlayer.create(this, R.raw.metronome_accent);
            }
        }
        else{
            mp = MediaPlayer.create(this, R.raw.metronome_sound);
        }


        //Porneste sunetul
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            if(bool_accent)
                incrAcc();
            playSound();
        }
    }

    private void incrAcc(){
        accent++;
    }

    private void startTimer(){
        countdownTimer = new CountDownTimer(timeLeftMilis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMilis = millisUntilFinished;
                updateCountdown();

            }

            @Override
            public void onFinish() {
                countdownRunning = false;
                stopClick();
                resetTimer();

            }
        }.start();

        countdownRunning = true;
    }

    private void pauseTimer(){
        countdownTimer.cancel();
        countdownRunning = false;
    }

    private void resetTimer(){
        timeLeftMilis = START_TIMI_MILIS;
        updateCountdown();
    }

    private void updateCountdown(){
        int min = (int) (timeLeftMilis / 1000) / 60;
        int sec = (int) (timeLeftMilis / 1000) % 60;
        String timeForm = String.format(Locale.getDefault(),"%02d:%02d", min, sec);
        countdownTextView.setText(timeForm);
    }

    private void stopClick(){
        if(bool_countdown){
            pauseTimer();
        }
        mainTimer.cancel();
        mainTimer = null;
        playButton.setImageResource(android.R.drawable.ic_media_play);
    }


} // END of MAIN ACTVT
