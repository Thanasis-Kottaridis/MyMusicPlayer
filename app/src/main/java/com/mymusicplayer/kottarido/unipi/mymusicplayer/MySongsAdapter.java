package com.mymusicplayer.kottarido.unipi.mymusicplayer;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MySongsAdapter extends RecyclerView.Adapter<MySongsAdapter.MySongsHolder> {

    private List<Song> phoneSongs;

    private OnItemClickListener listener;

    //o constructor tis class
    public  MySongsAdapter(List<Song> phoneSongs){
        this.phoneSongs = phoneSongs;
    }

    //setter tou my song
    public void setPhoneSongs(List<Song> phoneSongs){
        this.phoneSongs = phoneSongs;
    }

    @NonNull
    @Override
    public MySongsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.song_item,viewGroup,false);
        return new MySongsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MySongsHolder holder, int i) {
        Song song = phoneSongs.get(i);
        holder.Title.setText(song.getTitle());
        holder.Artist.setText("Artist: "+song.getArtist());

        if (song.isLoadedInApp())
            holder.CheckSong.setImageResource(R.drawable.ic_file_download);
    }

    @Override
    public int getItemCount() {
        return phoneSongs.size();
    }

    class MySongsHolder extends RecyclerView.ViewHolder{

        private ImageView CheckSong;
        private TextView Title;
        private TextView Artist;

        public MySongsHolder(@NonNull View view) {
            super(view);

            CheckSong = view.findViewById(R.id.CheckSong);
            Title = view.findViewById(R.id.SongTitle);
            Artist = view.findViewById(R.id.SongArtist);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (listener != null && pos != RecyclerView.NO_POSITION){
                        listener.onItemClick(phoneSongs.get(pos));
                    }
                }
            });

        }
    }

    //gia na ftia3oume event sto onClick tou kathe item tou recycle view
    //prepei na ftia3oume enan listener(interface)

    public interface OnItemClickListener{
        void onItemClick(Song song);
    }

    // episis dimiourgoume kai tin methodo setOnItemClickListener
    // i opoia dexete ena instance pou kanei implement to onItemClickListener
    // gia na ipoxreosoume opoion to kalei na kanei implement to interface mas

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

}
