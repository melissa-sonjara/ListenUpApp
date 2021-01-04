package com.sonjara.listenup;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SearchDialog extends DialogFragment
{
    private RecyclerView m_checkList = null;
    private EditText m_location_name_search = null;
    private Button m_searchButton = null;

    private ServiceChecklistAdapter m_serviceChecklistAdapter = null;

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null. This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p>A default View can be returned by calling {@link #Fragment(int)} in your
     * constructor. Otherwise, this method returns null.
     *
     * <p>It is recommended to <strong>only</strong> inflate the layout in this method and move
     * logic that operates on the returned View to {@link #onViewCreated(View, Bundle)}.
     *
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dialog_search, container, false);

        final MainActivity activity = (MainActivity)this.getActivity();
        m_serviceChecklistAdapter = new ServiceChecklistAdapter(activity);

        m_location_name_search = (EditText)view.findViewById(R.id.search_name);
        m_checkList = (RecyclerView)view.findViewById(R.id.service_search_checklist);
        m_searchButton = (Button)view.findViewById(R.id.search_button);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        m_checkList.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration  = new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL);
        m_checkList.addItemDecoration(dividerItemDecoration);
        m_checkList.setAdapter(m_serviceChecklistAdapter);

        final SearchDialog me = this;

        m_searchButton.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v)
            {
                activity.setServiceFilter(me.getServiceFilter());
                activity.setLocationNameFilter(me.m_location_name_search.getText().toString());
                activity.applyFilter();

                getDialog().dismiss();
            }
        });
        return view;
    }

    private String getServiceFilter()
    {
        return m_serviceChecklistAdapter.getServiceIdFilter();
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
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}
