package com.sonjara.listenup;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sonjara.listenup.database.DatabaseHelper;
import com.sonjara.listenup.database.Issue;
import com.sonjara.listenup.database.IssueType;
import com.sonjara.listenup.database.SafetyIssueSource;
import com.sonjara.listenup.database.SubIssueType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IssueFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IssueFormFragment extends Fragment implements IssueTypeViewAdapter.OnIssueTypeSelected
{

    private ArrayList<SafetyIssueSource> m_sources;

    private EditText m_issueName;
    private Spinner m_issueSource;
    private RecyclerView m_issueType;

    private TextView m_issueSubTypeTitle;
    private Spinner m_issueSubType;

    private Button m_issueNextButton;
    private Button m_issueBackButton;

    private IssueTypeViewAdapter m_issueTypeAdapter;

    private IssueViewModel m_issueViewModel = null;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayAdapter<SafetyIssueSource> m_issueSourceAdapter;

    public IssueFormFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IssueFormFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IssueFormFragment newInstance(String param1, String param2)
    {
        IssueFormFragment fragment = new IssueFormFragment();
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
        MainActivity activity = (MainActivity)getActivity();

        m_issueViewModel = new ViewModelProvider(activity).get(IssueViewModel.class);

        DatabaseHelper db = DatabaseHelper.getInstance();

        List<SafetyIssueSource> sources = db.getActiveIssueSources();
        m_sources = (ArrayList<SafetyIssueSource>)sources;

        View view = inflater.inflate(R.layout.fragment_issue_form, container, false);

        m_issueName = (EditText)view.findViewById(R.id.issue_name);
        m_issueSource = (Spinner)view.findViewById(R.id.issue_source);
        m_issueType = (RecyclerView)view.findViewById(R.id.issue_type);
        m_issueSubTypeTitle = (TextView)view.findViewById(R.id.sub_issue_type_title);
        m_issueSubType = (Spinner)view.findViewById(R.id.sub_issue_type);
        m_issueNextButton = (Button)view.findViewById(R.id.issue_form_1_next);
        m_issueBackButton = (Button)view.findViewById(R.id.issue_form_1_back);

        m_issueName.setOnFocusChangeListener(new View.OnFocusChangeListener()
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

        m_issueSourceAdapter = new ArrayAdapter<SafetyIssueSource>(activity, android.R.layout.simple_spinner_dropdown_item, m_sources);
        m_issueSourceAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

        m_issueSource.setAdapter(m_issueSourceAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(m_issueType.getContext());
        m_issueType.setLayoutManager(layoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(m_issueType.getContext(),
                layoutManager.getOrientation());
        m_issueType.addItemDecoration(divider);

        m_issueTypeAdapter = new IssueTypeViewAdapter(db.getActiveIssueTypes());
        m_issueTypeAdapter.setOnIssueTypeSelected(this);
        m_issueType.setAdapter(m_issueTypeAdapter);

        m_issueNextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                nextStep();
            }
        });
        m_issueBackButton.setOnClickListener((new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                back();
            }
        }));
        return view;
    }

    private void back()
    {
        try
        {
            Issue issue = m_issueViewModel.getIssue();
            if (issue.issue_id != 0)
            {
                saveData();
            }
            IssueFormFragmentDirections.HideIssueForm action = IssueFormFragmentDirections.hideIssueForm();
            Navigation.findNavController(getView()).navigate(action);
        }
        catch(SQLException e)
        {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
        Issue issue = m_issueViewModel.getIssue();
        m_issueName.setText(issue.title);
        m_issueTypeAdapter.setSelectedIssueTypeId(issue.issue_type_id);
        int pos = -1;
        if (issue.issue_type_id != 0)
        {
            onIssueTypeSelected(issue.getIssueType());

            ArrayAdapter<SubIssueType> adapter = (ArrayAdapter<SubIssueType>)m_issueSubType.getAdapter();
            for(int i = 0; i < adapter.getCount(); ++i)
            {
                if (adapter.getItem(i).sub_issue_type_id == issue.sub_issue_type_id)
                {
                    pos = i;
                    break;
                }
            }
        }

        m_issueSubType.setSelection(pos, false);
    }

    private void nextStep()
    {
        try
        {
            saveData();
            IssueFormFragmentDirections.ActionIssueFormNext action = IssueFormFragmentDirections.actionIssueFormNext();
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

        Issue issue = m_issueViewModel.getIssue();
        issue.title = m_issueName.getText().toString();
        issue.safety_issue_source_id = m_sources.get(m_issueSource.getSelectedItemPosition()).safety_issue_source_id;

        IssueType type = m_issueTypeAdapter.getSelectedIssueType();
        issue.issue_type_id = (type != null) ? type.issue_type_id : 0;
        ArrayAdapter<SubIssueType> adapter = (ArrayAdapter<SubIssueType>)m_issueSubType.getAdapter();

        if (issue.status == null || "".equals(issue.status))
        {
            issue.status = "In Progress";
        }

        if (adapter == null || adapter.getCount() == 0)
        {
            issue.sub_issue_type_id = 0;
        }
        else
        {
            issue.sub_issue_type_id = adapter.getItem(m_issueSubType.getSelectedItemPosition()).sub_issue_type_id;
        }

        db.saveIssue(issue);
    }

    @Override
    public void onIssueTypeSelected(IssueType type)
    {
        MainActivity activity = (MainActivity)getActivity();

        ArrayList<SubIssueType> subIssueTypes = (ArrayList<SubIssueType>)type.getSubTypes();

        ArrayAdapter<SubIssueType> adapter =
                new ArrayAdapter<SubIssueType>(activity,  android.R.layout.simple_spinner_dropdown_item, subIssueTypes);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        m_issueSubType.setAdapter(adapter);

        if (subIssueTypes.size() > 0)
        {
            m_issueSubType.setVisibility(View.VISIBLE);
            m_issueSubTypeTitle.setVisibility(View.VISIBLE);
        }
        else
        {
            m_issueSubType.setVisibility(View.INVISIBLE);
            m_issueSubTypeTitle.setVisibility(View.INVISIBLE);
        }
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