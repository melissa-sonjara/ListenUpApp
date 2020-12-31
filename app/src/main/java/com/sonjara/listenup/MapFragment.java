package com.sonjara.listenup;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sonjara.listenup.database.DatabaseHelper;
import com.sonjara.listenup.database.DatabaseSyncHelper;
import com.sonjara.listenup.database.LocationDetails;
import com.sonjara.listenup.map.LocationInfoWindow;
import com.sonjara.listenup.map.LocationMarker;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment
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
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView)v.findViewById(R.id.mapView);

        mapView.setTileSource(TileSourceFactory.MAPNIK);

        mapView.setMultiTouchControls(true);
        mapView.setTilesScaledToDpi(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(13);
        GeoPoint startPoint = new GeoPoint(3.53, 31.35);
        mapController.setCenter(startPoint);

        MainActivity activity = (MainActivity)getActivity();
        locationIW = new LocationInfoWindow(R.layout.location_infowindow, mapView, this);

        showLocations();
        return v;
    }

    public void showLocations()
    {
        DatabaseHelper db = DatabaseHelper.getInstance();

        List<LocationDetails> locations = db.getLocations();

        if (locations == null) return;

        for(LocationDetails location: locations)
        {
            if (location.latitude.equals("") || location.longitude.equals("")) continue;

            double latitude = Double.parseDouble(location.latitude);
            double longitude = Double.parseDouble(location.longitude);

            GeoPoint locationPoint = new GeoPoint(latitude, longitude);
            Marker locationMarker = new LocationMarker(mapView, location);
            locationMarker.setIcon(this.getResources().getDrawable(R.drawable.listenup_marker));
            locationMarker.setPosition(locationPoint);
            locationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            locationMarker.setTitle(location.name);
            locationMarker.setInfoWindow(locationIW);

            mapView.getOverlays().add(locationMarker);
        }
    }

    public void handleLocationDetails(LocationDetails location)
    {
        MapFragmentDirections.ActionShowLocationDetails action = MapFragmentDirections.actionShowLocationDetails();
        action.setLocationId(location.location_id);
        Navigation.findNavController(getView()).navigate(action);
    }

}