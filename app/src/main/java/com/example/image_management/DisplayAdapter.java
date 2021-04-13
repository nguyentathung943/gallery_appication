package com.example.image_management;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class DisplayAdapter extends BaseAdapter {
    Context context;

    public DisplayAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = View.inflate(context, R.layout.display_item, null);
        final ImageView display = (ImageView) view.findViewById(R.id.display_item);
        switch (position){
            case 0:
                display.setImageResource(R.drawable.column1);
                break;
            case 1:
                display.setImageResource(R.drawable.column2);
                break;
            case 2:
                display.setImageResource(R.drawable.column3);
                break;
        }
        return view;
    }



}
