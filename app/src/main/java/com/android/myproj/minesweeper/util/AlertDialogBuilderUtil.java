package com.android.myproj.minesweeper.util;

import android.app.Activity;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.android.myproj.minesweeper.R;

public class AlertDialogBuilderUtil {

    public static AlertDialog.Builder buildAlertDialog(
            Activity activity, String title, String message, String posButtonText, String negButtonText,
            DialogInterface.OnClickListener posAction, DialogInterface.OnClickListener negAction,
            boolean isCancelable) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyDialogTheme);

        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(posButtonText, posAction);
        builder.setNegativeButton(negButtonText, negAction);

        builder.setCancelable(isCancelable);

        return builder;
    }

    public static AlertDialog.Builder buildAlertDialog(
            Activity activity, String title, String message, String posButtonText, String negButtonText,
            DialogInterface.OnClickListener posAction, DialogInterface.OnClickListener negAction,
            DialogInterface.OnCancelListener cancelAction, boolean isCancelable) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyDialogTheme);

        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(posButtonText, posAction);
        builder.setNegativeButton(negButtonText, negAction);
        builder.setOnCancelListener(cancelAction);

        builder.setCancelable(isCancelable);

        return builder;
    }

}
