package com.angopapo.datooapp.app;


import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.angopapo.datooapp.R;
import com.angopapo.datooapp.auth.WelcomeActivity;
import com.angopapo.datooapp.helpers.QuickHelp;
import com.angopapo.datooapp.home.HomeActivity;
import com.angopapo.datooapp.models.datoo.User;
import com.angopapo.datooapp.utils.SharedPrefUtil;
import com.angopapo.datooapp.utils.StatusBarUtil;
import com.angopapo.datooapp.utils.Tools;
import com.parse.ParseUser;


public class DispatchActivity extends AppCompatActivity {

    User mCurrentUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

      requestWindowFeature(Window.FEATURE_NO_TITLE);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    mCurrentUser = (User)User.getCurrentUser();

      StatusBarUtil.useTransparentBarWithCurrentBackground(this);
      StatusBarUtil.setLightMode(this);

      Tools.setSystemBarColor(this, R.color.white);
      Tools.setSystemBarLight(this);


    // Check if there is a valid user session or not

    if (mCurrentUser != null )  {

      if (!mCurrentUser.getLanguage().isEmpty()){
        new SharedPrefUtil(this).setLanguage(this, mCurrentUser.getLanguage());
      } else {
        mCurrentUser.setLanguage(new SharedPrefUtil(this).getLanguage(this));
        mCurrentUser.saveInBackground();
      }

      if (mCurrentUser.isUserBlocked()){

        QuickHelp.showToast(this, getString(R.string.user_blocked_by_admin), true);

        ParseUser.logOut();
        QuickHelp.goToActivityAndFinish(this, WelcomeActivity.class);

      } else {

        QuickHelp.goToActivityAndFinish(this, HomeActivity.class);
      }

    } else {

        QuickHelp.goToActivityAndFinish(this, WelcomeActivity.class);
    }


  }

}
