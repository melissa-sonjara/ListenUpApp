package com.sonjara.listenup;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.sonjara.listenup.database.DatabaseHelper;
import com.sonjara.listenup.database.DatabaseSync;
import com.sonjara.listenup.database.ImageCache;
import com.sonjara.listenup.database.LocationDetails;
import com.sonjara.listenup.map.ISearchFilterable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private DatabaseSync syncHelper = null;
    private ImageCache m_imageCache = null;

    private IssueViewModel m_issueViewModel;

    private double m_mapZoomLevel = 13.0;
    private GeoPoint m_mapCenter = new GeoPoint(3.53, 31.35);
    private Fragment m_currentFragment = null;

    private String m_serviceFilter = "";
    private String m_locationNameFilter = "";

    private FloatingActionButton m_issueButton;

    private Menu m_menu = null;

    public Menu getMenu()
    {
        return m_menu;
    }

    private void setMenu(Menu menu)
    {
        m_menu = menu;
    }

    public String getServiceFilter()
    {
        return m_serviceFilter;
    }

    public void setServiceFilter(String serviceFilter)
    {
        m_serviceFilter = serviceFilter;
    }

    public String getLocationNameFilter()
    {
        return m_locationNameFilter;
    }

    public void setLocationNameFilter(String locationNameFilter)
    {
        m_locationNameFilter = locationNameFilter;
    }

    public Fragment getCurrentFragment()
    {
        return m_currentFragment;
    }

    public void setCurrentFragment(Fragment currentFragment)
    {
        m_currentFragment = currentFragment;
    }

    public double getMapZoomLevel()
    {
        return m_mapZoomLevel;
    }

    public void setMapZoomLevel(double mapZoomLevel)
    {
        m_mapZoomLevel = mapZoomLevel;
    }

    public GeoPoint getMapCenter()
    {
        return m_mapCenter;
    }

    public void setMapCenter(GeoPoint mapCenter)
    {
        m_mapCenter = mapCenter;
    }

    public DatabaseSync getSyncHelper()
    {
        return syncHelper;
    }
    public ImageCache getImageCache() { return m_imageCache; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
       // Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        m_imageCache = new ImageCache(this);
        m_serviceFilter = "1";

        DatabaseHelper     helper  = new DatabaseHelper(this);
        syncHelper = new DatabaseSync(this, helper, m_imageCache);
        //syncHelper.sync();

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        final MainActivity me = this;

        m_issueViewModel = new ViewModelProvider(this).get(IssueViewModel.class);

        m_issueButton = findViewById(R.id.fab);
        m_issueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(me, R.id.nav_host_fragment).navigate(R.id.action_global_issue_list);
                hideIssueButton();
            }
        });

        if (isFirstRun())
        {
            startActivity(new Intent(this, ListenUpIntro.class));
        }

        if (getLastSyncTime() == null)
        {
            handleSyncMenuItem();
        }
    }

    public void hideIssueButton()
    {
        m_issueButton.hide();
    }

    public void showIssueButton()
    {
        m_issueButton.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id)
        {
            case R.id.sync:

                handleSyncMenuItem();
                return true;

            case R.id.menu_item_search:
                handleSearchMenuItem();
                return true;

            case R.id.menu_item_recenter:

                handleGoToCurrentLocation();
                return true;

            case R.id.action_settings:

                handleSettingsDialog();
                return true;

            case R.id.menu_item_location_list:
                handleShowLocationList();
                return true;

            case R.id.menu_item_map:
                handleShowMap();
                return true;

            case R.id.menu_item_about:

                handleShowAbout();
                return true;

            case R.id.menu_item_website:
                handleGoToWebsite();
                return true;

            case R.id.menu_show_tutorial:
                handleShowTutorial();
                return true;

            default:

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleShowTutorial()
    {
        startActivity(new Intent(this, ListenUpIntro.class));
    }

    private void handleGoToWebsite()
    {
        Intent Getintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://listenup.sonjara.com"));
        startActivity(Getintent);
    }

    private void handleSettingsDialog()
    {
        SettingsDialog dialog = new SettingsDialog();
        dialog.show(this.getSupportFragmentManager(), "SettingsDialog");
    }

    private void handleShowMap()
    {
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_global_show_map);
        showIssueButton();

    }

    private void handleShowLocationList()
    {

        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_global_show_location_list);
        showIssueButton();
    }

    private void handleShowAbout()
    {
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_global_show_about);
    }

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        //mapView.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        //mapView.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    /**
     * Called after {@link #onStop} when the current activity is being
     * re-displayed to the user (the user has navigated back to it).  It will
     * be followed by {@link #onStart} and then {@link #onResume}.
     *
     * <p>For activities that are using raw {@link Cursor} objects (instead of
     * creating them through
     * {@link #managedQuery(Uri, String[], String, String[], String)},
     * this is usually the place
     * where the cursor should be requeried (because you had deactivated it in
     * {@link #onStop}.
     *
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onStop
     * @see #onStart
     * @see #onResume
     */
    @Override
    protected void onRestart()
    {
        super.onRestart();

        if (isNetworkAvailable())
        {
            DatabaseHelper db = DatabaseHelper.getInstance();
            if (db.submissionsWaiting() > 0)
            {
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_global_issue_list);
                hideIssueButton();
            }
        }
    }

    public void handleSyncMenuItem()
    {
        SyncDialog dialog = new SyncDialog();
        dialog.show(this.getSupportFragmentManager(), "SyncDialog");
    }

    public void handleSearchMenuItem()
    {
        SearchDialog dialog = new SearchDialog();
        dialog.show(this.getSupportFragmentManager(), "SearchDialog");
    }

    public void handleLogin()
    {
        LoginDialog dialog = new LoginDialog();
        dialog.show(this.getSupportFragmentManager(), "LoginDialog");
    }

    public void showLoginDialog()
    {
        LoginDialog dialog = new LoginDialog();
        dialog.show(this.getSupportFragmentManager(), "LoginDialog");
    }

    public String getToken()
    {
        return getPreferences(Context.MODE_PRIVATE).getString("token", null);
    }

    public Date getTokenExpiry()
    {
        String dateStr = getPreferences(Context.MODE_PRIVATE).getString("token_expiry", null);
        if (dateStr == null) return null;

        SimpleDateFormat sdf = new SimpleDateFormat(MainActivity.DATE_PATTERN);
        try
        {
            Date d = sdf.parse(dateStr);
            return d;
        }
        catch (ParseException e)
        {
            return null;
        }
    }

    public void setToken(String token, Date expiry)
    {
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("token", token);

        if (expiry != null)
        {
            SimpleDateFormat sdf = new SimpleDateFormat(MainActivity.DATE_PATTERN);

            String dateStr = sdf.format(expiry);
            editor.putString("token_expiry", dateStr);
        }
        editor.apply();
    }

    public void clearToken()
    {
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("token", null);
        editor.putString("token_expiry", null);
        editor.apply();
    }

    public Date getLastSyncTime()
    {
        String dateStr = getPreferences(Context.MODE_PRIVATE).getString("last_sync", null);
        if (dateStr == null) return null;

        SimpleDateFormat sdf = new SimpleDateFormat(MainActivity.DATE_PATTERN);
        try
        {
            Date d = sdf.parse(dateStr);
            return d;
        }
        catch (ParseException e)
        {
            return null;
        }
    }

    public void setLastSyncTime(Date date)
    {

        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        SimpleDateFormat sdf = new SimpleDateFormat(MainActivity.DATE_PATTERN);

        String dateStr = sdf.format(date);
        editor.putString("last_sync", dateStr);
        editor.apply();
    }

    public boolean isFirstRun()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean intro_run = preferences.getBoolean("intro_run", false);
        return !intro_run;
    }

    public void handleGoToCurrentLocation()
    {
       Fragment fragment = getCurrentFragment();
       if (fragment instanceof MapFragment)
       {
           ((MapFragment)fragment).centerOnCurrentLocation();
       }
    }

    public void fragmentAttached(Fragment fragment)
    {
        Menu menu = getMenu();
        setCurrentFragment(fragment);
        if (menu != null)
        {
            if (fragment instanceof MapFragment)
            {
                menu.findItem(R.id.menu_item_map).setVisible(false);
                menu.findItem(R.id.menu_item_location_list).setVisible(true);
            }
            else if (fragment instanceof LocationListFragment)
            {
                menu.findItem(R.id.menu_item_map).setVisible(true);
                menu.findItem(R.id.menu_item_location_list).setVisible(false);
            }
        }
    }

    public void applyFilter()
    {
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof ISearchFilterable)
        {
            ((ISearchFilterable)fragment).reapplyFilter();
        }
    }

    public List<LocationDetails> getFilteredLocations()
    {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance();
        List<LocationDetails> unfilteredLocations = dbHelper.getLocations();

        LinkedList<LocationDetails> filtered = new LinkedList<>();

        if (unfilteredLocations == null) return filtered;

        String nameFilter = getLocationNameFilter().toLowerCase();

        String[] serviceIds = getServiceFilter().split(",");

        for(LocationDetails location : unfilteredLocations)
        {
            if (!"".equals(nameFilter))
            {
                if (!location.name.toLowerCase().contains(nameFilter) &&
                    !location.address.toLowerCase().contains(nameFilter)) continue;
            }

            if (serviceIds.length > 0 && !location.hasService(serviceIds)) continue;

            filtered.add(location);
        }

        return filtered;
    }
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    private LocationManager m_locationManager = null;
    private boolean m_isGPSEnabled = false;
    private boolean m_isNetworkEnabled = false;
    private boolean m_canGetLocation = false;
    private Location m_location = null;
    private double m_latitude;
    private double m_longitude;

    public Location getLocation() {
        try {
            m_locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

            // getting GPS status
            m_isGPSEnabled = m_locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            m_isNetworkEnabled = m_locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!m_isGPSEnabled && !m_isNetworkEnabled)
            {
                // no network provider is enabled
            }
            else
            {
                this.m_canGetLocation = true;
                // First get location from Network Provider
                if (m_isNetworkEnabled) {
                    //check the network permission
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                    }

                    Log.d("Network", "Network");
                    if (m_locationManager != null)
                    {
                        m_location = m_locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (m_location != null)
                        {
                            m_latitude = m_location.getLatitude();
                            m_longitude = m_location.getLongitude();
                        }
                    }
                }

                // if GPS Enabled get lat/long using GPS Services
                if (m_isGPSEnabled)
                {
                    if (m_location == null)
                    {
                        //check the network permission
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                        }

                        Log.d("GPS Enabled", "GPS Enabled");
                        if (m_locationManager != null)
                        {
                            m_location = m_locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if (m_location != null)
                            {
                                m_latitude = m_location.getLatitude();
                                m_longitude = m_location.getLongitude();
                            }
                        }
                    }
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return m_location;
    }

    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}