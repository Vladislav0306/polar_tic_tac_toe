package org.o7planning.penguins_vs_polar_bears;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    private SoundPool soundPool;
    private int soundID;
    boolean loaded = false;
    int num = 0;
    private int currentApiVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startService(new Intent(MainActivity.this, SoundServiceMenu.class));
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_main);
        View view0 = findViewById(R.id.onePlayerBtn);
        View view1 = findViewById(R.id.twoPlayerBtn);
        View view2 = findViewById(R.id.quitBtn);
        view0.setOnTouchListener(this);
        view1.setOnTouchListener(this);
        view2.setOnTouchListener(this);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
                Log.e("Test", "sampleId=" + sampleId + " status=" + status);
            }
        });
        soundID = soundPool.load(this, R.raw.menu_tab, 1);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            float actualVolume = (float) audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC);
            float maxVolume = (float) audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float volume = actualVolume / maxVolume;
            if (loaded) {
                num = num + 1;
                soundPool.play(soundID, volume, volume, 1, 0, 1f);
            }
        }
        return false;
    }

    public void onClickSingle(View view) {
        stopService(new Intent(MainActivity.this, SoundServiceMenu.class));
        Intent intent = new Intent(this, SinglePlayer.class);
        startActivity(intent);
    }

    public void onClickMulti(View view) {
        stopService(new Intent(MainActivity.this, SoundServiceMenu.class));
        Intent intent = new Intent(this, MultiPlayer.class);
        startActivity(intent);
    }

    public void onClickQuit(View view) {
        finishAffinity();
        System.exit(0);
    }
}