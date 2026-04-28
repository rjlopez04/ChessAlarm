package com.example.chessalarm.alarm;

public class StopAlarmCommand implements Command {

    private final AlarmController.SoundPlayer soundPlayer;

    public StopAlarmCommand(AlarmController.SoundPlayer soundPlayer) {
        this.soundPlayer = soundPlayer;
    }

    @Override
    public void execute() {
        soundPlayer.stop();
    }
}
