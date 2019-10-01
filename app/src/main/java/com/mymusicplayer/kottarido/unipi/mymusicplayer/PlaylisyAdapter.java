package com.mymusicplayer.kottarido.unipi.mymusicplayer;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PlaylisyAdapter extends RecyclerView.Adapter<PlaylisyAdapter.PlaylistViewHolder> {

    // lista me ta song
    private List<Song> songs;
    // position tou current song
    private int pos;

    private OnItemClickListener listener;

    // CONSTRUCTOR TIS CLASS
    public PlaylisyAdapter(List<Song> songs, int pos){
        this.songs = songs;
        this.pos = pos;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.playlist_song_item,viewGroup,false);
            return new PlaylistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int i) {
        Song song = songs.get(i);
        holder.Title.setText(song.getTitle());
        holder.Artist.setText("Artist: "+song.getArtist());

        if (i == pos)
            holder.PlaySong.setImageResource(R.drawable.ic_play);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    class PlaylistViewHolder extends RecyclerView.ViewHolder{

        private ImageView PlaySong;
        private TextView Title;
        private TextView Artist;

        public PlaylistViewHolder(@NonNull View view) {
            super(view);

            PlaySong = view.findViewById(R.id.PlaySong);
            Title = view.findViewById(R.id.SongTitle);
            Artist = view.findViewById(R.id.SongArtist);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (listener != null && pos != RecyclerView.NO_POSITION){
                        listener.onItemClick(pos);
                    }
                }
            });
        }
    }

    //gia na ftia3oume event sto onClick tou kathe item tou recycle view
    //prepei na ftia3oume enan listener(interface)

    public interface OnItemClickListener{
        void onItemClick(int pos);
    }

    // episis dimiourgoume kai tin methodo setOnItemClickListener
    // i opoia dexete ena instance pou kanei implement to onItemClickListener
    // gia na ipoxreosoume opoion to kalei na kanei implement to interface mas

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
