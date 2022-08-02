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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.library.log.LogService;
import com.android.split.R;
import com.android.split.adapter.NameAdapter;

import java.util.List;

public class NameFragment extends Fragment {

    private Activity activity;

    private View rootLayout;
    private RecyclerView rv_names;
    private Button btn_add;

    private NameAdapter nameAdapter;

    private boolean isAttached;

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
        this.rv_names = this.rootLayout.findViewById(R.id.rv_names);
        this.btn_add = this.rootLayout.findViewById(R.id.btn_add);
        this.nameAdapter = new NameAdapter(this.activity);
    }

    private void setting() {
        this.rv_names.setAdapter(this.nameAdapter);
        this.nameAdapter.addName();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.rv_names.setLayoutManager(layoutManager);

        this.btn_add.setOnClickListener(view -> NameFragment.this.nameAdapter.addName());
    }

    public List<String> getNames() {
        return this.nameAdapter.getNames();
    }

}
