package com.mymusicplayer.kottarido.unipi.mymusicplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MySongsActivity extends AppCompatActivity implements SpinnerDialogClass.DialogListener{

    //RequestCodes
    public static final int READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1;

    //Bundle codes
    public static final String BUNDLE_CODE_TITLE = "com.unipi.kottarido.mymusicplayer.BUDNLE_CODE_TITLE";
    public static final String BUNDLE_CODE_ARTIST = "com.unipi.kottarido.mymusicplayer.BUDNLE_CODE_ARTIST";
    public static final String BUNDLE_CODE_SONG_ID = "com.unipi.kottarido.mymusicplayer.BUDLE_SONG_ID";
    public static final String BUNDLE_CODE_YEAR_RELEASE = "com.unipi.kottarido.mymusicplayer.BUNDLE_CODE_YEAR_RELEASE";
    public static final String BUNDLE_CODE_LANGUAGE = "com.unipi.kottarido.mymusicplayer.BUNDLE_CODE_LANGUAGE";


    private Intent intent;

    private List<Song> phoneSongs;
    private List<Song> mySongs;

    //gia to RecycleView
    private RecyclerView songsView;
    private RecyclerView.Adapter mySongsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //metavliti gia na 3exorisoume an einai proto set up i oxi
    private boolean firstSetUp;
    //gia na 3eroume an exei ginei update i oxi
    private boolean updated;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_songs);

        //gia na emfanistei to back sto menu
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //fortonei to intent
        intent = getIntent();

        // elenxei an to intent exei extras. An exei simenei oti den einai to proto setUp
        // Ara prokite o xristis na kanei kapoio update
        if (intent.hasExtra(MainActivity.EXTRA_MY_SONGS_UPDATE)) {
            firstSetUp = false;
            updated = false;
            mySongs = ShearedPreferencesHelper.loadMySongs(this);
            phoneSongs = new ArrayList<>();
            CheckForPermission();
            getSupportActionBar().setTitle("Songs");
        } else {
            firstSetUp = true;
            phoneSongs = new ArrayList<>();
            mySongs = new ArrayList<>();
            CheckForPermission();
            getSupportActionBar().setTitle("Step 2: Set Up Your Songs");
        }

        //vriskoume to SongsView
        songsView = findViewById(R.id.SongsView);

        mLayoutManager = new LinearLayoutManager(this);
        songsView.setLayoutManager(mLayoutManager);

        //kanoume instantiate ton adapter pou dimiourgisame
        //kai pername ton adapter sto songs view
        //me auton ton tropo leme pos na xiristei ta items
        mySongsAdapter = new MySongsAdapter(phoneSongs);
        songsView.setAdapter(mySongsAdapter);


        //ftiaxnw event sto onItemClick tou Recycle view (dil tou interface pou ftia3ame ston adapter)
        //to xrisimopoiooume opos xrisimopioume ta apla onclick

        ((MySongsAdapter) mySongsAdapter).setOnItemClickListener(new MySongsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Song song) {
                SpinnerDialogClass dialog = new SpinnerDialogClass();
                Bundle args = new Bundle();
                args.putLong(BUNDLE_CODE_SONG_ID, song.getId());
                args.putString(BUNDLE_CODE_TITLE, song.getTitle());
                args.putString(BUNDLE_CODE_ARTIST, song.getArtist());
                args.putString(BUNDLE_CODE_YEAR_RELEASE, song.getYearReleased());
                args.putString(BUNDLE_CODE_LANGUAGE, song.getLanguage());
                dialog.setArguments(args);

                dialog.show(getSupportFragmentManager(), "Set song's information");
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                phoneSongs.get(viewHolder.getAdapterPosition()).setLoadedInApp(false);
                updated = true;

                //show dialog
                ((MySongsAdapter) mySongsAdapter).setPhoneSongs(phoneSongs);
                songsView.setAdapter(mySongsAdapter);

            }
        }).attachToRecyclerView(songsView);

    }

    //methodos pou elenxei gia permission
    //kai an iparxei to permission kalei tin getSongList
    private void CheckForPermission() {
        //elenxos gia permission
        //an exei dothei to read external storage permission apo ton xristi
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            getSongList();
        else
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSongList();
                ((MySongsAdapter) mySongsAdapter).setPhoneSongs(phoneSongs);
                songsView.setAdapter(mySongsAdapter);
            } else {
                Toast.makeText(this,
                        "Read External Stroage permission is nesassary in order to load the songs from your device",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    //methodos pou fortonei ta tragoudia tou kinitou
    private void getSongList() {

        //apotikeuw se ena temp list ta id ton tragoudion
        //pou exoun fortothei stin euarmogi

        List<String> temp = new ArrayList<>();
        for (Song s : mySongs) {
            temp.add(String.valueOf(s.getId()));
        }

        String[] IdArray = new String[temp.size()];
        temp.toArray(IdArray);

        //o content resolver xriazete gia na kanw query ta tragoudia apo tin siskeui
        ContentResolver musicResolver = getContentResolver();
        //to uri den exw katalavi akrivos ti einai
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //ftiaxnei enan cursor me ta apotelesmata pou tha epistrepei to query
        //sto opoio exei oristi sto selection poia tha einai i were sinthiki tou
        //kai sto selectionArgs poies tha einai oi times pou tha parei to erotimatiko
//        IdArray = new String[1];
//        IdArray[0]="1";
//        Cursor musicCursor = musicResolver.query(musicUri,null,MediaStore.Audio.Media._ID +" != ?",IdArray,null);
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null, null);

        //i domi tou cursor miazei me pinaka sql
        //elenxoume an ta data pou peirame apo ton cursor einai egkira
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //pare tis stiles
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                phoneSongs.add(new Song(thisId, thisTitle, thisArtist));
            } while (musicCursor.moveToNext());

        }


        musicCursor.close();

        //ean den einai proto setUp
        if (!firstSetUp) {
            //elenxei pia tragoudia tou kinitou einai idi fortomena stin efarmogi
            for (Song song : phoneSongs) {
                for (Song mySong : mySongs) {
                    if (song.getId() == mySong.getId()) {
                        phoneSongs.set(phoneSongs.indexOf(song),mySong);
                    }
                }
            }
        }
    }

    //ftiaxnei to menu sto activity

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return true;
    }

//    //ti tha ginei sto onclick kathe katigorias tou menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.NextOrSaveButton:
                    mySongs = new ArrayList<>();
                    for(Song s:phoneSongs){
                        if(s.isLoadedInApp())
                            mySongs.add(s);
                    }
                    if(mySongs.size() > 0){
                        ShearedPreferencesHelper.saveMySongs(getApplicationContext(),mySongs);
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                    else {
                        ShowMessage("You need to set at least one song to the system");
                    }
                return true;
            case android.R.id.home:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("If you do so all the settings at this step will reset")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.setTitle("Are you sure you want to go to a previous step?" );
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //sto patima tou apply tou custom dialog
    @Override
    public void applyText(String[] Answers, long songID) {


        for (Song s : phoneSongs) {
            if (s.getId() == songID) {
                s.setArtist(Answers[0]);
                s.setMusicKind(Answers[1]);
                s.setYearReleased(Answers[2]);
                s.setLanguage(Answers[3]);
                s.setLoadedInApp(true);
                break;
            }
        }


        if (!this.isFinishing()) {
            //an den einai sto proto setup tote exei ginei update
            if (!firstSetUp)
                updated = true;

            //show dialog
            ((MySongsAdapter) mySongsAdapter).setPhoneSongs(phoneSongs);
            songsView.setAdapter(mySongsAdapter);
        } else {
            Toast.makeText(this, "something went wrong!", Toast.LENGTH_LONG).show();
        }


    }

    public void ShowMessage(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Alert!");
        builder.setMessage(s);
        builder.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(intent.hasExtra(MainActivity.EXTRA_MY_SONGS_UPDATE)){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
        setResult(RESULT_CANCELED);
    }
}
