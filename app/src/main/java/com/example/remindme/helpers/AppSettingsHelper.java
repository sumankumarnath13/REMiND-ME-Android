package com.example.remindme.helpers;

import com.example.remindme.dataModels.AppSetting;

import javax.annotation.ParametersAreNonnullByDefault;

import io.realm.Realm;

public class AppSettingsHelper {

    private static final String ID = "ID";

    private AppSettingsHelper() {
        Realm realm = Realm.getDefaultInstance();
        AppSetting setting = realm.where(AppSetting.class).equalTo("id", ID).findFirst();
        if (setting != null) {
            use24hourTime = setting.use24hourTime;
            disableAllReminders = setting.disableAllReminders;
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

    private void update() {
        final AppSetting setting = new AppSetting();
        setting.id = ID;
        setting.disableAllReminders = disableAllReminders;
        setting.use24hourTime = use24hourTime;

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
