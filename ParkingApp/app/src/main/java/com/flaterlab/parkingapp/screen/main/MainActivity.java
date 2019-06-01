package com.flaterlab.parkingapp.screen.main;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.ColorStateList;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.flaterlab.parkingapp.R;
import com.flaterlab.parkingapp.adapter.PolygonRVAdapter;
import com.flaterlab.parkingapp.model.ParkingZone;
import com.flaterlab.parkingapp.model.Polygon;
import com.flaterlab.parkingapp.screen.ParkingApp;
import com.flaterlab.parkingapp.screen.fragments.MapFragment;
import com.flaterlab.parkingapp.util.BaseActivity;
import com.flaterlab.parkingapp.util.DialogUtils;
import com.flaterlab.parkingapp.view.AnimatedRecyclerView;

import java.util.List;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private MainViewModel mViewModel;
    private List<ParkingZone> mParkingZones;
    private ParkingZone mCurrentZone;
    private Location mCurrentLocation;
    private Polygon mCurrentPolygon;
    private AnimatedRecyclerView mListLayout;
    private PolygonRVAdapter mAdapter;
    private TextView tvCurrentZone, tvCurrentPolygon, tvTimer;
    private ImageView ivStatusParked;
    private ImageButton ibtSearch, ibtPolygonList;
    private ColorStateList mDefaultTextColor;
    private CheckBox chbAccuracy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initViewModel();
    }

    private void initUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvCurrentZone = toolbar.findViewById(R.id.tv_parking_zone);
        tvCurrentPolygon = toolbar.findViewById(R.id.tv_polygon);
        tvTimer = toolbar.findViewById(R.id.tv_timer);
        ivStatusParked = toolbar.findViewById(R.id.iv_parking_status);
        mDefaultTextColor = tvCurrentPolygon.getTextColors();

        mListLayout = findViewById(R.id.rv_list);
        initListLayout();

        ibtSearch = findViewById(R.id.ibt_search);
        ibtSearch.setOnClickListener(v -> {
            if (mCurrentZone != null) {
                log(TAG, mCurrentZone.getPolygons().get(0).isFree() + "");
                mAdapter.update(mCurrentZone.getFreePolygons(mCurrentLocation));
                mListLayout.setVisible(true);
            }
        });

        ibtPolygonList = findViewById(R.id.ibt_list);
        ibtPolygonList.setOnClickListener(v -> {
            sortAndUpdatePolygons();
            mListLayout.setVisible(true);
        });

        chbAccuracy = findViewById(R.id.chb_accuracy);
        chbAccuracy.setOnCheckedChangeListener((buttonView, isChecked) -> {
           mViewModel.setAccuracy(isChecked);
        });

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayShowTitleEnabled(false);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().add(R.id.ll_main_container, new MapFragment()).commit();
    }

    private void initListLayout() {
        PolygonRVAdapter.OnPolygonClickedListener listener = polygon -> {
            mListLayout.toggle();
            mViewModel.onPolygonClicked(polygon);
        };
        mAdapter = new PolygonRVAdapter(this, listener);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mListLayout.setLayoutManager(layoutManager);
        mListLayout.setHasFixedSize(true);
        mListLayout.setAdapter(mAdapter);
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.init(((ParkingApp) getApplication()).getZoneRepository());

        initObservers();
    }

    private void initObservers() {

        mViewModel.getCurrentLocation().observe(this, location -> {
            mCurrentLocation = location;
        });

        mViewModel.getCurrentPolygon().observe(this, polygon -> {
            mCurrentPolygon = polygon;
            if (polygon != null) {
                tvCurrentPolygon.setText(String.format(getString(R.string.main_title_polygon), polygon.getId()));
            }
        });

        mViewModel.getCurrentParkingZone().observe(this, zone -> {
            if (zone != null) {
                mCurrentZone = zone;
                tvCurrentZone.setText(zone.getName());

                sortAndUpdatePolygons();
            }
        });

        mViewModel.getAllParkingZones().observe(this, parkingZones -> {
            mParkingZones = parkingZones;
        });

        mViewModel.getStartingCountingTimer().observe(this, time -> {
            if (time != null) {
                tvTimer.setText(String.valueOf(time));
            } else {
                if (mCurrentPolygon != null) {
                    setParkedStatus();
                }
                tvTimer.setText("");
            }
        });

        mViewModel.getParkingResult().observe(this, result -> {
            setNotParkedStatus();
            if (result != null) {
                DialogUtils.showParkingResultDialog(this, result);
            }
        });
    }

    private void sortAndUpdatePolygons() {
        if (mCurrentZone != null) {
            log(TAG, mCurrentZone + "  " + mCurrentLocation);
            mAdapter.update(mCurrentZone.getSortedPolygons(mCurrentLocation));
        }
    }

    private void setParkedStatus() {
        tvCurrentZone.setTextColor(getResources().getColor(R.color.colorMainGreen));
        tvCurrentPolygon.setTextColor(getResources().getColor(R.color.colorMainGreen));
        ivStatusParked.setVisibility(View.VISIBLE);
    }

    private void setNotParkedStatus() {
        tvCurrentZone.setTextColor(mDefaultTextColor);
        tvCurrentPolygon.setTextColor(mDefaultTextColor);
        ivStatusParked.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mListLayout.getVisibility() == View.VISIBLE) {
            mListLayout.setVisible(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_history) {

        } else if (id == R.id.nav_exit) {
            super.onBackPressed();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
