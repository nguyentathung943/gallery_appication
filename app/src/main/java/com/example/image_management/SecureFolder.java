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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SecureFolder extends AppCompatActivity {
    public void ShowChangePIN(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});
        builder.setTitle(R.string.change_pin);
        builder.setMessage("4 digits are required");
        builder.setView(input);
        builder.setPositiveButton("Ok", (dialog, which) ->{
            String a = input.getText().toString();
            if (a.length() < 4){
                Toast.makeText(this,"PIN must contains 4 digits", Toast.LENGTH_SHORT).show();
                ShowChangePIN();
            }
            else{
                Context context = getApplicationContext();
                try {
                    FileOutputStream fout = context.openFileOutput("PIN.txt", Context.MODE_PRIVATE);
                    fout.write(input.getText().toString().getBytes());
                    Toast.makeText(this, "PIN Changed Successfully",Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("No",null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void SecureMenu(View view){
        switch (view.getId()){
            case R.id.sc_pin:
                ShowChangePIN();
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
