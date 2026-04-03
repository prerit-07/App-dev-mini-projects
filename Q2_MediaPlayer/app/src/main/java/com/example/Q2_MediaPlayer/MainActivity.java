package com.example.Q2_MediaPlayer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    MediaPlayer audioPlayer; // Sirf ek main variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Simple UI load


        // 1. VIDEO LOGIC (URL se chalana)

        VideoView videoView = findViewById(R.id.myVideoView);
        EditText videoUrlInput = findViewById(R.id.videoUrlInput);

        findViewById(R.id.btnOpenUrl).setOnClickListener(v -> {
            String url = videoUrlInput.getText().toString();
            videoView.setVideoURI(Uri.parse(url)); // URL set kiya
            videoView.setMediaController(new MediaController(this)); // Play/Pause screen par dikhaya
            videoView.start(); // Video chalu
            Toast.makeText(this, "Playing Video", Toast.LENGTH_SHORT).show();
        });


        // 2. AUDIO LOGIC (Disk se chalana)

        // A. Load File
        findViewById(R.id.btnOpenFile).setOnClickListener(v -> {
            if (audioPlayer != null) audioPlayer.release(); // Purana gaana hatao
            audioPlayer = MediaPlayer.create(this, R.raw.sample_audio); // Naya load karo
            Toast.makeText(this, "Audio Loaded", Toast.LENGTH_SHORT).show();
        });

        // B. Play
        findViewById(R.id.btnPlayAudio).setOnClickListener(v -> {
            if (audioPlayer != null) audioPlayer.start();
        });

        // C. Pause
        findViewById(R.id.btnPauseAudio).setOnClickListener(v -> {
            if (audioPlayer != null) audioPlayer.pause();
        });

        // D. Stop (Smart Trick: Pause karo aur 0 sec par le jao)
        findViewById(R.id.btnStopAudio).setOnClickListener(v -> {
            if (audioPlayer != null) {
                audioPlayer.pause();
                audioPlayer.seekTo(0);
            }
        });

        // E. Restart
        findViewById(R.id.btnRestartAudio).setOnClickListener(v -> {
            if (audioPlayer != null) {
                audioPlayer.seekTo(0); // 0 sec par le jao
                audioPlayer.start();   // Play kar do
            }
        });
    }

    // App band hone par memory free karna
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioPlayer != null) audioPlayer.release();
    }
}