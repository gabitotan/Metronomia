package com.example.metronomia;

public class Song {
    private String nume;
    private String bpm;
    private boolean accent;
    private String id;

    public Song(){}

    public Song(String nume, String bpm, boolean accent, String id) {
        this.nume = nume;
        this.bpm = bpm;
        this.accent = accent;
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public String getBpm() {
        return bpm;
    }

    public boolean getAccent(){
        return accent;
    }

    public String getId(){return id;}
}
