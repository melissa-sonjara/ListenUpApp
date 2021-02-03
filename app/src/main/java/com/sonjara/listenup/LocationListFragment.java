package com.sonjara.listenup;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.sonjara.listenup.database.LocationDetails;
import com.sonjara.listenup.dummy.DummyContent;
import com.sonjara.listenup.map.ISearchFilterable;

import org.osmdroid.util.GeoPoint;

/**
 * A fragment representing a list of Items.
 */
public class LocationListFragment extends Fragment implements ISearchFilterable,
        LocationDetailsViewAdapter.OnShowLocationDetails,
        LocationDetailsViewAdapter.OnShowMapLocation
{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private LocationDetailsViewAdapter m_adapter = null;
    private ViewSwitcher m_viewSwitcher;
    private TextView m_emptyMessage;
    private RecyclerView m_locationList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LocationListFragment()
    {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static LocationListFragment newInstance(int columnCount)
    {
        LocationListFragment fragment = new LocationListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_location_list_list, container, false);

        m_viewSwitcher = (ViewSwitcher)view.findViewById(R.id.location_list_view_switcher);
        m_locationList = (RecyclerView)view.findViewById(R.id.location_list);
        m_emptyMessage = (TextView)view.findViewById(R.id.location_list_empty_message);

        // Set the adapter
        if (m_locationList instanceof RecyclerView)
        {
            Context context = view.getContext();
            MainActivity activity = (MainActivity)getActivity();

            if (mColumnCount <= 1)
            {
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                m_locationList.setLayoutManager(layoutManager);
                DividerItemDecoration divider = new DividerItemDecoration(m_locationList.getContext(),
                        layoutManager.getOrientation());
                m_locationList.addItemDecoration(divider);
            }
            else
            {
                m_locationList.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            m_adapter = new LocationDetailsViewAdapter(activity.getFilteredLocations());
            m_adapter.setOnShowLocationDetails(this);
            m_adapter.setOnShowMapLocation(this);
            m_locationList.setAdapter(m_adapter);
            updateView();
        }
        return view;
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

    @Override
    public void reapplyFilter()
    {
        MainActivity activity = (MainActivity)getActivity();
        m_adapter.setLocations(activity.getFilteredLocations());
        updateView();
        m_adapter.notifyDataSetChanged();
    }

    private void updateView()
    {
        if (m_adapter.getItemCount() == 0)
        {
            if (m_viewSwitcher.getNextView().getId() == R.id.location_list_empty_placeholder)
            {
                m_viewSwitcher.showNext();
            }
        }
        else
        {
            if (m_viewSwitcher.getNextView().getId() == R.id.location_list)
            {
                m_viewSwitcher.showNext();
            }
        }
    }

    @Override
    public void OnShowMapLocation(LocationDetails location)
    {
        MainActivity activity = (MainActivity)getActivity();
        if (!"".equals(location.latitude) && !"".equals(location.longitude))
        {
            double lat = Double.parseDouble(location.latitude);
            double lng = Double.parseDouble(location.longitude);
            activity.setMapCenter(new GeoPoint(lat, lng));
            activity.setMapZoomLevel(15.0);

            LocationListFragmentDirections.ActionShowMap action = LocationListFragmentDirections.actionShowMap();
            Navigation.findNavController(getView()).navigate(action);
        }
    }

    @Override
    public void OnShowLocationDetails(LocationDetails location)
    {
        LocationListFragmentDirections.ShowLocationDetails action = LocationListFragmentDirections.showLocationDetails();
        action.setLocationId(location.location_id);
        Navigation.findNavController(getView()).navigate(action);
    }


}