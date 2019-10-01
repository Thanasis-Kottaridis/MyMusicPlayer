package com.mymusicplayer.kottarido.unipi.mymusicplayer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class SpinnerDialogClass extends AppCompatDialogFragment {

    // gia ta views tou dialog
    private EditText artistText;
    private Spinner genderSpinner;
    private EditText yearReleasedText;
    private EditText languageText;

    private long songID;
    private String Title;
    private String songArtist;
    private String yearRelease;
    private String songLanguage;
    private String [] musicPreferences;
    private DialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // fortonei ta eidoi mousikis apo to strings.xml
        musicPreferences = getResources().getStringArray(R.array.musicPreferences);

        // fortonei ta ipolipa stixia tou tragoudiou apo to bundle
        songID = getArguments().getLong(MySongsActivity.BUNDLE_CODE_SONG_ID);
        Title = getArguments().getString(MySongsActivity.BUNDLE_CODE_TITLE);
        songArtist = getArguments().getString(MySongsActivity.BUNDLE_CODE_ARTIST);
        yearRelease = getArguments().getString(MySongsActivity.BUNDLE_CODE_YEAR_RELEASE);
        songLanguage = getArguments().getString(MySongsActivity.BUNDLE_CODE_LANGUAGE);

        // creates the alert dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // kanei inflate to layout tou dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_spinner_dialog,null);

        // vriskei ta views tou dialog
        artistText = view.findViewById(R.id.ArtistEditText);
        genderSpinner = view.findViewById(R.id.SongKindSpinner);
        yearReleasedText = view.findViewById(R.id.yearReleaseEditText);
        languageText = view.findViewById(R.id.songLanguageEditText);

        //enimeronei ta views me tis rimes pou einai kataxorimenes sto sistima
        artistText.setHint(songArtist);
        yearReleasedText.setHint(yearRelease);
        languageText.setHint(songLanguage);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,musicPreferences);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        builder.setView(view)
                .setTitle(Title)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String artist = artistText.getText().toString();
                        String songGender = genderSpinner.getSelectedItem().toString();
                        String year = yearReleasedText.getText().toString();
                        String language = languageText.getText().toString();
                        listener.applyText(new String []{artist,songGender,year,language}, songID);
                    }
                });

        return builder.create();
    }

    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (DialogListener) context;
        } catch (ClassCastException e) {
            //throw new ClassCastException(context.toString() + "Must implement ExampleDialogListener");
        }
    }

    //ftiaxnw ena functional interface
    public interface DialogListener{
        void applyText(String [] Answers , long songID);
    }
}
