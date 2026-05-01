package com.example.chessalarm.alarm;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class AlarmController {

    public enum State { IDLE, RINGING }

    public interface SoundPlayer {
        void start();
        void stop();
    }

    private static AlarmController instance;

    private final SoundPlayer soundPlayer;
    private final List<AlarmStateListener> listeners = new ArrayList<>();
    private State state = State.IDLE;

    AlarmController(SoundPlayer soundPlayer) {
        this.soundPlayer = soundPlayer;
    }

    public static synchronized AlarmController getInstance(Context context) {
        if (instance == null) {
            instance = new AlarmController(new RingtoneSoundPlayer(context.getApplicationContext()));
        }
        return instance;
    }

    public State getState() {
        return state;
    }

    public void addListener(AlarmStateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(AlarmStateListener listener) {
        listeners.remove(listener);
    }

    public void trigger() {
        if (state == State.RINGING) return;
        state = State.RINGING;
        new StartAlarmCommand(soundPlayer).execute();
        for (AlarmStateListener l : new ArrayList<>(listeners)) {
            l.onAlarmTriggered();
        }
    }

    public void dismiss() {
        if (state == State.IDLE) return;
        state = State.IDLE;
        new StopAlarmCommand(soundPlayer).execute();
        for (AlarmStateListener l : new ArrayList<>(listeners)) {
            l.onAlarmDismissed();
        }
    }

    /**
     * Stops the alarm sound without dismissing the alarm. State remains {@link State#RINGING}
     * so the user must still solve the puzzle to fully dismiss. No-op when idle.
     */
    public void silence() {
        if (state == State.IDLE) return;
        new StopAlarmCommand(soundPlayer).execute();
    }
}
