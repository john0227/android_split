package com.android.split.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.android.library.log.LogService;
import com.android.split.R;
import com.android.split.logic.Logic;
import com.android.split.util.ConvertUnitUtil;
import com.android.split.util.DecimalFormatUtil;
import com.android.split.vo.TransactionMemberVo;
import com.otaliastudios.zoom.ZoomLayout;

import java.util.List;

public class ResultFragment extends Fragment {

    private Activity activity;

    private View rootLayout;
    private LinearLayout startTable_container;
    private LinearLayout startNetIncome_container;
    private LinearLayout afterTable_container;
    private LinearLayout afterNetIncome_container;

    private Logic logic;

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
        } catch (Exception e) {
            LogService.error(this.activity, e.getMessage(), e);
        }

        return this.rootLayout;
    }

    private void init() {
        this.startTable_container = this.rootLayout.findViewById(R.id.startTable_container);
        this.startNetIncome_container = this.rootLayout.findViewById(R.id.startNetIncome_container);
        this.afterTable_container = this.rootLayout.findViewById(R.id.afterTable_container);
        this.afterNetIncome_container = this.rootLayout.findViewById(R.id.afterNetIncome_container);

        this.logic = Logic.getInstance();
    }

    public void onLoad() {
        // Show before simplifying
        createTransferTable(logic.getTransferTable(), this.startTable_container);

        // Show after simplifying
        logic.simplify();
        createTransferTable(logic.getTransferTable(), this.afterTable_container);
    }

    private void createTransferTable(double[][] transferTable, LinearLayout tableContainer) {
        ZoomLayout zoomLayout = createZoomLayout((int) ConvertUnitUtil.convertDpToPx(this.activity, 80) * transferTable.length + 100);
        ConstraintLayout container = (ConstraintLayout) LayoutInflater.from(this.activity).inflate(R.layout.tablelayout, zoomLayout, false);
        LinearLayout tableLayout = container.findViewById(R.id.tablelayout);
        tableLayout.removeAllViews();
        tableContainer.removeAllViews();

        TextView tvSender = createTextView(R.string.sender, -90f, null, android.R.color.transparent,
                Gravity.CENTER|Gravity.BOTTOM, 14, -1, Typeface.BOLD);
        TextView tvRcver = createTextView(R.string.receiver, 0f, null, android.R.color.transparent,
                Gravity.CENTER, 14, -1, Typeface.BOLD);
        container.addView(tvSender);
        container.addView(tvRcver);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(container);
        constraintSet.connect(tvSender.getId(), ConstraintSet.RIGHT, tableLayout.getId(), ConstraintSet.LEFT);
        constraintSet.connect(tvSender.getId(), ConstraintSet.TOP, tableLayout.getId(), ConstraintSet.TOP);
        constraintSet.connect(tvSender.getId(), ConstraintSet.BOTTOM, tableLayout.getId(), ConstraintSet.BOTTOM, 5);
        constraintSet.connect(tvRcver.getId(), ConstraintSet.BOTTOM, tableLayout.getId(), ConstraintSet.TOP, 5);
        constraintSet.connect(tvRcver.getId(), ConstraintSet.LEFT, tableLayout.getId(), ConstraintSet.LEFT);
        constraintSet.connect(tvRcver.getId(), ConstraintSet.RIGHT, tableLayout.getId(), ConstraintSet.RIGHT);
        constraintSet.applyTo(container);

        zoomLayout.addView(container);
        tableContainer.addView(zoomLayout);

        // width = length of one cell * total number of cells
        int width = (this.logic.getLongest() * 15) * transferTable.length;
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

    private ZoomLayout createZoomLayout(int height) {
        ZoomLayout zoomLayout = new ZoomLayout(this.activity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
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
