package com.mymusicplayer.kottarido.unipi.mymusicplayer;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ShearedPreferencesHelper {

    //preference file
    public static final String PREF_FILE = "shared preferences";
    //user profile key
    public static final String MY_SONGS_KEY = "my songs";

    //tin kanw static gia na mporw na tin kalesw xoris antikimeno
    //etsi oste na apofigo to instantiate tou helper kathe fora pou thelw na kanw read i write
    static void saveMySongs(Context context, List<Song> mySongs){
        //apothikevei tin lista me ta tragoudia sto json
        SharedPreferences preferences = context.getSharedPreferences(PREF_FILE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mySongs);
        editor.putString(MY_SONGS_KEY,json);
        editor.commit();
    }

        static List<Song> loadMySongs(Context context){
            //fortonei to pref file
            SharedPreferences preferences = context.getSharedPreferences(PREF_FILE,Context.MODE_PRIVATE);
            //ftisxnei ena neo gson obj
            Gson gson = new Gson();
            //diavazei tin sting timi pou einai apotikeumenei sto pref file
            String json = preferences.getString(MY_SONGS_KEY,null);
            //dilwnw ton tipo pou tha ginei metatropei to string pou diavasa
            Type type = new TypeToken<ArrayList<Song>>(){}.getType();
            //ftiaxnw to gason metatrepontas to string sto type pou orisa
            return gson.fromJson(json,type);
    }
}
