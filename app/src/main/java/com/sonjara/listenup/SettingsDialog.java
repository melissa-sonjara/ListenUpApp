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

import com.sonjara.listenup.database.DatabaseHelper;
import com.sonjara.listenup.database.DatabaseSync;
import com.sonjara.listenup.database.MobileUserDetails;
import com.sonjara.listenup.database.OperationalArea;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SettingsDialog extends DialogFragment
{
    private TextView m_lastSyncDate;
    private TextView m_tokenExpiresDate;
    private Button m_dismissButton;
    private Button m_signOutButton;
    private TextView m_loginName;
    private TextView m_loginNameLabel;
    private TextView m_organizationName;
    private TextView m_operationalAreaLabel;
    private TextView m_operationalArea;

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
        m_loginNameLabel = (TextView)view.findViewById(R.id.login_name_label);
        m_loginName = (TextView)view.findViewById(R.id.login_name);
        m_organizationName = (TextView)view.findViewById(R.id.login_organization);
        m_operationalAreaLabel = (TextView)view.findViewById(R.id.operational_area_label);
        m_operationalArea = (TextView)view.findViewById(R.id.login_operational_area);

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
                activity.clearToken();
                DatabaseHelper db = DatabaseHelper.getInstance();
                db.clearMobileUserData();
                dismiss();
            }
        });

        DatabaseHelper db = DatabaseHelper.getInstance();
        MobileUserDetails user = db.getMobileUserDetails();
        if (user != null)
        {
            m_loginName.setText(user.name);
            m_organizationName.setText(user.organization);
            OperationalArea area = db.getOperationalArea(user.operational_area_id);
            if (area != null)
            {
                m_operationalArea.setText(area.name);
            }
            else
            {
                m_operationalArea.setText("Not set");
            }

            m_loginNameLabel.setVisibility(View.VISIBLE);
            m_loginName.setVisibility(View.VISIBLE);
            m_organizationName.setVisibility(View.VISIBLE);
            m_operationalAreaLabel.setVisibility(View.VISIBLE);
            m_operationalArea.setVisibility(View.VISIBLE);
        }
        else
        {
            m_loginNameLabel.setVisibility(View.GONE);
            m_loginName.setVisibility(View.GONE);
            m_organizationName.setVisibility(View.GONE);
            m_operationalAreaLabel.setVisibility(View.GONE);
            m_operationalArea.setVisibility(View.GONE);
        }
        return view;

    }
}
