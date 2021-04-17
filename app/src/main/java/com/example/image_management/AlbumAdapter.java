package com.example.image_management;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    ArrayList<String> listFile;
    private ClickItemListener clickItemListener;
    public AlbumAdapter(ArrayList<String> listFile, Context context, ClickItemListener clickItemListener) {
        this.context = context;
        this.listFile = listFile;
        this.clickItemListener = clickItemListener;
    }

    Context context;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.album_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.albumText.setText(listFile.get(position));
        System.out.println("Index " + position);

    }

    @Override
    public int getItemCount() {
        return listFile.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView albumText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumText = (TextView) itemView.findViewById(R.id.album_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickItemListener.onClick(getAdapterPosition());
                }
            });
        }

    }
    public interface ClickItemListener{
        void onClick(int position);
    }
}