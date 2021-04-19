package com.example.image_management;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.google.android.material.color.MaterialColors;
import com.hanks.passcodeview.PasscodeView;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.AttributedCharacterIterator;
import java.util.Locale;

public class Security extends AppCompatActivity {
    PasscodeView passcodeView;
    int VIEW_REQUEST = 555;
    protected boolean checkPasswordSet(){
        try{
            String a = getPassword();
            return (a.length()==4);
        }
        catch (Exception e){
            Log.e("Exception", "Read write failed: " + e.toString());
        }
        return false;
    }
    protected String getPassword(){
        String filename = "PIN.txt";
        Context context = getApplicationContext();
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                while (line != null) {
                    stringBuilder.append(line);
                    line = reader.readLine();
                }
                return  stringBuilder.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    };
    protected void showKeyboard(){
        String pass = getPassword();
        passcodeView = findViewById(R.id.passcodeView);
        passcodeView.setPasscodeLength(4).setLocalPasscode(pass).setListener(new PasscodeView.PasscodeViewListener() {
            @Override
            public void onFail() {
                Toast.makeText(getApplicationContext(),"WRONG PIN!",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(String number) {
                Intent go = new Intent(Security.this, Archive.class);
                go.putExtra("secure",true);
                startActivityForResult(go, VIEW_REQUEST);
            }
        });
    }
    public void show_fillCode(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Security.this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});
        builder.setTitle("First time logging in?, please initialize your PIN");
        builder.setMessage("4 digits are required");
        builder.setView(input);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                if (value.length() == 4){
                    System.out.println(value);
                    savePassword(value);
                    Toast.makeText(Security.this, "PIN Created successfully",Toast.LENGTH_SHORT).show();
                    showKeyboard();
                }
                else{
                    show_fillCode();
                }
            }});
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                show_fillCode();
            }});
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    protected void savePassword(String pass){
        Context context = getApplicationContext();
        try {
            FileOutputStream fout = context.openFileOutput("PIN.txt", Context.MODE_PRIVATE);
            fout.write(pass.getBytes());
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            finish();
        }
        else if(requestCode==VIEW_REQUEST){
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password);
        boolean check = checkPasswordSet();
        System.out.println(check);
        if (!check){
            show_fillCode();
        }
        else{
            showKeyboard();
        }
    }

}
