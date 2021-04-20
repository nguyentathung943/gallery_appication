package com.example.image_management;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SecureFolder extends AppCompatActivity {
    public void showValidate() throws IOException {
        Context context = getApplicationContext();
        FileInputStream fis = null;
        fis = context.openFileInput("PIN.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String oldPin = reader.readLine();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});
        builder.setTitle(R.string.validate_pin1);
        builder.setMessage(R.string.validate_pin2);
        builder.setView(input);
        builder.setNegativeButton(R.string.no,null);
        builder.setPositiveButton(R.string.ok, (dialog, which) ->{
            String a = input.getText().toString();
            if (a.length() < 4){
                Toast.makeText(this,R.string.digit_constraint, Toast.LENGTH_SHORT).show();
                ShowChangePIN();
            }
            else{
                    if (a.equals(oldPin)){
                        ShowChangePIN();
                    }
                    else{
                        Toast.makeText(this,R.string.wrong_pin, Toast.LENGTH_SHORT).show();
                        try {
                            showValidate();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public Boolean checkFileExist(){
        String newPath = getApplicationInfo().dataDir + "/files/PIN.txt";
        File a = new File(newPath);
        return a.exists();
    }
    public void ShowChangePIN(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});
        builder.setTitle(R.string.change_pin1);
        builder.setMessage(R.string.change_pin2);
        builder.setView(input);
        builder.setPositiveButton(R.string.ok, (dialog, which) ->{
            String a = input.getText().toString();
            if (a.length() < 4){
                Toast.makeText(this,R.string.digit_constraint, Toast.LENGTH_SHORT).show();
                ShowChangePIN();
            }
            else{
                Context context = getApplicationContext();
                try {
                    FileOutputStream fout = context.openFileOutput("PIN.txt", Context.MODE_PRIVATE);
                    fout.write(input.getText().toString().getBytes());
                    Toast.makeText(this, R.string.pin_change_success,Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton(R.string.no,null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void SecureMenu(View view) throws IOException {
        switch (view.getId()){
            case R.id.sc_pin:
                if(!checkFileExist()){
                    startActivity(new Intent(SecureFolder.this, Security.class));
                    Toast.makeText(this, R.string.initial_secure_folder,Toast.LENGTH_SHORT).show();
                }
                else {
                    showValidate();
                }
                break;
            case R.id.sc_archive:
                startActivity(new Intent(SecureFolder.this, Security.class));
                break;
        }
    }
    public void back(View view){
        finish();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.security_menu);
    }

}
