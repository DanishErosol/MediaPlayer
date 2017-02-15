package com.example.ero_sol_pc_04.mediaplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static String LOG_TAG= "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISION = 200;
    private static  String mFileName = null;
    private
    TextView t1;
    ImageButton  bt3,bt1, btnPause;
    SeekBar sb;
    MediaRecorder mRecorder = null;
    MediaPlayer mPlayer = null;
    boolean mStartRecording = true;
    boolean mStartPlaying = true;
    private double startTime = 0;
    private double finalTime =0;
    private static int oneTimeOnly = 0;

    private Handler myHandler = new Handler() ;;



    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt1 = (ImageButton) findViewById(R.id.play);
        bt1.setVisibility(View.VISIBLE);
        bt3 = (ImageButton) findViewById(R.id.recordButton);
        sb =(SeekBar) findViewById(R.id.seekBar);
        t1 = (TextView) findViewById(R.id.textView2) ;
        btnPause = (ImageButton) findViewById(R.id.pause);
        btnPause.setVisibility(View.GONE);


        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int seekBarProgress = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarProgress = progress;

            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                t1.setText("Progress: " + seekBarProgress + " / " + seekBar.getMax());
                Toast.makeText(getApplicationContext(), "SeekBar Touch Stop ", Toast.LENGTH_SHORT).show();
            }

        });




        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";



        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISION);


        t1.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                startTime))));


        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {

                    bt1.setVisibility(View.GONE);
                    btnPause.setVisibility(View.VISIBLE);

                } else {
                   bt1.setVisibility(View.VISIBLE);
                    btnPause.setVisibility(View.GONE);
                }
                mStartPlaying = !mStartPlaying;

            }
        });



        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlaying();
                bt1.setVisibility(View.VISIBLE);
                btnPause.setVisibility(View.GONE);
            }
        });



        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(mStartRecording);

                mStartRecording = !mStartRecording;
            }
        });

    }





    private void startRecording()
    {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try
        {
            mRecorder.prepare();

        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e(LOG_TAG,"Prepare Failed");

        }
        mRecorder.start();
    }



    @Override
    public void onStop() {
        super.onStop();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }
    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }
    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }
    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();

        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
            finalTime = mPlayer.getDuration();
            startTime = mPlayer.getCurrentPosition();
            if (oneTimeOnly == 0) {
                sb.setMax((int) finalTime);
                (oneTimeOnly >1);
            }
            t1.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            sb.setProgress((int)startTime);
            myHandler.postDelayed(UpdateSongTime, 1000);
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }


    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mPlayer.getCurrentPosition();
            t1.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            sb.setProgress((int)startTime);
            myHandler.postDelayed(this, 1000);
        }
    };
}
