package com.sonjara.listenup.map;

import android.content.Context;

import com.sonjara.listenup.database.LocationDetails;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class LocationMarker extends Marker
{
    private LocationDetails m_location;

    public LocationDetails getLocation()
    {
        return m_location;
    }

    public LocationMarker(MapView mapView, LocationDetails location)
    {
        super(mapView);
        m_location = location;
    }

    public LocationMarker(MapView mapView, Context resourceProxy, LocationDetails location)
    {
        super(mapView, resourceProxy);
        m_location = location;
    }
}
