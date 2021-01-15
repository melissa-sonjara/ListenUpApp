package com.sonjara.listenup;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.sonjara.listenup.database.Issue;

import java.util.List;

public class IssueListViewAdapter extends RecyclerView.Adapter<IssueListViewAdapter.ViewHolder>
{
    public interface OnIssueClicked
    {
        public void OnIssueClicked(Issue issue);
    }

    public interface OnDeleteIssueClicked
    {
        public void OnDeleteIssueClicked(Issue issue);
    }

    private IssueListViewAdapter.OnIssueClicked m_onIssueClicked = null;
    private IssueListViewAdapter.OnDeleteIssueClicked m_onDeleteIssueClicked = null;

    public OnIssueClicked getOnIssueClicked()
    {
        return m_onIssueClicked;
    }

    public void setOnIssueClicked(OnIssueClicked onIssueClicked)
    {
        m_onIssueClicked = onIssueClicked;
    }

    public OnDeleteIssueClicked getOnDeleteIssueClicked()
    {
        return m_onDeleteIssueClicked;
    }

    public void setOnDeleteIssueClicked(OnDeleteIssueClicked onDeleteIssueClicked)
    {
        m_onDeleteIssueClicked = onDeleteIssueClicked;
    }

    private List<Issue> m_issues;

    public List<Issue> getIssues()
    {
        return m_issues;
    }

    public void setIssues(List<Issue> issues)
    {
        m_issues = issues;
    }
    public IssueListViewAdapter(List<Issue> items)
    {
        m_issues = items;
    }

    @Override
    public IssueListViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.issue_list_card_layout, parent, false);
        return new IssueListViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final IssueListViewAdapter.ViewHolder holder, int position)
    {
        String status = m_issues.get(position).status;
        holder.m_issue = m_issues.get(position);
        holder.m_name.setText(m_issues.get(position).title);
        holder.m_status.setText(status);
        if ("In Progress".equals(status))
        {
            holder.m_status.setTextColor(holder.getView().getResources().getColor(R.color.in_progress));
        }
        else if ("Pending".equals(status))
        {
            holder.m_status.setTextColor(holder.getView().getResources().getColor(R.color.pending));
        }
        else if ("Submitted".equals(status))
        {
            holder.m_status.setTextColor(holder.getView().getResources().getColor(R.color.submitted));
        }

        holder.m_deleteIcon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Title")
                        .setMessage("Do you really want delete this issue report?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                if (m_onDeleteIssueClicked != null)
                                {
                                    m_onDeleteIssueClicked.OnDeleteIssueClicked(holder.m_issue);
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
        holder.getView().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                IssueListViewAdapter.OnIssueClicked handler = getOnIssueClicked();
                if (handler != null)
                {
                    handler.OnIssueClicked(holder.m_issue);
                }
            }
        });

    }

    @Override
    public int getItemCount()
    {
        return m_issues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View m_view;
        public final TextView m_name;
        public final TextView m_status;
        public Issue m_issue;
        public final ImageView m_deleteIcon;

        public ViewHolder(View view)
        {
            super(view);
            m_view = view;
            m_name = (TextView) view.findViewById(R.id.issue_list_name);
            m_status = (TextView) view.findViewById(R.id.issue_list_status);
            m_deleteIcon = (ImageView) view.findViewById(R.id.issue_list_delete);
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