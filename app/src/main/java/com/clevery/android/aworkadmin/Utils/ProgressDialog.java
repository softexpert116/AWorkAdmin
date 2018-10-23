package com.clevery.android.aworkadmin.Utils;

import android.app.Dialog;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.clevery.android.aworkadmin.R;


public class ProgressDialog extends Dialog {
    private static ProgressDialog progressDialog;
    private ImageView iv;
    private Context context;

    public ProgressDialog(Context _context) {
        super(_context, R.style.TransparentProgressDialog);
        context = _context;
        setContentView(R.layout.dialog_progress);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        iv = (ImageView)findViewById(R.id.iv_progress);
        Glide.with(context)
                .asGif()
                .load(R.drawable.loader).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(iv);
//        Glide.with(context).load(R.drawable.loader).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade().into(iv);
    }

    @Override
    public void show() {
        super.show();
    }

    public static void showDlg(Context context) {
        if (progressDialog != null) {
            progressDialog = null;
        }

        progressDialog = new ProgressDialog(context);
        progressDialog.show();
    }

    public static void hideDlg() {
        if (progressDialog == null)
            return;

        if (progressDialog.isShowing())
            progressDialog.dismiss();

        progressDialog = null;
    }
}
