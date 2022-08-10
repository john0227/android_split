package com.android.split.adapter;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.split.R;
import com.android.split.util.DecimalFormatUtil;
import com.android.split.vo.TransactionMemberVo;

import java.util.List;

public class ResultRecyclerAdapter extends RecyclerView.Adapter<ResultRecyclerAdapter.ResultHolder> {

    private final Activity activity;
    private final List<TransactionMemberVo> simplifiedTransactions;

    public ResultRecyclerAdapter(Activity activity, List<TransactionMemberVo> simplifiedTransactions) {
        this.activity = activity;
        this.simplifiedTransactions = simplifiedTransactions;
    }

    @NonNull
    @Override
    public ResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.activity);
        View itemView = inflater.inflate(R.layout.rv_item_simplified_transfers, parent, false);
        return new ResultHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultHolder holder, int position) {
        TransactionMemberVo transaction = this.simplifiedTransactions.get(position);

        if (transaction == null) {
            holder.layout_arrow_container.removeView(holder.iv_arrow);
            holder.tv_transfer_amount.setText("No transfers needed");
            holder.tv_transfer_amount.setTextSize(16);
            holder.tv_transfer_amount.setGravity(Gravity.CENTER);
            return;
        }

        // Bind tv_sender_name
        holder.tv_sender_name.setText(transaction.getSender());

        // Bind tv_rcver_name
        holder.tv_rcver_name.setText(transaction.getRcver());

        // Bind tv_transfer_amount
        holder.tv_transfer_amount.setText(DecimalFormatUtil.format(transaction.getAmount()));
    }

    @Override
    public int getItemCount() {
        return this.simplifiedTransactions.size();
    }

    public static class ResultHolder extends RecyclerView.ViewHolder {

        private final LinearLayout layout_arrow_container;
        private final TextView tv_sender_name;
        private final TextView tv_transfer_amount;
        private final TextView tv_rcver_name;
        private final ImageView iv_arrow;

        public ResultHolder(@NonNull View itemView) {
            super(itemView);
            this.layout_arrow_container = itemView.findViewById(R.id.layout_arrow_container);
            this.tv_sender_name = itemView.findViewById(R.id.tv_sender_name);
            this.tv_transfer_amount = itemView.findViewById(R.id.tv_transfer_amount);
            this.tv_rcver_name = itemView.findViewById(R.id.tv_rcver_name);
            this.iv_arrow = itemView.findViewById(R.id.iv_arrow);
        }

    }

}
