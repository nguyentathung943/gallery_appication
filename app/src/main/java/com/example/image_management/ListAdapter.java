package com.example.image_management;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.io.File;
import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    ArrayList<Item> list;
    ArrayList<String> path;
    private ClickImageListener clickImageListener;
    public ListAdapter(ArrayList<Item> list, ArrayList<String> path, Context context, ClickImageListener clickImageListener) {
        this.list = list;
        this.context = context;
        this.path = path;
        this.clickImageListener = clickImageListener;
    }

    Context context;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        Item item = list.get(position);
        Glide.with(context)
                .load(item.getPath())
                .centerCrop()
//                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(viewHolder.imageView);
        if(!item.getTime().equals("")){
            viewHolder.duration.setText(item.getTime());
            viewHolder.duration.setPadding(3, 3, 3, 3);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView duration;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgView);
            duration = itemView.findViewById(R.id.duration);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickImageListener.onClick(getAdapterPosition());
                }
            });
        }

    }
    public interface ClickImageListener{
        void onClick(int position);
    }

}
