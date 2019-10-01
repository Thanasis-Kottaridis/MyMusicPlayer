package com.mymusicplayer.kottarido.unipi.mymusicplayer;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private List<Song> favoriteSongs;

    public FavoriteAdapter(List<Song> favoriteSongs){
        this.favoriteSongs = favoriteSongs;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.favorite_song_item,viewGroup,false);
        return new FavoriteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int i) {

        if(i<3) {
            Song song = favoriteSongs.get(i);
            holder.Title.setText(song.getTitle());
            holder.Artist.setText("Artist: " + song.getArtist());
            holder.Gender.setText("Song Gender: " + song.getMusicKind());
            holder.Language.setText("Language: " + song.getLanguage());
            holder.TotalPlays.setText("Total Plays: " + song.getTotalPlays());
        }
    }

    @Override
    public int getItemCount() {
        return favoriteSongs.size();
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder{

        private TextView Title;
        private TextView Artist;
        private TextView Gender;
        private TextView Language;
        private TextView TotalPlays;

        public FavoriteViewHolder(@NonNull View view) {
            super(view);

            Title = view.findViewById(R.id.song_title);
            Artist = view.findViewById(R.id.song_artist);
            Gender = view.findViewById(R.id.song_gender);
            Language = view.findViewById(R.id.song_language);
            TotalPlays = view.findViewById(R.id.song_plays);

        }
    }
}
