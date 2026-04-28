package com.example.chessalarm.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.chessalarm.PuzzleActivity;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmController.getInstance(context).trigger();

        Intent puzzle = new Intent(context, PuzzleActivity.class);
        puzzle.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(puzzle);
    }
}
