package com.bankledger.safegem.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bankledger.safegem.R;
import com.bankledger.safegem.ui.widget.CommonDialog;
import com.bankledger.safegem.ui.widget.CommonProgressDialog;

/**
 * Date：2018/8/18
 * Author: bankledger
 */

public class DialogUtil {

    private static CommonProgressDialog dialog;

    public static void showProgressDialog(Context mContext) {
        if (mContext != null) {
            if (dialog == null) {
                dialog = new CommonProgressDialog(mContext);
                dialog.setTitle("");
                dialog.setCancelable(true);
                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface d, int keyCode, KeyEvent event) {
                        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                            dialog.dismiss();
                            dialog = null;
                        }
                        return false;
                    }
                });
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
                dialog.setCancelable(true);
                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface d, int keyCode, KeyEvent event) {
                        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                            dialog.dismiss();
                            dialog = null;
                        }
                        return false;
                    }
                });
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

    /**
     * 图片弹框
     */
    public static void showImageDialog(Context context, Bitmap bitmap, final OnClickConfirmListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_image_valid, null);
        ImageView imageView = view.findViewById(R.id.img_valid);
        view.findViewById(R.id.text).setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageBitmap(bitmap);
        final EditText editText = view.findViewById(R.id.et_valid);
        editText.setVisibility(View.VISIBLE);
        final CommonDialog dialog = new CommonDialog(context);
        dialog.setTitle(R.string.hint_valid_img);
        dialog.setMiddleView(view);
        dialog.setAutoDismiss(false);
        dialog.setOnClickCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnClickCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnClickConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickConfirm(editText.getText().toString());
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 文本弹框
     */
    public static void showTextDialog(Context context, int str, final View.OnClickListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_image_valid, null);
        TextView tv = view.findViewById(R.id.text);
        view.findViewById(R.id.img_valid).setVisibility(View.GONE);
        tv.setVisibility(View.VISIBLE);
        view.findViewById(R.id.et_valid).setVisibility(View.GONE);
        tv.setText(str);
        final CommonDialog dialog = new CommonDialog(context);
        dialog.setTitle(R.string.hint_str);
        dialog.setMiddleView(view);
        dialog.setAutoDismiss(false);
        dialog.setOnClickCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnClickCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnClickConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                listener.onClick(view);
            }
        });
        dialog.show();
    }

    public static void showTextDialog(Context context, int title, int msg, final View.OnClickListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_image_valid, null);
        TextView tv = view.findViewById(R.id.text);
        view.findViewById(R.id.img_valid).setVisibility(View.GONE);
        tv.setVisibility(View.VISIBLE);
        view.findViewById(R.id.et_valid).setVisibility(View.GONE);
        tv.setText(msg);
        final CommonDialog dialog = new CommonDialog(context);
        dialog.setTitle(title);
        dialog.setMiddleView(view);
        dialog.setAutoDismiss(false);
        dialog.setOnClickCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnClickCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnClickConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                listener.onClick(view);
            }
        });
        dialog.show();
    }

    public static void showTextDialog(Context context, String title, String msg, final View.OnClickListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_image_valid, null);
        TextView tv = view.findViewById(R.id.text);
        view.findViewById(R.id.img_valid).setVisibility(View.GONE);
        tv.setVisibility(View.VISIBLE);
        view.findViewById(R.id.et_valid).setVisibility(View.GONE);
        tv.setText(msg);
        final CommonDialog dialog = new CommonDialog(context);
        dialog.setTitle(title);
        dialog.setMiddleView(view);
        dialog.setAutoDismiss(false);
        dialog.setOnClickCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnClickCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnClickConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                listener.onClick(view);
            }
        });
        dialog.show();
    }

    /**
     * 文本弹框
     */
    public static void showEditDialog(Context context, View.OnClickListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_image_valid, null);
        EditText editText = view.findViewById(R.id.et_valid);
        editText.setVisibility(View.VISIBLE);
        view.findViewById(R.id.img_valid).setVisibility(View.GONE);
        view.findViewById(R.id.text).setVisibility(View.GONE);
        final CommonDialog dialog = new CommonDialog(context);
        dialog.setTitle(R.string.hint_str);
        dialog.setMiddleView(view);
        dialog.setAutoDismiss(false);
        dialog.setOnClickCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnClickCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnClickConfirmListener(listener);
        dialog.show();
    }

    /**
     * 文本弹框
     */
    public static void showEditDialog(Context context, String name, String title, String hint, OnClickConfirmListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_image_valid, null);
        EditText editText = view.findViewById(R.id.et_valid);
        editText.setFilters(new InputFilter[]{new EditLengthInputFilter(8)});
        editText.setVisibility(View.VISIBLE);
        editText.setHint(hint);
        editText.setText(name);
        editText.setSelection(name.length());
        view.findViewById(R.id.img_valid).setVisibility(View.GONE);
        view.findViewById(R.id.text).setVisibility(View.GONE);
        final CommonDialog dialog = new CommonDialog(context);
        dialog.setTitle(title);
        dialog.setMiddleView(view);
        dialog.setAutoDismiss(false);
        dialog.setOnClickCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnClickCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnClickConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().length() == 0) {
                    ToastUtil.showShort(context, hint);
                    return;
                }
                dialog.dismiss();
                listener.onClickConfirm(editText.getText().toString());
            }
        });
        dialog.show();
    }

    public interface OnClickConfirmListener {
        void onClickConfirm(String content);
    }

}

