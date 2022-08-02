package com.android.split.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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
    private CardView startTable_container;

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

        this.logic = Logic.getInstance();
    }

    public void onLoad() {
        createTransferTable(logic.getTransferTable());
    }

    private void createTransferTable(double[][] transferTable) {
        ZoomLayout zoomLayout = createZoomLayout((int) ConvertUnitUtil.convertDpToPx(this.activity, 70) * transferTable.length);
        LinearLayout tableLayout = createLinearLayout();
        zoomLayout.addView(tableLayout);
        this.startTable_container.addView(zoomLayout);
        // width = length of one cell * total number of cells
        int width = (this.logic.getLongest() * 15) * transferTable.length;
        // Create LinearLayoutParam to be used
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                (int) ConvertUnitUtil.convertDpToPx(this.activity, width),
                (int) ConvertUnitUtil.convertDpToPx(this.activity, 70)
        );
        LinearLayout.LayoutParams cellParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        // Create first row containing names
        LinearLayout linearLayout = new LinearLayout(this.activity);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setLayoutParams(layoutParams);
        TextView textView;
        for (int i = 0; i < this.names.size() + 1; i++) {
            textView = new TextView(this.activity);
            textView.setText(i == 0 ? "" : this.names.get(i - 1));
            textView.setLayoutParams(cellParams);
            textView.setBackgroundResource(R.drawable.border);
            textView.setGravity(Gravity.CENTER);
            linearLayout.addView(textView);
        }
        tableLayout.addView(linearLayout);
        // Create the rest of rows
        for (int r = 0; r < transferTable.length - 1; r++) {
            linearLayout = new LinearLayout(this.activity);
            linearLayout.setLayoutParams(layoutParams);
            for (int c = 0; c < transferTable[r].length; c++) {
                textView = new TextView(this.activity);
                textView.setText(c == 0 ? this.names.get(r) : DecimalFormatUtil.format(transferTable[r][c - 1]));
                textView.setLayoutParams(cellParams);
                textView.setBackgroundResource(R.drawable.border);
                textView.setGravity(Gravity.CENTER);
                linearLayout.addView(textView);
            }
            tableLayout.addView(linearLayout);
        }
    }

    private ZoomLayout createZoomLayout(int height) {
        ZoomLayout zoomLayout = new ZoomLayout(this.activity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        layoutParams.setMargins(0, 70, 0, 70);
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

    private LinearLayout createLinearLayout() {
        LinearLayout linearLayout = new LinearLayout(this.activity);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        return linearLayout;
    }

}
