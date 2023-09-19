package com.angopapo.datooapp.home;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.angopapo.datooapp.R;
import com.angopapo.datooapp.app.Application;
import com.angopapo.datooapp.app.Config;
import com.angopapo.datooapp.app.Constants;
import com.angopapo.datooapp.helpers.QuickActions;
import com.angopapo.datooapp.helpers.QuickHelp;
import com.angopapo.datooapp.helpers.SendNotifications;
import com.angopapo.datooapp.home.connections.ConnectionsFragment;
import com.angopapo.datooapp.home.encounters.EncountersFragment;
import com.angopapo.datooapp.home.encounters.EncountersFragmentAds;
import com.angopapo.datooapp.home.live.LiveFragment;
import com.angopapo.datooapp.home.nearby.PeopleNearbyFragment;
import com.angopapo.datooapp.home.nearby.PeopleNearbyFragmentAds;
import com.angopapo.datooapp.home.payments.PaymentsActivity;
import com.angopapo.datooapp.home.profile.MyProfileFragment;
import com.angopapo.datooapp.home.settings.basicInfo.BasicInfoActivity;
import com.angopapo.datooapp.home.uploads.UploadsActivity;
import com.angopapo.datooapp.models.datoo.User;
import com.angopapo.datooapp.modules.cardstackview.CardStackLayoutManager;
import com.angopapo.datooapp.modules.cardstackview.CardStackView;
import com.angopapo.datooapp.modules.cardstackview.Direction;
import com.angopapo.datooapp.modules.cardstackview.RewindAnimationSetting;
import com.angopapo.datooapp.modules.merlin.Bindable;
import com.angopapo.datooapp.modules.merlin.Connectable;
import com.angopapo.datooapp.modules.merlin.Disconnectable;
import com.angopapo.datooapp.modules.merlin.Merlin;
import com.angopapo.datooapp.modules.merlin.MerlinsBeard;
import com.angopapo.datooapp.modules.merlin.NetworkStatus;
import com.angopapo.datooapp.modules.showcaseview.GuideView;
import com.angopapo.datooapp.modules.showcaseview.config.DismissType;
import com.angopapo.datooapp.modules.showcaseview.config.Gravity;
import com.angopapo.datooapp.utils.SharedPrefUtil;
import com.angopapo.datooapp.utils.Tools;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.greysonparrelli.permiso.Permiso;

import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements Connectable, Disconnectable, Bindable {

    public static String EXTRA_USER_TO_ID = "user_to_id";
    public static String EXTRA_TYPE = "extra_type";

    public static String VIEW_TYPE_MY_PROFILE = "MY_PROFILE";
    public static String VIEW_TYPE_NEAR_BY = "NEAR_BY";
    public static String VIEW_TYPE_ENCOUNTERS = "ENCOUNTERS";
    public static String VIEW_TYPE_CONNECTIONS = "CONNECTIONS";
    public static String VIEW_TYPE_STREAMING = "STREAMING";

    RelativeLayout mAdView;

    private Fragment fragment;
    private FragmentManager fragmentManager;

    CoordinatorLayout rootView;

    BottomSheetDialog sheetDialog;

    BottomNavigationView navigation;

    Toolbar mToolbar;
    LinearLayout mInternetLayout;
    TextView mNetworkTitle, mNetworkDesc;

    ImageView mIconLeft, mIconRight, mIconUndo;

    User mCurrentUser;

    View mShadowView;

    String myIntent = null;
    String myIntentType = null;

    private SharedPrefUtil sharedPrefUtil;

    Merlin merlin;
    MerlinsBeard merlinsBeard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        rootView = findViewById(R.id.view);
        navigation = findViewById(R.id.navigation_bottom);
        mToolbar = findViewById(R.id.toolbar);
        mInternetLayout = findViewById(R.id.screenConnectionLost);
        mNetworkTitle = findViewById(R.id.screenConnectionLost_title);
        mNetworkDesc = findViewById(R.id.screenConnectionLost_description);
        mAdView = findViewById(R.id.adView);

        mShadowView = findViewById(R.id.shawdow_view);

        mAdView.setVisibility(View.GONE);

        merlinsBeard = new MerlinsBeard.Builder().build(this);

        myIntent = getIntent().getStringExtra(EXTRA_USER_TO_ID);
        myIntentType = getIntent().getStringExtra(EXTRA_TYPE);

        if (myIntent != null){

            QuickHelp.showLoading(this, true);

            ParseQuery<User> parseQuery = User.getUserQuery();
            parseQuery.getInBackground(myIntent, (user, e) -> {

                if (e == null){

                    if (myIntentType.equals(SendNotifications.PUSH_TYPE_MESSAGES)){

                        QuickHelp.goToActivityChat(this, user);
                    } else {

                        QuickActions.showProfile(this, user, false);
                    }

                    QuickHelp.hideLoading(this);

                } else {
                    QuickHelp.hideLoading(this);

                    QuickHelp.logoutForInvalidSession(HomeActivity.this, e);
                }
            });
        }

        mIconLeft = findViewById(R.id.icon_left);
        mIconRight = findViewById(R.id.icon_right);
        mIconUndo = findViewById(R.id.icon_undo);

        merlin = createMerlin();

        mInternetLayout.setVisibility(View.GONE);

        Tools.setSystemBarColor(this, R.color.white);
        Tools.setSystemBarLight(this);

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        fragmentManager = getSupportFragmentManager();
        fragment = new EncountersFragment();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.frame_layout, fragment).commit();

        mShadowView.setVisibility(View.GONE);

        Permiso.getInstance().setActivity(HomeActivity.this);

        mCurrentUser = (User) ParseUser.getCurrentUser();

        sharedPrefUtil = new SharedPrefUtil(this);

        QuickHelp.setAds(this, mAdView, mCurrentUser);

        navigation.setSelectedItemId(R.id.navigation_encounters);

        navigation.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {

                        case R.id.navigation_mess:

                            fragment = new ConnectionsFragment();
                            mShadowView.setVisibility(View.VISIBLE);
                            break;

                        case R.id.navigation_public:

                            if (Config.isNearByNativeAdsActivated){
                                fragment = new PeopleNearbyFragmentAds();
                            } else {
                                fragment = new PeopleNearbyFragment();
                            }

                            mShadowView.setVisibility(View.VISIBLE);

                            break;

                        case R.id.navigation_live:

                            mShadowView.setVisibility(View.VISIBLE);
                            fragment = new LiveFragment();

                            break;

                        case R.id.navigation_encounters:
                            fragment = new EncountersFragment();
                            mShadowView.setVisibility(View.GONE);

                            break;

                        case R.id.navigation_profile:

                            fragment = new MyProfileFragment();

                            mShadowView.setVisibility(View.VISIBLE);
                            break;

                    }

                    final FragmentTransaction transaction1 = fragmentManager.beginTransaction();
                    transaction1.replace(R.id.frame_layout, fragment).commit();
                    return true;

                });
    }

    public void checkPhoto(){

        if (mCurrentUser.getProfilePhotos().size() == 0){

            sheetDialog = new BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme);
            sheetDialog.setContentView(R.layout.include_photo_empty);
            sheetDialog.setCancelable(false);
            sheetDialog.setCanceledOnTouchOutside(false);

            Button button = sheetDialog.findViewById(R.id.primary_action);

            if (button != null) {
                button.setOnClickListener(v -> {

                    if (sheetDialog.isShowing()){
                        sheetDialog.dismiss();

                        QuickHelp.goToActivityWithNoClean(this, UploadsActivity.class);
                    }
                });
            }

            if (sheetDialog != null && !sheetDialog.isShowing()){
                sheetDialog.show();
            }

        } else if (mCurrentUser.getColFullName().isEmpty()){

            QuickHelp.showToast(HomeActivity.this, getString(R.string.basic_info_needed), true);
            QuickHelp.goToActivityWithNoClean(HomeActivity.this, BasicInfoActivity.class);

        } else if (mCurrentUser.getColGender().isEmpty()){

            QuickHelp.showToast(HomeActivity.this, getString(R.string.basic_info_needed), true);
            QuickHelp.goToActivityWithNoClean(HomeActivity.this, BasicInfoActivity.class);

        } else if (mCurrentUser.getBirthDate() == null){

            QuickHelp.showToast(HomeActivity.this, getString(R.string.basic_info_needed), true);
            QuickHelp.goToActivityWithNoClean(HomeActivity.this, BasicInfoActivity.class);
        }
    }

    public void isUndoShow(boolean show, CardStackLayoutManager manager, CardStackView cardStackView){

        if (show){
            mIconUndo.setVisibility(View.VISIBLE);

            mIconUndo.setOnClickListener(v -> {

                if (Config.isPremiumEnabled){

                    if (User.getUser().isPremium()){

                        useUndo(manager, cardStackView);

                    } else {

                        QuickHelp.goToActivityWithNoClean(this, PaymentsActivity.class, PaymentsActivity.DATOO_PAYMENT_TYPE, PaymentsActivity.TYPE_DATOO_PREMIUM, PaymentsActivity.DATOO_PREMIUM_TYPE, PaymentsActivity.TYPE_DATOO_PREMIUM_UNDO);
                    }

                } else {

                    useUndo(manager, cardStackView);
                }
            });

        } else {
            mIconUndo.setVisibility(View.GONE);
        }

    }

    public void useUndo(CardStackLayoutManager manager, CardStackView cardStackView){

        RewindAnimationSetting setting = new RewindAnimationSetting.Builder()
                .setDirection(Direction.Bottom)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .build();
        manager.setRewindAnimationSetting(setting);
        cardStackView.rewind();
    }

    public void isUndoShow(boolean show){
        if (!show){
            mIconUndo.setVisibility(View.GONE);
        }
    }

    public void initializeToolBar(int iconLeft, int iconRight, String view){

        mIconUndo.setVisibility(View.GONE);

        mIconLeft.setImageResource(iconLeft);
        mIconRight.setImageResource(iconRight);

        if (view.equals(VIEW_TYPE_NEAR_BY)){

            if (!sharedPrefUtil.getBoolean(Constants.IS_NEARBY_SHOWCASE_SHOWED)){

                popularityShowCase(mIconLeft);
            }

        } else if (view.equals(VIEW_TYPE_ENCOUNTERS)){

            if (!sharedPrefUtil.getBoolean(Constants.IS_ENCOUNTERS_SHOWCASE_SHOWED)){

                LikesShowCase(mIconLeft);
            }

        }

        mIconLeft.setOnClickListener(v -> {

            if (view.equals(VIEW_TYPE_NEAR_BY)){

                if (Config.isNearByNativeAdsActivated){
                    new PeopleNearbyFragmentAds().getIconLeft(this);
                } else {
                    new PeopleNearbyFragment().getIconLeft(this);
                }

            } else if (view.equals(VIEW_TYPE_ENCOUNTERS)){

                new EncountersFragment().getIconLeft(this, mCurrentUser);

            } else if (view.equals(VIEW_TYPE_MY_PROFILE)){

                new MyProfileFragment().getIconLeft(this);

            } else if (view.equals(VIEW_TYPE_CONNECTIONS)){

                new ConnectionsFragment().getIconLeft(this);

            } else if (view.equals(VIEW_TYPE_STREAMING)){

                new LiveFragment().getIconLeft(this);
            }

        });

        mIconRight.setOnClickListener(v -> {

            if (view.equals(VIEW_TYPE_NEAR_BY)){

                if (Config.isNearByNativeAdsActivated){
                    new PeopleNearbyFragmentAds().getIconRight(mCurrentUser, this);
                } else {
                    new PeopleNearbyFragment().getIconRight(mCurrentUser, this);
                }

            } else  if (view.equals(VIEW_TYPE_ENCOUNTERS)){

                if (Config.isEncountersNativeAdsActivated){
                    new EncountersFragmentAds().getIconRight(mCurrentUser, this);
                } else {
                    new EncountersFragment().getIconRight(mCurrentUser, this);
                }


            } else  if (view.equals(VIEW_TYPE_MY_PROFILE)){

                new MyProfileFragment().getIconRight(this);

            } else if (view.equals(VIEW_TYPE_STREAMING)){

                new LiveFragment().getIconRight(this, mCurrentUser);
            }

        });

    }

    public void filterShowCase(View view){

        new GuideView.Builder(this)
                .setTitle(getString(R.string.show_case_filter_title))
                .setContentText(getString(R.string.show_case_filter))
                .setGravity(Gravity.auto) //optional
                .setDismissType(DismissType.anywhere) //optional - default DismissType.targetView
                .setTargetView(view)
                .setContentTextSize(12)//optional
                .setTitleTextSize(12)//optional
                .setGuideListener(view1 -> sharedPrefUtil.saveBoolean(Constants.IS_NEARBY_SHOWCASE_SHOWED, true))
                .build()
                .show();
    }

    public void popularityShowCase(View view){

        new GuideView.Builder(this)
                .setTitle(getString(R.string.show_case_pop_title))
                .setContentText(String.format("%s\n %s, %s", getString(R.string.show_case_popularity), getString(R.string.app_name), getString(R.string.show_case_increase)))
                .setGravity(Gravity.auto) //optional
                .setDismissType(DismissType.anywhere) //optional - default DismissType.targetView
                .setTargetView(view)
                .setContentTextSize(12)//optional
                .setTitleTextSize(12)//optional
                .setGuideListener(view1 -> filterShowCase(mIconRight))
                .build()
                .show();
    }

    public void LikesShowCase(View view){

        new GuideView.Builder(this)
                .setTitle(getString(R.string.show_case_likes_title))
                .setContentText(getString(R.string.encoun_likes_showcase_explain))
                .setGravity(Gravity.auto) //optional
                .setDismissType(DismissType.anywhere) //optional - default DismissType.targetView
                .setTargetView(view)
                .setContentTextSize(12)//optional
                .setTitleTextSize(12)//optional
                .setGuideListener(view1 -> sharedPrefUtil.saveBoolean(Constants.IS_ENCOUNTERS_SHOWCASE_SHOWED, true))
                .build()
                .show();
    }

    public void updateFilterNearby() {

        if (Config.isNearByNativeAdsActivated){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, PeopleNearbyFragmentAds.newInstance()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, PeopleNearbyFragment.newInstance()).commit();
        }
    }

    public void updateFilterEncounters() {

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, EncountersFragment.newInstance()).commit();
    }

    public void updateFilterLive() {

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, LiveFragment.newInstance()).commit();
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();

        } else {

           QuickHelp.onBackPressed(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permiso.getInstance().onRequestPermissionResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onPause() {
        //merlin.unbind();
        super.onPause();

        if (sheetDialog != null && sheetDialog.isShowing()){
            sheetDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Permiso.getInstance().setActivity(this);

        checkPhoto();

        registerConnectable(this);
        registerDisconnectable(this);
        registerBindable(this);

        Application.getInstance().getWorkerThread().connectToRtmService(String.valueOf(mCurrentUser.getUid()));

        if (!merlinsBeard.isConnected()){
            mInternetLayout.setVisibility(View.VISIBLE);
            mNetworkTitle.setText(getString(R.string.error_network_connection_lost));
            mNetworkDesc.setText(getString(R.string.error_connection_non_modal_no_internet_description));
        }


        if (mCurrentUser.getGeoPoint() != null){
            QuickHelp.updateUserLocation(mCurrentUser);
        }

        mCurrentUser.fetchInBackground();

    }

    @Override
    protected void onStart() {
        super.onStart();
        merlin.bind();
    }

    @Override
    protected void onStop() {
        super.onStop();
        merlin.unbind();
    }


    /////////////////// Network Connection old ////////////

    public void Connection(boolean isConnected){

        if (isConnected){

            runOnUiThread(() -> {

                mInternetLayout.setVisibility(View.VISIBLE);
                mNetworkTitle.setText(getString(R.string.network_connecting));
                mNetworkDesc.setText(getString(R.string.connecting_internet_description));

                merlinsBeard.hasInternetAccess(hasAccess -> {

                    Log.v("MERLIN hasAccess", String.valueOf(hasAccess));

                    if (hasAccess){

                        Application.getInstance().getWorkerThread().connectToRtmService(String.valueOf(mCurrentUser.getUid()));

                        mInternetLayout.setVisibility(View.GONE);

                    } else {

                        mInternetLayout.setVisibility(View.VISIBLE);
                        mNetworkTitle.setText(getString(R.string.error_network_connection_lost));
                        mNetworkDesc.setText(getString(R.string.error_connection_non_modal_no_internet_description));
                    }
                });
            });

        } else {

            runOnUiThread(() -> {

                mInternetLayout.setVisibility(View.VISIBLE);
                mNetworkTitle.setText(getString(R.string.error_network_connection_lost));
                mNetworkDesc.setText(getString(R.string.error_connection_non_modal_no_internet_description));
            });
        }
    }

    //@Override
    protected Merlin createMerlin() {
        return new Merlin.Builder()
                .withConnectableCallbacks()
                .withDisconnectableCallbacks()
                .withBindableCallbacks()
                .build(this);
    }

    @Override
    public void onBind(NetworkStatus networkStatus) {

        Log.v("MERLIN STATUS", networkStatus.toString());
    }

    @Override
    public void onConnect() {

        Log.v("MERLIN", "CONNECTED");

        Connection(true);
    }

    @Override
    public void onDisconnect() {

        Log.v("MERLIN", "DISCONNECTED");

        Connection(true);
    }

    protected void registerConnectable(Connectable connectable) {
        merlin.registerConnectable(connectable);
    }

    protected void registerDisconnectable(Disconnectable disconnectable) {
        merlin.registerDisconnectable(disconnectable);
    }

    protected void registerBindable(Bindable bindable) {
        merlin.registerBindable(bindable);
    }
}
