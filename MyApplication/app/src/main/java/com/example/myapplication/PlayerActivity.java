package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

import static com.example.myapplication.AlbumDetails.albumSongs;
import static com.example.myapplication.MainActivity.musicFiles;
import static com.example.myapplication.MainActivity.repeat;
import static com.example.myapplication.MainActivity.shuffle;
import static com.example.myapplication.MusicAdapter.musicFile;

public class PlayerActivity extends AppCompatActivity
        implements MediaPlayer.OnCompletionListener {

    TextView songName, artistName, playTime, endTime;
    ImageView coverArt, btnNext, btnPrevious, btnBack,btnShuffle, btnRepeat, btnMenu;
    SeekBar durationBar;
    FloatingActionButton btnPlayPause;

    static ArrayList<MusicFiles> dataSongs = new ArrayList<>();
    static MediaPlayer mediaPlayer;
    static Uri uri;

    int positionMusic = -1;

    private Handler handler = new Handler();
    private Thread playThread, prevThread, nextThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initViews(); //Setup ID for UI
        
        setupMusicPosition();

        durationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000); //sec -> millisec
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null){
                    int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    durationBar.setProgress(currentPosition);
                    playTime.setText(formatTime(currentPosition));
                }
                handler.postDelayed(this, 500); //delay 0.5 sec
            }
        });

        btnShuffle.setOnClickListener(v -> {
            if (shuffle){
                shuffle = false;
                btnShuffle.setImageResource(R.drawable.ic_shuffle_off);
            } else {
                shuffle = true;
                btnShuffle.setImageResource(R.drawable.ic_shuffle_on);
            }
        });

        btnRepeat.setOnClickListener(v ->{
            if (repeat){
                repeat = false;
                btnRepeat.setImageResource(R.drawable.ic_repeat_off);
            } else {
                repeat = true;
                btnRepeat.setImageResource(R.drawable.ic_repeat_on);
            }
        });

    } //onCreate

    private String formatTime(int currentPosition) {

        String minutes = String.valueOf(currentPosition / 60);
        String seconds = String.valueOf(currentPosition % 60);
        //Log.d("TIME", minutes + ":"+seconds);
        if (seconds.length() == 1){
            return minutes + ":" + "0" + seconds;
        }
        return minutes + ":" + seconds;
    } //formatTime

    private void setupMusicPosition() {
        positionMusic = getIntent().getIntExtra("position", -1);
        //Log.d("TAG", dataSongs.get(positionMusic).getTitle());

        String sender = getIntent().getStringExtra("sender");
        if (sender != null && sender.equals("albumDetails")){
            dataSongs = albumSongs;
        }
        else {
            dataSongs = musicFile;
        } //get data form Main Activity (static variable)

        positionMusic = getIntent().getIntExtra("position", -1);
        //Log.d("TAG", dataSongs.get(positionMusic).getTitle());

        //songName.setText(dataSongs.get(positionMusic).getTitle());
        //artistName.setText(dataSongs.get(positionMusic).getArtists());

        if (dataSongs != null) {  //SET UP DATA FOR PER MUSIC
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(dataSongs.get(positionMusic).getPath());
        }
        if (mediaPlayer !=  null){  //The condition that have song which is playing
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        } else {  //The condition that don't have a song is playing
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }
        durationBar.setMax(mediaPlayer.getDuration() / 1000);  //millisecon -> secon
        Log.d("TAG", String.valueOf(mediaPlayer.getDuration() / 1000));

        setupUIMusic(positionMusic);
    } //setupMusicPosition

    private void setupUIMusic (int position){
        Uri uri = Uri.parse(dataSongs.get(position).getPath());
        metaData(uri);
        songName.setText(dataSongs.get(position).getTitle());
        artistName.setText(dataSongs.get(position).getArtists());
    }

    private void initViews() {

        //Information illustrate
        songName = findViewById(R.id.song_name);
        artistName = findViewById(R.id.song_art);
        playTime = findViewById(R.id.tvPlayTime);
        endTime = findViewById(R.id.tvEndTime);
        coverArt = findViewById(R.id.cover_art);
        durationBar = findViewById(R.id.seekBar);

        //Function button
        btnNext = findViewById(R.id.id_next);
        btnPrevious = findViewById(R.id.id_prev);
        btnBack = findViewById(R.id.buttonBack);
        btnShuffle = findViewById(R.id.id_shuffle);
        btnRepeat = findViewById(R.id.id_repeat);
        btnPlayPause = findViewById(R.id.play_pause_music);

        btnMenu = findViewById(R.id.buttonMenu);
    } //initView

    private void metaData (Uri uri){  //set image
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int duration =  Integer.parseInt(dataSongs.get(positionMusic).getDuration()) / 1000;
        endTime.setText(formatTime(duration));
        byte[] art = retriever.getEmbeddedPicture();
        if (art != null){
            Glide.with(this)
                    .asBitmap()
                    .load(art)
                    .into(coverArt);
        } else {
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.images)
                    .into(coverArt);
        }

    } //metaData

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if (shuffle){
            btnShuffle.setImageResource(R.drawable.ic_shuffle_on);
        } else {
            btnShuffle.setImageResource(R.drawable.ic_shuffle_off);
        }

        if (repeat){
            btnRepeat.setImageResource(R.drawable.ic_repeat_on);
        } else {
            btnRepeat.setImageResource(R.drawable.ic_repeat_off);
        }

        playThreadBtn();
        nextThreadbtn();
        prevThreadBtn();
    } //onResume

    private void playThreadBtn() {
        playThread = new Thread(){
            @Override
            public void run() {
                super.run();

                btnPlayPause.setOnClickListener( v ->{
                    btnPlayPauseClicked();
                });
            }
        };
        playThread.start();
    }

    private void nextThreadbtn() {
        nextThread = new Thread(){
            @Override
            public void run() {
                super.run();

                btnNext.setOnClickListener( v ->{
                    btnNextClicked();
                });
            }
        };
        nextThread.start();
    }

    private void prevThreadBtn() {
        prevThread = new Thread(){
            @Override
            public void run() {
                super.run();

                btnPrevious.setOnClickListener( v ->{
                    btnPrevClicked();
                });
            }
        };
        prevThread.start();
    }

    private void btnPlayPauseClicked() {
        if (mediaPlayer.isPlaying()){
            btnPlayPause.setImageResource(R.drawable.ic_play);
            mediaPlayer.pause();
            durationBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){
                        int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        durationBar.setProgress(currentPosition);
                    }
                    handler.postDelayed(this, 1000); //delay 1 sec
                }
            });
        } else {
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
            durationBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){
                        int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        durationBar.setProgress(currentPosition);
                    }
                    handler.postDelayed(this, 1000); //delay 1 sec
                }
            });
        }
    } //voidButtonPlayClicked

    private void btnPrevClicked() {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffle && !repeat){
                positionMusic = getRandom(dataSongs.size());
            } else if (!shuffle && !repeat) {
                positionMusic = (((positionMusic - 1 ) < 0) ?  (dataSongs.size()-1) : (positionMusic - 1));
            }

            uri = Uri.parse(dataSongs.get(positionMusic).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            setupUIMusic(positionMusic);

            durationBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){
                        int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        durationBar.setProgress(currentPosition);
                    }
                    handler.postDelayed(this, 1000); //delay 1 sec
                }
            });
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
        } else {
            mediaPlayer.release();
            if (shuffle && !repeat){
                positionMusic = getRandom(dataSongs.size());
            } else if (!shuffle && !repeat) {
                positionMusic = (((positionMusic - 1 ) < 0) ?  (dataSongs.size()-1) : (positionMusic - 1));
            }

            uri = Uri.parse(dataSongs.get(positionMusic).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            setupUIMusic(positionMusic);

            durationBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){
                        int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        durationBar.setProgress(currentPosition);
                    }
                    handler.postDelayed(this, 1000); //delay 1 sec
                }
            });
            btnPlayPause.setImageResource(R.drawable.ic_play);

        }

    } //voidButtonPlayClicked

    private void btnNextClicked() {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffle && !repeat){
                positionMusic = getRandom(dataSongs.size());
            } else if (!shuffle && !repeat) {
                positionMusic = ((positionMusic + 1) % dataSongs.size());
            }
            uri = Uri.parse(dataSongs.get(positionMusic).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            setupUIMusic(positionMusic);

            durationBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){
                        int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        durationBar.setProgress(currentPosition);
                    }
                    handler.postDelayed(this, 1000); //delay 1 sec
                }
            });
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
        } else {
            mediaPlayer.release();
            if (shuffle && !repeat){
                positionMusic = getRandom(dataSongs.size());
            } else if (!shuffle && !repeat) {
                positionMusic = ((positionMusic + 1) % dataSongs.size());
            }
            uri = Uri.parse(dataSongs.get(positionMusic).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            setupUIMusic(positionMusic);

            durationBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){
                        int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        durationBar.setProgress(currentPosition);
                    }
                    handler.postDelayed(this, 1000); //delay 1 sec
                }
            });
            btnPlayPause.setImageResource(R.drawable.ic_play);

        }
    } //voidButtonPlayClicked

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i);
    }



    @Override
    public void onCompletion(MediaPlayer mp) {
       this.btnNextClicked();
       if (mediaPlayer != null){
           mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
           mediaPlayer.start();
           mediaPlayer.setOnCompletionListener(this);
       }
    }
}