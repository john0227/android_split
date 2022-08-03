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
import com.android.split.adapter.TransactionRecyclerAdapter;
import com.android.split.vo.TransactionMemberVo;

import java.util.List;

public class TransactionFragment extends Fragment {

    private Activity activity;

    private View rootLayout;
    private NestedScrollView nsv_transactions;
    private RecyclerView rv_transactions;
    private Button btn_add_transaction;

    private TransactionRecyclerAdapter transactionRecyclerAdapter;

    private final List<String> names;
    private final List<TransactionMemberVo> transactions;
    private boolean isAttached;

    public TransactionFragment(List<String> names, List<TransactionMemberVo> transactions) {
        this.names = names;
        this.transactions = transactions;
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
            this.rootLayout = inflater.inflate(R.layout.fragment_add_transaction, container, false);
            init();
            setting();
        } catch (Exception e) {
            LogService.error(this.activity, e.getMessage(), e);
        }

        return this.rootLayout;
    }

    private void init() {
        this.nsv_transactions = this.rootLayout.findViewById(R.id.nsv_transactions);
        this.rv_transactions = this.rootLayout.findViewById(R.id.rv_transactions);
        this.btn_add_transaction = this.rootLayout.findViewById(R.id.btn_add_transaction);
        this.transactionRecyclerAdapter = new TransactionRecyclerAdapter(this.activity, this.names, this.transactions);
    }

    private void setting() {
        this.rv_transactions.setAdapter(this.transactionRecyclerAdapter);
        this.transactionRecyclerAdapter.addTransaction();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.rv_transactions.setLayoutManager(layoutManager);

        this.btn_add_transaction.setOnClickListener(view -> {
            this.transactionRecyclerAdapter.addTransaction();
            this.nsv_transactions.post(() -> this.nsv_transactions.smoothScrollTo(0, this.nsv_transactions.getHeight()));
        });
    }


}
