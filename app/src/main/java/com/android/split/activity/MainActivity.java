package com.android.split.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.android.library.log.LogService;
import com.android.split.R;
import com.android.split.adapter.MyPagerAdapter;
import com.android.split.fragment.NameFragment;
import com.android.split.fragment.ResultFragment;
import com.android.split.fragment.TransactionFragment;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 vp2_split;
    private MyPagerAdapter pagerAdapter;

    private Fragment[] fragments;
    private NameFragment nameFragment;
    private TransactionFragment transactionFragment;
    private ResultFragment resultFragment;

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

        this.nameFragment = new NameFragment();
        this.transactionFragment = new TransactionFragment();
        this.resultFragment = new ResultFragment();
        this.fragments = new Fragment[] {
                this.nameFragment,
                this.transactionFragment,
                this.resultFragment
        };

        this.pagerAdapter = new MyPagerAdapter(this, this.fragments);
    }

    private void setting() {
        this.vp2_split.setAdapter(this.pagerAdapter);
        this.vp2_split.setUserInputEnabled(false);
    }

}