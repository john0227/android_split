package com.android.split.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.android.library.log.LogService;
import com.android.split.R;
import com.android.split.adapter.MyPagerAdapter;
import com.android.split.dialog.WarningDialog;
import com.android.split.fragment.NameFragment;
import com.android.split.fragment.ResultFragment;
import com.android.split.fragment.TransactionFragment;
import com.android.split.logic.Logic;
import com.android.split.vo.TransactionMemberVo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 vp2_split;
    private Button btn_back;
    private Button btn_next;
    private View.OnClickListener[] listeners;

    private Fragment[] fragments;
    private NameFragment nameFragment;
    private TransactionFragment transactionFragment;
    private ResultFragment resultFragment;

    private MyPagerAdapter pagerAdapter;
    private Logic logic;

    private List<String> names;
    private List<TransactionMemberVo> transactions;

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
        this.names = new ArrayList<>();
        this.transactions = new ArrayList<>();

        this.vp2_split = findViewById(R.id.vp2_split);
        this.btn_back = findViewById(R.id.btn_back);
        this.btn_next = findViewById(R.id.btn_next);
        this.listeners = new View.OnClickListener[] {
                this.nextListener1,
                this.nextListener2,
                null
        };

        this.nameFragment = new NameFragment(this.names);
        this.transactionFragment = new TransactionFragment(this.names, this.transactions);
        this.resultFragment = new ResultFragment();
        this.fragments = new Fragment[] {
                this.nameFragment,
                this.transactionFragment,
                this.resultFragment
        };

        this.pagerAdapter = new MyPagerAdapter(this, this.fragments);
        this.logic = Logic.create();
    }

    private void setting() {
        this.vp2_split.setAdapter(this.pagerAdapter);
        this.vp2_split.setUserInputEnabled(false);
        this.vp2_split.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setEnabled(btn_back, position > 0);
                setEnabled(btn_next, position < 2);
                btn_next.setText(position != 1
                        ? R.string.btn_next_text1
                        : R.string.btn_next_text2
                );
                btn_next.setOnClickListener(listeners[position]);
            }
        });
        this.btn_back.setOnClickListener(view -> this.vp2_split.setCurrentItem(this.vp2_split.getCurrentItem() - 1));
    }

    private void setEnabled(Button button, boolean enabled) {
        button.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
        button.setEnabled(enabled);
    }

    private boolean areAllUniqueNames() {
        Set<String> nameSet = new HashSet<>(this.names);
        return nameSet.size() == this.names.size();
    }

    private boolean areAllValidTransactions() {
        for (TransactionMemberVo transaction : this.transactions) {
            if (transaction.getSender().equals(transaction.getRcver())) {
                return false;
            }
        }
        return true;
    }

    private final View.OnClickListener nextListener1 = view -> {
        if (this.names.size() < 2) {
            WarningDialog.show(this, "Need more people", "There must be at least two people", "Fix");
            return;
        }

        if (!areAllUniqueNames()) {
            WarningDialog.show(this, "Names are not unique", "All names must be different", "Fix");
            return;
        }

        this.logic.addPeople(this.names);
        this.vp2_split.setCurrentItem(this.vp2_split.getCurrentItem() + 1);
    };

    private final View.OnClickListener nextListener2 = view -> {
        if (!areAllValidTransactions()) {
            WarningDialog.show(this, "Invalid transactions", "Sender and receiver must be different", "Fix");
            return;
        }

        try {
            for (TransactionMemberVo transaction : this.transactions) {
                this.logic.addTransfer(transaction.getSender(), transaction.getRcver(), transaction.getAmount(), transaction.shouldReplace());
            }
        } catch (Exception e) {
            WarningDialog.show(this, "Something went wrong", "Please try again", "Ok");
            return;
        }
        this.vp2_split.setCurrentItem(this.vp2_split.getCurrentItem() + 1);
    };

}