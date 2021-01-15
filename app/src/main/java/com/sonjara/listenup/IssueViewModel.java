package com.sonjara.listenup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sonjara.listenup.database.Issue;

public class IssueViewModel extends ViewModel
{
    private final MutableLiveData<Issue> m_issue = new MutableLiveData<>();

    public MutableLiveData<Issue> getWrappedIssue()
    {
        return m_issue;
    }

    public Issue getIssue()
    {
        return m_issue.getValue();
    }

    public void newIssue()
    {
        m_issue.setValue(new Issue());
    }

    public void setIssue(Issue issue)
    {
        m_issue.setValue(issue);
    }

    // TODO: Implement the ViewModel
}