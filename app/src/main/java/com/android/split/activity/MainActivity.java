package com.android.split.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.android.library.log.LogService;
import com.android.split.R;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 vp2_split;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_main);

            init();
            setting();
        } catch (Exception e) {
            LogService.error(this, e.getMessage(), e);
        }
    }

    private void init() {
        this.vp2_split = findViewById(R.id.vp2_split);
    }

    private void setting() {

    }

}