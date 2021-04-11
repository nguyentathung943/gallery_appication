package com.example.image_management;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class LanguageAdapter extends BaseAdapter {
    Context context;

    public LanguageAdapter(Context context, List<String> languageList) {
        this.context = context;
        this.languageList = languageList;
    }

    List<String> languageList;
    @Override
    public int getCount() {
        return languageList.size();
    }

    @Override
    public Object getItem(int position) {
        return languageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = View.inflate(context, R.layout.language_item, null);
        final TextView language = (TextView)view.findViewById(R.id.language);
        language.setText(languageList.get(position));
        return view;
    }
}
