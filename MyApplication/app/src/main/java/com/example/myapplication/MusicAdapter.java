package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<MusicFiles> musicFiles;

    MusicAdapter (Context context, ArrayList<MusicFiles> musicFiles){
        this.context = context;
        this.musicFiles = musicFiles;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate((R.layout.music_item),
                        parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.file_name.setText(musicFiles.get(position).getTitle());
        byte[] image = getAlbumArt(musicFiles.get(position).getPath());
        if (image != null){
            Glide.with(context).asBitmap()
                    .load(image).into(holder.image);
        }
        else {
            Glide.with(context)
                    .load(R.drawable.images)
                    .into(holder.image);
        }

        holder.itemView.setOnClickListener(v->{
            Intent intent = new Intent(context, PlayerActivity.class);

            //Put position information of musicfiles
            intent.putExtra("position", position);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return musicFiles.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{

        TextView file_name;
        ImageView image;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            file_name = itemView.findViewById(R.id.music_file_name);
            image = itemView.findViewById(R.id.music_image);

        }
    }

    private byte[] getAlbumArt (String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}
