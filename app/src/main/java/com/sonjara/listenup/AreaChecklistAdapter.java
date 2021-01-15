package com.sonjara.listenup;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sonjara.listenup.database.AreaHierarchyNode;
import com.sonjara.listenup.database.IssueEvidence;

import java.util.LinkedList;
import java.util.List;

public class AreaChecklistAdapter extends RecyclerView.Adapter
{
    List<AreaHierarchyNode> m_nodeList;

    private List<String> m_areaIds;

    public AreaChecklistAdapter(List<AreaHierarchyNode> nodeList)
    {
        m_areaIds = new LinkedList<String>();
        m_nodeList = nodeList;
    }


    public List<String> getAreaIds()
    {
        return m_areaIds;
    }

    public String getAreaIdsAsString()
    {
        return TextUtils.join(",", m_areaIds);
    }

    public void setAreaIds(String areaIds)
    {
        m_areaIds = new LinkedList<String>();

        if (areaIds != null && !"".equals(areaIds))
        {
            String[] ids = areaIds.split(",");

            for (String id : ids)
            {
                m_areaIds.add(id);
            }
        }

        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView m_areaLabel;
        CheckBox m_checkbox;
        View     m_view;
        ImageView m_chevron;

        public TextView getAreaLabel()
        {
            return m_areaLabel;
        }

        public CheckBox getCheckbox()
        {
            return m_checkbox;
        }

        public ImageView getChevron()
        {
            return m_chevron;
        }

        public View getView() { return m_view; }

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            m_view = itemView;
            m_areaLabel = (TextView)itemView.findViewById(R.id.area_name);
            m_checkbox = (CheckBox)itemView.findViewById(R.id.area_checkbox);
            m_chevron = (ImageView)itemView.findViewById(R.id.area_chevron);
        }
    }

    /**
     * Called when RecyclerView needs a new {@link AreaChecklistAdapter.ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(AreaChecklistAdapter.ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(AreaChecklistAdapter.ViewHolder, int)
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.area_card_layout, parent,false);
        return new AreaChecklistAdapter.ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link AreaChecklistAdapter.ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link AreaChecklistAdapter.ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(AreaChecklistAdapter.ViewHolder, int, List)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        final AreaHierarchyNode areaNode = m_nodeList.get(position);
        final AreaChecklistAdapter.ViewHolder h = (AreaChecklistAdapter.ViewHolder)holder;

        float scale = h.getView().getResources().getDisplayMetrics().density;
        int indent = (int) (20 * areaNode.getLevel() *scale + 0.5f);
        int width = (int)(20 * scale + 0.5f);

        ViewGroup.LayoutParams params = h.getChevron().getLayoutParams();
        params.width = width + indent;
        h.getChevron().setPadding(indent, 0, 0, 0);
        h.getAreaLabel().setText(areaNode.getName());

        if (m_areaIds.contains(String.valueOf(areaNode.getArea_id())))
        {
            h.getCheckbox().setChecked(true);
        }

        final AreaChecklistAdapter me = this;

        h.getCheckbox().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                CheckBox cbox = (CheckBox)v;
                if (cbox.isChecked())
                {
                    me.addIssueEvidenceId(areaNode.getArea_id());
                }
                else
                {
                    me.removeIssueEvidenceId(areaNode.getArea_id());
                }
            }
        });

        h.getView().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CheckBox cbox = h.getCheckbox();
                cbox.toggle();
                if (cbox.isChecked())
                {
                    me.addIssueEvidenceId(areaNode.getArea_id());
                }
                else
                {
                    me.removeIssueEvidenceId(areaNode.getArea_id());
                }
            }
        });
    }

    void addIssueEvidenceId(int safety_issue_evidence_id)
    {
        String val = String.valueOf(safety_issue_evidence_id);
        if (!m_areaIds.contains(val))
        {
            m_areaIds.add(val);
        }
    }

    void removeIssueEvidenceId(int safety_issue_evidence_id)
    {
        String val = String.valueOf(safety_issue_evidence_id);
        m_areaIds.remove(val);
    }


    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount()
    {
        return m_nodeList.size();
    }
}
