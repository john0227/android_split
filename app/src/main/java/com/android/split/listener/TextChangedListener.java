package com.android.split.listener;

import android.text.Editable;
import android.text.TextWatcher;

public class TextChangedListener implements TextWatcher {

    private OnTextChangedListener onTextChangedListener;

    public interface OnTextChangedListener {
        void onTextChanged(Editable s);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        if (this.onTextChangedListener != null) {
            this.onTextChangedListener.onTextChanged(s);
        }
    }

    public void setOnTextChangedListener(OnTextChangedListener onTextChangedListener) {
        this.onTextChangedListener = onTextChangedListener;
    }

}
