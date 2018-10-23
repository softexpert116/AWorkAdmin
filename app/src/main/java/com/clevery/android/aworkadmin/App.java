package com.clevery.android.aworkadmin;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class App extends Application {

    public static App app;
    public static SharedPreferences prefs;
    private static Context mContext;

    public static String DATE_FORMAT = "h:mm a, dd/MM/yyyy";
    public static String DATE_FORMAT1 = "dd/MM/yyyy";
    public static boolean relogin = false;

    public static String NEW_PARTY_ARRAY = "NEW_PARTY_ARRAY";
    public static String NEW_FRIEND_ARRAY = "NEW_FRIEND_ARRAY";
    public static String NEW_CHAT_ARRAY = "NEW_CHAT_ARRAY";
    public static String NEW_JOIN_ARRAY = "NEW_JOIN_ARRAY";
    public static int mainIndex = -1;
    private static final int MAX_SMS_MESSAGE_LENGTH = 160;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        app = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_api_url)).build()
        );
    }
    public static void parsePush(String type, String senderId, String receiverId, String partyId, String title, String message) {
        final HashMap<String, String> params = new HashMap<>();
        params.put("type", type);
        params.put("senderId", senderId);
        params.put("receiverId", receiverId);
        params.put("partyId", partyId);
        params.put("title", title);
        params.put("message", message);
// Calling the cloud code function
        ParseCloud.callFunctionInBackground("afterwork", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object response, ParseException exc) {
                if(exc == null) {
                    // The function was executed, but it's interesting to check its response
                }
                else {
                    // Something went wrong
                }
            }
        });

    }
    public static void parseEmail(String emailAddress, String emailSubject, String emailBody) {
        Map<String, String> params = new HashMap<>();

// Create the fields "emailAddress", "emailSubject" and "emailBody"
// As Strings and use this piece of code to add it to the request
        params.put("toEmail", emailAddress);
        params.put("subject", emailSubject);
        params.put("body", emailBody);

        ParseCloud.callFunctionInBackground("sendEmail", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object response, ParseException exc) {
                if(exc == null) {
                    // The function executed, but still has to check the response
//                    Toast.makeText(mContext, mContext.getString(R.string.email_success_message), Toast.LENGTH_LONG).show();
                }
                else {
                    // Something went wrong
//                    Toast.makeText(mContext, exc.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
    public static void setPreferences(String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void setPreference(String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putString(key, value)
                .commit();
    }
    public static String readPreference(String key, String defaultValue) {
        String value = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(key, defaultValue);
        return value;
    }
    public static void removePreference(String key) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
    }

    //    public static void setPreference_JsonObject(String key, JSONObject jsonObject) {
//        Gson gson = new Gson();
//        String json = gson.toJson(jsonObject);
//        setPreference(key, json);
//    }
//    public static JSONObject readPreference_JsonObject(String key) {
//        String json = readPreference(key, "");
//        Type type = new TypeToken<JSONObject>(){}.getType();
//        Gson gson = new Gson();
//        JSONObject jsonObject = gson.fromJson(json, type);
//        return jsonObject;
//    }
//    public static String getSelectedLang() {
//        String lang = En;
//        JSONObject jsonObject = App.readPreference_JsonObject(App.MY_INFO);
//        if (jsonObject != null) {
//            lang = getJsonValue(jsonObject, "lang");
//        }
//        return lang;
//    }
//    public static Configuration getSelectedConfiguration() {
//        String lang = En;
//        JSONObject jsonObject = App.readPreference_JsonObject(App.MY_INFO);
//        if (jsonObject != null) {
//            lang = getJsonValue(jsonObject, "lang");
//        }
//        Configuration config = new Configuration();
//        if (lang.equals(App.Fr)) {
//            config.locale = Locale.FRENCH;
//        } else if (lang.equals(App.De)) {
//            config.locale = Locale.GERMAN;
//        } else {
//            config.locale = Locale.ENGLISH;
//        }
//        return config;
//    }
    public static String getJsonValue(JSONObject jsonObject, String key) {
        String value = "";
        try {
            value = jsonObject.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
    public static void setPreference_array_String(String key, ArrayList<String> list) {
        Set<String> tasksSet = new HashSet<String>(list);
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putStringSet(key, tasksSet)
                .commit();
    }

    public static ArrayList<String> readPreference_array_String(String key) {
        Set<String> tasksSet = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getStringSet(key, new HashSet<String>());
        ArrayList<String> tasksList = new ArrayList<String>(tasksSet);
        return tasksList;
    }
    public static void showToast(String string) {
        Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();

    }
    public static void cancelAllNotifications() {
        NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

    }

    public static void social_share(Context context, String url) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, url);
        context.startActivity(Intent.createChooser(share, "Title of the dialog the system will open"));
    }
    public void generateHashkey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.ujs.acer.Oyoo",  PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                String hashKey = Base64.encodeToString(md.digest(), Base64.NO_WRAP);
                Log.e("hashkey -------------", hashKey); /// CkLR2IIEs9xCrDGJbKOQ/Jr3exE=
// release key hash: w6gx2BgXV0ybPMNC4PfbKnfpu50=
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {
        }
    }
    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int count = listAdapter.getCount();
        int totalHeight = 0;
        for (int i = 0; i < count; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        params.height *= 1.2;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    public static void DialNumber(String number, Context context)
    {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
//            intent.setData(Uri.parse("tel:" + number));
            Uri uri = ussdToCallableUri(number);
            intent.setData(uri);
//            context.startActivity(intent);
        } catch (SecurityException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT);
        }
    }

    private static Uri ussdToCallableUri(String ussd) {

        String uriString = "";

        if(!ussd.startsWith("tel:"))
            uriString += "tel:";

        for(char c : ussd.toCharArray()) {

            if(c == '#')
                uriString += Uri.encode("#");
            else
                uriString += c;
        }

        return Uri.parse(uriString);
    }
    public static int getThemeAccentColor (final Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme ().resolveAttribute (R.attr.colorAccent, value, true);
        return value.data;
    }
    public static void openUrl (String url, Context context) {
        if (!URLUtil.isValidUrl(url)) {
            Toast.makeText(context, "Invalid url", Toast.LENGTH_SHORT);
            return;
        }
        Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static String getTimestampString()
    {
        long tsLong = System.currentTimeMillis();
        String ts =  Long.toString(tsLong);
        return ts;
    }
    public static int getPrimaryColor() {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = mContext.getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        TypedArray arr = mContext.obtainStyledAttributes(typedValue.data, new int[]{
                        android.R.attr.textColorPrimary});
        int primaryColor = arr.getColor(0, -1);
        return primaryColor;
    }
}