package com.example.android.photogallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.photogallery.Models.Video;

import java.util.ArrayList;


public class PlayVideoActivity extends AppCompatActivity {

    VideoView videoView;
    ImageView prev, next, pause;
    SeekBar seekBar;
    int video_index = 0;
    double current_pos, total_duration;
    TextView current, total;
    LinearLayout showProgress;
    Handler mHandler,handler;
    boolean isVisible = true;
    RelativeLayout relativeLayout;

    private Video currentVideo;
    private ArrayList<Video> videoList;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        Intent callingIntent = getIntent();
        Bundle bundle = callingIntent.getExtras();
        videoList = bundle.getParcelableArrayList("listVideo");
        Log.e("PLAY_VIDEO:", "" + videoList.size());
        video_index = bundle.getInt("position");
        currentVideo = videoList.get(video_index);

        setVideo();
    }

    public void setVideo() {

        videoView = (VideoView) findViewById(R.id.videoview);
        prev = (ImageView) findViewById(R.id.prev);
        next = (ImageView) findViewById(R.id.next);
        pause = (ImageView) findViewById(R.id.pause);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        current = (TextView) findViewById(R.id.current);
        total = (TextView) findViewById(R.id.total);
        showProgress = (LinearLayout) findViewById(R.id.showProgress);
        relativeLayout = (RelativeLayout) findViewById(R.id.relative);

        mHandler = new Handler(Looper.myLooper());
        handler = new Handler(Looper.myLooper());

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                video_index++;
                if (video_index < (videoList.size())) {
                    playVideo(video_index);
                } else {
                    video_index = 0;
                    playVideo(video_index);
                }
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                setVideoProgress();
            }
        });

        playVideo(video_index);
        prevVideo();
        nextVideo();
        setPause();
        hideLayout();
    }

    // play video file
    public void playVideo(int pos) {
        try  {
            currentVideo = videoList.get(pos);
            videoView.setVideoURI(currentVideo.get_videoUri());
            videoView.start();
            pause.setImageResource(R.drawable.baseline_pause_circle_outline_black_18dp);
            video_index = pos;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // display video progress
    public void setVideoProgress() {
        //get the video duration
        current_pos = videoView.getCurrentPosition();
        total_duration = videoView.getDuration();

        //display video duration
        total.setText(timeConversion((long) total_duration));
        current.setText(timeConversion((long) current_pos));
        seekBar.setMax((int) total_duration);
        final Handler handler = new Handler(Looper.myLooper());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    current_pos = videoView.getCurrentPosition();
                    current.setText(timeConversion((long) current_pos));
                    seekBar.setProgress((int) current_pos);
                    handler.postDelayed(this, 1000);
                } catch (IllegalStateException ed){
                    ed.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 1000);

        //seekbar change listner
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                current_pos = seekBar.getProgress();
                videoView.seekTo((int) current_pos);
            }
        });
    }

    //play previous video
    public void prevVideo() {
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (video_index > 0) {
                    video_index--;
                    playVideo(video_index);
                } else {
                    video_index = videoList.size() - 1;
                    playVideo(video_index);
                }
            }
        });
    }

    //play next video
    public void nextVideo() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (video_index < (videoList.size() - 1)) {
                    video_index++;
                    playVideo(video_index);
                } else {
                    video_index = 0;
                    playVideo(video_index);
                }
            }
        });
    }

    //pause video
    public void setPause() {
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    pause.setImageResource(R.drawable.baseline_play_arrow_black_18dp);
                } else {
                    videoView.start();
                    pause.setImageResource(R.drawable.baseline_pause_circle_outline_black_18dp);
                }
            }
        });
    }

    //time conversion
    public String timeConversion(long value) {
        String songTime;
        int dur = (int) value;
        int hrs = (dur / 3600000);
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000 / 1000;

        if (hrs > 0) {
            songTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            songTime = String.format("%02d:%02d", mns, scs);
        }
        return songTime;
    }

    // hide progress when the video is playing
    public void hideLayout() {

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                showProgress.setVisibility(View.GONE);
                isVisible = false;
            }
        };
        handler.postDelayed(runnable, 5000);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacks(runnable);
                if (isVisible) {
                    showProgress.setVisibility(View.GONE);
                    isVisible = false;
                } else {
                    showProgress.setVisibility(View.VISIBLE);
                    mHandler.postDelayed(runnable, 5000);
                    isVisible = true;
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}