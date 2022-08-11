package com.android.split.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.library.log.LogService;
import com.android.split.R;
import com.android.split.adapter.NameRecyclerAdapter;

import java.util.List;

public class NameFragment extends SplitFragment {

    public static final int PAGE_NUM = 0;

    private Activity activity;

    private View rootLayout;
    private NestedScrollView nsv_names;
    private RecyclerView rv_names;
    private Button btn_add_name;

    private NameRecyclerAdapter nameRecyclerAdapter;

    private final List<String> names;
    private boolean isAttached;

    public NameFragment(List<String> names) {
        this.names = names;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.isAttached = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (isAttached) {
            this.activity = getActivity();
        }

        try {
            this.rootLayout = inflater.inflate(R.layout.fragment_add_name, container, false);
            init();
            setting();
        } catch (Exception e) {
            LogService.error(this.activity, e.getMessage(), e);
        }


        return this.rootLayout;
    }

    @Override
    public void onLoad() {
        // Scroll to top
        this.scrollTo(0, false);
        // Populate RecyclerView if list is empty
        if (this.nameRecyclerAdapter != null && this.names.isEmpty()) {
            this.nameRecyclerAdapter.addName();
        }
    }

    @Override
    public void refresh() {
        this.nameRecyclerAdapter.removeAllNames();
    }

    private void init() {
        this.nsv_names = this.rootLayout.findViewById(R.id.nsv_names);
        this.rv_names = this.rootLayout.findViewById(R.id.rv_names);
        this.btn_add_name = this.rootLayout.findViewById(R.id.btn_add_name);
        this.nameRecyclerAdapter = new NameRecyclerAdapter(this.activity, this.names);
    }

    private void setting() {
        this.rv_names.setAdapter(this.nameRecyclerAdapter);
        this.nameRecyclerAdapter.addName();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.rv_names.setLayoutManager(layoutManager);

        this.btn_add_name.setOnClickListener(view -> {
            this.nameRecyclerAdapter.addName();
            this.scrollTo(this.nsv_names.getHeight(), true);  // scroll to bottom
        });
    }

    private void scrollTo(int height, boolean smoothScroll) {
        if (this.nsv_names == null) {
            return;
        }

        this.nsv_names.post(() -> {
            if (smoothScroll) {
                this.nsv_names.smoothScrollTo(0, height);
            } else {
                this.nsv_names.scrollTo(0, height);
            }
        });
    }

}
