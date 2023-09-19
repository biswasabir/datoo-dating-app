package com.angopapo.datooapp.home.nearby;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.angopapo.datooapp.R;
import com.angopapo.datooapp.adapters.datoo.UsersNearAdapter;
import com.angopapo.datooapp.adapters.datoo.UsersNearSpotLightAdapter;
import com.angopapo.datooapp.app.Application;
import com.angopapo.datooapp.app.Config;
import com.angopapo.datooapp.app.Constants;
import com.angopapo.datooapp.auth.WelcomeActivity;
import com.angopapo.datooapp.helpers.QuickHelp;
import com.angopapo.datooapp.home.HomeActivity;
import com.angopapo.datooapp.home.popularity.PopularityActivity;
import com.angopapo.datooapp.models.datoo.User;
import com.angopapo.datooapp.modules.rangeBarView.OnRangeChangedListener;
import com.angopapo.datooapp.modules.rangeBarView.RangeSeekBar;
import com.angopapo.datooapp.modules.topsheet.TopSheetBehavior;
import com.angopapo.datooapp.modules.topsheet.TopSheetDialog;
import com.angopapo.datooapp.utils.ParseRecyclerQueryAdapter;
import com.angopapo.datooapp.utils.SharedPrefUtil;
import com.angopapo.datooapp.utils.StaggeredGridLayoutManagerWrapper;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.greysonparrelli.permiso.Permiso;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ui.widget.ParseQueryAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PeopleNearbyFragment extends Fragment {

    private static final int REQUEST_CHECK_SETTINGS = 1123;
    private User mCurrentUser;

    private TopSheetDialog sheetDialog;

    private SwipeRefreshLayout swipeRefreshLayout;

    private UsersNearAdapter mUsersNearAdapter;
    private RecyclerView mRecyclerView;

    private LinearLayout mEmptyView, mEmptyLayout, mLoadingLayout, mGetStaredLayout;

    private TextView mErrorDesc, mErrorTitle;

    private ImageView mErrorImage;

    private SharedPrefUtil sharedPrefUtil;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private ArrayList<User> mUsersNearSpotLight;
    private UsersNearSpotLightAdapter mUsersNearSpotLightAdapter;

    public PeopleNearbyFragment() {
        // Required empty public constructor

    }

    public static PeopleNearbyFragment newInstance() {
        return new PeopleNearbyFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle b) {
        super.onViewCreated(view, b);


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUsersNearSpotLight = new ArrayList<>();
        mUsersNearSpotLightAdapter = new UsersNearSpotLightAdapter(getActivity(), mUsersNearSpotLight);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_users_near, container, false);

        swipeRefreshLayout = v.findViewById(R.id.swiperefreshlayout);
        mRecyclerView = v.findViewById(R.id.rvChat);

        mEmptyView = v.findViewById(R.id.empty_view);
        mEmptyLayout = v.findViewById(R.id.empty_layout);
        mLoadingLayout = v.findViewById(R.id.loading);


        mErrorImage = v.findViewById(R.id.image);
        mErrorTitle = v.findViewById(R.id.title);
        mErrorDesc = v.findViewById(R.id.brief);

        mGetStaredLayout = v.findViewById(R.id.nearby_explanation);
        AppCompatButton mGetStartedBtn = v.findViewById(R.id.peopleNearby_explanationButton);

        RecyclerView mRecyclerViewSpotLight = v.findViewById(R.id.spotlight_rv);

        mCurrentUser = User.getUser();
        mCurrentUser.fetchInBackground();

        sharedPrefUtil = new SharedPrefUtil(getContext());

        mGetStaredLayout.setVisibility(View.GONE);

        Permiso.getInstance().setActivity(Objects.requireNonNull(getActivity()));

        if (getActivity() != null) {

            ((HomeActivity) getActivity()).initializeToolBar(QuickHelp.getPopularityLevelIndicator(mCurrentUser), R.drawable.ic_navigation_bar_filter, HomeActivity.VIEW_TYPE_NEAR_BY);
        }

        setHasOptionsMenu(true);

        swipeRefreshLayout.setRefreshing(false);
        setLoading();
        mUsersNearAdapter = new UsersNearAdapter(getQueryFactory(), true, getActivity());
        mUsersNearAdapter.addOnQueryLoadListener(new ParseRecyclerQueryAdapter.OnQueryLoadListener<User>() {
            @Override
            public void onLoaded(List<User> usersNear, ParseException e) {

                if (usersNear != null) {

                    if (usersNear.size() > 0) {

                        if (sharedPrefUtil.getBoolean(Constants.IS_NEARBY_NEW, true)) { //

                            mGetStaredLayout.setVisibility(View.VISIBLE);
                            sharedPrefUtil.saveBoolean(Constants.IS_NEARBY_NEW, false);
                        }

                        hideLoading(true);

                    } else {

                        hideLoading(false);

                        mEmptyLayout.setVisibility(View.VISIBLE);

                        mLoadingLayout.setVisibility(View.GONE);
                        mEmptyView.setVisibility(View.VISIBLE);

                        mErrorImage.setImageResource(R.drawable.ic_users);
                        mErrorTitle.setText(R.string.you_dont_have_any_people_near);
                        mErrorDesc.setText(R.string.no_one_found_update);
                    }
                } else {

                    hideLoading(false);

                    if (e.getCode() == ParseException.CONNECTION_FAILED) {

                        mErrorImage.setImageResource(R.drawable.ic_blocker_large_connection_grey1);
                        mErrorTitle.setText(R.string.not_internet_connection);
                        mErrorDesc.setText(R.string.settings_no_inte);

                    } else if (e.getCode() == ParseException.INVALID_SESSION_TOKEN) {

                        User.logOut();
                        QuickHelp.goToActivityAndFinish(getActivity(), WelcomeActivity.class);

                    } else {

                        mErrorImage.setImageResource(R.drawable.ic_close);
                        mErrorTitle.setText(R.string.error_ocurred);
                        mErrorDesc.setText(e.getLocalizedMessage());

                    }
                }

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onLoading() {

            }
        });


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        createLocationRequest();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                if (!mCurrentUser.isLocationTypeNearby()) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...

                    ParseGeoPoint parseGeoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
                    mCurrentUser.setGeoPoint(parseGeoPoint);
                    mCurrentUser.setHasGeoPoint(true);
                    mCurrentUser.saveInBackground();
                }
            }
        };

        LinearLayoutManager layoutManagerSpotlight = new LinearLayoutManager(getActivity());
        layoutManagerSpotlight.setOrientation(RecyclerView.HORIZONTAL);

        mRecyclerViewSpotLight.setAdapter(mUsersNearSpotLightAdapter);
        mRecyclerViewSpotLight.setItemViewCacheSize(12);
        mRecyclerViewSpotLight.setHasFixedSize(true);
        mRecyclerViewSpotLight.setNestedScrollingEnabled(true);
        mRecyclerViewSpotLight.setBackgroundResource(R.color.white);
        mRecyclerViewSpotLight.setBackgroundColor(Color.WHITE);
        mRecyclerViewSpotLight.setLayoutManager(layoutManagerSpotlight);

        mRecyclerView.setAdapter(mUsersNearAdapter);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManagerWrapper(3, StaggeredGridLayoutManager.VERTICAL);

        mRecyclerView.setItemViewCacheSize(12);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setBackgroundResource(R.color.white);
        mRecyclerView.setBackgroundColor(Color.WHITE);
        mRecyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(() -> {

            swipeRefreshLayout.setRefreshing(true);
            loadUsers(mCurrentUser);

        });

        mGetStartedBtn.setOnClickListener(v1 -> {

            sharedPrefUtil.saveBoolean(Constants.IS_NEARBY_NEW, false);
            mGetStaredLayout.setVisibility(View.GONE);
        });

        updateLocation();

        return v;

    }

    protected void createLocationRequest() {

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void getIconLeft(Activity activity) {

        QuickHelp.goToActivityWithNoClean(activity, PopularityActivity.class);

    }

    public ParseQueryAdapter.QueryFactory getQueryFactory() {

        ParseQueryAdapter.QueryFactory<User> factory = () -> {

            ArrayList<User> userArrayList = new ArrayList<>();
            userArrayList.add(mCurrentUser);

            ParseQuery<User> UsersNearQuery = User.getUserQuery();
            UsersNearQuery.whereNotEqualTo(User.COL_ID, mCurrentUser.getObjectId());
            UsersNearQuery.whereEqualTo(User.COL_HAS_GEO_POINT, true); // Only show users with location
            UsersNearQuery.whereExists(User.COL_PHOTOS); // Only show users with at lest one picture
            UsersNearQuery.whereExists(User.COL_BIRTHDATE); // Only show users with birthday
            UsersNearQuery.whereExists(User.COL_FULL_NAME); // Only show users with name
            UsersNearQuery.whereExists(User.COL_GENDER); // Only show users with gender
            UsersNearQuery.whereGreaterThanOrEqualTo(User.COL_AGE, mCurrentUser.getPrefMinAge()); // Minimum Age
            UsersNearQuery.whereLessThanOrEqualTo(User.COL_AGE, mCurrentUser.getPrefMaxAge()); // Maximum Age
            UsersNearQuery.whereNotEqualTo(User.PRIVACY_ALMOST_INVISIBLE, true);
            UsersNearQuery.whereNotContainedIn(User.BLOCKED_USERS, userArrayList);
            UsersNearQuery.whereNotEqualTo(User.USER_BLOCKED_STATUS, true);

            if (!Config.ShowBlockedUsersOnQuery && mCurrentUser.getBlockedUsers() != null && mCurrentUser.getBlockedUsers().size() > 0) {

                List<String> blockedUserId = new ArrayList<>();

                for (User user : mCurrentUser.getBlockedUsers()) {
                    if (!blockedUserId.contains(user.getObjectId())) {
                        blockedUserId.add(user.getObjectId());
                    }
                }

                UsersNearQuery.whereNotContainedIn(User.COL_ID, blockedUserId);
            }

            UsersNearQuery.whereWithinKilometers(User.COL_GEO_POINT, mCurrentUser.getGeoPoint(), mCurrentUser.getPrefDistance());

            if (!mCurrentUser.getPrefGender().equals(User.GENDER_BOTH)) { // Gender
                UsersNearQuery.whereEqualTo(User.COL_GENDER, mCurrentUser.getPrefGender());
            }

            switch (mCurrentUser.getPrefStatus()) {
                case User.STATUS_ALL:
                case User.STATUS_NEW:

                    UsersNearQuery.orderByAscending(User.KEY_CREATED_AT);

                    break;
                case User.STATUS_ONLINE:

                    UsersNearQuery.orderByAscending(User.KEY_UPDATED_AT);
                    UsersNearQuery.whereGreaterThanOrEqualTo(User.KEY_UPDATED_AT, QuickHelp.getMinutesToOnline());

                    break;

                default:

                    UsersNearQuery.orderByAscending(User.VIP_MOVE_TO_TOP);
                    break;
            }

            UsersNearQuery.setLimit(Config.MaxUsersNearToShow);

            return UsersNearQuery;
        };

        return factory;
    }

    public void getIconRight(User user, Activity activity) {

        sheetDialog = new TopSheetDialog(activity);
        sheetDialog.setContentView(R.layout.layout_nearby_filter);
        sheetDialog.getBehavior().setState(TopSheetBehavior.STATE_EXPANDED);
        sheetDialog.setCancelable(true);
        sheetDialog.show();
        sheetDialog.setCanceledOnTouchOutside(true);

        ProgressBar progressBar = sheetDialog.findViewById(R.id.loading);
        LinearLayout content = sheetDialog.findViewById(R.id.content);
        FrameLayout location = sheetDialog.findViewById(R.id.locationInput);
        TextView cityLabel = sheetDialog.findViewById(R.id.cityLabel);
        TextView ageRange = sheetDialog.findViewById(R.id.rangeBarLabel);
        RadioGroup gender = sheetDialog.findViewById(R.id.gender_radio_group);
        RadioGroup status = sheetDialog.findViewById(R.id.filter_radio_group);
        ImageView closeBtn = sheetDialog.findViewById(R.id.filter_decline);
        ImageView doneBtn = sheetDialog.findViewById(R.id.filter_confirm);
        RangeSeekBar rangeSeekBar = sheetDialog.findViewById(R.id.rangeBar);
        TextView distanceRange = sheetDialog.findViewById(R.id.rangeBarDistanceLabel);
        RangeSeekBar rangeSeekBarDistance = sheetDialog.findViewById(R.id.rangeBar_distance);

        RadioButton genderMale = sheetDialog.findViewById(R.id.radio_male);
        RadioButton genderFemale = sheetDialog.findViewById(R.id.radio_female);
        RadioButton genderBoth = sheetDialog.findViewById(R.id.radio_both);

        RadioButton statusAll = sheetDialog.findViewById(R.id.radio_filter_all);
        RadioButton statusOnline = sheetDialog.findViewById(R.id.radio_filter_online);
        RadioButton statusNew = sheetDialog.findViewById(R.id.radio_filter_new);


        assert cityLabel != null;
        assert content != null;
        assert progressBar != null;

        content.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        // Check if preference location is nearby
        if (!user.isLocationTypeNearby() && !user.getLocation().isEmpty()) {
            // check if user has valid location
            cityLabel.setText(user.getLocation());

        } else {

            cityLabel.setText(Application.getInstance().getBaseContext().getString(R.string.people_nearby));
        }

        if (rangeSeekBar != null) {
            rangeSeekBar.setSeekBarMode(RangeSeekBar.SEEKBAR_MODE_RANGE);
            rangeSeekBar.setRange(Config.MinimumAgeToRegister, Config.MaximumAgeToRegister);
            rangeSeekBar.setValue(user.getPrefMinAge(), user.getPrefMaxAge());

            rangeSeekBar.setTickMarkMode(RangeSeekBar.TRICK_MARK_MODE_NUMBER);
            rangeSeekBar.setTickMarkGravity(RangeSeekBar.TRICK_MARK_GRAVITY_CENTER);
        }

        if (rangeSeekBarDistance != null) {
            rangeSeekBarDistance.setSeekBarMode(RangeSeekBar.SEEKBAR_MODE_SINGLE);
            rangeSeekBarDistance.setRange(Config.MinDistanceBetweenUsers, Config.MaxDistanceBetweenUsers);
            rangeSeekBarDistance.setValue(user.getPrefDistance());

            rangeSeekBarDistance.setTickMarkMode(RangeSeekBar.TRICK_MARK_MODE_NUMBER);
            rangeSeekBarDistance.setTickMarkGravity(RangeSeekBar.TRICK_MARK_GRAVITY_CENTER);
        }


        if (ageRange != null) {
            ageRange.setText(String.format(Locale.US, "%d - %d", user.getPrefMinAge(), user.getPrefMaxAge()));
        }

        if (distanceRange != null) {
            distanceRange.setText(String.format(Locale.US, Application.getInstance().getBaseContext().getString(R.string.distance_range_filter), user.getPrefDistance()));
        }

        assert genderMale != null;
        assert genderFemale != null;
        assert genderBoth != null;
        user.fetchInBackground();

        switch (user.getPrefGender()) {
            case User.GENDER_MALE:

                genderMale.setChecked(true);

                break;
            case User.GENDER_FEMALE:

                genderFemale.setChecked(true);

                break;
            case User.GENDER_BOTH:

                genderBoth.setChecked(true);
                break;
        }

        switch (user.getPrefStatus()) {
            case User.STATUS_ALL:

                assert statusAll != null;
                statusAll.setChecked(true);

                break;
            case User.STATUS_ONLINE:

                assert statusOnline != null;
                statusOnline.setChecked(true);

                break;
            case User.STATUS_NEW:

                assert statusNew != null;
                statusNew.setChecked(true);
                break;
        }

        content.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);


        if (gender != null) {
            gender.setOnCheckedChangeListener((group, checkedId) -> {

                if (checkedId == R.id.radio_male) {

                    user.setPrefGender(User.GENDER_MALE);

                } else if (checkedId == R.id.radio_female) {

                    user.setPrefGender(User.GENDER_FEMALE);

                } else if (checkedId == R.id.radio_both) {

                    user.setPrefGender(User.GENDER_BOTH);

                }
            });
        }

        if (status != null) {
            status.setOnCheckedChangeListener((group, checkedId) -> {

                if (checkedId == R.id.radio_filter_all) {

                    user.setPrefStatus(User.STATUS_ALL);

                } else if (checkedId == R.id.radio_filter_online) {

                    user.setPrefStatus(User.STATUS_ONLINE);

                } else if (checkedId == R.id.radio_filter_new) {

                    user.setPrefStatus(User.STATUS_NEW);

                }
            });
        }


        assert rangeSeekBar != null;
        assert ageRange != null;

        rangeSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {

                ageRange.setText(String.format(Locale.US, "%d - %d", (int) leftValue, (int) rightValue));
                user.setPrefMinAge((int) leftValue);
                user.setPrefMaxAge((int) rightValue);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });

        assert distanceRange != null;
        assert rangeSeekBarDistance != null;

        rangeSeekBarDistance.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {

                distanceRange.setText(String.format(Locale.US, Application.getInstance().getBaseContext().getString(R.string.distance_range_filter), (int) leftValue));
                user.setPrefDistance((int) leftValue);
                ///user.setPrefMaxAge((int) rightValue);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

        });


        if (closeBtn != null) {
            closeBtn.setOnClickListener(v -> {

                if (sheetDialog.isShowing()) {
                    sheetDialog.cancel();
                }
            });
        }

        if (doneBtn != null) {
            doneBtn.setOnClickListener(v -> {
                if (sheetDialog.isShowing()) {
                    sheetDialog.cancel();
                }

                user.saveInBackground();
                user.fetchIfNeededInBackground();

                ((HomeActivity) activity).updateFilterNearby();
            });
        }

        if (location != null) {
            location.setOnClickListener(v -> {
                if (sheetDialog.isShowing()) {
                    sheetDialog.cancel();
                }

                QuickHelp.goToActivityWithNoClean(activity, ManualLocationActivity.class);
            });
        }
    }

    private void loadUsers(User mCurrentUser) {

        if (mCurrentUser.getGeoPoint() != null) {

            mUsersNearAdapter.loadObjects();
            updateLocation();

        } else {

            hideLoading(false);

            mErrorImage.setImageResource(R.drawable.ic_location);
            mErrorTitle.setText(R.string.loca_n_found);
            mErrorDesc.setText(R.string.trying_update);

            if (Build.VERSION.SDK_INT >= 23) {

                checkLastLocation();

            } else {

                getLastLocation();
            }
        }
    }

    private void setLoading() {

        mEmptyLayout.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.VISIBLE);
        mGetStaredLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void hideLoading(boolean isLoaded) {

        if (isLoaded) {
            mEmptyLayout.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);

        } else {
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
        mLoadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Objects.requireNonNull(getActivity()).onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkLastLocation() {

        Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
            @Override
            public void onPermissionResult(Permiso.ResultSet resultSet) {
                if (resultSet.areAllPermissionsGranted()) {

                    getLastLocation();

                } else {

                    if (isAdded()) {

                        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {

                            // Enable Location button here.
                            Toast.makeText(getActivity(), getString(R.string.msg_permission_required), Toast.LENGTH_LONG).show();

                            hideLoading(false);

                            mErrorImage.setImageResource(R.drawable.ic_location);
                            mErrorTitle.setText(R.string.permission_alert);
                            mErrorDesc.setText(R.string.permissin_in_location);
                        });
                    }

                }
            }

            @Override
            public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                Permiso.getInstance().showRationaleInDialog(null,
                        getString(R.string.msg_permission_required),
                        null, callback);
            }
        }, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);


    }

    private void updateLocation() {

        if (!mCurrentUser.isLocationTypeNearby()) {
            return;
        }

        Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
            @SuppressLint("MissingPermission")
            @Override
            public void onPermissionResult(Permiso.ResultSet resultSet) {
                if (resultSet.areAllPermissionsGranted()) {

                    if (mFusedLocationClient == null) {
                        return;
                    }
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(Objects.requireNonNull(getActivity()), location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object

                            ParseGeoPoint parseGeoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
                            mCurrentUser.setGeoPoint(parseGeoPoint);
                            mCurrentUser.setHasGeoPoint(true);
                            mCurrentUser.saveInBackground();
                            mUsersNearAdapter.loadObjects();

                        } else checkLocationSettings();
                    }).addOnFailureListener(e -> {

                        checkLocationSettings();

                    });
                }
            }

            @Override
            public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                Permiso.getInstance().showRationaleInDialog(null,
                        getString(R.string.msg_permission_required),
                        null, callback);
            }
        }, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);


    }

    private void checkLocationSettings() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(getActivity(), locationSettingsResponse -> {
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...

            if (mCurrentUser.isLocationTypeNearby()) {
                startLocationUpdates();
            } else {
                stopLocationUpdates();
            }

        });

        task.addOnFailureListener(getActivity(), e -> {
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        });
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void initializeSpotlight(){

        mUsersNearSpotLight.clear();
        mUsersNearSpotLight.add(mCurrentUser);
        mUsersNearSpotLightAdapter.notifyDataSetChanged();

        ArrayList<User> userArrayList = new ArrayList<>();
        userArrayList.add(mCurrentUser);

        ParseQuery<User> UsersNearQuery = User.getUserQuery();
        UsersNearQuery.whereNotEqualTo(User.COL_ID, mCurrentUser.getObjectId()); // Dont query currentUser
        UsersNearQuery.whereEqualTo(User.COL_HAS_GEO_POINT, true); // Only show users with location
        UsersNearQuery.whereExists(User.COL_PHOTOS); // Only show users with at lest one picture
        UsersNearQuery.whereExists(User.COL_BIRTHDATE); // Only show users with birthday

        UsersNearQuery.whereWithinKilometers(User.COL_GEO_POINT, mCurrentUser.getGeoPoint(), mCurrentUser.getPrefDistance());

        UsersNearQuery.whereNotEqualTo(User.PRIVACY_ALMOST_INVISIBLE, true); // Remove users that are insisible
        UsersNearQuery.whereNotContainedIn(User.BLOCKED_USERS, userArrayList); // Removed all blockers
        UsersNearQuery.whereGreaterThanOrEqualTo(User.VIP_MORE_VISITS, new Date());
        UsersNearQuery.orderByAscending(User.VIP_MORE_VISITS);
        UsersNearQuery.whereNotEqualTo(User.USER_BLOCKED_STATUS, true);

        if (!Config.ShowBlockedUsersOnQuery && mCurrentUser.getBlockedUsers() != null && mCurrentUser.getBlockedUsers().size() > 0){

            List<String> blockedUserId = new ArrayList<>();

            for (User user : mCurrentUser.getBlockedUsers()) {
                if (!blockedUserId.contains(user.getObjectId())) {
                    blockedUserId.add(user.getObjectId());
                }
            }

            UsersNearQuery.whereNotContainedIn(User.COL_ID, blockedUserId);
        }

        UsersNearQuery.findInBackground((objects, e) -> {

            if (objects != null && objects.size() > 0){

                mUsersNearSpotLight.clear();
                mUsersNearSpotLight.add(mCurrentUser);
                mUsersNearSpotLight.addAll(objects);
                mUsersNearSpotLightAdapter.notifyDataSetChanged();

            }
        });
    }


    @SuppressLint("MissingPermission")
    private void getLastLocation(){

        if (!mCurrentUser.isLocationTypeNearby()){
            return;
        }

        if (mFusedLocationClient == null){
            return;
        }

        mFusedLocationClient.getLastLocation().addOnSuccessListener(Objects.requireNonNull(getActivity()), location -> {
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                // Logic to handle location object

                ParseGeoPoint parseGeoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
                mCurrentUser.setGeoPoint(parseGeoPoint);
                mCurrentUser.setHasGeoPoint(true);

                mCurrentUser.saveInBackground(e -> {

                    if (e == null){

                        loadUsers(mCurrentUser);
                        setLoading();

                    } else {

                        hideLoading(false);

                        mErrorImage.setImageResource(R.drawable.ic_location);
                        mErrorTitle.setText(R.string.permission_alert);
                        mErrorDesc.setText(R.string.faailed_try_again);

                    }
                });

            } else checkLocationSettings();
        }).addOnFailureListener(e -> {

            checkLocationSettings();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permiso.getInstance().onRequestPermissionResult(requestCode, permissions, grantResults);

    }

    @Override
    public void onResume() {
        super.onResume();
        Permiso.getInstance().setActivity(Objects.requireNonNull(getActivity()));

        loadUsers(mCurrentUser);

        initializeSpotlight();

        if (mCurrentUser.isLocationTypeNearby()){
            startLocationUpdates();
        } else {
            stopLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    public boolean isInternetAvailable(){
        ConnectivityManager cm =
                (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}