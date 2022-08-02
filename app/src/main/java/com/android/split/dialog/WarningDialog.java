package com.android.split.dialog;

import android.app.Activity;
import android.app.AlertDialog;

public class WarningDialog {

    public static void show(Activity activity, String title, String msg, String cancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setNegativeButton(cancel, (dialog, which) -> {});
        builder.setCancelable(false);

        builder.show();
    }

}
