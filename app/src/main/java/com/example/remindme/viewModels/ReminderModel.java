package com.example.remindme.viewModels;

import com.example.remindme.dataModels.ReminderActive;

import java.util.Date;

public class ReminderModel {
    public boolean isValid;
    public String validation_message;

    public int id;
    public String name;
    public String note;
    public Date time;
    public boolean isEnable = true;
    public boolean isVibrate = false;
    public ReminderRepeatModel repeatModel = new ReminderRepeatModel();
    public ReminderSnoozeModel snoozeModel = new ReminderSnoozeModel();

    public static ReminderActive transform(ReminderModel from){
        ReminderActive to = new ReminderActive();
        to.id = from.id;
        to.name = from.name;
        to.note = from.note;
        to.time = from.time;
        to.isEnable = from.isEnable;
        to.isVibrate = from.isVibrate;

        switch (from.repeatModel.repeatOption){
            default:
            case None:
                to.repeatOption = 0;
                break;
            case Hourly:
                to.repeatOption = 1;
                break;
            case Daily:
                to.repeatOption = 2;

                to.isRepeatOn_Sun = from.repeatModel.dailyModel.isSun;
                to.isRepeatOn_Mon = from.repeatModel.dailyModel.isMon;
                to.isRepeatOn_Tue = from.repeatModel.dailyModel.isTue;
                to.isRepeatOn_Wed = from.repeatModel.dailyModel.isWed;
                to.isRepeatOn_Thu = from.repeatModel.dailyModel.isThu;
                to.isRepeatOn_Fri = from.repeatModel.dailyModel.isFri;
                to.isRepeatOn_Sat = from.repeatModel.dailyModel.isSat;

                break;
            case Weekly:
                to.repeatOption = 3;
                break;
            case Monthly:
                to.repeatOption = 4;

                to.isRepeatOn_Jan = from.repeatModel.monthlyModel.isJan;
                to.isRepeatOn_Feb = from.repeatModel.monthlyModel.isFeb;
                to.isRepeatOn_Mar = from.repeatModel.monthlyModel.isMar;
                to.isRepeatOn_Apr = from.repeatModel.monthlyModel.isApr;
                to.isRepeatOn_May = from.repeatModel.monthlyModel.isMay;
                to.isRepeatOn_Jun = from.repeatModel.monthlyModel.isJun;
                to.isRepeatOn_Jul = from.repeatModel.monthlyModel.isJul;
                to.isRepeatOn_Aug = from.repeatModel.monthlyModel.isAug;
                to.isRepeatOn_Sep = from.repeatModel.monthlyModel.isSep;
                to.isRepeatOn_Oct = from.repeatModel.monthlyModel.isOct;
                to.isRepeatOn_Nov = from.repeatModel.monthlyModel.isNov;
                to.isRepeatOn_Dec = from.repeatModel.monthlyModel.isDec;

                break;
            case Yearly:
                to.repeatOption = 5;
                break;
        }

        to.isSnoozeEnable = from.snoozeModel.isEnable;

        if(from.snoozeModel.isEnable){

            switch (from.snoozeModel.countOptions)
            {
                default:
                case R3:
                    to.snoozeCount = 3;
                    break;
                case R5:
                    to.snoozeCount = 5;
                    break;
                case RC:
                    to.snoozeCount = -1;
                    break;
            }

            switch (from.snoozeModel.intervalOption)
            {
                default:
                case M5:
                    to.snoozeInterval = 5;
                    break;
                case M10:
                    to.snoozeInterval = 10;
                    break;
                case M15:
                    to.snoozeInterval = 15;
                    break;
                case M30:
                    to.snoozeInterval = 30;
                    break;
            }
        }
        else {
            to.snoozeCount = 0;
            to.snoozeInterval = 0;
        }

        return  to;
    }

    public static ReminderModel transform(ReminderActive from){
        ReminderModel to = new ReminderModel();
        to.id = from.id;
        to.name = from.name;
        to.note = from.note;
        to.time = from.time;
        to.isEnable = from.isEnable;
        to.isVibrate = from.isVibrate;

        switch (from.repeatOption){
            default:
            case 0:
                to.repeatModel.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.None;
                break;
            case 1:
                to.repeatModel.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.Hourly;
                break;
            case 2:
                to.repeatModel.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.Daily;

                to.repeatModel.dailyModel.isSun = from.isRepeatOn_Sun;
                to.repeatModel.dailyModel.isMon = from.isRepeatOn_Mon;
                to.repeatModel.dailyModel.isTue = from.isRepeatOn_Tue;
                to.repeatModel.dailyModel.isWed = from.isRepeatOn_Wed;
                to.repeatModel.dailyModel.isThu = from.isRepeatOn_Thu;
                to.repeatModel.dailyModel.isFri = from.isRepeatOn_Fri;
                to.repeatModel.dailyModel.isSat = from.isRepeatOn_Sat;

                break;
            case 3:
                to.repeatModel.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.Weekly;
                break;
            case 4:
                to.repeatModel.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.Monthly;

                to.repeatModel.monthlyModel.isJan = from.isRepeatOn_Jan;
                to.repeatModel.monthlyModel.isFeb = from.isRepeatOn_Feb;
                to.repeatModel.monthlyModel.isMar = from.isRepeatOn_Mar;
                to.repeatModel.monthlyModel.isApr = from.isRepeatOn_Apr;
                to.repeatModel.monthlyModel.isMay = from.isRepeatOn_May;
                to.repeatModel.monthlyModel.isJun = from.isRepeatOn_Jun;
                to.repeatModel.monthlyModel.isJul = from.isRepeatOn_Jul;
                to.repeatModel.monthlyModel.isAug = from.isRepeatOn_Aug;
                to.repeatModel.monthlyModel.isSep = from.isRepeatOn_Sep;
                to.repeatModel.monthlyModel.isOct = from.isRepeatOn_Oct;
                to.repeatModel.monthlyModel.isNov = from.isRepeatOn_Nov;
                to.repeatModel.monthlyModel.isDec = from.isRepeatOn_Dec;

                break;
            case 5:
                to.repeatModel.repeatOption = ReminderRepeatModel.ReminderRepeatOptions.Yearly;
                break;
        }

        to.snoozeModel.isEnable = from.isSnoozeEnable;

        if(from.isSnoozeEnable){

            switch (from.snoozeCount){
                default:
                case 3:
                    to.snoozeModel.countOptions = ReminderSnoozeModel.SnoozeCountOptions.R3;
                    break;
                case 5:
                    to.snoozeModel.countOptions = ReminderSnoozeModel.SnoozeCountOptions.R5;
                    break;
                case -1:
                    to.snoozeModel.countOptions = ReminderSnoozeModel.SnoozeCountOptions.RC;
                    break;
            }

            switch (from.snoozeInterval){
                default:
                case 5:
                    to.snoozeModel.intervalOption = ReminderSnoozeModel.SnoozeIntervalOptions.M5;
                    break;
                case 10:
                    to.snoozeModel.intervalOption = ReminderSnoozeModel.SnoozeIntervalOptions.M10;
                    break;
                case 15:
                    to.snoozeModel.intervalOption = ReminderSnoozeModel.SnoozeIntervalOptions.M15;
                    break;
                case 30:
                    to.snoozeModel.intervalOption = ReminderSnoozeModel.SnoozeIntervalOptions.M30;
                    break;
            }
        }

        return  to;
    }
}
