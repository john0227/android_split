package com.android.split.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.library.log.LogService;
import com.android.split.R;
import com.android.split.adapter.ResultRecyclerAdapter;
import com.android.split.logic.Logic;
import com.android.split.util.ConvertUnitUtil;
import com.android.split.util.DecimalFormatUtil;
import com.android.split.vo.TransactionMemberVo;
import com.otaliastudios.zoom.ZoomLayout;

import java.util.List;
import java.util.Locale;

public class ResultFragment extends Fragment {

    private FragmentActivity activity;

    private View rootLayout;
    private LinearLayout startTable_container;
    private LinearLayout startNetExpense_container;
    private LinearLayout afterTable_container;
    private LinearLayout afterNetExpense_container;
    private RecyclerView rv_simplified_transfers;

    private Logic logic;
    private ResultRecyclerAdapter resultRecyclerAdapter;

    private final List<String> names;
    private final List<TransactionMemberVo> transactions;
    private boolean isAttached;

    public ResultFragment(List<String> names, List<TransactionMemberVo> transactions) {
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
            this.rootLayout = inflater.inflate(R.layout.fragment_result, container, false);
            init();
            setting();
        } catch (Exception e) {
            LogService.error(this.activity, e.getMessage(), e);
        }

        return this.rootLayout;
    }

    private void init() {
        this.startTable_container = this.rootLayout.findViewById(R.id.startTable_container);
        this.startNetExpense_container = this.rootLayout.findViewById(R.id.startNetExpense_container);
        this.afterTable_container = this.rootLayout.findViewById(R.id.afterTable_container);
        this.afterNetExpense_container = this.rootLayout.findViewById(R.id.afterNetExpense_container);
        this.rv_simplified_transfers = this.rootLayout.findViewById(R.id.rv_simplified_transfers);

        this.logic = Logic.getInstance();
    }

    private void setting() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.rv_simplified_transfers.setLayoutManager(layoutManager);
    }

    public void onLoad() {
        // Show before simplifying
        showTransferTable(logic.getTransferTable(), this.startTable_container);
        showNetExpenseTable(logic.getTransferTable(), this.startNetExpense_container);

        // Show after simplifying
        logic.simplify();
        showTransferTable(logic.getTransferTable(), this.afterTable_container);
        showNetExpenseTable(logic.getTransferTable(), this.afterNetExpense_container);

        // Show simplified transfers
        this.resultRecyclerAdapter = new ResultRecyclerAdapter(this.activity, this.logic.getTransactions());
        this.rv_simplified_transfers.setAdapter(this.resultRecyclerAdapter);
    }

    private void showTransferTable(double[][] transferTable, LinearLayout tableContainer) {
        ZoomLayout zoomLayout = createZoomLayout((int) ConvertUnitUtil.convertDpToPx(this.activity, 80) * transferTable.length + 100);
        ConstraintLayout container = (ConstraintLayout) LayoutInflater.from(this.activity).inflate(R.layout.tablelayout, zoomLayout, false);
        LinearLayout tableLayout = container.findViewById(R.id.tablelayout);

        TextView tvSender = createTextView(R.string.sender, -90f, null, android.R.color.transparent,
                Gravity.CENTER|Gravity.BOTTOM, 14, -1, Typeface.BOLD);
        TextView tvRcver = createTextView(R.string.receiver, 0f, null, android.R.color.transparent,
                Gravity.CENTER, 14, -1, Typeface.BOLD);
        container.addView(tvSender);
        container.addView(tvRcver);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(container);
        constraintSet.connect(tvSender.getId(), ConstraintSet.RIGHT, tableLayout.getId(), ConstraintSet.LEFT, 3);
        constraintSet.connect(tvSender.getId(), ConstraintSet.TOP, tableLayout.getId(), ConstraintSet.TOP);
        constraintSet.connect(tvSender.getId(), ConstraintSet.BOTTOM, tableLayout.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(tvRcver.getId(), ConstraintSet.BOTTOM, tableLayout.getId(), ConstraintSet.TOP, -15);
        constraintSet.connect(tvRcver.getId(), ConstraintSet.LEFT, tableLayout.getId(), ConstraintSet.LEFT);
        constraintSet.connect(tvRcver.getId(), ConstraintSet.RIGHT, tableLayout.getId(), ConstraintSet.RIGHT);
        constraintSet.applyTo(container);

        zoomLayout.addView(container);
        tableContainer.addView(zoomLayout);

        // width = (length of one cell + padding) * total number of cells
        int width = (this.logic.getLongest() * 20 + 2 * 30) * transferTable.length;  // in DP
        // Create LinearLayoutParam to be used
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                (int) ConvertUnitUtil.convertDpToPx(this.activity, width),
                (int) ConvertUnitUtil.convertDpToPx(this.activity, 80)
        );
        LinearLayout.LayoutParams cellParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        // Create first row containing names
        LinearLayout linearLayout = new LinearLayout(this.activity);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setLayoutParams(layoutParams);
        TextView textView;
        for (int i = 0; i < this.names.size() + 1; i++) {
            textView = createTextView(i == 0 ? "" : this.names.get(i - 1), 0f, cellParams, R.drawable.border, Gravity.CENTER, 14, -1, Typeface.BOLD);
            linearLayout.addView(textView);
        }
        tableLayout.addView(linearLayout);
        // Create the rest of rows
        for (int r = 0; r < transferTable.length - 1; r++) {
            linearLayout = new LinearLayout(this.activity);
            linearLayout.setLayoutParams(layoutParams);
            for (int c = 0; c < transferTable[r].length; c++) {
                if (c == 0) {
                    textView = createTextView(this.names.get(r), 0f, cellParams, R.drawable.border, Gravity.CENTER, 14, -1, Typeface.BOLD);
                } else {
                    String s = DecimalFormatUtil.format(transferTable[r][c - 1]);
                    textView = createTextView(s, 0f, cellParams, R.drawable.border, Gravity.CENTER, 14,
                            s.equals("0") ? R.color.bottomNavView_iconInactive : R.color.purple_700,
                            s.equals("0") ? Typeface.NORMAL : Typeface.BOLD);
                }
                linearLayout.addView(textView);
            }
            tableLayout.addView(linearLayout);
        }
    }

    private void showNetExpenseTable(double[][] transferTable, LinearLayout tableContainer) {
        //width = (length of one cell + padding) * total number of cells
        int width1 = (int) ConvertUnitUtil.convertDpToPx(this.activity, Math.max(this.logic.getLongestName(), 4) * 20 + 2 * 30);  // in PX
        // Create LinearLayoutParam to be used by the header row
        LinearLayout tableLayout = new LinearLayout(this.activity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) ConvertUnitUtil.convertDpToPx(this.activity, 80)
        );
        tableLayout.setLayoutParams(layoutParams);
        // Create header
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(width1, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams cellParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        TextView tvHeader;
        for (int i = 0; i < 2; i++) {
            tvHeader = createTextView(i == 0 ? "Name" : "Net Expenses", 0, i == 0 ? nameParams : cellParams,
                    R.drawable.border, Gravity.CENTER, 14, -1, Typeface.BOLD);
            tableLayout.addView(tvHeader);
        }
        tableContainer.addView(tableLayout);
        // Create rest of the table
        layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) ConvertUnitUtil.convertDpToPx(this.activity, 200)
        );
        TextView tvCell;
        for (int i = 0; i < this.names.size(); i++) {
            tableLayout = new LinearLayout(this.activity);
            tableLayout.setLayoutParams(layoutParams);
            String grossExpense = DecimalFormatUtil.format(transferTable[i][this.names.size()]);
            String grossIncome = DecimalFormatUtil.format(transferTable[this.names.size()][i]);
            double netExpense = transferTable[i][this.names.size()] - transferTable[this.names.size()][i];
            for (int j = 0; j < 2; j++) {
                String text = j == 0
                        ? this.names.get(i)
                        : netExpense > 0
                            ? String.format(Locale.US, "Must send %s\nMust receive %s\nIn total, must send %s",
                                            grossExpense, grossIncome, DecimalFormatUtil.format(netExpense))
                            : String.format(Locale.US, "Must send %s\nMust receive %s\nIn total, must receive %s",
                                            grossExpense, grossIncome, DecimalFormatUtil.format(Math.abs(netExpense)));
                tvCell = createTextView(text, 0, j == 0 ? nameParams : cellParams, R.drawable.border,
                                        Gravity.CENTER, 14, R.color.purple_700, Typeface.NORMAL);
                tableLayout.addView(tvCell);
            }
            tableContainer.addView(tableLayout);
        }
    }

    private ZoomLayout createZoomLayout(int height) {
        ZoomLayout zoomLayout = new ZoomLayout(this.activity);
        ZoomLayout.LayoutParams layoutParams = new ZoomLayout.LayoutParams(ZoomLayout.LayoutParams.MATCH_PARENT, height);
        layoutParams.setMargins(0, 30, 0, 20);
        zoomLayout.setLayoutParams(layoutParams);
        zoomLayout.setHorizontalScrollBarEnabled(true);
        zoomLayout.setVerticalScrollBarEnabled(true);
        zoomLayout.setHorizontalPanEnabled(true);
        zoomLayout.setVerticalPanEnabled(true);
        zoomLayout.setZoomEnabled(true);
        zoomLayout.setHasClickableChildren(false);
        zoomLayout.setMaxZoom(3.0f);
        zoomLayout.setMinZoom(0.7f);
        zoomLayout.setOverPinchable(true);
        zoomLayout.setOverScrollHorizontal(false);
        zoomLayout.setOverScrollVertical(false);
        zoomLayout.setFocusable(true);
        return zoomLayout;
    }

    private TextView createTextView(int resId, float rotation, ViewGroup.LayoutParams params, int bg, int gravity, int size, int color, int tf) {
        return createTextView(getResources().getString(resId), rotation, params, bg, gravity, size, color, tf);
    }

    private TextView createTextView(String s, float rotation, ViewGroup.LayoutParams params, int bg, int gravity, int size, int color, int tf) {
        TextView tv = new TextView(this.activity);
        tv.setText(s);
        tv.setHeight((int) ConvertUnitUtil.convertDpToPx(this.activity, 80));
        tv.setRotation(rotation);
        if (params != null) {
            tv.setLayoutParams(params);
        }
        tv.setBackgroundResource(bg);
        tv.setGravity(gravity);
        tv.setTextSize(size);
        if (color != -1) {
            tv.setTextColor(color);
        }
        tv.setTypeface(tv.getTypeface(), tf);
        tv.setId(View.generateViewId());
        return tv;
    }

}
