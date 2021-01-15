package com.sonjara.listenup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.sonjara.listenup.database.DatabaseSync;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SettingsDialog extends DialogFragment
{
    private TextView m_lastSyncDate;
    private TextView m_tokenExpiresDate;
    private Button m_dismissButton;
    private Button m_signOutButton;

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null. This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p>A default View can be returned by calling {@link #Fragment(int)} in your
     * constructor. Otherwise, this method returns null.
     *
     * <p>It is recommended to <strong>only</strong> inflate the layout in this method and move
     * logic that operates on the returned View to {@link #onViewCreated(View, Bundle)}.
     *
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final MainActivity activity = (MainActivity)getActivity();
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        m_lastSyncDate = (TextView)view.findViewById(R.id.last_sync_date);
        m_tokenExpiresDate = (TextView)view.findViewById(R.id.token_expires);
        m_dismissButton = (Button)view.findViewById(R.id.settings_dismiss_button);
        m_signOutButton = (Button)view.findViewById(R.id.sign_out_button);

        Date lastSync = activity.getLastSyncTime();
        Date tokenExpires = activity.getTokenExpiry();

        SimpleDateFormat sdf = new SimpleDateFormat(MainActivity.DATE_PATTERN);
        if (lastSync == null)
        {
            m_lastSyncDate.setText("Never synced");
        }
        else
        {
            m_lastSyncDate.setText(sdf.format(lastSync));
        }

        if (tokenExpires == null)
        {
            m_tokenExpiresDate.setText("Not signed in");
        }
        else
        {
            m_tokenExpiresDate.setText(sdf.format(tokenExpires));
        }


        m_dismissButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });

        m_signOutButton.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v)
            {
                activity.setToken(null, null);
                dismiss();
            }
        });
        return view;

    }
}
