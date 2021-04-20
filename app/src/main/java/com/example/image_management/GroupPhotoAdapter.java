package com.example.image_management;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GroupPhotoAdapter extends RecyclerView.Adapter<GroupPhotoAdapter.ViewHolder> {
    Context context;
    ArrayList<ArrayList<Item>> listPhotoGroup;
    ArrayList<String> listDate;
    int column;
    public GroupPhotoAdapter(Context context, ArrayList<ArrayList<Item>> listPhotoGroup, ArrayList<String> listDate) {
        this.context = context;
        this.listPhotoGroup = listPhotoGroup;
        this.listDate = listDate;
        this.column = 3;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.group_photo, parent, false);
        return new GroupPhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupPhotoAdapter.ViewHolder viewHolder = (GroupPhotoAdapter.ViewHolder) holder;
        viewHolder.groupDate.setText(listDate.get(position));
        viewHolder.recyclerView.setHasFixedSize(true);
        viewHolder.listAdapter = new ListAdapter(listPhotoGroup.get(position), context, (ListAdapter.ClickImageListener) context);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, column);
        viewHolder.recyclerView.setLayoutManager(mLayoutManager);
        viewHolder.recyclerView.setAdapter(viewHolder.listAdapter);
    }


    public void SetDisplay(int column){
        this.column = column;
//        viewHolder.recyclerView.setLayoutManager(new GridLayoutManager(Archive.this, position + 1));
//        recyclerView.setAdapter(listAdapter);
    }
    @Override
    public int getItemCount() {
        return listDate.size();
    }
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//    @Override
//    public int getItemViewType(int position) {
//        return position;
//    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView groupDate;
        RecyclerView recyclerView;
        ListAdapter listAdapter;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupDate = (TextView) itemView.findViewById(R.id.group_date);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }
    }
}