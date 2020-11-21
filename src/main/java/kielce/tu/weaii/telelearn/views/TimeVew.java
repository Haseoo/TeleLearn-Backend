package kielce.tu.weaii.telelearn.views;

import lombok.Value;

import java.time.Duration;

@Value
public class TimeVew {
    long hours;
    long minutes;

    public static TimeVew form(Duration duration) {
        return new TimeVew(duration.toHours(), duration.minusHours(duration.toHours()).toMinutes());
    }
}
