package com.sonjara.listenup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.sonjara.listenup.database.DatabaseHelper;
import com.sonjara.listenup.database.DatabaseSync;
import com.sonjara.listenup.database.LocationDetails;
import com.sonjara.listenup.database.Service;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MapView mapView = null;
    private DatabaseSync syncHelper = null;

    public DatabaseSync getSyncHelper()
    {
        return syncHelper;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
       // Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        DatabaseHelper     helper  = new DatabaseHelper(this);
        syncHelper = new DatabaseSync(this, helper);
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

        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        //mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(13);
        GeoPoint startPoint = new GeoPoint(3.53, 31.35);
        mapController.setCenter(startPoint);

        showLocations();
    }

    public void showLocations()
    {
        List<LocationDetails> locations = syncHelper.getDatabaseHelper().getLocations();

        if (locations == null) return;

        for(LocationDetails location: locations)
        {
            if (location.latitude.equals("") || location.longitude.equals("")) continue;

            double latitude = Double.parseDouble(location.latitude);
            double longitude = Double.parseDouble(location.longitude);

            GeoPoint locationPoint = new GeoPoint(latitude, longitude);
            Marker locationMarker = new Marker(mapView);
            locationMarker.setPosition(locationPoint);
            locationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            locationMarker.setIcon(null);
            locationMarker.setTitle(location.name);

            mapView.getOverlays().add(locationMarker);
        }
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

            case R.id.action_settings:

                return true;

            default:

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        mapView.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        mapView.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    public void handleSyncMenuItem()
    {
        SyncDialog dialog = new SyncDialog();
        dialog.show(this.getSupportFragmentManager(), "SyncDialog");
    }
}