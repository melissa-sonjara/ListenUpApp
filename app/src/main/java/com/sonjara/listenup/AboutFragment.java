package com.sonjara.listenup;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutFragment extends Fragment
{


    private Button m_goToWebsite;
    private Button m_dismissButton;

    public AboutFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment AboutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutFragment newInstance()
    {
        return new AboutFragment();
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        m_goToWebsite = (Button)v.findViewById(R.id.go_to_website);
        m_dismissButton = (Button)v.findViewById(R.id.dismiss_about);

        m_goToWebsite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent Getintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://listenup.sonjara.com"));
                startActivity(Getintent);

            }
        });

        m_dismissButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                MainActivity activity = (MainActivity)getActivity();
                Fragment fragment = activity.getCurrentFragment();
                View parent = getView();
                if (parent != null)
                {
                    if (fragment instanceof MapFragment)
                    {
                        AboutFragmentDirections.ActionHideAbout action = AboutFragmentDirections.actionHideAbout();
                        Navigation.findNavController(parent).navigate(action);
                    }
                    else if (fragment instanceof LocationListFragment)
                    {
                        AboutFragmentDirections.AboutHideToList action = AboutFragmentDirections.aboutHideToList();
                        Navigation.findNavController(parent).navigate(action);

                    }
                }
            }
        });
        return v;
    }
}