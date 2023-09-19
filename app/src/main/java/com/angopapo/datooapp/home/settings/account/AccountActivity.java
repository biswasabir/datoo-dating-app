package com.angopapo.datooapp.home.settings.account;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.angopapo.datooapp.R;
import com.angopapo.datooapp.auth.WelcomeActivity;
import com.angopapo.datooapp.helpers.QuickHelp;
import com.angopapo.datooapp.models.datoo.User;
import com.angopapo.datooapp.utils.Tools;
import com.parse.ParseUser;

import java.util.Objects;

public class AccountActivity extends AppCompatActivity {

    Toolbar toolbar;

    User mCurrentUser;

    LinearLayout mGetPasswordLayout, mSignOutLayout;

    EditText mEmail;
    AppCompatButton mGetPassword, mSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_account);

        toolbar = findViewById(R.id.toolbar);

        mGetPasswordLayout = findViewById(R.id.get_password_layout);
        mSignOutLayout = findViewById(R.id.signout_layout);

        mEmail = findViewById(R.id.accountEmail);
        mGetPassword = findViewById(R.id.accountGetPassword);

        mSignOut = findViewById(R.id.accountSignOut);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.account));
        getSupportActionBar().setElevation(4);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Tools.setSystemBarColor(this, R.color.white);
        Tools.setSystemBarLight(this);

        mCurrentUser = (User) User.getCurrentUser();


        if (mCurrentUser != null){

            if (mCurrentUser.isPasswordEnabled()){

                mGetPasswordLayout.setVisibility(View.GONE);
                mSignOutLayout.setVisibility(View.VISIBLE);

            } else {

                mGetPasswordLayout.setVisibility(View.VISIBLE);
                mSignOutLayout.setVisibility(View.GONE);

                if (!mCurrentUser.getEmail().isEmpty()){

                    mEmail.setText(mCurrentUser.getEmail());
                }

            }
        }

        mGetPassword.setOnClickListener(v -> {

            String email = mEmail.getText().toString().replaceAll(" ", "").trim();

            if (email.length() == 0) {
                mEmail.setError(getString(R.string.email_empty));

            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){

                mEmail.setError(getString(R.string.invalid_email));
            } else {
                getPassword(email);
            }
        });


        mSignOut.setOnClickListener(v -> signOut());


    }
    public void getPassword(String email){

        QuickHelp.showLoading(this, false);

        if (!mCurrentUser.getEmail().isEmpty() && mCurrentUser.getEmail().equals(email)){

            requestPassword(email);

        } else {

            mCurrentUser.setEmail(email);
            mCurrentUser.saveInBackground(e -> {

                if (e == null){

                    requestPassword(email);

                } else {

                    QuickHelp.hideLoading(this);
                    QuickHelp.showToast(this, getString(R.string.error_ocurred), true);
                }
            });
        }
    }

    public void requestPassword(String email){

        User.requestPasswordResetInBackground(email, e -> {

            if (e == null){

                mCurrentUser.setPasswordEnabled(true);
                mCurrentUser.saveInBackground(e1 -> {
                    if (e1 == null){
                        onBackPressed();
                    }
                });

                QuickHelp.hideLoading(this);

                QuickHelp.showToast(this, getString(R.string.email_newpass_sent), false);

            } else {

                QuickHelp.logoutForInvalidSession(this, e);

                QuickHelp.hideLoading(this);
                QuickHelp.showToast(this, getString(R.string.error_ocurred), true);
            }
        });
    }

    public void signOut(){

        QuickHelp.showLoading(this, false);

        QuickHelp.removeUserToInstallation();

        mCurrentUser.deleteInstallation();
        mCurrentUser.saveInBackground(e -> {

            if (e == null){

                ParseUser.logOutInBackground(e1 -> {

                    if (e1 == null){

                        QuickHelp.hideLoading(this);
                        QuickHelp.showToast(this, getString(R.string.profile_account_signout_success), false);
                        QuickHelp.goToActivityLogout(this, WelcomeActivity.class);

                    } else {
                        QuickHelp.hideLoading(this);
                        QuickHelp.showToast(this, getString(R.string.error_ocurred), true);
                    }
                });

            } else {

                QuickHelp.hideLoading(this);
                QuickHelp.showToast(this, getString(R.string.error_ocurred), true);
            }
        });

    }

    public void signOutWipe(){

        ParseUser.logOutInBackground();
        QuickHelp.showToast(this, getString(R.string.profile_account_signout_success), false);
        QuickHelp.goToActivityLogout(this, WelcomeActivity.class);

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