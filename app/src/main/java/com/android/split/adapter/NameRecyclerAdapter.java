package com.android.split.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.split.R;
import com.android.split.listener.TextChangedListener;

import java.util.List;

public class NameRecyclerAdapter extends RecyclerView.Adapter<NameRecyclerAdapter.NameHolder> {

    private final Activity activity;
    private final List<String> names;

    public NameRecyclerAdapter(Activity activity, List<String> names) {
        this.activity = activity;
        this.names = names;
    }

    @NonNull
    @Override
    public NameHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.activity);
        View view = inflater.inflate(R.layout.rv_item_names, parent, false);
        return new NameHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NameHolder holder, int position) {
        if (position == this.names.size() - 1) {
            holder.et_name.requestFocus();
        }
        // Update name if EditText field is changed
        TextChangedListener textChangedListener = new TextChangedListener();
        textChangedListener.setOnTextChangedListener(editable -> this.names.set(position, editable.toString()));
        holder.et_name.addTextChangedListener(textChangedListener);
        // Remove name if ImageButton is pressed
        holder.ibtn_delete_name.setOnClickListener(view -> {
            this.names.remove(holder.getAdapterPosition());
            this.notifyItemRemoved(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return this.names.size();
    }

    public void addName() {
        this.names.add("");
        this.notifyItemInserted(this.names.size() - 1);
    }

    public static class NameHolder extends RecyclerView.ViewHolder {

        private final EditText et_name;
        private final ImageButton ibtn_delete_name;

        public NameHolder(@NonNull View itemView) {
            super(itemView);
            this.et_name = itemView.findViewById(R.id.et_name);
            this.ibtn_delete_name = itemView.findViewById(R.id.ibtn_delete_name);
        }

    }

}
