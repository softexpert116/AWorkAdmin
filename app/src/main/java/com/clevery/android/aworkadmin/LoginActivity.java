package com.clevery.android.aworkadmin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clevery.android.aworkadmin.Utils.AlertUtil;
import com.clevery.android.aworkadmin.Utils.ProgressDialog;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseSession;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.btn_signin);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        Button btn_password = (Button)findViewById(R.id.btn_password);
        btn_password.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dlg = new Dialog(LoginActivity.this);
                Window window = dlg.getWindow();
                View view1 = getLayoutInflater().inflate(R.layout.dialog_send_email, null);
                dlg.setContentView(view1);
                final EditText edit_email = (EditText)view1.findViewById(R.id.edit_email);
                window.setGravity(Gravity.CENTER);
                window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                dlg.show();
                Button btn_submit = (Button)view1.findViewById(R.id.btn_submit);
                btn_submit.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String email = edit_email.getText().toString().trim();
                        if (email.length() == 0) {
                            Toast.makeText(LoginActivity.this, "Please fill in email field.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            Toast.makeText(LoginActivity.this, "Invalid email address.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        parseForgotPassword(email);
                        dlg.dismiss();
                    }
                });
                dlg.show();
            }
        });
        checkUserLoggedIn();
    }
    private void parseForgotPassword(String email) {
        ProgressDialog.showDlg(LoginActivity.this);
        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
            @Override
            public void done(ParseException e) {
                ProgressDialog.hideDlg();
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "Please confirm your email to reset your password.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void checkUserLoggedIn() {

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            return;
        } else {
            ProgressDialog.showDlg(LoginActivity.this);
            ParseSession.getCurrentSessionInBackground(new GetCallback<ParseSession>() {
                @Override
                public void done(ParseSession object, ParseException e) {
                    if (object != null) {
                        goToMain();
                    } else {
                        ParseUser.logOut();
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    ProgressDialog.hideDlg();
                }
            });
        }
    }
    private void goToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
    private void attemptLogin() {
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString().trim();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            parseLogin(username, password);
        }
    }

    private boolean isUsernameValid(String email) {
        //TODO: Replace this with your own logic
        return !email.contains(" ");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 4;
    }

    void parseLogin(String username, String password){
        ProgressDialog.showDlg(LoginActivity.this);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                ProgressDialog.hideDlg();
                if (parseUser != null) {
                    if (!parseUser.getBoolean("isAdmin")) {
                        ParseUser.logOut();
                        AlertUtil.showAlert(LoginActivity.this, "This account is not Admin. Please try another one.");
                    } else {
                        boolean emailVerified = parseUser.getBoolean("emailVerified");
                        if (emailVerified) {
                            goToMain();
                        } else {
                            ParseUser.logOut();
                            AlertUtil.showAlert(LoginActivity.this, "Please verify your email address before login.");
                        }
                    }
                } else {
                    ParseUser.logOut();
                    if (e.getCode() == 101) {
                        Toast.makeText(LoginActivity.this, "Invalid username or password.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}

