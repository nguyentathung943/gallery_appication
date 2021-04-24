package com.example.image_management;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.renderscript.ScriptIntrinsicYuvToRGB;
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
    private ClickImageListener clickImageListener;
    public ListAdapter(ArrayList<Item> list, Context context, ClickImageListener clickImageListener) {
        this.list = list;
        this.context = context;
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
                .into(viewHolder.imageView);
        if(item.getType() == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO){
            System.out.println("Index " + position + " duration " + item.getTime());
            viewHolder.duration.setText(item.getTime());
            viewHolder.duration.setPadding(3, 3, 3, 3);
            viewHolder.duration.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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
                    clickImageListener.onClick(list.get(getAdapterPosition()));
                }
            });
        }

    }
    public interface ClickImageListener{
        void onClick(Item item);
    }

}
