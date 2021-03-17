package com.example.image_management;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hanks.passcodeview.PasscodeView;

public class Security extends AppCompatActivity {
    PasscodeView passcodeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password);
        passcodeView = findViewById(R.id.passcodeView);
        passcodeView.setPasscodeLength(4).setLocalPasscode("1234").setListener(new PasscodeView.PasscodeViewListener() {
            @Override
            public void onFail() {
                Toast.makeText(getApplicationContext(),"WRONG PIN",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(String number) {
                Intent go = new Intent(Security.this, CameraActivity.class);
                startActivity(go);
            }
        });
    }


}
