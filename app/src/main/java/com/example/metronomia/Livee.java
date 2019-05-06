package com.example.metronomia;

import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Livee extends AppCompatActivity implements ExampleDialog.ExampleDialogListener {
    int MS_MIN = 60000;
    int bpm = 100;
    int division = 1;
    int accent = 3;
    int mod_accent = 4;
    int listSelectedPosition;
    String masura;
    String numeCantec;
    boolean bool_accent = false;
    Timer mainTimer;
    MyTimerTask timerTask;
    TextView bpmTextView;
    ImageButton playButton;
    ImageButton saveButton;
    ImageButton deleteButton;
    RecyclerView myRecylerView;
    RecyclerView.Adapter adapter;
    ArrayList<Song> songList;
    Switch accentSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livee);

        Button butonPractice2 = (Button) findViewById(R.id.buttonPracticeActivity2);
        playButton = (ImageButton) findViewById(R.id.playButton3);
        saveButton = (ImageButton) findViewById(R.id.imageButtonSave);
        deleteButton = (ImageButton) findViewById(R.id.imageButtonDelete);
        ImageButton upButton = (ImageButton) findViewById(R.id.upImageButton3);
        ImageButton downButton = (ImageButton) findViewById(R.id.downImageButton3);


        bpmTextView = (TextView) findViewById(R.id.bpmTextView3);
        bpmTextView.setText("100");

        accentSwitch = (Switch) findViewById(R.id.accentSwitch2);

        Spinner subdivisionSpinner = (Spinner) findViewById(R.id.subdivisionSpinner2);
        Spinner masuraSpinner = (Spinner) findViewById(R.id.timeMeasureSpinner2);

        ArrayList<String> listaSubdivisions = new ArrayList<>(); listaSubdivisions.add("1");listaSubdivisions.add("2"); listaSubdivisions.add("3"); listaSubdivisions.add("4");
        ArrayList<String> listaMasura = new ArrayList<>(); listaMasura.add("4/4"); listaMasura.add("2/4"); listaMasura.add("3/4"); listaMasura.add("5/4");

        final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.spinner_items, listaSubdivisions);
        adapter1.setDropDownViewResource(R.layout.spinner_dropdown);
        subdivisionSpinner.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,  R.layout.spinner_items, listaMasura);
        adapter2.setDropDownViewResource(R.layout.spinner_dropdown);
        masuraSpinner.setAdapter(adapter2);

        //lista
        myRecylerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        myRecylerView.setHasFixedSize(true);
        myRecylerView.setLayoutManager(new LinearLayoutManager(this));

        songList = new ArrayList<>();

        adapter = new MyAdapter(songList, this);
        myRecylerView.setAdapter(adapter);

        //initializare lista din baza de date
        initListaCantece();

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
                masura = cif;
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
                    mainTimer.cancel();
                    mainTimer = null;
                    playButton.setImageResource(android.R.drawable.ic_media_play);
                }
                else{

                    accent = mod_accent*division - 1;
                    //TIMER PRINCIPAL
                    mainTimer = new Timer();
                    timerTask = new MyTimerTask();
                    mainTimer.scheduleAtFixedRate(timerTask, 0, MS_MIN / (bpm*division));
                    playButton.setImageResource(android.R.drawable.ic_media_pause);
                }

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExampleDialog exampleDialog = new ExampleDialog();
                exampleDialog.show(getSupportFragmentManager(), "example dialog");
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ID = songList.get(listSelectedPosition).getId();
                songList.remove(listSelectedPosition);
                myRecylerView.removeViewAt(listSelectedPosition);
                adapter.notifyItemRemoved(listSelectedPosition);
                adapter.notifyItemRangeChanged(listSelectedPosition, songList.size());

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Songs").child(ID);

                myRef.removeValue();
            }
        });

        butonPractice2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mainTimer!= null){
                    mainTimer.cancel();
                }
                finish();
            }
        });

        mLiveObj = this;


    }

    private void playSound(){
        MediaPlayer mp = MediaPlayer.create(this, R.raw.metronome_sound);

        if(bool_accent){
            if(accent % (mod_accent*division) == 0) {
                mp = MediaPlayer.create(this, R.raw.metronome_accent);
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                    }
                });
            }
            else{
                mp = MediaPlayer.create(this, R.raw.metronome_sound);
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                    }
                });
            }
        }
        else{
            mp = MediaPlayer.create(this, R.raw.metronome_sound);
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
        }

    }

    public static Livee mLiveObj;
    public static Livee getInstance(){
        return  mLiveObj;
    }

    public void setSongView(String b, int i, boolean a){
        bpm = Integer.parseInt(b);
        if(bpm >= 300)
            bpm = 300;
        bpmTextView.setText(Integer.toString(bpm));

        listSelectedPosition = i;

        if(a){
            accentSwitch.setChecked(true);
        }
        else{
            accentSwitch.setChecked(false);
        }

        if(mainTimer!=null){
            mainTimer.cancel();
            mainTimer = new Timer();
            timerTask = new MyTimerTask();
            mainTimer.scheduleAtFixedRate(timerTask, 0, MS_MIN / (bpm*division));
        }
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            if(bool_accent)
                accent++;

            playSound();

        }
    }

    Song song;


    @Override
    public void applyText(String songName) {
        numeCantec = songName;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("Songs");
        String id = myRef.push().getKey();

        song = new Song(numeCantec, Integer.toString(bpm), bool_accent, id);
        songList.add(song);
        adapter.notifyDataSetChanged();

        myRef.child(id).setValue(song);

    }

    public void initListaCantece(){
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        final Query myQuery = mRootRef.child("Songs").orderByChild("bpm");
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for(DataSnapshot songSnap: dataSnapshot.getChildren()) {
                        Song s = songSnap.getValue(Song.class);
                        songList.add(s);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

}
