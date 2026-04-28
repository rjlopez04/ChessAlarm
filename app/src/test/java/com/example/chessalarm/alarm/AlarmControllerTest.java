package com.example.chessalarm.alarm;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class AlarmControllerTest {

    private static class FakeSoundPlayer implements AlarmController.SoundPlayer {
        int starts = 0;
        int stops = 0;
        @Override public void start() { starts++; }
        @Override public void stop() { stops++; }
    }

    private static class RecordingListener implements AlarmStateListener {
        int triggered = 0;
        int dismissed = 0;
        @Override public void onAlarmTriggered() { triggered++; }
        @Override public void onAlarmDismissed() { dismissed++; }
    }

    private FakeSoundPlayer sound;
    private AlarmController controller;

    @Before
    public void setUp() {
        sound = new FakeSoundPlayer();
        controller = new AlarmController(sound);
    }

    @Test
    public void initialStateIsIdle() {
        assertSame(AlarmController.State.IDLE, controller.getState());
    }

    @Test
    public void triggerMovesToRingingAndStartsSound() {
        controller.trigger();
        assertSame(AlarmController.State.RINGING, controller.getState());
        assertEquals(1, sound.starts);
    }

    @Test
    public void dismissAfterTriggerStopsSoundAndReturnsToIdle() {
        controller.trigger();
        controller.dismiss();
        assertSame(AlarmController.State.IDLE, controller.getState());
        assertEquals(1, sound.stops);
    }

    @Test
    public void listenerNotifiedOnTriggerAndDismiss() {
        RecordingListener listener = new RecordingListener();
        controller.addListener(listener);

        controller.trigger();
        controller.dismiss();

        assertEquals(1, listener.triggered);
        assertEquals(1, listener.dismissed);
    }

    @Test
    public void removedListenerIsNotNotified() {
        RecordingListener listener = new RecordingListener();
        controller.addListener(listener);
        controller.removeListener(listener);

        controller.trigger();

        assertEquals(0, listener.triggered);
    }

    @Test
    public void duplicateAddDoesNotDoubleNotify() {
        RecordingListener listener = new RecordingListener();
        controller.addListener(listener);
        controller.addListener(listener);

        controller.trigger();

        assertEquals(1, listener.triggered);
    }

    @Test
    public void triggerWhileRingingIsNoOp() {
        controller.trigger();
        controller.trigger();

        assertEquals(1, sound.starts);
    }

    @Test
    public void dismissWhileIdleIsNoOp() {
        controller.dismiss();

        assertEquals(0, sound.stops);
        assertSame(AlarmController.State.IDLE, controller.getState());
    }
}
