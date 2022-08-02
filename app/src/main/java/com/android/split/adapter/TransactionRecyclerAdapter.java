package com.android.split.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.split.R;
import com.android.split.listener.TextChangedListener;
import com.android.split.vo.TransactionMemberVo;

import java.util.ArrayList;
import java.util.List;

public class TransactionRecyclerAdapter extends RecyclerView.Adapter<TransactionRecyclerAdapter.TransactionHolder> {

    private final Activity activity;
    private final List<String> names;
    private final List<TransactionMemberVo> transactions;

    public TransactionRecyclerAdapter(Activity activity, List<String> names, List<TransactionMemberVo> transactions) {
        this.activity = activity;
        this.names = names;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.activity);
        View view = inflater.inflate(R.layout.rv_item_transactions, parent, false);
        return new TransactionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionHolder holder, int position) {
        if (position == this.transactions.size() - 1) {
            holder.et_amount.requestFocus();
        }

        // Populate spinners
        this.populateSpinner(activity, holder.sp_sender);
        this.populateSpinner(activity, holder.sp_rcver);
        // Set listeners to spinners
        holder.sp_sender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                transactions.get(holder.getAdapterPosition()).setSender((String) parent.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        holder.sp_rcver.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                transactions.get(holder.getAdapterPosition()).setRcver((String) parent.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        // Select default sender and receiver
        holder.sp_sender.setSelection(0);
        holder.sp_rcver.setSelection(1);
        // Set listener to EditText
        TextChangedListener textChangedListener = new TextChangedListener();
        textChangedListener.setOnTextChangedListener(editable -> {
            String strAmount = editable.toString();
            double amount = strAmount.isEmpty() ? 0 : Double.parseDouble(strAmount);
            this.transactions.get(holder.getAdapterPosition()).setAmount(amount);
        });
        holder.et_amount.addTextChangedListener(textChangedListener);
        // Set listener to CheckBox
        holder.cb_replace_amount.setOnCheckedChangeListener(
                (buttonView, isChecked) -> this.transactions.get(holder.getAdapterPosition()).setReplace(isChecked)
        );
        // Set listener to ImageButton
        holder.ibtn_delete_transaction.setOnClickListener(view -> {
            this.transactions.remove(holder.getAdapterPosition());
            this.notifyItemRemoved(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return this.transactions.size();
    }

    public void addTransaction() {
        this.transactions.add(new TransactionMemberVo("", "", 0.0, false));
        this.notifyItemInserted(this.transactions.size() - 1);
    }

    private void populateSpinner(Activity activity, Spinner spinner) {
        List<String> items = new ArrayList<>(this.names);
        items.add("Everyone");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public static class TransactionHolder extends RecyclerView.ViewHolder {

        private final Spinner sp_sender;
        private final Spinner sp_rcver;
        private final EditText et_amount;
        private final CheckBox cb_replace_amount;
        private final ImageButton ibtn_delete_transaction;

        public TransactionHolder(@NonNull View itemView) {
            super(itemView);
            this.sp_sender = itemView.findViewById(R.id.sp_sender);
            this.sp_rcver = itemView.findViewById(R.id.sp_rcver);
            this.et_amount = itemView.findViewById(R.id.et_amount);
            this.cb_replace_amount = itemView.findViewById(R.id.cb_replace_amount);
            this.ibtn_delete_transaction = itemView.findViewById(R.id.ibtn_delete_transaction);
        }

    }

}
