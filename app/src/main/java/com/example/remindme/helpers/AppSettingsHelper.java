package com.example.remindme.helpers;

import android.content.Context;

import com.example.remindme.R;
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
        final Realm realm = Realm.getDefaultInstance();
        final AppSetting setting = realm.where(AppSetting.class).equalTo("id", ID).findFirst();
        realm.close();
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

    private String dateFormat;

    public String getDateFormat(Context context) {
        if (dateFormat == null) {
            return context.getResources().getStringArray(R.array.values_date_format)[0];
        }
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

        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @ParametersAreNonnullByDefault
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(setting);
            }
        }, realm::close);
    }

}
