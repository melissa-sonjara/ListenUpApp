package com.sonjara.listenup.database;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.j256.ormlite.dao.Dao;

import org.json.JSONArray;

import java.sql.SQLException;

public class DatabaseSyncHelper<T, A>
{
    private String m_endpoint;
    private String m_args;
    private DatabaseSync m_sync = null;

    private Class m_class;
    private Class m_arrayClass;

    public Class getClazz()
    {
        return m_class;
    }

    public Class getArrayClass()
    {
        return m_arrayClass;
    }

    public java.lang.String getEndpoint()
    {
        return m_endpoint;
    }

    public void setEndpoint(java.lang.String m_endpoint)
    {
        this.m_endpoint = m_endpoint;
    }

    public java.lang.String getArgs()
    {
        return m_args;
    }

    public void setArgs(java.lang.String m_args)
    {
        this.m_args = m_args;
    }

    public DatabaseSync getSync()
    {
        return m_sync;
    }

    public void setSync(DatabaseSync m_sync)
    {
        this.m_sync = m_sync;
    }

    public DatabaseSyncHelper(DatabaseSync sync, Class clazz, Class arrayClass, String endpoint, String args)
    {
        m_class = clazz;
        m_arrayClass = arrayClass;
        setSync(sync);
        setEndpoint(endpoint);
        setArgs(args);
    }

    public void sync(String url)
    {
        GsonObjectRequest<A> request = new GsonObjectRequest<A>(m_sync.getGson(), url, m_arrayClass,
        new Response.Listener<A>()
        {
            @Override
            public void onResponse(A response)
            {

                Log.v("ListenUp", response.toString());

                try
                {
                    DatabaseHelper helper = m_sync.getDatabaseHelper();
                    Dao<T, Integer> dao = helper.getDao(m_class);

                    for (T item : (T[]) response)
                    {
                        dao.createOrUpdate(item);
                    }

                    m_sync.onTableSynced();

                } catch (SQLException e)
                {
                    Log.v("ListenUp", e.getMessage());
                }
            }
        },
        new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.v("ListenUp", error.getMessage());
            }
        });

        m_sync.getQueue().add(request);
    }
}
