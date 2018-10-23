package com.clevery.android.aworkadmin.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.clevery.android.aworkadmin.MainActivity;
import com.clevery.android.aworkadmin.R;
import com.clevery.android.aworkadmin.Utils.AlertUtil;
import com.clevery.android.aworkadmin.Utils.ProgressDialog;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class SettingFragment extends Fragment {
    MainActivity activity;
    Button btn_update;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        final EditText username = (EditText)v.findViewById(R.id.username);
        final EditText password = (EditText)v.findViewById(R.id.password);
        final EditText email = (EditText)v.findViewById(R.id.email);
        username.setText(ParseUser.getCurrentUser().getUsername());
        email.setText(ParseUser.getCurrentUser().getEmail());
        final CheckBox checkBox = (CheckBox)v.findViewById(R.id.checkBox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    Toast.makeText(activity, "Email update will require email verification.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_update = (Button)v.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameStr = username.getText().toString().trim();
                String passwordStr = password.getText().toString();
                String emailStr = email.getText().toString();
                if (usernameStr.length()*passwordStr.length() == 0) {
                    AlertUtil.showAlert(activity, "Please fill in all fields");
                    return;
                }
                if (passwordStr.length() < 8) {
                    AlertUtil.showAlert(activity, "Password must be more than 8 characters.");
                    return;
                }
                if (emailStr.length() == 0 && checkBox.isChecked()) {
                    AlertUtil.showAlert(activity, "Please fill in email field.");
                    return;
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
                    AlertUtil.showAlert(activity, "Invalid email address.");
                    return;
                }
                parseUpdateAdmin(usernameStr, passwordStr, emailStr, checkBox.isChecked());
            }
        });
        return v;
    }
    private void parseUpdateAdmin(final String username, final String password, final String email, final boolean emailChecked) {
        btn_update.setEnabled(false);
        ProgressDialog.showDlg(activity);
        final ParseUser user = ParseUser.getCurrentUser();
        if (emailChecked) {
            user.setEmail(email);
        }
        user.setUsername(username);
        user.setPassword(password);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    if (emailChecked) {
                        Toast.makeText(activity, "Profile has been updated successfully. Please verify your email to re-login", Toast.LENGTH_LONG).show();
                        activity.finish();
                    } else {
                        Toast.makeText(activity, "Profile has been updated successfully.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                ProgressDialog.hideDlg();
                btn_update.setEnabled(true);
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
