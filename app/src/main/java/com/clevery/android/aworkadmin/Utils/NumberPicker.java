package com.clevery.android.aworkadmin.Utils;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.clevery.android.aworkadmin.R;

public class NumberPicker extends Dialog {
    public static android.widget.NumberPicker numberPicker;
    private Context context;
    public int selectedValue = 0;

    public interface OnClickListner {
        void OnClickOk();
        void OnClickCancel();
    }

    public NumberPicker.OnClickListner onClickListner = null;

    public NumberPicker(Context _context, int min, int max, String title) {
        super(_context, R.style.Theme_AppCompat_Light_Dialog);
        context = _context; selectedValue = min;
        setContentView(R.layout.dialog_number_picker);
        TextView txt_title = (TextView)findViewById(R.id.txt_title);
        txt_title.setText(title);
        Button b1 = (Button) findViewById(R.id.button1);
        Button b2 = (Button) findViewById(R.id.button2);
        numberPicker = (android.widget.NumberPicker)findViewById(R.id.numberPicker1);
        numberPicker.setMaxValue(max);
        numberPicker.setMinValue(min);
        numberPicker.setWrapSelectorWheel(false);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedValue = numberPicker.getValue();
                onClickListner.OnClickOk();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListner.OnClickCancel();
            }
        });
        this.show();
    }
}
