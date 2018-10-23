package com.clevery.android.aworkadmin.Utils;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

public class AlertUtil {

    /**
     * Show Alert Dialog
     *
     * @param context
     * @param titleId
     * @param messageId
     */
    public static void showAlert(Context context, int titleId, int messageId) {
        Dialog dlg = new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert).setTitle(titleId)
                .setPositiveButton(android.R.string.ok, null)
                .setMessage(messageId).create();

        dlg.show();
    }

    /**
     * Show Alert Dialog
     *
     * @param context
     * @param titleId
     * @param message
     */
    public static void showAlert(Context context, int titleId, String message) {
        Dialog dlg = new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert).setTitle(titleId)
                .setPositiveButton(android.R.string.ok, null)
                .setMessage(message).create();

        dlg.show();
    }

    /**
     * Show Alert Dialog
     *
     * @param context
     * @param message
     */
    public static void showAlert(Context context, String message) {
        Dialog dlg = new AlertDialog.Builder(context)
                .setPositiveButton(android.R.string.ok, null)
                .setMessage(message).create();

        dlg.show();
    }

    /**
     * Show Alert Dialog
     *
     * @param context
     * @param message
     * @param positiveListener
     */


    /**
     * Show Alert Dialog
     *
     * @param context

     * @param positiveButtontxt
     * @param positiveListener
     * @param negativeButtontxt
     * @param negativeListener
     */
    public static void showAlert(Context context, String message,
                                 CharSequence positiveButtontxt,
                                 DialogInterface.OnClickListener positiveListener,
                                 CharSequence negativeButtontxt,
                                 DialogInterface.OnClickListener negativeListener) {
        Dialog dlg = new AlertDialog.Builder(context)
                 .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("")
                .setPositiveButton(positiveButtontxt, positiveListener)
//                .setNegativeButton(negativeButtontxt, negativeListener)
                .setMessage(message).setCancelable(false).create();

        dlg.show();
    }
    public static void showAlert1(Context context, String message,
                                  CharSequence positiveButtontxt,
                                  DialogInterface.OnClickListener positiveListener,
                                  CharSequence negativeButtontxt,
                                  DialogInterface.OnClickListener negativeListener) {
        Dialog dlg = new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("")
                .setPositiveButton(positiveButtontxt, positiveListener)
                .setNegativeButton(negativeButtontxt, negativeListener)
                .setMessage(message).setCancelable(false).create();

        dlg.show();
    }

    /**
     * Show Alert Dialog
     *
     * @param context
     * @param titleId
     * @param message
     * @param positiveButtontxt
     * @param positiveListener
     */
    public static void showAlert(Context context, int titleId, String message,
                                 CharSequence positiveButtontxt,
                                 DialogInterface.OnClickListener positiveListener) {
        Dialog dlg = new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert).setTitle(titleId)
                .setPositiveButton(positiveButtontxt, positiveListener)
                .setMessage(message).setCancelable(false).create();

        dlg.show();
    }

    /**
     * Show Alert Dialog
     *
     * @param context
     * @param titleId
     * @param message
     * @param view
     * @param positiveButtontxt
     * @param positiveListener
     */
    public static void showAlert(Context context, int titleId, String message,
                                 View view,
                                 CharSequence positiveButtontxt,
                                 DialogInterface.OnClickListener positiveListener) {
        Dialog dlg = new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert).setTitle(titleId)
                .setPositiveButton(positiveButtontxt, positiveListener)
                .setMessage(message)
                .setView(view).setCancelable(true).create();

        dlg.show();
    }

    /**
     * Show Alert Dialog
     *
     * @param context
     * @param message
     * @param view
     * @param positiveButtontxt
     * @param positiveListener
     */

}
