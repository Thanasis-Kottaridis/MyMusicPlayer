package com.mymusicplayer.kottarido.unipi.mymusicplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    // favorite songs
    private List<Song> favoriteSongs;

    //gia to RecycleView
    private RecyclerView favoriteSongsView;
    private RecyclerView.Adapter favoriteAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        // loads songs from SP
        favoriteSongs = ShearedPreferencesHelper.loadMySongs(this);
        // kanei order tin favoriteSongs by total plays
        Collections.sort(favoriteSongs);
        Collections.reverse(favoriteSongs);


        // kanei setup to recycler view
        //vriskoume to SongsView
        favoriteSongsView= findViewById(R.id.FavoriteSongsView);

        mLayoutManager = new LinearLayoutManager(this);
        favoriteSongsView.setLayoutManager(mLayoutManager);

        //kanoume instantiate ton adapter pou dimiourgisame
        //kai pername ton adapter sto songs view
        //me auton ton tropo leme pos na xiristei ta items
        favoriteAdapter = new FavoriteAdapter(favoriteSongs);
        favoriteSongsView.setAdapter(favoriteAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
    }
}
