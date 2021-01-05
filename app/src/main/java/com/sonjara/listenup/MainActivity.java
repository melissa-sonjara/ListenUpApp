package com.sonjara.listenup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.sonjara.listenup.database.DatabaseHelper;
import com.sonjara.listenup.database.DatabaseSync;
import com.sonjara.listenup.database.ImageCache;
import com.sonjara.listenup.database.LocationDetails;
import com.sonjara.listenup.database.Service;
import com.sonjara.listenup.map.ISearchFilterable;
import com.sonjara.listenup.map.LocationInfoWindow;
import com.sonjara.listenup.map.LocationMarker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.preference.PreferenceManager;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseSync syncHelper = null;
    private ImageCache m_imageCache = null;

    private double m_mapZoomLevel = 13.0;
    private GeoPoint m_mapCenter = new GeoPoint(3.53, 31.35);
    private Fragment m_currentFragment = null;

    private String m_serviceFilter = "";
    private String m_locationNameFilter = "";

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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

                return true;

            case R.id.menu_item_about:

                handleShowAbout();
                return true;

            default:

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleShowAbout()
    {
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof MapFragment)
        {
            MapFragmentDirections.ActionShowAbout action = MapFragmentDirections.actionShowAbout();
            Navigation.findNavController(fragment.getView()).navigate(action);
        }
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

    public void handleGoToCurrentLocation()
    {
       Fragment fragment = getCurrentFragment();
       if (fragment instanceof MapFragment)
       {
           ((MapFragment)fragment).centerOnCurrentLocation();
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

        String nameFilter = getLocationNameFilter();

        String[] serviceIds = getServiceFilter().split(",");

        for(LocationDetails location : unfilteredLocations)
        {
            if (!"".equals(nameFilter))
            {
                if (!location.name.contains(nameFilter)) continue;
            }

            if (serviceIds.length > 0 && !location.hasService(serviceIds)) continue;

            filtered.add(location);
        }

        return filtered;
    }

}