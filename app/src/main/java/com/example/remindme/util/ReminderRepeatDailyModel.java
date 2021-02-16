package com.example.remindme.util;

import androidx.annotation.NonNull;

public class ReminderRepeatDailyModel {
    public boolean isSun = true;
    public boolean isMon = true;
    public boolean isTue = true;
    public boolean isWed = true;
    public boolean isThu = true;
    public boolean isFri = true;
    public boolean isSat = true;

    @NonNull
    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        if(isSun){
            builder.append("SUN ");
        }
        if(isMon){
            builder.append("MON ");
        }
        if(isThu){
            builder.append("TUE ");
        }
        if(isWed){
            builder.append("WED ");
        }
        if(isThu){
            builder.append("THU ");
        }
        if(isFri){
            builder.append("FRI ");
        }
        if(isSat){
            builder.append("SAT ");
        }

        return builder.toString().trim();
    }
}
