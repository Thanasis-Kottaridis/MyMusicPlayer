package com.mymusicplayer.kottarido.unipi.mymusicplayer;

public class Song implements Comparable<Song>{

    private long id;
    private String title;
    private String artist;
    private String musicKind;
    private String yearReleased;
    private String language;
    private boolean loadedInApp;
    private Integer totalPlays;

    public Song(long id, String title, String artist) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        musicKind = "<UNKNOWN>";
        yearReleased = "<UNKNOWN>";
        language = "<UNKNOWN>";
        loadedInApp = false;
        totalPlays = 0;
    }


    public int getTotalPlays() {
        return totalPlays;
    }

    public void setTotalPlays(int totalPlays) {
        this.totalPlays = totalPlays;
    }

    public void increaseTotalPlays() {
        totalPlays = totalPlays + 1;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getMusicKind() {
        return musicKind;
    }

    public void setMusicKind(String musicKind) {
        this.musicKind = musicKind;
    }

    public String getYearReleased() {
        return yearReleased;
    }

    public void setYearReleased(String yearReleased) {
        this.yearReleased = yearReleased;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isLoadedInApp() {
        return loadedInApp;
    }

    public void setLoadedInApp(boolean loadedInApp) {
        this.loadedInApp = loadedInApp;
    }

    @Override
    public int compareTo(Song song) {
        return this.totalPlays.compareTo(song.getTotalPlays());
    }
}
