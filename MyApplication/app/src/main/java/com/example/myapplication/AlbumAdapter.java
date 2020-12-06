package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<MusicFiles> musicFiles;
    View view;

    public AlbumAdapter (Context context, ArrayList<MusicFiles> musicFiles){
        this.context = context;
        this.musicFiles = musicFiles;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.album_item, parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.albumName.setText(musicFiles.get(position).getAlbum());
        byte[] image = holder.getAlbumArt(musicFiles.get(position).getPath());
        if (image != null){
            Glide.with(context).asBitmap()
                    .load(image).into(holder.albumImage);
        }
        else {
            Glide.with(context)
                    .load(R.drawable.images)
                    .into(holder.albumImage);
        }

        holder.itemView.setOnClickListener(v ->{
            Intent intent = new Intent(context, AlbumDetails.class);
            intent.putExtra("albumName", musicFiles.get(position).getAlbum());
            Log.e ("TAG", String.valueOf(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return musicFiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView albumImage;
        TextView albumName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            albumImage = itemView.findViewById(R.id.album_image);
            albumName = itemView.findViewById(R.id.album_name);
        }

        private byte[] getAlbumArt (String uri){
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(uri);
            byte[] art = retriever.getEmbeddedPicture();
            retriever.release();
            return art;
        }
    }
}
