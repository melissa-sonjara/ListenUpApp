package com.sonjara.listenup.map;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sonjara.listenup.MainActivity;
import com.sonjara.listenup.MapFragment;
import com.sonjara.listenup.R;
import com.sonjara.listenup.database.LocationDetails;

import org.osmdroid.api.IMapView;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayWithIW;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

public class LocationInfoWindow extends InfoWindow
{
    protected LocationMarker m_selectedMarker;
    protected MapFragment m_mapFragment;
    /**
     * @param layoutResId layout that must contain these ids: bubble_title,bubble_description,
     *                    bubble_subdescription, bubble_image
     * @param mapView
     */
    public LocationInfoWindow(int layoutResId, MapView mapView, MapFragment fragment)
    {
        super(layoutResId, mapView);
        m_mapFragment = fragment;
    }

    @Override
    public void onOpen(Object item)
    {
        m_selectedMarker = (LocationMarker) item;

        if (mView == null)
        {
            Log.w(IMapView.LOGTAG, "Error trapped, MarkerInfoWindow.open, mView is null!");
            return;
        }

        LocationDetails location = m_selectedMarker.getLocation();

        if (mView==null) {
            Log.w(IMapView.LOGTAG, "Error trapped, BasicInfoWindow.open, mView is null!");
            return;
        }

        TextView title=((TextView)mView.findViewById(R.id.location_iw_title));
        TextView description=((TextView)mView.findViewById(R.id.location_iw_description));
        TextView subdescription = ((TextView)mView.findViewById(R.id.location_iw_subdescription));

        title.setText(location.name);

        if (location.address != null && !("".equals(location.address)))
        {
            description.setText(location.address);
            description.setVisibility(View.VISIBLE);
        }
        else
        {
            description.setVisibility(View.GONE);
        }

        String services = location.getServicesText();
        if (!("".equals(services)))
        {
            subdescription.setVisibility(View.VISIBLE);
            subdescription.setText(services);
        }
        else
        {
            subdescription.setVisibility(View.GONE);
        }

        LinearLayout layout = ((LinearLayout)mView.findViewById(R.id.location_iw_layout));
        layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                m_mapFragment.handleLocationDetails(m_selectedMarker.getLocation());
            }
        });
    }

    @Override public void onClose() {
        m_selectedMarker = null;
        //by default, do nothing else
    }
}
