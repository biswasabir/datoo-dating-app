package com.angopapo.datooapp.home.settings.about;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.angopapo.datooapp.BuildConfig;
import com.angopapo.datooapp.R;
import com.angopapo.datooapp.app.Config;
import com.angopapo.datooapp.helpers.QuickHelp;
import com.angopapo.datooapp.home.settings.WebUrlsActivity;
import com.angopapo.datooapp.utils.Tools;

import java.util.Objects;

public class AboutActivity extends AppCompatActivity {

    Toolbar toolbar;
    LinearLayout mTermsOfUse, mPrivacyPolicy, mAboutApp;
    TextView mAboutAppText, mAppVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_about);

        toolbar = findViewById(R.id.toolbar);

        mAboutAppText = findViewById(R.id.about_app);
        mAppVersion = findViewById(R.id.app_version);

        mTermsOfUse = findViewById(R.id.terms_and_conditions);
        mPrivacyPolicy = findViewById(R.id.privacy_policy);
        mAboutApp = findViewById(R.id.about_settings);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.about_settings));
        getSupportActionBar().setElevation(4);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Tools.setSystemBarColor(this, R.color.white);
        Tools.setSystemBarLight(this);

        mAboutAppText.setText(String.format("%s %s", getString(R.string.about_settings), getString(R.string.app_name)));
        mAppVersion.setText(String.format("v%s", BuildConfig.VERSION_NAME));

        mTermsOfUse.setOnClickListener(v -> goToUrl(Config.TERMS_OF_USE));
        mPrivacyPolicy.setOnClickListener(v -> goToUrl(Config.PRIVACY_POLICY));
        mAboutApp.setOnClickListener(v -> goToAboutApp());

    }

    public void goToUrl(String url){
        QuickHelp.goToActivityWithNoClean(this, WebUrlsActivity.class, WebUrlsActivity.WEB_URL_TYPE, url);
    }

    public void goToAboutApp(){
        QuickHelp.goToActivityWithNoClean(this, AboutAppActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}