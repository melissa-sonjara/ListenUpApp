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
import android.widget.Button;
import android.widget.Toast;

import com.sonjara.listenup.database.DatabaseHelper;
import com.sonjara.listenup.database.Issue;

import java.sql.SQLException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IssueListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IssueListFragment extends Fragment implements IssueListViewAdapter.OnIssueClicked, IssueListViewAdapter.OnDeleteIssueClicked
{
    private Button m_reportIssueButton;
    private RecyclerView m_issueList;

    public IssueListFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment IssueListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IssueListFragment newInstance()
    {
        IssueListFragment fragment = new IssueListFragment();
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
        View view = inflater.inflate(R.layout.fragment_issue_list, container, false);

        m_issueList = (RecyclerView)view.findViewById(R.id.issue_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        m_issueList.setLayoutManager(layoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(m_issueList.getContext(),
                layoutManager.getOrientation());
        m_issueList.addItemDecoration(divider);

        DatabaseHelper db = DatabaseHelper.getInstance();

        IssueListViewAdapter adapter = new IssueListViewAdapter(db.getIssues());
        adapter.setOnIssueClicked(this);
        adapter.setOnDeleteIssueClicked(this);

        m_issueList.setAdapter(adapter);

        m_reportIssueButton = (Button)view.findViewById(R.id.report_issue_button);
        m_reportIssueButton.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v)
            {

                IssueViewModel issueViewModel = new ViewModelProvider(getActivity()).get(IssueViewModel.class);
                issueViewModel.newIssue();
                IssueListFragmentDirections.ShowIssueForm action = IssueListFragmentDirections.showIssueForm();
                Navigation.findNavController(getView()).navigate(action);
            }
        });
        return view;
    }

    @Override
    public void OnIssueClicked(Issue issue)
    {
        IssueViewModel viewModel = new ViewModelProvider(getActivity()).get(IssueViewModel.class);
        viewModel.setIssue(issue);

        IssueListFragmentDirections.ShowIssueForm action = IssueListFragmentDirections.showIssueForm();
        Navigation.findNavController(getView()).navigate(action);
    }

    @Override
    public void OnDeleteIssueClicked(Issue issue)
    {
        IssueViewModel viewModel = new ViewModelProvider(getActivity()).get(IssueViewModel.class);
        viewModel.setIssue(null);

        try
        {
            DatabaseHelper db = DatabaseHelper.getInstance();
            db.deleteIssue(issue);
            IssueListViewAdapter adapter = (IssueListViewAdapter)m_issueList.getAdapter();
            adapter.setIssues(db.getIssues());
            adapter.notifyDataSetChanged();
        }
        catch(SQLException e)
        {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
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