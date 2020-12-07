package com.burak.humanbenchmarks;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.widget.Toast;

public class LoadingDialog {

    private Activity activity;
    private AlertDialog alertDialog;

    LoadingDialog(Activity myActivity)
    {
        activity = myActivity;
    }

    private Boolean controlForDismiss = false;

    void loadingAlertDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_loading_screen, null));
        builder.setCancelable(false);
        controlForDismiss = true;
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    void dismissDialog()
    {
        if (controlForDismiss){
            alertDialog.dismiss();
            controlForDismiss = false;
        }
    }

}
