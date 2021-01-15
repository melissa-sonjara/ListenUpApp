package com.sonjara.listenup;

import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.sonjara.listenup.database.DatabaseHelper;
import com.sonjara.listenup.database.Issue;
import com.sonjara.listenup.database.IssueType;
import com.sonjara.listenup.database.SafetyIssueSource;
import com.sonjara.listenup.database.SubIssueType;

import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class IssueForm2Fragment extends Fragment
{

    private IssueViewModel m_issueViewModel = null;

    private EditText m_issueDescription;
    private RecyclerView m_issueEvidence;
    private EditText m_issueDateCollected;

    private Button m_issueNextButton;
    private Button m_issuePrevButton;
    
    private EvidenceChecklistAdapter m_issueEvidenceAdapter;
    private EditText m_issueContactName;

    public static IssueForm2Fragment newInstance()
    {
        return new IssueForm2Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_issue_form_2, container, false);

        MainActivity activity = (MainActivity)getActivity();

        m_issueViewModel = new ViewModelProvider(activity).get(IssueViewModel.class);

        m_issueDescription = (EditText)view.findViewById(R.id.issue_description);
        m_issueEvidence = (RecyclerView)view.findViewById(R.id.issue_evidence);
        m_issueDateCollected = (EditText)view.findViewById(R.id.issue_date_collected);
        m_issueContactName = (EditText)view.findViewById(R.id.issue_contact_name);
        m_issuePrevButton = (Button)view.findViewById(R.id.issue_form_2_previous);
        m_issueNextButton = (Button)view.findViewById(R.id.issue_form_2_next);
        
        DatabaseHelper db = DatabaseHelper.getInstance();

        m_issueDescription.setOnFocusChangeListener(new View.OnFocusChangeListener()
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

        m_issueContactName.setOnFocusChangeListener(new View.OnFocusChangeListener()
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(m_issueEvidence.getContext());
        m_issueEvidence.setLayoutManager(layoutManager);

        m_issueEvidenceAdapter = new EvidenceChecklistAdapter(db.getActiveEvidences());
        m_issueEvidence.setAdapter(m_issueEvidenceAdapter);

        m_issueDateCollected.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDatePicker();
            }
        });

        m_issueDateCollected.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus)
                {
                    showDatePicker();
                }
                else
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
        m_issueDescription.setText(issue.description);

        if (issue.date_collected != null)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("d/M/y");
            m_issueDateCollected.setText(sdf.format(issue.date_collected));
        }

        m_issueEvidenceAdapter.setIssueEvidenceIds(issue.evidence);
        m_issueContactName.setText(issue.contact_name);
    }

    private void showDatePicker()
    {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        DatePickerDialog picker = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        m_issueDateCollected.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
        picker.show();
    }

    private void prevStep()
    {
        try
        {
            saveData();
            IssueForm2FragmentDirections.ActionIssueForm2Prev action = IssueForm2FragmentDirections.actionIssueForm2Prev();
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
            IssueForm2FragmentDirections.ActionIssueForm2Next action = IssueForm2FragmentDirections.actionIssueForm2Next();
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
        issue.description = m_issueDescription.getText().toString();
        issue.contact_name = m_issueContactName.getText().toString();
        issue.evidence = m_issueEvidenceAdapter.getIssueEvidenceIdsAsString();

        SimpleDateFormat sdf = new SimpleDateFormat("d/M/y");
        Date d = null;
        try
        {
            d = new Date(sdf.parse(m_issueDateCollected.getText().toString()).getTime());
        }
        catch(ParseException e)
        {
        }

        issue.date_collected = d;
        db.saveIssue(issue);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        m_issueViewModel = new ViewModelProvider(this).get(IssueViewModel.class);
        // TODO: Use the ViewModel
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