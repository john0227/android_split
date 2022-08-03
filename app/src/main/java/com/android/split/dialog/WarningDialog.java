package com.android.split.dialog;

import android.app.Activity;
import android.app.AlertDialog;

import com.android.split.R;

public class WarningDialog {

    public static void show(Activity activity, String title, String msg, String cancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyDialogTheme);

        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setNegativeButton(cancel, (dialog, which) -> {});
        builder.setCancelable(false);

        builder.show();
    }

    public static void show(Activity activity, String title, String msg,
                            String pos, Runnable posAction, String neg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyDialogTheme);

        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(pos, ((dialog, which) -> posAction.run()));
        builder.setNegativeButton(neg, (dialog, which) -> {});
        builder.setCancelable(false);

        builder.show();
    }

}
