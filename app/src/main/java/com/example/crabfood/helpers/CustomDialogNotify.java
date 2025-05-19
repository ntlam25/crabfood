package com.example.crabfood.helpers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.crabfood.R;
import com.google.android.material.button.MaterialButton;

public class CustomDialogNotify {
    private Dialog dialog;
    private TextView tvTitle, tvMessage, tvSubMessage;
    private MaterialButton btnOk;
    private ImageView imageView;
    private OnDialogClickListener listener;

    public interface OnDialogClickListener {
        void onOkClicked();
    }

    public CustomDialogNotify(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_box);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        // Initialize views
        tvTitle = dialog.findViewById(R.id.tv_title);
        tvMessage = dialog.findViewById(R.id.tv_message);
        tvSubMessage = dialog.findViewById(R.id.tv_sub_message);
        btnOk = dialog.findViewById(R.id.btn_ok);
        imageView = dialog.findViewById(R.id.icon_dialog);

        // Set default texts
        tvTitle.setText("Order Successful");
        tvMessage.setText("Thanks for order!");
        tvSubMessage.setText("See you in the next order!");
        imageView.setImageResource(R.drawable.ic_check);

        btnOk.setOnClickListener(v -> {
            if (listener != null) listener.onOkClicked();
            dismiss();
        });
    }

    public void setListener(OnDialogClickListener listener) {
        this.listener = listener;
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
    public CustomDialogNotify setTitle(String title) {
        tvTitle.setText(title);
        return this;
    }

    public CustomDialogNotify setMessage(String message) {
        tvMessage.setText(message);
        return this;
    }

    public CustomDialogNotify setSubMessage(String subMessage) {
        tvSubMessage.setText(subMessage);
        return this;
    }
    public CustomDialogNotify setIconDialog(int idICon) {
        imageView.setImageResource(idICon);
        return this;
    }
    public CustomDialogNotify setButtonText(String text) {
        btnOk.setText(text);
        return this;
    }
}
