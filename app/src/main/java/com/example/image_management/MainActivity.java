package com.example.image_management;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.camera_application.R;

import static java.sql.DriverManager.println;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
    }

    public void main_menu_onclick(View view) {
        switch ((view.getId())) {
            case R.id.mn_camera: {
                break;
            }

            case R.id.mn_setting: {
                setContentView(R.layout.setting);
                break;
            }
            case R.id.mn_PIN: {
                setContentView(R.layout.password);
                break;
            }
            case R.id.mn_album: {
                break;
            }
            case R.id.mn_archive: {
                break;
            }
            case R.id.mn_theme: {
                setContentView(R.layout.theme);
                break;
            }
            case R.id.btn_back: {
                setContentView(R.layout.main_menu);
                break;
            }

        }
    }
}