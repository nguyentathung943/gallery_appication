package com.example.image_management;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Locale;

public class Setting extends AppCompatActivity{
    TextView languageText, languageDefault, language, headerTitle;
    LinearLayout languageLayout;
    ArrayList<String> listCode;
    LanguageAdapter languageAdapter;
    ArrayList<String> listLanguage;
    int theme;
    String lang;
    com.example.image_management.Configuration config;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        listLanguage = new ArrayList<>();
        listLanguage.add("English");
        listLanguage.add("Tiếng Việt");
        listCode = new ArrayList<>();
        listCode.add("en");
        listCode.add("vi");

        Intent intent = getIntent();
        theme = intent.getIntExtra("theme", 0);
        lang = intent.getStringExtra("language");

        config = new com.example.image_management.Configuration(getApplication());

        languageLayout = (LinearLayout) findViewById(R.id.language_layout);
        languageText = (TextView) findViewById(R.id.language_text);
        languageDefault = (TextView) findViewById(R.id.language_default);
        headerTitle = (TextView) findViewById(R.id.header_title);
        language = (TextView) findViewById(R.id.language);
        language.setText(listLanguage.get(listCode.indexOf(lang)));
        languageAdapter = new LanguageAdapter(this, listLanguage);

        languageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeLanguage();
            }
        });
    }
    public void ChangeLanguage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
        builder.setTitle(R.string.language_text);
        builder.setAdapter(languageAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                ChangeLanguage(listCode.get(position));
                languageText.setText(R.string.language_text);
                languageDefault.setText(R.string.language_default);
                headerTitle.setText(R.string.setting);
                language.setText(listLanguage.get(position));
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void ChangeLanguage(String language){
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(new Locale(language.toLowerCase()));
        resources.updateConfiguration(configuration, displayMetrics);
        config.saveConfig(theme, language);
    }

    public void back(View v){
        this.finish();
    }
}
