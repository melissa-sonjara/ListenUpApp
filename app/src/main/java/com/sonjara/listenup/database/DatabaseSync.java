package com.sonjara.listenup.database;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sonjara.listenup.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class DatabaseSync
{
    public interface SyncUpdateListener
    {
        void onSyncUpdate(String stage, String status, int synced, int total);
    }

    public static final int LOGIN_SUCCESS = 1;
    public static final int LOGIN_ERROR = 0;

    public interface LoginResultListener
    {
        void onLoginResult(int status, String message);
    }

    private MainActivity m_activity;

    private RequestQueue m_queue = null;
    private String m_url = "https://listenup.sonjara.com/api/";
    private String token = null;

    private Boolean syncing = false;
    private int m_tablesSynced = 0;
    private int m_tablesToSync = 0;

    private int m_imagesCached = 0;
    private int m_imageTotal = 0;

    private SyncUpdateListener m_syncUpdateListener = null;

    public void setSyncUpdateListener(SyncUpdateListener listener)
    {
        m_syncUpdateListener = listener;
    }

    public LoginResultListener getLoginResultListener()
    {
        return m_loginResultListener;
    }

    public void setLoginResultListener(LoginResultListener loginResultListener)
    {
        m_loginResultListener = loginResultListener;
    }

    private LoginResultListener m_loginResultListener = null;

    public DatabaseHelper getDatabaseHelper()
    {
        return m_dbHelper;
    }

    private DatabaseHelper m_dbHelper = null;

    private ImageCache m_imageCache = null;

    public ImageCache getImageCache()
    {
        return m_imageCache;
    }

    public RequestQueue getQueue()
    {
        return m_queue;
    }

    public Gson getGson()
    {
        return gson;
    }

    public void setGson(Gson gson)
    {
        this.gson = gson;
    }

    private Gson gson;

    public DatabaseSync(MainActivity activity, DatabaseHelper helper, ImageCache cache)
    {
        m_activity = activity;
        m_dbHelper = helper;
        m_imageCache = cache;
        m_queue = Volley.newRequestQueue(activity.getApplicationContext());
        //gson = new Gson();
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    public void authenticate()
    {
        token = m_activity.getToken();

        if (token == null)
        {
            m_activity.handleLogin();
        }
        else
        {
            if (syncing) doSync();
        }
    }

    public void login(String username, String password)
    {
        String url = m_url + "authenticate?username=" + username + "&password=" + password;

        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("ListenUp", response.toString());
                try
                {
                    JSONObject result =response.getJSONObject("result");
                    token = result.getString("token");
                    Date expiry = null;
                    String expiryStr = result.getString("expiry_date");
                    if (!"".equals(expiryStr))
                    {
                        SimpleDateFormat sdf = new SimpleDateFormat(MainActivity.DATE_PATTERN);
                        try
                        {
                            expiry = sdf.parse(expiryStr);
                        }
                        catch(ParseException e)
                        {
                            Log.e("ListenUp", "Invalid date format: " + expiryStr);
                        }
                    }
                    m_activity.setToken(token, expiry);

                    if (m_loginResultListener != null)
                    {
                        m_loginResultListener.onLoginResult(LOGIN_SUCCESS, "");
                    }
                }
                catch (JSONException e)
                {
                    if (m_loginResultListener != null)
                    {
                        m_loginResultListener.onLoginResult(LOGIN_ERROR, "Incorrect email or password");
                    }
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error)
            {
               Log.v("ListenUp", error.getMessage());
               m_loginResultListener.onLoginResult(LOGIN_ERROR, error.getMessage());
            }
        });

        m_queue.add(request);
    }

    public void sync()
    {
        syncing = true;
        authenticate();
    }

    private void doSync()
    {
        m_tablesSynced = 0;
        m_tablesToSync = 0;

        syncTable(new DatabaseSyncHelper<Service, Service[]>(this, Service.class, Service[].class, "service", ""));
        syncTable(new DatabaseSyncHelper<AreaDetails, AreaDetails[]>(this, AreaDetails.class, AreaDetails[].class, "area_details", ""));
        syncTable(new DatabaseSyncHelper<LocationDetails, LocationDetails[]>(this, LocationDetails.class, LocationDetails[].class, "location_details", ""));
        syncTable(new DatabaseSyncHelper<IssueType, IssueType[]>(this, IssueType.class, IssueType[].class, "issue_type", ""));
        syncTable(new DatabaseSyncHelper<SubIssueType, SubIssueType[]>(this, SubIssueType.class, SubIssueType[].class, "sub_issue_type", ""));
        syncTable(new DatabaseSyncHelper<SafetyIssueSource, SafetyIssueSource[]>(this, SafetyIssueSource.class, SafetyIssueSource[].class, "issue_source", ""));
        syncTable(new DatabaseSyncHelper<IssueEvidence, IssueEvidence[]>(this, IssueEvidence.class, IssueEvidence[].class, "issue_evidence", ""));
        syncTable(new DatabaseSyncHelper<CampService, CampService[]>(this, CampService.class, CampService[].class, "camp_service", ""));
    }

    public void syncTable(DatabaseSyncHelper target)
    {
        ++m_tablesToSync;

        String endpoint = target.getEndpoint();
        String args = target.getArgs();

        if (token == null)
        {
            this.authenticate();
        }

        String url = m_url + endpoint + "?";
        url += args;

        if (!args.equals(""))
        {
            url += "&";
        }

        url += "token=" + token;

        target.sync(url);

        if (m_syncUpdateListener != null)
        {
            m_syncUpdateListener.onSyncUpdate("Syncing data", "Syncing", m_tablesSynced, m_tablesToSync);
        }
    }

    public void onTableSynced()
    {
        ++m_tablesSynced;
        String status = (m_tablesSynced == m_tablesToSync) ? "Completed" : "Syncing";
        if (m_syncUpdateListener != null)
        {
            m_syncUpdateListener.onSyncUpdate("Syncing data", status, m_tablesSynced, m_tablesToSync);
        }
    }

    public void submitIssue(Issue issue)
    {
        if (issue == null || issue.status != "Pending") return;

        String json = new Gson().toJson(issue);

    }

    public void cacheImages()
    {
        List<Service> services = m_dbHelper.getServices();
        for(Service s: services)
        {
            if (s.image_id != 0)
            {
                m_imageCache.cacheImage(s.image_id);
            }
        }
    }

    public void clearDatabase()
    {
        m_dbHelper.clearDatabase();
    }

    public void rebuildDatabase()
    {
        m_dbHelper.rebuildDatabase();
    }
}
