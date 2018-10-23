package com.clevery.android.aworkadmin;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.clevery.android.aworkadmin.Utils.Utils;
import com.github.chrisbanes.photoview.PhotoView;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MediaPlayerActivity extends AppCompatActivity {
    ArrayList<ParseObject> array_media = new ArrayList<>();
    int index = 0;
    ParseObject media = null;
    int duration = 0;
    Button btn_play;
    TextView txt_progress;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        array_media = (ArrayList<ParseObject>) getIntent().getSerializableExtra("MEDIA_ARRAY");
        index = getIntent().getIntExtra("INDEX", 0);
        media = array_media.get(index);
        btn_play = (Button)findViewById(R.id.btn_play);
        txt_progress = (TextView)findViewById(R.id.txt_progress);
        Button btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        videoView = (VideoView) findViewById(R.id.iv_video);
        RelativeLayout fl_video = (RelativeLayout)findViewById(R.id.fl_video);
        PhotoView photoView = (PhotoView)findViewById(R.id.iv_photo);
        final ImageView img_loader = (ImageView)findViewById(R.id.img_loader);
        if (media.getBoolean("isVideo")) {
            fl_video.setVisibility(View.VISIBLE);
            photoView.setVisibility(View.GONE);
            videoView.setVideoURI(Uri.parse(media.getParseFile("file").getUrl()));
            videoView.requestFocus();
            videoView.start();
            img_loader.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .asGif()
                    .load(R.drawable.loader).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(img_loader);
            btn_play.setEnabled(false);
            btn_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (videoView.isPlaying()) {
                        btn_play.setBackgroundResource(android.R.drawable.ic_media_play);
                        videoView.pause();
                    } else {
                        btn_play.setBackgroundResource(android.R.drawable.ic_media_pause);
                        videoView.start();
                    }
                }
            });
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    img_loader.setVisibility(View.GONE);
                    Toast.makeText(MediaPlayerActivity.this, "Video is getting ready. Please wait ...", Toast.LENGTH_LONG).show();
                    btn_play.setBackgroundResource(android.R.drawable.ic_media_pause);
                    btn_play.setEnabled(true);
                    duration = videoView.getDuration();
                    start_timer();
                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    btn_play.setBackgroundResource(android.R.drawable.ic_media_play);
                }
            });

        } else {
            img_loader.setVisibility(View.GONE);
            fl_video.setVisibility(View.GONE);
            photoView.setVisibility(View.VISIBLE);
            photoView.setZoomable(true);
            Glide.with(this).load(media.getParseFile("file").getUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.default_pic).fitCenter().dontAnimate()).into(photoView);
        }
    }
    private Timer timer = new Timer();
    private Handler handler = new Handler();

    private void start_timer(){
        timer = new Timer();
        handler = new Handler();

        timer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (videoView != null) {
                            if (videoView.isPlaying()) {
                                video_progress();
                            }
                        }
                    }
                });
            }
        }, 0, 500);
    }
    private void kill_timer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }
    void video_progress() {
        String cur = Utils.getFormattedTimeStr(videoView.getCurrentPosition());
        String total = Utils.getFormattedTimeStr(duration);
        txt_progress.setText(cur + "/" + total);
    }

    @Override
    protected void onDestroy() {
        kill_timer();
        videoView = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
