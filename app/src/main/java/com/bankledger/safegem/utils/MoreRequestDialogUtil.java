package com.bankledger.safegem.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;

import com.bankledger.safegem.ui.widget.CommonProgressDialog;


/**
 * Date：2018/9/20
 * Author: bankledger
 */
public class MoreRequestDialogUtil {

    private static CommonProgressDialog dialog;

    public static void showProgressDialog(Context mContext) {
        if (mContext != null) {
            if (dialog == null) {
                dialog = new CommonProgressDialog(mContext);
                dialog.setTitle("");
                dialog.setCancelable(false);
//                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//                    @Override
//                    public boolean onKey(DialogInterface d, int keyCode, KeyEvent event) {
//                        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
//                            dialog.dismiss();
//                            dialog = null;
//                        }
//                        return false;
//                    }
//                });
                dialog.show();
            } else {
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            }
        }
    }

    public static void showProgressDialog(Context mContext, final String showStr) {
        if (mContext != null) {
            if (dialog == null) {
                dialog = new CommonProgressDialog(mContext);
                dialog.setTitle(showStr);
                dialog.setCancelable(false);
//                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//                    @Override
//                    public boolean onKey(DialogInterface d, int keyCode, KeyEvent event) {
//                        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
//                            dialog.dismiss();
//                            dialog = null;
//                        }
//                        return false;
//                    }
//                });
                dialog.show();
            } else {
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            }
        }
    }

    public static void dismissProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

}
