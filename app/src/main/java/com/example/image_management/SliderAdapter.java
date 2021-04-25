package com.example.image_management;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.transition.Hold;
import com.smarteist.autoimageslider.SliderViewAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;

class SliderAdapter extends SliderViewAdapter<SliderAdapter.Holder> {
    ArrayList<SlideShowItem> list;
    public SliderAdapter(ArrayList<SlideShowItem> list){
        this.list = list;
    }
    @Override
    public SliderAdapter.Holder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slide_show,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(SliderAdapter.Holder viewHolder, int position) {
        viewHolder.image.setImageDrawable(list.get(position).draw);
        viewHolder.title.setText(list.get(position).name);
        viewHolder.order.setText(list.get(position).order);
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
