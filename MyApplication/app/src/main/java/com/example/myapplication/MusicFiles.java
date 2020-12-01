package com.example.myapplication;

public class MusicFiles {
    private String path;
    private String title;
    private String artists;
    private String album;
    private String duration;

    public MusicFiles(String path, String title, String artists, String album, String duration) {
        this.path = path;
        this.title = title;
        this.artists = artists;
        this.album = album;
        this.duration = duration;
    }

    public MusicFiles() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
