package com.sonjara.listenup;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.sonjara.listenup.database.DatabaseHelper;
import com.sonjara.listenup.database.Issue;

import java.sql.SQLException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IssueForm4Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IssueForm4Fragment extends Fragment
{
    private RecyclerView m_areaChecklist;

    private AreaChecklistAdapter m_areaChecklistAdapter;

    private Button m_issueNextButton;
    private Button m_issuePrevButton;
    private Switch m_tagLocationSwitch;

    public IssueForm4Fragment()
    {
        // Required empty public constructor
    }


    public static IssueForm4Fragment newInstance()
    {
        IssueForm4Fragment fragment = new IssueForm4Fragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_issue_form_4, container, false);

        m_areaChecklist = (RecyclerView)view.findViewById(R.id.issue_areas);
        m_issuePrevButton = (Button)view.findViewById(R.id.issue_form_4_previous);
        m_issueNextButton = (Button)view.findViewById(R.id.issue_form_4_next);
        m_tagLocationSwitch = (Switch)view.findViewById(R.id.issue_tag_location);
        DatabaseHelper db = DatabaseHelper.getInstance();


        LinearLayoutManager layoutManager = new LinearLayoutManager(m_areaChecklist.getContext());
        m_areaChecklist.setLayoutManager(layoutManager);

        m_areaChecklistAdapter = new AreaChecklistAdapter(db.getAreaHierarchy(db.getCurrentOperationalAreaID()));
        m_areaChecklist.setAdapter(m_areaChecklistAdapter);


        m_issueNextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveAndComplete();
            }
        });

        m_issuePrevButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                prevStep();
            }
        });
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        populateView();
    }

    private void populateView()
    {
        IssueViewModel issueViewModel = new ViewModelProvider(getActivity()).get(IssueViewModel.class);
        Issue issue = issueViewModel.getIssue();
        m_areaChecklistAdapter.setAreaIds(issue.areas);
    }


    private void prevStep()
    {
        try
        {
            saveData();
            IssueForm4FragmentDirections.ActionIssueForm4Prev action = IssueForm4FragmentDirections.actionIssueForm4Prev();
            Navigation.findNavController(getView()).navigate(action);
        }
        catch(SQLException e)
        {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void saveAndComplete()
    {
        try
        {
            IssueViewModel issueViewModel = new ViewModelProvider(getActivity()).get(IssueViewModel.class);
            Issue issue = issueViewModel.getIssue();
            issue.status = "Pending";


            saveData();
            IssueForm4FragmentDirections.ActionIssueForm4Complete action = IssueForm4FragmentDirections.actionIssueForm4Complete();
            Navigation.findNavController(getView()).navigate(action);
        }
        catch(SQLException e)
        {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void saveData() throws SQLException
    {
        DatabaseHelper db = DatabaseHelper.getInstance();

        IssueViewModel issueViewModel = new ViewModelProvider(getActivity()).get(IssueViewModel.class);
        Issue issue = issueViewModel.getIssue();
        issue.areas = m_areaChecklistAdapter.getAreaIdsAsString();

        if (m_tagLocationSwitch.isChecked())
        {
            MainActivity activity = (MainActivity)getActivity();
            Location l = activity.getLocation();
            if (l != null)
            {
                issue.longitude = String.valueOf(l.getLongitude());
                issue.latitude = String.valueOf(l.getLatitude());
            }
        }
        db.saveIssue(issue);
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
}