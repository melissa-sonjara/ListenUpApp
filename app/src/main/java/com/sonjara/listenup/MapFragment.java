package com.sonjara.listenup;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sonjara.listenup.database.DatabaseHelper;
import com.sonjara.listenup.database.DatabaseSyncHelper;
import com.sonjara.listenup.database.LocationDetails;
import com.sonjara.listenup.map.ISearchFilterable;
import com.sonjara.listenup.map.LocationInfoWindow;
import com.sonjara.listenup.map.LocationMarker;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements ISearchFilterable
{

    private MapView mapView = null;

    private LocationInfoWindow locationIW;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MyLocationNewOverlay m_locationOverlay;

    private LinkedList<LocationMarker> m_markers = new LinkedList<LocationMarker>();

    public MapFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2)
    {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        MainActivity activity = (MainActivity)getActivity();

        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView)v.findViewById(R.id.mapView);

        mapView.setTileSource(TileSourceFactory.MAPNIK);

        mapView.setMultiTouchControls(true);
        mapView.setTilesScaledToDpi(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(activity.getMapZoomLevel());
        mapController.setCenter(activity.getMapCenter());

        GpsMyLocationProvider provider = new GpsMyLocationProvider(getContext());
        provider.addLocationSource(LocationManager.GPS_PROVIDER);
        provider.addLocationSource(LocationManager.NETWORK_PROVIDER);

        m_locationOverlay = new MyLocationNewOverlay(provider, mapView);
        m_locationOverlay.enableMyLocation();
        m_locationOverlay.setDrawAccuracyEnabled(true);

        m_locationOverlay.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                Log.i("ListenUp", "I was ran on the first fix");
                FragmentActivity activity = getActivity();
                if (activity != null)
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            GeoPoint myLocation = m_locationOverlay.getMyLocation();
                            if (myLocation != null)
                                Toast.makeText(getContext(), "GPS location acquired", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(getContext(), "Unable to get GPS location at this time", Toast.LENGTH_LONG).show();
                        }
                    });

            }
        });

        mapView.getOverlays().add(m_locationOverlay);

        locationIW = new LocationInfoWindow(R.layout.location_infowindow, mapView, this);

        showLocations();
        return v;
    }

    /**
     * Called when a fragment is first attached to its context.
     * {@link #onCreate(Bundle)} will be called after this.
     *
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        ((MainActivity)getActivity()).fragmentAttached(this);
    }

    public void clearLocations()
    {
        for(LocationMarker marker : m_markers)
        {
            mapView.getOverlays().remove(marker);
        }

        m_markers = new LinkedList<LocationMarker>();
    }

    public void showLocations()
    {
        MainActivity activity = (MainActivity)getActivity();
        List<LocationDetails> locations = activity.getFilteredLocations();

        if (locations == null) return;

        for(LocationDetails location: locations)
        {
            if (location.latitude.equals("") || location.longitude.equals("")) continue;

            double latitude = Double.parseDouble(location.latitude);
            double longitude = Double.parseDouble(location.longitude);

            GeoPoint locationPoint = new GeoPoint(latitude, longitude);
            LocationMarker locationMarker = new LocationMarker(mapView, location);
            locationMarker.setIcon(this.getResources().getDrawable(R.drawable.listenup_marker));
            locationMarker.setPosition(locationPoint);
            locationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            locationMarker.setTitle(location.name);
            locationMarker.setInfoWindow(locationIW);

            m_markers.add(locationMarker);
            mapView.getOverlays().add(locationMarker);
        }
    }

    public void handleLocationDetails(LocationDetails location)
    {
        double currentZoom = mapView.getZoomLevelDouble();
        GeoPoint currentPoint = (GeoPoint)mapView.getMapCenter();
        MainActivity activity = (MainActivity)getActivity();
        activity.setMapZoomLevel(currentZoom);
        activity.setMapCenter(currentPoint);
        MapFragmentDirections.ActionShowLocationDetails action = MapFragmentDirections.actionShowLocationDetails();
        action.setLocationId(location.location_id);
        Navigation.findNavController(getView()).navigate(action);
    }

    public void centerOnCurrentLocation()
    {
        GeoPoint currentLocation = m_locationOverlay.getMyLocation();
        if (currentLocation != null)
        {
            IMapController controller = mapView.getController();
            controller.animateTo(currentLocation);
        }
        else
        {
            Toast.makeText(getContext(), "We are having trouble getting a GPS fix. Please check your app permissions if this keeps happening, and ensure that Location Services are enabled", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void reapplyFilter()
    {
        clearLocations();
        showLocations();
        mapView.refreshDrawableState();
    }

    /**
     * Called when the Fragment is no longer resumed.  This is generally
     * tied to {@link Activity#onPause() Activity.onPause} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onPause()
    {
        super.onPause();
        mapView.onPause();
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume()
    {
        super.onResume();
        mapView.onResume();
    }
}