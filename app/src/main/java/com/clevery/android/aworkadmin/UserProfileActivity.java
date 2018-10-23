package com.clevery.android.aworkadmin;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.clevery.android.aworkadmin.Adapter.FeedList_Adapter;
import com.clevery.android.aworkadmin.Adapter.PartyList_Adapter;
import com.clevery.android.aworkadmin.Utils.BlockDialog;
import com.clevery.android.aworkadmin.Utils.ProgressDialog;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {
    boolean isPartySelected = true;
    ArrayList<ParseObject> array_events = new ArrayList<>();
    ArrayList<ParseObject> array_posts = new ArrayList<>();
    ArrayList<ParseObject> array_media = new ArrayList<>();
    ArrayList<ParseObject> array_comment = new ArrayList<>();

    PartyList_Adapter adapter_party;
    FeedList_Adapter adapter_feed;
    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("User Profile");
        user = (ParseUser) getIntent().getParcelableExtra("USER");
        TextView txt_name = (TextView)findViewById(R.id.txt_name);
        TextView txt_email = (TextView)findViewById(R.id.txt_email);
        TextView txt_phone = (TextView)findViewById(R.id.txt_phone);
        txt_name.setText(user.getString("fullname"));
        txt_email.setText(user.getString("username"));
        txt_phone.setText(user.getString("code") + " " + user.getString("number"));
        CircleImageView img_user = (CircleImageView)findViewById(R.id.img_user);
        Glide.with(this).load(user.getParseFile("photo").getUrl()).apply(new RequestOptions()
                .placeholder(R.drawable.ic_user).centerCrop().dontAnimate()).into(img_user);

        Button btn_block = (Button)findViewById(R.id.btn_block);
        btn_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BlockDialog blockDialog = new BlockDialog(UserProfileActivity.this);
                blockDialog.onClickListner = new BlockDialog.OnClickListner() {
                    @Override
                    public void OnClickOk() {
                        blockDialog.dismiss();
                    }

                    @Override
                    public void OnClickCancel() {
                        blockDialog.dismiss();
                    }
                };
                blockDialog.show();
            }
        });

        final ListView list_party = (ListView)findViewById(R.id.list_party);
        final ListView list_feed = (ListView)findViewById(R.id.list_feed);
        final Button btn_party = (Button)findViewById(R.id.btn_party);
        final Button btn_feed = (Button)findViewById(R.id.btn_feed);
        btn_party.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPartySelected = true;
                btn_party.setTextColor(Color.BLACK);
                btn_feed.setTextColor(Color.GRAY);
                list_party.setVisibility(View.VISIBLE);
                list_feed.setVisibility(View.GONE);
                parseEventFetch();
            }
        });
        btn_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPartySelected = false;
                btn_party.setTextColor(Color.GRAY);
                btn_feed.setTextColor(Color.BLACK);
                list_party.setVisibility(View.GONE);
                list_feed.setVisibility(View.VISIBLE);
                parsePostFetch();
            }
        });
        list_party.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(UserProfileActivity.this, PartyDetailActivity.class);
                intent.putExtra("OBJECT", array_events.get(i));
                startActivity(intent);
            }
        });
        adapter_party = new PartyList_Adapter(UserProfileActivity.this, array_events, null);
        list_party.setAdapter(adapter_party);

        adapter_feed = new FeedList_Adapter(UserProfileActivity.this, array_posts, array_media, array_comment, null);
        list_feed.setAdapter(adapter_feed);
        parseEventFetch();
    }
    private void parseEventFetch() {
        ProgressDialog.showDlg(UserProfileActivity.this);
        Date currentDate = Calendar.getInstance().getTime();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.include("user");
        query.orderByAscending("from");
        query.whereEqualTo("user", user);
        query.whereGreaterThan("to", currentDate);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    array_events.clear();
                    array_events.addAll(objects);
                    adapter_party.notifyDataSetChanged();
                } else {
                    Toast.makeText(UserProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                ProgressDialog.hideDlg();
            }
        });
    }

    private void parsePostFetch() {
        ProgressDialog.showDlg(UserProfileActivity.this);
        ParseQuery<ParseObject> query0 = ParseQuery.getQuery("Post");
        query0.include("user");
        query0.whereEqualTo("user", user);
        query0.orderByDescending("createdAt");
        query0.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> posts, ParseException e) {
                if (e == null) {
                    array_posts.clear();
                    array_posts.addAll(posts);
                    if (posts.size() == 0) {
                        adapter_feed.notifyDataSetChanged();
                        ProgressDialog.hideDlg();
                        return;
                    }
                    ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Media");
                    query1.include("post");
                    query1.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> medias, ParseException e) {
                            if (e == null) {
                                array_media.clear();
                                array_media.addAll(medias);
                                ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Comment");
                                query2.include("user");
                                query2.orderByDescending("createdAt");
                                query2.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> comments, ParseException e) {
                                        ProgressDialog.hideDlg();
                                        if (e == null) {
                                            array_comment.clear();
                                            array_comment.addAll(comments);
                                            adapter_feed.notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(UserProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                        ProgressDialog.hideDlg();
                                    }
                                });
                            } else {
                                Toast.makeText(UserProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                ProgressDialog.hideDlg();
                            }
                        }
                    });
                } else {
                    Toast.makeText(UserProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    ProgressDialog.hideDlg();
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Write your logic here
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
