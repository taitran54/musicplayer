package com.example.myapplication;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder>{

    private Context context;
    static ArrayList<MusicFiles> musicFile;

    MusicAdapter (Context context, ArrayList<MusicFiles> musicFiles){
        this.context = context;
        this.musicFile = musicFiles;
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
        holder.file_name.setText(musicFile.get(position).getTitle());
        byte[] image = getAlbumArt(musicFile.get(position).getPath());
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

        holder.menuPopup.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener( item ->{
                switch (item.getItemId()){
                    case R.id.delete:
                        Log.d("TAG", "here");
                        deleteFile (position, v);
                }

                return true;
            });
        });
    }

    private void deleteFile(int position, View v) {

        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(musicFile.get(position).getId()));
        File file = new File("content://"+ musicFile.get(position).getPath());

        if (file.exists()){Log.e("TAG", musicFile.get(position).getPath() );}
        boolean deleted = file.delete();
        if (deleted) {
            context.getContentResolver().delete(contentUri, null, null);  //Important
            musicFile.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, musicFile.size());
            Snackbar.make(v, "File deleted! ", Snackbar.LENGTH_LONG)
                    .show();
        }
        else {
            Snackbar.make(v, "File can't be deleted! ", Snackbar.LENGTH_LONG)
                    .show();
        }
    }


    @Override
    public int getItemCount() {
        return musicFile.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{

        TextView file_name;
        ImageView image, menuPopup;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            file_name = itemView.findViewById(R.id.music_file_name);
            image = itemView.findViewById(R.id.music_image);
            menuPopup = itemView.findViewById(R.id.menu_popup);
        }
    }

    private byte[] getAlbumArt (String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

    public void updatePlayList (ArrayList<MusicFiles> musicFiles){
        this.musicFile = new ArrayList<>();
        this.musicFile.addAll(musicFiles);
        notifyDataSetChanged();
    }
}
