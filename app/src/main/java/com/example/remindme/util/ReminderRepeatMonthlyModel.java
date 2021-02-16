package com.example.remindme.util;

import androidx.annotation.NonNull;

public class ReminderRepeatMonthlyModel {
    public boolean isJan = true;
    public boolean isFeb = true;
    public boolean isMar = true;
    public boolean isApr = true;
    public boolean isMay = true;
    public boolean isJun = true;
    public boolean isJul = true;
    public boolean isAug = true;
    public boolean isSep = true;
    public boolean isOct = true;
    public boolean isNov = true;
    public boolean isDec = true;

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        if(isJan){
            builder.append("Jan ");
        }
        if(isFeb){
            builder.append("Feb ");
        }
        if(isMar){
            builder.append("Mar ");
        }
        if(isApr){
            builder.append("Apr ");
        }
        if(isMay){
            builder.append("May ");
        }
        if(isJun){
            builder.append("Jun ");
        }
        if(isJul){
            builder.append("Jul ");
        }
        if(isAug){
            builder.append("Aug ");
        }
        if(isSep){
            builder.append("Sep ");
        }
        if(isOct){
            builder.append("Oct ");
        }
        if(isNov){
            builder.append("Nov ");
        }
        if(isDec){
            builder.append("Dec ");
        }

        return builder.toString().trim();
    }
}
