package com.sonjara.listenup;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sonjara.listenup.database.LocationDetails;
import com.sonjara.listenup.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class LocationDetailsViewAdapter extends RecyclerView.Adapter<LocationDetailsViewAdapter.ViewHolder>
{
    public interface OnShowMapLocation
    {
        public void OnShowMapLocation(LocationDetails location);
    }

    public interface OnShowLocationDetails
    {
        public void OnShowLocationDetails(LocationDetails location);
    }

    private OnShowMapLocation m_onShowMapLocation = null;
    private OnShowLocationDetails m_onShowLocationDetails = null;

    public OnShowMapLocation getOnShowMapLocation()
    {
        return m_onShowMapLocation;
    }

    public void setOnShowMapLocation(OnShowMapLocation onShowMapLocationHandler)
    {
        m_onShowMapLocation = onShowMapLocationHandler;
    }

    public OnShowLocationDetails getOnShowLocationDetails()
    {
        return m_onShowLocationDetails;
    }

    public void setOnShowLocationDetails(OnShowLocationDetails onShowLocationDetails)
    {
        m_onShowLocationDetails = onShowLocationDetails;
    }

    private List<LocationDetails> m_locations;

    public List<LocationDetails> getLocations()
    {
        return m_locations;
    }

    public void setLocations(List<LocationDetails> locations)
    {
        m_locations = locations;
    }
    public LocationDetailsViewAdapter(List<LocationDetails> items)
    {
        m_locations = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_location_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        holder.m_location = m_locations.get(position);
        holder.m_name.setText(m_locations.get(position).name);
        holder.m_address.setText(m_locations.get(position).address);
        holder.getView().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                OnShowLocationDetails handler = getOnShowLocationDetails();
                if (handler != null)
                {
                    handler.OnShowLocationDetails(holder.m_location);
                }
            }
        });

        holder.m_markerIcon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                OnShowMapLocation handler = getOnShowMapLocation();
                if (handler != null)
                {
                    handler.OnShowMapLocation(holder.m_location);
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return m_locations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View m_view;
        public final ImageView m_markerIcon;
        public final TextView m_name;
        public final TextView m_address;
        public LocationDetails m_location;

        public ViewHolder(View view)
        {
            super(view);
            m_view = view;
            m_markerIcon = (ImageView) view.findViewById(R.id.location_marker_icon);
            m_name = (TextView) view.findViewById(R.id.location_name);
            m_address = (TextView) view.findViewById(R.id.location_address);
        }

        public View getView()
        {
            return m_view;
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + m_name.getText() + "'";
        }
    }
}