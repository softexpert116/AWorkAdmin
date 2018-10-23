package com.clevery.android.aworkadmin;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.clevery.android.aworkadmin.Utils.BlockDialog;
import com.clevery.android.aworkadmin.Utils.ProgressDialog;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class PartyDetailActivity extends AppCompatActivity {
    List<ParseUser> users = new ArrayList<>();
    LinearLayout layout;
    ParseObject object;
    TextView txt_invites, txt_nouser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_detail);
        object = (ParseObject) getIntent().getParcelableExtra("OBJECT");

        Button btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button btn_block = (Button)findViewById(R.id.btn_block);
        btn_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BlockDialog blockDialog = new BlockDialog(PartyDetailActivity.this);
                blockDialog.onClickListner = new BlockDialog.OnClickListner() {
                    @Override
                    public void OnClickOk() {
                        MainActivity.parseBlockEvent(PartyDetailActivity.this, object, blockDialog.abuseType, null, PartyDetailActivity.this);
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
        layout = (LinearLayout) findViewById(R.id.ly_invites);
        TextView txt_name = (TextView)findViewById(R.id.txt_name);
        TextView txt_description = (TextView)findViewById(R.id.txt_description);
        final TextView txt_place = (TextView)findViewById(R.id.txt_place);
        TextView txt_type = (TextView)findViewById(R.id.txt_type);
        final TextView txt_time = (TextView)findViewById(R.id.txt_time);
        txt_invites = (TextView)findViewById(R.id.txt_invites);
        txt_nouser = (TextView)findViewById(R.id.txt_nouser);
        TextView txt_publisher = (TextView)findViewById(R.id.txt_publisher);
        ImageView img_photo = (ImageView)findViewById(R.id.img_photo);
        CircleImageView img_publisher = (CircleImageView)findViewById(R.id.img_publisher);
        txt_name.setText(object.getString("name"));
        txt_description.setText(object.getString("description"));
        txt_place.setText(object.getString("place"));
        txt_type.setText(object.getString("type"));
        if (object.getString("type").equals("Free")) {
            txt_type.setBackgroundColor(getColor(R.color.colorFree));
        } else {
            txt_type.setBackgroundColor(getColor(R.color.colorPaid));
        }
        String from = new SimpleDateFormat(App.DATE_FORMAT).format(object.getDate("from"));
        String to = new SimpleDateFormat(App.DATE_FORMAT).format(object.getDate("to"));
        txt_time.setText(from + " ~ " + to);
        ParseObject user = object.getParseObject("user");
        txt_publisher.setText(user.getString("fullname"));
        Glide.with(this).load(user.getParseFile("photo").getUrl()).apply(new RequestOptions()
                .placeholder(R.drawable.ic_user).centerCrop().dontAnimate()).into(img_publisher);
        Glide.with(this).load(object.getParseFile("photo").getUrl()).apply(new RequestOptions()
                .placeholder(R.drawable.default_pic).centerCrop().dontAnimate()).into(img_photo);

    }
    private void setInvites() {
        txt_invites.setText("Invites: " + String.valueOf(object.getInt("attend")) + "/"+ String.valueOf(object.getInt("invites")));
        txt_invites.setTextColor(Color.WHITE);
        if (object.getInt("attend") == object.getInt("invites")) {
            txt_invites.setTextColor(getColor(R.color.colorAccent));
        }
    }
    private void addInviteUsers() {
        if (users.size() == 0) {
            txt_nouser.setVisibility(View.VISIBLE);
        } else {
            txt_nouser.setVisibility(View.GONE);
        }
        layout.removeAllViews();
        for (final ParseUser user:users) {
            CircleImageView imageView = new CircleImageView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(150, 150);
            lp.setMargins(20, 0, 0, 0);
            imageView.setLayoutParams(lp);
            imageView.setPadding(2, 2, 2, 2);
            Glide.with(this).load(user.getParseFile("photo").getUrl()).apply(new RequestOptions()
                    .placeholder(R.drawable.ic_user).centerCrop().dontAnimate()).into(imageView);
//            Glide.with(EventDetailActivity.this).load(user.getParseFile("photo").getUrl()).placeholder(R.drawable.profile).dontAnimate().into(imageView);
            layout.addView(imageView);
        }
    }
    private void parseJoinFetch() {
        ProgressDialog.showDlg(PartyDetailActivity.this);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Join");
        query.include("user");
        query.whereEqualTo("event", object);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    users.clear();
                    for (ParseObject object:objects) {
                        ParseUser user = (ParseUser) object.getParseObject("user");
                        users.add(user);
                    }
                    addInviteUsers();
                } else {
                    Toast.makeText(PartyDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                ProgressDialog.hideDlg();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        parseJoinFetch();
        setInvites();
    }
}
