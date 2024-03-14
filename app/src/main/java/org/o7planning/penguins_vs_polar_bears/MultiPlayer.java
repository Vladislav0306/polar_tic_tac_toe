package org.o7planning.penguins_vs_polar_bears;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MultiPlayer extends AppCompatActivity implements View.OnTouchListener {
    private SoundPool soundPool;
    private int soundID;
    boolean loaded = false;
    int num = 0;
    ImageButton[][] buttonBoard = new ImageButton[3][3];
    int[][] gameBoard = new int[3][3];
    int oppID;
    int whoseTurn = 1;
    int moves = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startService(new Intent(MultiPlayer.this, SoundServicePlay.class));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player);
        final RadioGroup rGroup = (RadioGroup) findViewById(R.id.radioGroup);
        final RadioButton penguinRadio = (RadioButton) rGroup.findViewById(R.id.penguinsChoice);
        final RadioButton polarBearRadio = (RadioButton) rGroup.findViewById(R.id.polarBearsChoice);
        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkId) {
                ;
                if (checkId == R.id.polarBearsChoice) {
                    penguinRadio.setEnabled(false);
                    oppID = R.drawable.penguin;
                    setupGame(R.drawable.polar_bear);
                } else {
                    polarBearRadio.setEnabled(false);
                    oppID = R.drawable.polar_bear;
                    setupGame(R.drawable.penguin);
                }
            }
        });

        View view0 = findViewById(R.id.button0);
        View view1 = findViewById(R.id.button1);
        View view2 = findViewById(R.id.button2);
        View view3 = findViewById(R.id.button3);
        View view4 = findViewById(R.id.button4);
        View view5 = findViewById(R.id.button5);
        View view6 = findViewById(R.id.button6);
        View view7 = findViewById(R.id.button7);
        View view8 = findViewById(R.id.button8);
        View view9 = findViewById(R.id.penguinsChoice);
        View view10 = findViewById(R.id.polarBearsChoice);
        View view11 = findViewById(R.id.restartBtn);
        View view12 = findViewById(R.id.backBtn);
        view0.setOnTouchListener(this);
        view1.setOnTouchListener(this);
        view2.setOnTouchListener(this);
        view3.setOnTouchListener(this);
        view4.setOnTouchListener(this);
        view5.setOnTouchListener(this);
        view6.setOnTouchListener(this);
        view7.setOnTouchListener(this);
        view8.setOnTouchListener(this);
        view9.setOnTouchListener(this);
        view10.setOnTouchListener(this);
        view11.setOnTouchListener(this);
        view12.setOnTouchListener(this);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
                Log.e("Test", "sampleId=" + sampleId + " status=" + status);
            }
        });
        soundID = soundPool.load(this, R.raw.play_tab, 1);
    }

    public void onClickRestart(View view) {
        stopService(new Intent(MultiPlayer.this, SoundServicePlay.class));
        Intent intent = new Intent(this, MultiPlayer.class);
        startActivity(intent);
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

    public void onClickBack(View view) {
        stopService(new Intent(MultiPlayer.this, SoundServicePlay.class));
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void setupGame(int iconID) {
        buttonBoard[0][0] = (ImageButton) findViewById(R.id.button0);
        buttonBoard[0][1] = (ImageButton) findViewById(R.id.button1);
        buttonBoard[0][2] = (ImageButton) findViewById(R.id.button2);
        buttonBoard[1][0] = (ImageButton) findViewById(R.id.button3);
        buttonBoard[1][1] = (ImageButton) findViewById(R.id.button4);
        buttonBoard[1][2] = (ImageButton) findViewById(R.id.button5);
        buttonBoard[2][0] = (ImageButton) findViewById(R.id.button6);
        buttonBoard[2][1] = (ImageButton) findViewById(R.id.button7);
        buttonBoard[2][2] = (ImageButton) findViewById(R.id.button8);
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j)
                gameBoard[i][j] = 0;
        }
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                buttonBoard[i][j].setOnClickListener(new GameClickListener(i, j, iconID));
            }
        }
    }

    class GameClickListener implements View.OnClickListener {
        int x, y, icon;

        public GameClickListener(int x, int y, int ic) {
            this.x = x;
            this.y = y;
            this.icon = ic;
        }

        public void onClick(View view) {
            if (whoseTurn == 1) {
                buttonBoard[x][y].setImageResource(icon);
                whoseTurn = 2;
                gameBoard[x][y] = 1;
            } else {
                buttonBoard[x][y].setImageResource(oppID);
                whoseTurn = 1;
                gameBoard[x][y] = 2;
            }
            buttonBoard[x][y].setEnabled(false);
            moves++;
            if (!checkWinner()) {
                if (moves == 9) {
                    TextView v = (TextView) findViewById(R.id.title);
                    v.setText("No Winner! Click Restart if you want to play a new game!");
                    v.setTextColor(Color.rgb(255, 252, 64));
                }
            } else {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (gameBoard[i][j] == 0) {
                            buttonBoard[i][j].setEnabled(false);
                        }
                    }
                }
            }
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (checkWinner()) {
                        if (moves != 9) {
                            buttonBoard[i][j].setEnabled(false);
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private boolean checkWinner() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (gameBoard[i][j] != 0) {
                    if ((gameBoard[(i + 1) % 3][j] == gameBoard[i][j]) &&
                            (gameBoard[(i + 2) % 3][j] == gameBoard[i][j])) {
                        if (gameBoard[(i + 1) % 3][j] == 1) {
                            TextView v = (TextView) findViewById(R.id.title);
                            v.setText("Player 1 is the winner! Click Restart if you want to play a new game!");
                            v.setTextColor(Color.rgb(55, 255, 0));
                        } else {
                            TextView v = (TextView) findViewById(R.id.title);
                            v.setText("Player 2 is the winner! Click Restart if you want to play a new game!");
                            v.setTextColor(Color.rgb(255, 0, 0));
                        }
                        if (gameBoard[(i + 2) % 3][j] == 1) {
                            TextView v = (TextView) findViewById(R.id.title);
                            v.setText("Player 1 is the winner! Click Restart if you want to play a new game!");
                            v.setTextColor(Color.rgb(55, 255, 0));
                            buttonBoard[i][j].setBackgroundColor(Color.rgb(55, 255, 0));
                            buttonBoard[i + 1][j].setBackgroundColor(Color.rgb(55, 255, 0));
                            buttonBoard[i + 2][j].setBackgroundColor(Color.rgb(55, 255, 0));
                        } else {
                            TextView v = (TextView) findViewById(R.id.title);
                            v.setText("Player 2 is the winner! Click Restart if you want to play a new game!");
                            v.setTextColor(Color.rgb(255, 0, 0));
                            buttonBoard[i][j].setBackgroundColor(Color.rgb(55, 255, 0));
                            buttonBoard[i + 1][j].setBackgroundColor(Color.rgb(55, 255, 0));
                            buttonBoard[i + 2][j].setBackgroundColor(Color.rgb(55, 255, 0));
                        }
                        return true;
                    }
                    if ((gameBoard[i][(j + 1) % 3] == gameBoard[i][j]) &&
                            (gameBoard[i][(j + 2) % 3] == gameBoard[i][j])) {
                        if (gameBoard[i][(j + 1) % 3] == 1) {
                            TextView v = (TextView) findViewById(R.id.title);
                            v.setText("Player 1 is the winner! Click Restart if you want to play a new game!");
                            v.setTextColor(Color.rgb(55, 255, 0));
                        } else {
                            TextView v = (TextView) findViewById(R.id.title);
                            v.setText("Player 2 is the winner! Click Restart if you want to play a new game!");
                            v.setTextColor(Color.rgb(255, 0, 0));
                        }
                        if (gameBoard[i][(j + 2) % 3] == 1) {
                            TextView v = (TextView) findViewById(R.id.title);
                            v.setText("Player 1 is the winner! Click Restart if you want to play a new game!");
                            v.setTextColor(Color.rgb(55, 255, 0));
                            buttonBoard[i][j].setBackgroundColor(Color.rgb(55, 255, 0));
                            buttonBoard[i][j + 1].setBackgroundColor(Color.rgb(55, 255, 0));
                            buttonBoard[i][j + 2].setBackgroundColor(Color.rgb(55, 255, 0));
                        } else {
                            TextView v = (TextView) findViewById(R.id.title);
                            v.setText("Player 2 is the winner! Click Restart if you want to play a new game!");
                            v.setTextColor(Color.rgb(255, 0, 0));
                            buttonBoard[i][j].setBackgroundColor(Color.rgb(55, 255, 0));
                            buttonBoard[i][j + 1].setBackgroundColor(Color.rgb(55, 255, 0));
                            buttonBoard[i][j + 2].setBackgroundColor(Color.rgb(55, 255, 0));
                        }
                        return true;
                    } else {
                        if (i == 0) {
                            if (j == 0) {
                                if (((gameBoard[1][1] == gameBoard[i][j]) &&
                                        (gameBoard[2][2] == gameBoard[i][j]))) {

                                    if (gameBoard[i][j] == 1) {
                                        TextView v = (TextView) findViewById(R.id.title);
                                        v.setText("Player 1 is the winner! Click Restart if you want to play a new game!");
                                        v.setTextColor(Color.rgb(55, 255, 0));
                                        buttonBoard[0][0].setBackgroundColor(Color.rgb(55, 255, 0));
                                        buttonBoard[1][1].setBackgroundColor(Color.rgb(55, 255, 0));
                                        buttonBoard[2][2].setBackgroundColor(Color.rgb(55, 255, 0));
                                    } else {
                                        TextView v = (TextView) findViewById(R.id.title);
                                        v.setText("Player 2 is the winner! Click Restart if you want to play a new game!");
                                        v.setTextColor(Color.rgb(255, 0, 0));
                                        buttonBoard[0][0].setBackgroundColor(Color.rgb(55, 255, 0));
                                        buttonBoard[1][1].setBackgroundColor(Color.rgb(55, 255, 0));
                                        buttonBoard[2][2].setBackgroundColor(Color.rgb(55, 255, 0));
                                    }
                                    return true;
                                }
                            }
                            if (j == 2) {
                                if (((gameBoard[1][1] == gameBoard[i][j]) &&
                                        (gameBoard[2][0] == gameBoard[i][j]))) {

                                    if (gameBoard[i][j] == 1) {
                                        TextView v = (TextView) findViewById(R.id.title);
                                        v.setText("Player 1 is the winner! Click Restart if you want to play a new game!");
                                        v.setTextColor(Color.rgb(55, 255, 0));
                                        buttonBoard[0][2].setBackgroundColor(Color.rgb(55, 255, 0));
                                        buttonBoard[1][1].setBackgroundColor(Color.rgb(55, 255, 0));
                                        buttonBoard[2][0].setBackgroundColor(Color.rgb(55, 255, 0));
                                    } else {
                                        TextView v = (TextView) findViewById(R.id.title);
                                        v.setText("Player 2 is the winner! Click Restart if you want to play a new game!");
                                        v.setTextColor(Color.rgb(255, 0, 0));
                                        buttonBoard[0][2].setBackgroundColor(Color.rgb(55, 255, 0));
                                        buttonBoard[1][1].setBackgroundColor(Color.rgb(55, 255, 0));
                                        buttonBoard[2][0].setBackgroundColor(Color.rgb(55, 255, 0));
                                    }
                                    return true;
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}