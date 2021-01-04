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

import com.sonjara.listenup.database.DatabaseHelper;
import com.sonjara.listenup.database.DatabaseSync;
import com.sonjara.listenup.database.ImageCache;
import com.sonjara.listenup.database.Service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

public class ServiceChecklistAdapter extends RecyclerView.Adapter
{
    MainActivity m_activity;
    List<Service> m_serviceList;

    private List<String> m_serviceIds;

    public ServiceChecklistAdapter(MainActivity activity)
    {
        m_activity = activity;
        m_serviceIds = new LinkedList<String>(Arrays.asList(activity.getServiceFilter().split(",")));
        m_imageCache = activity.getImageCache();
        m_serviceList = DatabaseHelper.getInstance().getServices();
    }

    private ImageCache m_imageCache = null;

    public List<String> getServiceIds()
    {
        return m_serviceIds;
    }

    public String getServiceIdFilter()
    {
       return TextUtils.join(",", m_serviceIds);
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView m_imageView;
        TextView m_serviceLabel;
        CheckBox m_checkbox;

        public ImageView getImageView()
        {
            return m_imageView;
        }

        public TextView getServiceLabel()
        {
            return m_serviceLabel;
        }

        public CheckBox getCheckbox()
        {
            return m_checkbox;
        }

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            m_imageView = (ImageView)itemView.findViewById(R.id.service_image);
            m_serviceLabel = (TextView)itemView.findViewById(R.id.service_name);
            m_checkbox = (CheckBox)itemView.findViewById(R.id.service_checkbox);
        }
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.service_card_layout,parent,false);
        return new ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        final Service service = m_serviceList.get(position);
        ServiceChecklistAdapter.ViewHolder h = (ServiceChecklistAdapter.ViewHolder)holder;

        h.getServiceLabel().setText(service.name);
        if (service.image_id != 0)
        {
            //m_imageCache.showImage(service.image_id, h.getImageView());
        }

        if (m_serviceIds.contains(String.valueOf(service.service_id)))
        {
            h.getCheckbox().setChecked(true);
        }

        final ServiceChecklistAdapter me = this;

        h.getCheckbox().setOnClickListener(new View.OnClickListener() {

               @Override
               public void onClick(View v)
               {
                   CheckBox cbox = (CheckBox)v;
                   if (cbox.isChecked())
                   {
                       me.addServiceId(service.service_id);
                   }
                   else
                   {
                       me.removeServiceId(service.service_id);
                   }
               }
        });
    }

    void addServiceId(int service_id)
    {
        String val = String.valueOf(service_id);
        if (!m_serviceIds.contains(val))
        {
            m_serviceIds.add(val);
        }
    }

    void removeServiceId(int service_id)
    {
        String val = String.valueOf(service_id);
        m_serviceIds.remove(val);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount()
    {
        return m_serviceList.size();
    }
}
