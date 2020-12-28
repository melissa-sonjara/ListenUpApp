package com.sonjara.listenup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.sonjara.listenup.database.DatabaseSync;

public class SyncDialog extends DialogFragment implements DatabaseSync.SyncUpdateListener
{
    // widgets
    private TextView m_header;
    private CheckBox m_fullSync;
    private ProgressBar m_syncProgress;
    private Button m_syncButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dialog_sync, container, false);
        m_header = view.findViewById(R.id.sync_heading);
        m_fullSync = view.findViewById(R.id.full_sync);
        m_syncProgress = view.findViewById(R.id.sync_progress);
        m_syncButton = view.findViewById(R.id.sync_ok_button);

        final SyncDialog me = this;

        m_syncButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                DatabaseSync sync = ((MainActivity)getActivity()).getSyncHelper();
                sync.setSyncUpdateListener(me);

                if (m_fullSync.isChecked())
                {
                    sync.rebuildDatabase();
                }
                sync.sync();
            }
        });

        return view;
    }

    @Override
    public void onSyncUpdate(String status, int synced, int total)
    {
        if (status.equals("Completed"))
        {
            DatabaseSync sync = ((MainActivity)getActivity()).getSyncHelper();
            sync.setSyncUpdateListener(null);

            getDialog().dismiss();
        }

        m_syncProgress.setMax(total);
        m_syncProgress.setProgress(synced);
    }
}
