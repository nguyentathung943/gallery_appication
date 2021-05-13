package com.example.image_management;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

class SliderAdapter extends SliderViewAdapter<SliderAdapter.Holder> {
    List<ImageData> list;
    private Context context;
    public SliderAdapter(Context context, List list) {
        this.list = list;
        this.context = context;
    }
    @Override
    public SliderAdapter.Holder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slide_show,parent,false);
        return new Holder(view);
    }
    @Override
    public void onBindViewHolder(SliderAdapter.Holder viewHolder, int position) {
        Glide.with(viewHolder.itemView)
                .load(list.get(position).path)
                .fitCenter()
                .into(viewHolder.image);
//        viewHolder.image.setImageDrawable(list.get(position).draw);
        viewHolder.title.setText(list.get(position).name);
        viewHolder.order.setText(list.get(position).order +"/" +list.size());
    }
    @Override
    public int getCount() {
        return list.size();
    }
    public class Holder extends SliderViewAdapter.ViewHolder{
        ImageView image;
        TextView title;
        TextView order;
        public Holder(View itemView) {
            super(itemView);
            order = itemView.findViewById(R.id.number_slide);
            image = itemView.findViewById(R.id.slide_show_item);
            title = itemView.findViewById(R.id.slide_show_title);
        }
    }
}
