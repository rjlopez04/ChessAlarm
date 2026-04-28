package com.example.chessalarm.alarm;

public class StartAlarmCommand implements Command {

    private final AlarmController.SoundPlayer soundPlayer;

    public StartAlarmCommand(AlarmController.SoundPlayer soundPlayer) {
        this.soundPlayer = soundPlayer;
    }

    @Override
    public void execute() {
        soundPlayer.start();
    }
}
