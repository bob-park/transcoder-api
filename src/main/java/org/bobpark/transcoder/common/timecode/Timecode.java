package org.bobpark.transcoder.common.timecode;

import lombok.Getter;

@Getter
public class Timecode {

    private final long hours;
    private final int minutes;
    private final int seconds;
    private final int milliseconds;

    public Timecode(long seconds) {

        this.milliseconds = 0;

        this.seconds = (int)(seconds % 60);
        this.minutes = (int)(seconds / 60 % 60);
        this.hours = (int)(seconds / 3_600);

    }

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
