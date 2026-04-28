package com.example.chessalarm.alarm;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

public class RingtoneSoundPlayer implements AlarmController.SoundPlayer {

    private static final String TAG = "RingtoneSoundPlayer";

    private final Context appContext;
    private MediaPlayer player;

    public RingtoneSoundPlayer(Context appContext) {
        this.appContext = appContext;
    }

    @Override
    public void start() {
        if (player != null) return;
        Uri uri = RingtoneManager.getActualDefaultRingtoneUri(appContext, RingtoneManager.TYPE_ALARM);
        if (uri == null) {
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }
        player = new MediaPlayer();
        try {
            player.setDataSource(appContext, uri);
            player.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
            player.setLooping(true);
            player.prepare();
            player.start();
        } catch (Exception e) {
            Log.e(TAG, "Failed to start alarm sound", e);
            player.release();
            player = null;
        }
    }

    @Override
    public void stop() {
        if (player == null) return;
        try {
            if (player.isPlaying()) player.stop();
        } catch (IllegalStateException ignored) {
        }
        player.release();
        player = null;
    }
}
