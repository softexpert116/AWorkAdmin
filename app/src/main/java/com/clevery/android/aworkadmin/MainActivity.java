package com.clevery.android.aworkadmin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clevery.android.aworkadmin.Fragments.DashboardFragment;
import com.clevery.android.aworkadmin.Fragments.FeedFragment;
import com.clevery.android.aworkadmin.Fragments.PartyFragment;
import com.clevery.android.aworkadmin.Fragments.ReportFragment;
import com.clevery.android.aworkadmin.Fragments.SettingFragment;
import com.clevery.android.aworkadmin.Fragments.UserFragment;
import com.clevery.android.aworkadmin.Utils.ProgressDialog;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    static public TextView txt_reports, txt_reports1;
    TextView txt_header;
    static public RelativeLayout ly_report;
    FragmentTransaction transaction;
    FrameLayout frameLayout;
    static FrameLayout fl_reports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ly_report = (RelativeLayout)findViewById(R.id.ly_report);
        txt_reports1 = (TextView) findViewById(R.id.txt_reports1);
        txt_header = (TextView)findViewById(R.id.txt_header);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fl_reports = (FrameLayout)navigationView.getMenu().findItem(R.id.nav_report).getActionView();
        txt_reports = (TextView) fl_reports.findViewById(R.id.txt_reports);

        frameLayout = (FrameLayout)findViewById(R.id.fl_container);
        selectFragment(new DashboardFragment());
        txt_header.setText("Dashboard");
        parseRegisterToken();
    }

    private void parseRegisterToken() {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", getString(R.string.firebase_sender_id));
        installation.put("userId", ParseUser.getCurrentUser().getObjectId());
        installation.saveInBackground();
        installation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
//                    Log.d("register token: ", e.getMessage());
//                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void selectFragment(Fragment fragment) {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_container, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you going to finish app?");
            builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    ActivityCompat.finishAffinity(MainActivity.this);
                    System.exit(0);
                }
            });
            builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            selectFragment(new DashboardFragment());
            txt_header.setText("Dashboard");
        } else if (id == R.id.nav_users) {
            selectFragment(new UserFragment());
            txt_header.setText("Users");
        } else if (id == R.id.nav_party) {
            selectFragment(new PartyFragment());
            txt_header.setText("Parties");
        } else if (id == R.id.nav_feed) {
            selectFragment(new FeedFragment());
            txt_header.setText("Live Feeds");
        } else if (id == R.id.nav_report) {
            selectFragment(new ReportFragment());
            txt_header.setText("Reports");
        } else if (id == R.id.nav_setting) {
            selectFragment(new SettingFragment());
            txt_header.setText("Setting");
        } else if (id == R.id.nav_logout) {
            ParseUser.logOut();
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void parseReportFetch() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Report");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() == 0) {
                        fl_reports.setVisibility(View.GONE);
                    } else {
                        fl_reports.setVisibility(View.VISIBLE);
                    }
                    txt_reports.setText(String.valueOf(objects.size()));
                } else {
//                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static void parseBlockEvent(final Context context, final ParseObject event, final String description, final PartyFragment fragment, final PartyDetailActivity activity) {
        ProgressDialog.showDlg(context);
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Join");
        query1.whereEqualTo("event", event);
        query1.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> joins, ParseException e) {
                if (e == null) {
                    for (ParseObject join: joins) {
                        try {
                            join.delete();
                        }catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        join.saveInBackground();
                    }
                    if (fragment != null) {
                        fragment.parseEventFetch();
                    }
                    if (activity != null) {
                        activity.finish();
                    }
                    parseBlockReport(context, "Event", event.getObjectId(), null);
                    Toast.makeText(context, "Successfully blocked!", Toast.LENGTH_SHORT).show();
                    App.parseEmail(event.getParseUser("user").getUsername(), "Party Block", "Your party (" + event.getString("name") + ") has been blocked. We've found out abuse of " + description.toLowerCase() + " from this party.");
                } else {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                ProgressDialog.hideDlg();
            }
        });
        try {
            event.delete();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        event.saveInBackground();
    }
    public static void parseBlockReport(final Context context, final String type, final String id, final ReportFragment fragment) {
//        ProgressDialog.showDlg(context);
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Report");
        query1.whereEqualTo("type", type);
        query1.whereEqualTo("id", id);
        query1.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
//                ProgressDialog.hideDlg();
                if (e == null) {
                    for (ParseObject object:objects) {
                        try {
                            object.delete();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        object.saveInBackground();
                    }
                } else {
                    if (e.getCode() != 101) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                if (fragment != null) {
                    fragment.parseMediaFetch();
                    fragment.parseReportFetch();
                }
            }
        });
        query1.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
            }
        });
    }

    public static void parseBlockPost(final Context context, final ParseObject post, final String description, final FeedFragment fragment) {
        ProgressDialog.showDlg(context);
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Media");
        query1.whereEqualTo("post", post);
        query1.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> medias, ParseException e) {
                if (e == null) {
                    for (ParseObject media: medias) {
                        try {
                            media.delete();
                        }catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        media.saveInBackground();
                    }
                    ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Comment");
                    query2.whereEqualTo("post", post);
                    query2.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> comments, ParseException e) {
                            if (e == null) {
                                ProgressDialog.hideDlg();
                                for (ParseObject comment: comments) {
                                    try {
                                        comment.delete();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                    comment.saveInBackground();
                                }
                                if (fragment != null) {
                                    fragment.parsePostFetch();
                                }
                                parseBlockReport(context, "Event", post.getObjectId(), null);
                                Toast.makeText(context, "Successfully blocked!", Toast.LENGTH_SHORT).show();
                                App.parseEmail(post.getParseUser("user").getUsername(), "Live Feed Block", "Your live feed (" + post.getString("description") + ") has been blocked. We've found out abuse of " + description.toLowerCase() + " from this feed.");
                            } else {
                                ProgressDialog.hideDlg();
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    ProgressDialog.hideDlg();
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        try {
            post.delete();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        post.saveInBackground();
    }
    public static void parseBlockUser(final Context context, final ParseUser user, final String description, final UserFragment fragment) {
        ProgressDialog.showDlg(context);
        ParseObject block = new ParseObject("Block");
        block.put("user", user);
        block.put("description", description);
        block.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                ProgressDialog.hideDlg();
                if (e == null) {
                    parseRefreshEventByBlockedUser(context, user);
                    fragment.parseBlockFetch();
                    fragment.parseUserFetch();
                    Toast.makeText(context, "Successfully blocked!", Toast.LENGTH_SHORT).show();
                    App.parseEmail(user.getUsername(), "Account Block", "Your account has been blocked. We've found out abuse of " + description.toLowerCase() + " from your account.");
                } else {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public static void parseRefreshEventByBlockedUser(final Context context, ParseUser user) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.whereEqualTo("user", user);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> events, ParseException e) {
                if (e == null) {
                    for (ParseObject event: events) {
                        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Join");
                        query1.whereEqualTo("event", event);
                        query1.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> joins, ParseException e) {
                                if (e == null) {
                                    for (ParseObject join: joins) {
                                        try {
                                            join.delete();
                                        }catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                        join.saveInBackground();
                                    }
                                } else {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        try {
                            event.delete();
                        }catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        event.saveInBackground();
                    }
                } else {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public static void parseUnBlockUser(final Context context, ParseObject user, final UserFragment fragment) {
        ProgressDialog.showDlg(context);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Block");
        query.whereEqualTo("user", user);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ProgressDialog.hideDlg();
                    for (ParseObject object:objects) {
                        try {
                            object.delete();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        object.saveInBackground();
                    }
                    fragment.parseBlockFetch();
                    fragment.parseUserFetch();
                    Toast.makeText(context, "Successfully unblocked!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        parseReportFetch();
        App.cancelAllNotifications();
    }
}
