package com.sonjara.listenup.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper
{
    private static final String DATABASE_NAME    = "listenup.db";
    private static final int    DATABASE_VERSION = 1;

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource)
    {
        try {
            TableUtils.createTable(connectionSource, Service.class);
            TableUtils.createTable(connectionSource, AreaDetails.class);
            TableUtils.createTable(connectionSource, LocationDetails.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
                          int oldVersion, int newVersion)
    {
        onCreate(db, connectionSource);
    }

    public void clearDatabase()
    {
        ConnectionSource connectionSource = getConnectionSource();

        try {
            TableUtils.dropTable(connectionSource, Service.class, true);
            TableUtils.dropTable(connectionSource, AreaDetails.class, true);
            TableUtils.dropTable(connectionSource, LocationDetails.class, true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void rebuildDatabase()
    {
        this.clearDatabase();
        this.onCreate(getWritableDatabase(), getConnectionSource());
    }

    private static DatabaseHelper sDatabaseHelper;

//    public static DatabaseHelper getInstance(Context context) {
//        if (sDatabaseHelper == null) {
//            sDatabaseHelper = new DatabaseHelper(context.getApplicationContext());
//        }
//
//        return sDatabaseHelper;
//    }

    public static DatabaseHelper getInstance() {
        return sDatabaseHelper;
    }

    public List<LocationDetails> getLocations()
    {
        ConnectionSource connectionSource = getConnectionSource();

        try
        {
            Dao<LocationDetails, Integer> dao = DaoManager.createDao(connectionSource, LocationDetails.class);
            List<LocationDetails> locations = dao.queryForAll();

            return locations;
        }
        catch(SQLException e)
        {
            return null;
        }
    }
}
