package com.example.remindme.util;

import androidx.annotation.NonNull;

public class ReminderSnoozeModel {

    public enum SnoozeIntervalOptions {
        M5,
        M10,
        M15,
        M30
    }

    public enum SnoozeRepeatOptions {
        R3,
        R5,
        RC,
    }

    public SnoozeIntervalOptions intervalOption = SnoozeIntervalOptions.M5;

    public SnoozeRepeatOptions repeatOption = SnoozeRepeatOptions.R3;

    public boolean enabled = true;

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        if(enabled){
            builder.append("Interval ");

            switch (intervalOption){
                default:
                case M5:
                    builder.append("5 min");
                    break;
                case M10:
                    builder.append("10 min");
                    break;
                case M15:
                    builder.append("15 min");
                    break;
                case M30:
                    builder.append("30 min");
                    break;
            }

            builder.append(", Repeat ");

            switch (repeatOption){
                default:
                case R3:
                    builder.append("3 times");
                    break;
                case R5:
                    builder.append("5 times");
                    break;
                case RC:
                    builder.append("Continuously");
                    break;
            }
        }
        else{
            builder.append("OFF");
        }

        return  builder.toString();
    }
}
