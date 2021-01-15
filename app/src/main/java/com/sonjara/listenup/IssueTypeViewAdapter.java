package com.sonjara.listenup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sonjara.listenup.database.IssueType;

import java.util.List;

public class IssueTypeViewAdapter extends RecyclerView.Adapter<IssueTypeViewAdapter.ViewHolder>
{

    public void setSelectedIssueTypeId(int issue_type_id)
    {
        m_selectedItemPos = -1;
        if (m_issueTypes == null) return;

        int pos = 0;
        for(IssueType type : m_issueTypes)
        {
            if (type.issue_type_id == issue_type_id)
            {
                m_selectedItemPos = pos;
                break;
            }

            pos++;
        }

        notifyDataSetChanged();
    }

    public interface OnIssueTypeSelected
    {
        public void onIssueTypeSelected(IssueType type);
    }

    private List<IssueType> m_issueTypes;
    private int m_selectedItemPos = -1;
    private OnIssueTypeSelected m_onIssueTypeSelected = null;

    public List<IssueType> getIssueTypes()
    {
        return m_issueTypes;
    }

    public void setIssueTypes(List<IssueType> issueTypes)
    {
        m_issueTypes = issueTypes;
    }

    public IssueTypeViewAdapter(List<IssueType> issueTypes)
    {
        m_issueTypes = issueTypes;
    }

    public OnIssueTypeSelected getOnIssueTypeSelected()
    {
        return m_onIssueTypeSelected;
    }

    public void setOnIssueTypeSelected(OnIssueTypeSelected onIssueTypeSelected)
    {
        m_onIssueTypeSelected = onIssueTypeSelected;
    }

    public final IssueType getSelectedIssueType()
    {
        return (m_selectedItemPos != -1) ? m_issueTypes.get(m_selectedItemPos) : null;
    }

    @Override
    public IssueTypeViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.issue_card_layout, parent, false);
        return new IssueTypeViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final IssueTypeViewAdapter.ViewHolder holder, int position)
    {
        holder.m_issueType = m_issueTypes.get(position);
        holder.m_name.setText(m_issueTypes.get(position).name);
        holder.m_description.setText(m_issueTypes.get(position).description);

        holder.m_radioButton.setChecked(m_selectedItemPos == position);
    }
    
    @Override
    public int getItemCount()
    {
        return m_issueTypes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View m_view;
        public final RadioButton m_radioButton;
        public final TextView m_name;
        public final TextView m_description;
        public IssueType m_issueType;

        public ViewHolder(View view)
        {
            super(view);
            m_view = view;
            m_radioButton = (RadioButton) view.findViewById(R.id.issue_type_radio_button);
            m_name = (TextView) view.findViewById(R.id.issue_type_name);
            m_description = (TextView) view.findViewById(R.id.issue_type_description);

            m_view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    m_selectedItemPos = getAdapterPosition();
                    notifyDataSetChanged();

                    if (m_onIssueTypeSelected != null)
                    {
                        m_onIssueTypeSelected.onIssueTypeSelected(m_issueTypes.get(m_selectedItemPos));
                    }
                }
            });

            m_radioButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    m_selectedItemPos = getAdapterPosition();
                    notifyDataSetChanged();

                    if (m_onIssueTypeSelected != null)
                    {
                        m_onIssueTypeSelected.onIssueTypeSelected(m_issueTypes.get(m_selectedItemPos));
                    }
                }
            });

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
