package com.example.remindme.util;

import androidx.annotation.NonNull;

public class ReminderRepeatModel {
    public enum ReminderRepeatOptions {
        None,
        Hourly,
        Daily,
        Weekly,
        Monthly,
        Yearly
    }
    public ReminderRepeatOptions repeatOption = ReminderRepeatOptions.Daily;
    public ReminderRepeatDailyModel dailyModel = new ReminderRepeatDailyModel();
    public ReminderRepeatMonthlyModel monthlyModel = new ReminderRepeatMonthlyModel();

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        switch (repeatOption){
            default:
            case None:
                builder.append("OFF");
                break;
            case Hourly:
                builder.append("Hourly");
                break;
            case Daily:
                builder.append("On ");
                builder.append(dailyModel.toString());
                break;
            case Weekly:
                builder.append("Weekly");
                break;
            case Monthly:
                builder.append("On ");
                builder.append(monthlyModel.toString());
                break;
            case Yearly:
                builder.append("Yearly");
                break;
        }

        return  builder.toString();
    }
}
