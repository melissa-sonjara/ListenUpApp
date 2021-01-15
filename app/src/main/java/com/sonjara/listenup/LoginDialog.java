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

public class LoginDialog extends DialogFragment implements DatabaseSync.LoginResultListener
{
    private TextView m_loginMessage;
    private EditText m_username;
    private EditText m_password;
    private Button   m_signInButton;

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
        View view = inflater.inflate(R.layout.dialog_login, container, false);
        m_loginMessage = (TextView)view.findViewById(R.id.login_message_area);
        m_username = (EditText)view.findViewById(R.id.username);
        m_password = (EditText)view.findViewById(R.id.password);
        m_signInButton = (Button)view.findViewById(R.id.login);

        m_loginMessage.setText("Please sign in to your Listen Up account to retrieve the latest data.");

        MainActivity activity = (MainActivity)getActivity();
        DatabaseSync sync = activity.getSyncHelper();
        sync.setLoginResultListener(this);
        m_signInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String username = m_username.getText().toString();
                String password = m_password.getText().toString();

                MainActivity activity = (MainActivity)getActivity();
                DatabaseSync sync = activity.getSyncHelper();
                sync.login(username, password);
            }
        });
        return view;

    }

    @Override
    public void onLoginResult(int status, String message)
    {
        MainActivity activity = (MainActivity)getActivity();
        DatabaseSync sync = activity.getSyncHelper();

        if (status == DatabaseSync.LOGIN_SUCCESS)
        {
            sync.setLoginResultListener(null);
            dismiss();
            sync.authenticate();
        }
        else
        {
            m_loginMessage.setText(message);
        }
    }
}
