package com.systemtech.update.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.systemtech.update.R;

public final class BrandedToast {

    private BrandedToast() {
    }

    public static void show(
            @NonNull Context context,
            @StringRes int message,
            int duration
    ) {
        show(context, context.getText(message), duration);
    }

    public static void show(
            @NonNull Context context,
            @NonNull CharSequence message,
            int duration
    ) {
        Context appContext = context.getApplicationContext();
        View toastView = LayoutInflater.from(appContext).inflate(R.layout.branded_toast, null);
        TextView messageView = toastView.findViewById(R.id.txtToastMessage);
        messageView.setText(message);

        Toast toast = new Toast(appContext);
        toast.setDuration(duration);
        toast.setView(toastView);
        toast.show();
    }
}
