package com.mymusicplayer.kottarido.unipi.mymusicplayer;

import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.internal.bind.TreeTypeAdapter;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    public static final String EXTRA_MY_SONGS_UPDATE = "com.unipi.kottarido.mymusicplayer.EXTRA_MY_SONGS_UPDATE";

    //REQUEST CODE
    public static final int GO_TO_ADD_SONG_REQUEST_CODE = 1;
    public static final int GO_TO_FAVORITES_REQUEST_CODE = 3;

    //gia to Navication View
    private NavigationView navigationView;
    private Toolbar CustomToolbar;
    private DrawerLayout drawer;

    // gia to recycler view
    private RecyclerView playlistView;
    private RecyclerView.Adapter playlistAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    // gia to music player
    private ImageView playPauseButton;
    private ImageView playNext;
    private ImageView playPrevious;
    private ImageView playShuffle;
    private SeekBar seekBar;
    private TextView timePlayed;
    private TextView timeRemaining;

    //MediaPlayer
    private MediaPlayer mediaPlayer;
    private int totalTime;

    // seekBar Thread
    private Thread thread;

    //My songs
    private List<Song> mySongs;

    // flags gia tin anapagogi ton komatiwn
    private int songPos;
    private boolean setup;
    private boolean playRandom;
    //random generator
    private Random random;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Elenxei an exei ginei setup i efarmogi
        if (ShearedPreferencesHelper.loadMySongs(this) != null) {
            mySongs = ShearedPreferencesHelper.loadMySongs(this);
            songPos = 0;
            setup = true;
            playRandom = false;

            //
            //kanei set up ton MediaPlayer
            //
            mediaPlayer = new MediaPlayer();
            //dilonei to idos pou tha anaparagei o MediaPlayer
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //orizoume tous listener pou exoun oristei sto telos tis class
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
        } else {
            startActivity(new Intent(this, SetUpProfileActivity.class));
            finish();
        }

        //
        // VRISKEI TA VIEWS
        //
        playlistView = findViewById(R.id.playlistView);
        playPauseButton = findViewById(R.id.PlayPauseSongButton_MusicPlayer);
        playNext = findViewById(R.id.PlayNext_MusicPlayer);
        playPrevious = findViewById(R.id.PlayPrevious_MusicPlayer);
        seekBar = findViewById(R.id.seekBar);
        timePlayed = findViewById(R.id.TimePlayedText_MusicPlayer);
        timeRemaining = findViewById(R.id.TimeRemainingText_MusicPlayer);
        playShuffle = findViewById(R.id.ShufflePlay_MusicPlayer);

        //
        //KANEI SETUP TON NAVIGATION DRAWER
        //
        //prosthetei to custom Toolbar sto activity
        CustomToolbar = findViewById(R.id.CustomToolbar);
        CustomToolbar.setTitle("");
        setSupportActionBar(CustomToolbar);

        //prosthetei to menu pou ftia3ame sto custom Activity bar mas ??? to kanei me alo tropo apo ton sinithismeno
        drawer = findViewById(R.id.drawer_layout);

        //auto einai gia na kanei to icon na peristrefete otan emfanizete o drawer ???
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, CustomToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //arxilopoiei to NavigationView
        navigationView = findViewById(R.id.nav_view);


        //
        //KANEI SETUP TO RECYCLER VIEW
        //
        mLayoutManager = new LinearLayoutManager(this);
        playlistView.setLayoutManager(mLayoutManager);

        //kanoume instantiate ton adapter pou dimiourgisame
        //kai pername ton adapter sto songs view
        //me auton ton tropo leme pos na xiristei ta items
        playlistAdapter = new PlaylisyAdapter(mySongs,songPos);
        playlistView.setAdapter(playlistAdapter);



        //ftiaxnw event sto onItemClick tou Recycle view (dil tou interface pou ftia3ame ston adapter)
        //to xrisimopoiooume opos xrisimopioume ta apla onclick
        ((PlaylisyAdapter) playlistAdapter).setOnItemClickListener(new PlaylisyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                songPos = pos;
                playSongs();

            }
        });

        //
        //oriszei tin simperifora ton views tou music player
        //
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    //an den paizei tragoudi
                    mediaPlayer.start();
                    playPauseButton.setImageResource(R.drawable.ic_pause_circle);
                } else {
                    mediaPlayer.pause();
                    playPauseButton.setImageResource(R.drawable.ic_play_circle);
                }
            }
        });

        //sto onClick tou playNext button
        playNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextMethod();
            }
        });

        //sto onClick tou PlayPrev button
        playPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreviousMethod();
            }
        });

        // sto onClick tou play shuffle
        playShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playRandom) {
                    playRandom = false;
                    playShuffle.setImageResource(R.drawable.ic_not_play_shuffle);
                }
                else {
                    playRandom = true;
                    playShuffle.setImageResource(R.drawable.ic_play_shuffle);
                    random = new Random();
                }
            }
        });

        //Seek Bar
        //rithmizoume tin seek bar na proxoraei mazi me to tragoudi
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.start();
            }
        });

        //Thread (Update seek bar and time Labels)

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer != null) {
                    try {
                        Message msg = new Message();
                        msg.what = mediaPlayer.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                    }
                }
            }
        });

        thread.start();

        //ftiaxnw listener gia otan ginete select kapio item tou nav view
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent;
                switch (menuItem.getItemId()) {
                    //navMenu settings
                    //navMenu logout
                    case R.id.navFavorites:
                        //stamataei ton media player
                        mediaPlayer.stop();

                        intent = new Intent(getApplicationContext(), FavoriteActivity.class);
                        startActivity(intent);

                        finish();
                        break;
                    case R.id.navMusicLibrary:
                        //stamataei ton media player
                        mediaPlayer.stop();

                        intent = new Intent(getApplicationContext(), MySongsActivity.class);
                        intent.putExtra(EXTRA_MY_SONGS_UPDATE, true);
                        startActivity(intent);

                        finish();
                        break;
                    case R.id.navPlaylist:
                        break;
                    case R.id.navAboutUs:
                        ShowMessage("About Us", "MyMusicPlayer Application Developer: \n" +
                                "Kottaridis Athanasios AM: P15059 \n");
                        break;
                    case R.id.navPrivacyPolicy:
                        ShowMessage("Privacy Policy", "This application has been made for educational purposes and is the Assignment for the semester of the subject Object Oriented Application Creation for the academic year 2018-2019");

                }
                return true;
            }
        });
    }

    //ftiaxnoume ton handler pou elenxei to thread tou seek bar
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            //update seek bar
            seekBar.setProgress(currentPosition);

            //update labels
            String playedTime = createTimeLabel(currentPosition);
            timePlayed.setText(playedTime);

            String remainingTime = createTimeLabel(totalTime - currentPosition);
            timeRemaining.setText("- " + remainingTime);
        }
    };

    //methodos pou ftiaxnei ti timi gia ta time labels
    // diladi ftiaxnei to format ton timon pou tha emfanizonte sta labels
    private String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }

    public void playSongs() {
        //kanw reset ton player
        mediaPlayer.reset();

        //vres to tragoudi apo tin list
        Song playSong = mySongs.get(songPos);
        //vriskei to uri tou tragoudiou apo to storage
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, playSong.getId());

        try {
            mediaPlayer.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        // au3anei ton arithmo ton foron pou exei paixtei kata 1
        // kai enimeronei kai ta SP
        mySongs.get(songPos).increaseTotalPlays();
        ShearedPreferencesHelper.saveMySongs(getApplicationContext(),mySongs);


        // enimeronei to recycler view
        //show dialog
        ((PlaylisyAdapter)playlistAdapter).setPos(songPos);
        playlistView.setAdapter(playlistAdapter);

        //kanei prepare to media player
        mediaPlayer.prepareAsync();
    }

    //method gia na kanei display ena message dialog
    public void ShowMessage(String title, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(text);
        builder.show();
    }

    //ti tha ginete sto patima to next button
    private void playNextMethod() {

        // an prokite gia tixea anaparagogi komatiou
        // vriskei ena tixeo komati arkei na einai diaforetiko apo auto pou paizei twra
        if (playRandom) {
            int n = 0;
            do {
                n = random.nextInt(mySongs.size());
            } while (n == songPos);

            songPos = n;
        } else {
            songPos++;
            //ama to pos einai sto telefteo tragoudi tis lists mas 3anapaei apo to 0
            if ((songPos > mySongs.size() - 1))
                songPos = 0;

        }
        // kalei tin methodo anaparagogeis tragoudiou
        playSongs();
    }

    //ti tha ginete sto patima tou previous button
    private void playPreviousMethod() {

        songPos--;
        //ama imaste sto proto tagoudi tis playlist mas paei sto telefteo
        if (songPos < 0)
            songPos = mySongs.size() - 1;
        playSongs();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if(!setup) {
            //an paizei online songs
            playNextMethod();
        }
        else {
            setup = false;
            playSongs();
        }

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        //start playback otan exei fortothei to trafoudi
        mediaPlayer.seekTo(0);
        //diavazei ton xrono diarkias tou tragoudiou
        totalTime = mediaPlayer.getDuration();
        //vazei ton xrono tou tragoudiou sto seekbar
        seekBar.setMax(totalTime);

        mediaPlayer.start();
        playPauseButton.setImageResource(R.drawable.ic_pause_circle);
    }

    // TI THA GINETE OTAN SINEXIZETE I ACTIVITY
    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }

    // TI THA GINETE STO PATIMA TOU BACK TOU KINITOU
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mediaPlayer.stop();
    }
}
