package com.sonjara.listenup;

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sonjara.listenup.database.DatabaseHelper;
import com.sonjara.listenup.database.Issue;

import java.sql.SQLException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IssueForm3Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IssueForm3Fragment extends Fragment
{

    private EditText m_issueSolution;
    private RecyclerView m_campServices;

    private Button m_issueNextButton;
    private Button m_issuePrevButton;

    private CampServiceChecklistAdapter m_campServiceChecklistAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public IssueForm3Fragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IssueForm3Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IssueForm3Fragment newInstance(String param1, String param2)
    {
        IssueForm3Fragment fragment = new IssueForm3Fragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_issue_form_3, container, false);

        MainActivity activity = (MainActivity)getActivity();

        m_issueSolution = (EditText)view.findViewById(R.id.issue_solution);
        m_campServices = (RecyclerView)view.findViewById(R.id.issue_camp_services);
        m_issuePrevButton = (Button)view.findViewById(R.id.issue_form_3_previous);
        m_issueNextButton = (Button)view.findViewById(R.id.issue_form_3_next);

        DatabaseHelper db = DatabaseHelper.getInstance();


        LinearLayoutManager layoutManager = new LinearLayoutManager(m_campServices.getContext());
        m_campServices.setLayoutManager(layoutManager);

        m_campServiceChecklistAdapter = new CampServiceChecklistAdapter(db.getActiveCampServices());
        m_campServices.setAdapter(m_campServiceChecklistAdapter);


        m_issueSolution.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus)
                {
                    InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        m_issueNextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                nextStep();
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
        populateView();
    }

    private void populateView()
    {
        IssueViewModel issueViewModel = new ViewModelProvider(getActivity()).get(IssueViewModel.class);
        Issue issue = issueViewModel.getIssue();
        m_issueSolution.setText(issue.recommendation);

        m_campServiceChecklistAdapter.setCampServiceIds(issue.camp_services);
    }


    private void prevStep()
    {
        try
        {
            saveData();
            IssueForm3FragmentDirections.ActionIssueForm3Prev action = IssueForm3FragmentDirections.actionIssueForm3Prev();
            Navigation.findNavController(getView()).navigate(action);
        }
        catch(SQLException e)
        {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void nextStep()
    {
        try
        {
            saveData();
            IssueForm3FragmentDirections.ActionIssueForm3Next action = IssueForm3FragmentDirections.actionIssueForm3Next();
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
        issue.recommendation = m_issueSolution.getText().toString();
        issue.camp_services = m_campServiceChecklistAdapter.getCampServiceIdsAsString();

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