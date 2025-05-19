package com.example.crabfood.helpers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.crabfood.R;

public class CustomDialogConfirm {
    private Dialog dialog;
    private TextView tvTitle, tvMessage;
    private Button btnNegative, btnPositive;
    private OnDialogClickListener listener;

    public interface OnDialogClickListener {
        void onOkClicked();
    }

    public CustomDialogConfirm(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        // Initialize views
        tvTitle = dialog.findViewById(R.id.dialog_title);
        tvMessage = dialog.findViewById(R.id.dialog_message);
        btnNegative = dialog.findViewById(R.id.btn_negative);
        btnPositive = dialog.findViewById(R.id.btn_positive);

        // Set default texts
        tvTitle.setText("Order Successful");
        tvMessage.setText("Thanks for order!");

        btnPositive.setOnClickListener(v -> {
            if (listener != null) listener.onOkClicked();
            dismiss();
        });

        btnNegative.setOnClickListener(v -> {
            dismiss();
        });
    }

    public CustomDialogConfirm setListener(OnDialogClickListener listener) {
        this.listener = listener;
        return this;
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    // Optional methods for customization
    public CustomDialogConfirm setTitle(String title) {
        tvTitle.setText(title);
        return this;
    }

    public CustomDialogConfirm setMessage(String message) {
        tvMessage.setText(message);
        return this;
    }

    public CustomDialogConfirm setButtonPositiveText(String text) {
        btnPositive.setText(text);
        return this;
    }

    public CustomDialogConfirm setButtonNegativeText(String text) {
        btnNegative.setText(text);
        return this;
    }
}
