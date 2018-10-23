package com.clevery.android.aworkadmin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.clevery.android.aworkadmin.App;
import com.clevery.android.aworkadmin.Fragments.FeedFragment;
import com.clevery.android.aworkadmin.MainActivity;
import com.clevery.android.aworkadmin.MediaPlayerActivity;
import com.clevery.android.aworkadmin.R;
import com.clevery.android.aworkadmin.Utils.BlockDialog;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedList_Adapter extends BaseAdapter {
    ArrayList<ParseObject> arrayList, array_media, array_comment;
    Context context;
    FeedFragment fragment;

    FeedList_Adapter() {
        context = null;
        arrayList = null;
        array_media = null;
        array_comment = null;
        fragment = null;
    }
    public FeedList_Adapter(Context _context, ArrayList<ParseObject> _arrayList, ArrayList<ParseObject> _array_media, ArrayList<ParseObject> _array_comment, FeedFragment _fragment) {
        context = _context;
        arrayList = _arrayList;
        array_media = _array_media;
        array_comment = _array_comment;
        fragment = _fragment;
    }
    @Override
    public int getCount() {
        if (arrayList == null)
            return 0;
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            int resource = R.layout.cell_feed;
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(resource, null);
        }
        final ParseObject post = arrayList.get(i);
        final ParseUser user = post.getParseUser("user");

        Button btn_block = (Button)view.findViewById(R.id.btn_block);
        btn_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BlockDialog blockDialog = new BlockDialog(context);
                blockDialog.onClickListner = new BlockDialog.OnClickListner() {
                    @Override
                    public void OnClickOk() {
                        MainActivity.parseBlockPost(context, post, blockDialog.abuseType, fragment);
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
        if (fragment == null) {
            btn_block.setVisibility(View.GONE);
        } else {
            btn_block.setVisibility(View.VISIBLE);
        }
        CircleImageView img_publisher = (CircleImageView)view.findViewById(R.id.img_publisher);
        TextView txt_name = (TextView)view.findViewById(R.id.txt_name);
        TextView txt_likes = (TextView)view.findViewById(R.id.txt_likes);
        TextView txt_date = (TextView)view.findViewById(R.id.txt_date);
        TextView txt_description = (TextView)view.findViewById(R.id.txt_description);
        Glide.with(context).load(user.getParseFile("photo").getUrl()).apply(new RequestOptions()
                .placeholder(R.drawable.ic_user).centerCrop().dontAnimate()).into(img_publisher);
        txt_name.setText(user.getString("fullname"));
        String dateStr = new SimpleDateFormat(App.DATE_FORMAT1).format(post.getCreatedAt());
        txt_date.setText(dateStr);
        txt_likes.setText("Likes: " + post.getNumber("likes").toString());
        txt_description.setText(post.getString("description"));

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.ly_media);
        layout.removeAllViews();
        layout.setPadding(0, 0, 0, 0);
        final ArrayList<ParseObject> current_medias = new ArrayList<>();
        for (ParseObject _media: array_media) {
            final ParseObject media = _media;
            if (media.getParseObject("post").getObjectId().equals(post.getObjectId())) {
                current_medias.add(media);
                RelativeLayout layout1 = new RelativeLayout(context);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(250, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(20, 20, 20, 20);

                layout1.setLayoutParams(layoutParams);

                final String mediaUrl = media.getParseFile("file").getUrl();

                if (media.getBoolean("isVideo")) {
                    ImageView videoView = new ImageView(context);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                    videoView.setLayoutParams(lp);
                    Glide.with(context)
                            .asBitmap()
                            .load(mediaUrl).thumbnail(0.1f).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop())
                            .into(videoView);
                    layout1.addView(videoView);

                    videoView.setAlpha(0.5f);
                    ImageView videoImage = new ImageView(context);
                    RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(100, 100);
                    lp1.addRule(RelativeLayout.CENTER_IN_PARENT);
                    videoImage.setLayoutParams(lp1);
                    videoImage.setImageResource(R.drawable.play_white);
                    layout1.addView(videoImage);

                    videoView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            goMediaPlayer(media);
                        }
                    });
                } else {
                    ImageView imageView = new ImageView(context);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                    imageView.setLayoutParams(lp);
                    Glide.with(context).load(mediaUrl)
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.default_pic).centerCrop().dontAnimate()).into(imageView);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setAdjustViewBounds(false);
                    imageView.setAlpha(1.0f);
                    layout1.addView(imageView);

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            goMediaPlayer(media);
                        }
                    });
                }
                layout1.setBackgroundColor(Color.BLACK);
                layout.addView(layout1);
            }
        }
        HorizontalScrollView scrollView = (HorizontalScrollView)view.findViewById(R.id.horizontal_scroll);
        if (current_medias.size() == 0) {
            scrollView.setVisibility(View.GONE);
        } else {
            scrollView.setVisibility(View.VISIBLE);
        }
        ArrayList<ParseObject> current_comments = new ArrayList<>();
        for (ParseObject comment:array_comment) {
            if (comment.getParseObject("post").getObjectId().equals(post.getObjectId())) {
                current_comments.add(comment);
            }
        }
        ListView listView = (ListView)view.findViewById(R.id.list_comment);
        CommentList_Adapter commentList_adapter = new CommentList_Adapter(context, current_comments);
        listView.setAdapter(commentList_adapter);
        App.setListViewHeightBasedOnChildren(listView);

        return view;
    }
    private void goMediaPlayer(ParseObject media) {
        Intent intent = new Intent(context, MediaPlayerActivity.class);
        intent.putExtra("MEDIA_ARRAY", array_media);
        intent.putExtra("INDEX", array_media.indexOf(media));
        context.startActivity(intent);
    }
}
