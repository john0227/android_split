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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.library.log.LogService;
import com.android.split.R;
import com.android.split.adapter.NameRecyclerAdapter;

import java.util.List;

public class NameFragment extends Fragment {

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
            this.nsv_names.post(() -> this.nsv_names.smoothScrollTo(0, this.nsv_names.getHeight()));
        });
    }

}
