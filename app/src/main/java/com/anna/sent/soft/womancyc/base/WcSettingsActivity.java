package com.anna.sent.soft.womancyc.base;

import com.anna.sent.soft.activity.BaseSettingsActivity;
import com.anna.sent.soft.settings.SettingsLanguage;
import com.anna.sent.soft.settings.SettingsTheme;
import com.anna.sent.soft.womancyc.shared.SettingsLanguageImpl;
import com.anna.sent.soft.womancyc.shared.SettingsThemeImpl;

public abstract class WcSettingsActivity extends BaseSettingsActivity {
    @Override
    protected String getAppTag() {
        return WcConstants.TAG;
    }

    @Override
    protected SettingsLanguage createSettingsLanguage() {
        return new SettingsLanguageImpl(this);
    }

    @Override
    protected SettingsTheme createSettingsTheme() {
        return new SettingsThemeImpl(this);
    }
}
