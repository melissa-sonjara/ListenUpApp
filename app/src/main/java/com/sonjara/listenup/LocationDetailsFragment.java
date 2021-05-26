package com.sonjara.listenup;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sonjara.listenup.database.DatabaseHelper;
import com.sonjara.listenup.database.LocationDetails;

import org.osmdroid.util.GeoPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationDetailsFragment extends Fragment
{
    public LocationDetails getLocation()
    {
        return m_location;
    }

    public void setLocation(LocationDetails location)
    {
        m_location = location;
    }

    private LocationDetails m_location;

    public LocationDetailsFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     * @return A new instance of fragment LocationDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocationDetailsFragment newInstance()
    {
        LocationDetailsFragment fragment = new LocationDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_location_details, container, false);
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
        ((MainActivity)getActivity()).setCurrentFragment(this);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            LocationDetailsFragmentArgs args = LocationDetailsFragmentArgs.fromBundle(bundle);
            DatabaseHelper db = DatabaseHelper.getInstance();
            LocationDetails location = db.getLocation(args.getLocationId());
            setLocation(location);

            View v = getView();
            TextView title = v.findViewById(R.id.location_details_name);
            TextView operatedBy = v.findViewById(R.id.operated_by);
            TextView serviceProvided = v.findViewById(R.id.services_provided);
            TextView address = v.findViewById(R.id.address);
            TextView addressTitle = v.findViewById(R.id.address_title);
            TextView hoursOfOperation = v.findViewById(R.id.hours_of_operation);
            TextView contactName = v.findViewById(R.id.contact_name);
            TextView contactPhoneTitle = v.findViewById(R.id.contact_phone_title);
            TextView contactPhone = v.findViewById(R.id.contact_phone_number);
            TextView notes = v.findViewById(R.id.location_notes);

            Button closeButton = v.findViewById(R.id.location_details_close_button);
            title.setText(location.name);
            operatedBy.setText(location.organization_name);
            serviceProvided.setText(location.getServicesText());

            if (location.hours_of_service == null || "".equals(location.hours_of_service))
            {
                hoursOfOperation.setText("Not Specified");
            }
            else
            {
                hoursOfOperation.setText(location.hours_of_service);
            }

            if (location.contact_name == null || "".equals(location.contact_name))
            {
                contactName.setText("Not Provided");
            }
            else
            {
                contactName.setText(location.contact_name);
            }

            if (location.address == null || "".equals(location.address))
            {
                addressTitle.setVisibility(View.GONE);
                address.setVisibility(View.GONE);
            }
            else
            {
                addressTitle.setVisibility(View.VISIBLE);
                address.setVisibility(View.VISIBLE);
                address.setText(location.address);
            }

            if (location.contact_phone == null || "".equals(location.contact_phone))
            {
                contactPhone.setVisibility(View.GONE);
                contactPhoneTitle.setVisibility(View.GONE);
            }
            else
            {
                contactPhone.setVisibility(View.VISIBLE);
                contactPhoneTitle.setVisibility(View.VISIBLE);
                contactPhone.setText(location.contact_phone);
            }

            notes.setText(location.notes);

            final LocationDetailsFragment me = this;
            closeButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    me.closeDetailsWindow();
                }
            });
        }
    }

    void closeDetailsWindow()
    {
        MainActivity activity = (MainActivity)getActivity();
        LocationDetails location = getLocation();
        if (!"".equals(location.latitude) && !"".equals(location.longitude))
        {
            double lat = Double.parseDouble(location.latitude);
            double lng = Double.parseDouble(location.longitude);
            activity.setMapCenter(new GeoPoint(lat, lng));
            activity.setMapZoomLevel(15.0);
        }

        LocationDetailsFragmentDirections.ActionHideLocationDetails action = LocationDetailsFragmentDirections.actionHideLocationDetails();
        Navigation.findNavController(getView()).navigate(action);
    }
}