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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class DatabaseSync
{
    public interface SyncUpdateListener
    {
        void onSyncUpdate(String stage, String status, int synced, int total);
    }

    private RequestQueue m_queue = null;
    private String m_url = "https://listenup.sonjara.com/api/";
    private String token = null;

    private String username = "admin";
    private String password = "Xcoria8192!";
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

    public DatabaseSync(AppCompatActivity context, DatabaseHelper helper, ImageCache cache)
    {
        m_dbHelper = helper;
        m_imageCache = cache;
        m_queue = Volley.newRequestQueue(context);
        //gson = new Gson();
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    public void authenticate()
    {
        String url = m_url + "authenticate?username=" + username + "&password=" + password;

        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("ListenUp", response.toString());
                try
                {
                    token = response.getJSONObject("result").getString("token");
                    if (syncing) doSync();
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error)
            {
               Log.v("ListenUp", error.getMessage());
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
