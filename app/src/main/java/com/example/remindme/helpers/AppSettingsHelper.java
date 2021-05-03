package com.example.remindme.helpers;

import com.example.remindme.dataModels.AppSetting;

import java.util.Calendar;

import javax.annotation.ParametersAreNonnullByDefault;

import io.realm.Realm;

public class AppSettingsHelper {

    public enum Themes {
        BLACK,
        LIGHT
    }

    private static final String ID = "ID";

    private AppSettingsHelper() {
        Realm realm = Realm.getDefaultInstance();
        AppSetting setting = realm.where(AppSetting.class).equalTo("id", ID).findFirst();
        if (setting != null) {
            use24hourTime = setting.use24hourTime;
            disableAllReminders = setting.disableAllReminders;
            firstDayOfWeek = setting.firstDayOfWeek;
            dateFormat = setting.dateFormat;
            if (setting.theme == 1) {
                theme = Themes.LIGHT;
            } else {
                theme = Themes.BLACK;
            }
        }
    }

    private static AppSettingsHelper instance;

    public static AppSettingsHelper getInstance() {
        if (instance == null) {
            instance = new AppSettingsHelper();
        }
        return instance;
    }

    private boolean disableAllReminders;

    public boolean isDisableAllReminders() {
        return disableAllReminders;
    }

    public void setDisableAllReminders(boolean value) {
        disableAllReminders = value;
        update();
    }

    private boolean use24hourTime;

    public boolean isUse24hourTime() {
        return use24hourTime;
    }

    public void setUse24hourTime(boolean value) {
        use24hourTime = value;
        update();
    }

    private int firstDayOfWeek = Calendar.SUNDAY;

    public int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    public void setFirstDayOfWeek(int value) {
        firstDayOfWeek = value;
        update();
    }

    private Themes theme = Themes.BLACK;

    public Themes getTheme() {
        return theme;
    }

    public void setTheme(Themes value) {
        theme = value;
        update();
    }

    private String dateFormat = "dd MMM yy";

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        update();
    }

    private void update() {
        final AppSetting setting = new AppSetting();
        setting.id = ID;
        setting.disableAllReminders = disableAllReminders;
        setting.use24hourTime = use24hourTime;
        setting.firstDayOfWeek = firstDayOfWeek;
        setting.dateFormat = dateFormat;

        switch (theme) {
            default:
                setting.theme = 0;
                break;

            case LIGHT:
                setting.theme = 1;
                break;
        }

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @ParametersAreNonnullByDefault
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(setting);
            }
        });
    }

}
