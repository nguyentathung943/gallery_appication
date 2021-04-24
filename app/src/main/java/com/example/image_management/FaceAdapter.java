package com.example.image_management;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FaceAdapter extends RecyclerView.Adapter<FaceAdapter.ViewHolder> {
    ArrayList<GroupFaceDetection> listGroupFace;
    private ClickFaceListener clickFaceListener;

    public FaceAdapter(ArrayList<GroupFaceDetection> listGroupFace, Context context, ClickFaceListener clickFaceListener) {
        this.context = context;
        this.listGroupFace = listGroupFace;
        this.clickFaceListener = clickFaceListener;
    }
    Context context;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.group_face_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.faceImage.setImageBitmap(listGroupFace.get(position).getFace());
    }

    @Override
    public int getItemCount() {
        return listGroupFace.size();
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
        ImageView faceImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            faceImage = (ImageView) itemView.findViewById(R.id.group_face_img);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickFaceListener.onClick(listGroupFace.get(getAdapterPosition()));
                }
            });
        }

    }
    public interface ClickFaceListener{
        void onClick(GroupFaceDetection position);
    }
}