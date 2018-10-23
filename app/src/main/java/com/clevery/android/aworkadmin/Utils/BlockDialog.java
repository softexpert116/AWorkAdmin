package com.clevery.android.aworkadmin.Utils;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.IdRes;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.clevery.android.aworkadmin.R;

public class BlockDialog extends Dialog {
    private static BlockDialog blockDialog;
    private Context context;
    public String abuseType = "";

    public interface OnClickListner {
        void OnClickOk();
        void OnClickCancel();
    }

    public OnClickListner onClickListner = null;

    public BlockDialog(Context _context) {
        super(_context, R.style.Theme_AppCompat_Light_Dialog);
        context = _context;
        setContentView(R.layout.dialog_block);
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                RadioButton rb = (RadioButton)findViewById(checkedId);
                abuseType = rb.getText().toString();
            }
        });
        Button btn_ok = (Button)findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (abuseType.length() == 0) {
                    Toast.makeText(context, context.getString(R.string.abuse_choose), Toast.LENGTH_LONG).show();
                } else {
                    onClickListner.OnClickOk();
                }
            }
        });
        Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListner.OnClickCancel();
            }
        });
    }
}
