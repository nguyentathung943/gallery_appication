package com.example.image_management;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
        Glide.with(context)
                .load(list.get(position).getImage())
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgView);
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
