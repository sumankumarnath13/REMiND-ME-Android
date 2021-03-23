package com.example.remindme.viewModels;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ReminderRepeatModel {
    public enum ReminderRepeatOptions {
        NONE,
        HOURLY,
        HOURLY_CUSTOM,
        DAILY,
        DAILY_CUSTOM,
        WEEKLY,
        WEEKLY_CUSTOM,
        MONTHLY,
        MONTHLY_CUSTOM,
        YEARLY
    }

    public int customMinute;
    public List<Integer> customHours;
    public List<Integer> customDays;
    public List<Integer> customWeeks;
    public List<Integer> customMonths;
    public ReminderRepeatOptions repeatOption;

    public ReminderRepeatModel() {
        repeatOption = ReminderRepeatOptions.DAILY;
        customHours = new ArrayList<>();
        customDays = new ArrayList<>();
        customWeeks = new ArrayList<>();
        customMonths = new ArrayList<>();
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        switch (repeatOption) {
            default:
            case NONE:
                builder.append("OFF");
                break;
            case HOURLY:
                builder.append("Hourly");
                break;
            case HOURLY_CUSTOM:
                builder.append("On ");
                for (int i = 0; i < customHours.size(); i++) {
                    int h = customHours.get(i) + 1;
                    if (h <= 11) {
                        builder.append(h + ":" + customMinute + " am,");
                    } else {
                        builder.append(h - 11 + ":" + customMinute + " pm,");
                    }
                }
                builder.append(" of every day");
                break;
            case DAILY:
                builder.append("Daily");
                break;
            case DAILY_CUSTOM:
                builder.append("On ");
                for (int i = 0; i < customDays.size(); i++) {
                    int value = customDays.get(i) + 1;
                    builder.append(value + ",");
                }
                builder.append(" of every week");
                break;
            case WEEKLY:
                builder.append("Weekly");
                break;
            case WEEKLY_CUSTOM:
                builder.append("On ");
                for (int i = 0; i < customWeeks.size(); i++) {
                    int value = customWeeks.get(i);
                    switch (value) {
                        default:
                        case 0:
                            builder.append("1st,");
                            break;
                        case 1:
                            builder.append("2nd,");
                            break;
                        case 2:
                            builder.append("3rd,");
                            break;
                        case 3:
                            builder.append("4th,");
                            break;
                    }
                }
                builder.append(" week of every month");
                break;
            case MONTHLY:
                builder.append("Monthly");
                break;
            case MONTHLY_CUSTOM:
                builder.append("On ");
                for (int i = 0; i < customMonths.size(); i++) {
                    int value = customMonths.get(i);
                    switch (value) {
                        default:
                        case 0:
                            builder.append("Jan ");
                            break;
                        case 1:
                            builder.append("Feb ");
                            break;
                        case 2:
                            builder.append("Mar ");
                            break;
                        case 3:
                            builder.append("Apr ");
                            break;
                        case 4:
                            builder.append("May ");
                            break;
                        case 5:
                            builder.append("Jun ");
                            break;
                        case 6:
                            builder.append("Jul ");
                            break;
                        case 7:
                            builder.append("Aug ");
                            break;
                        case 8:
                            builder.append("Sep ");
                            break;
                        case 9:
                            builder.append("Oct ");
                            break;
                        case 10:
                            builder.append("Nov ");
                            break;
                        case 11:
                            builder.append("Dec ");
                            break;
                    }
                }
                builder.append(" of every year");
                break;
            case YEARLY:
                builder.append("Yearly");
                break;
        }

        return builder.toString();
    }


}
