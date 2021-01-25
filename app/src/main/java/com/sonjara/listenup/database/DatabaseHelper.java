package com.sonjara.listenup.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper
{
    private static final String DATABASE_NAME    = "listenup.db";
    private static final int    DATABASE_VERSION = 1;

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        sDatabaseHelper = this;
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource)
    {
        try {
            TableUtils.createTable(connectionSource, Service.class);
            TableUtils.createTable(connectionSource, AreaDetails.class);
            TableUtils.createTable(connectionSource, LocationDetails.class);
            TableUtils.createTable(connectionSource, IssueType.class);
            TableUtils.createTable(connectionSource, SubIssueType.class);
            TableUtils.createTable(connectionSource, SafetyIssueSource.class);
            TableUtils.createTable(connectionSource, IssueEvidence.class);
            TableUtils.createTable(connectionSource, CampService.class);
            TableUtils.createTableIfNotExists(connectionSource, Issue.class);
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
            TableUtils.dropTable(connectionSource, IssueType.class, true);
            TableUtils.dropTable(connectionSource, SubIssueType.class, true);
            TableUtils.dropTable(connectionSource, SafetyIssueSource.class, true);
            TableUtils.dropTable(connectionSource, IssueEvidence.class, true);
            TableUtils.dropTable(connectionSource, CampService.class, true);
            TableUtils.dropTable(connectionSource, Issue.class, true);
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

    public LocationDetails getLocation(int location_id)
    {
        ConnectionSource connectionSource = getConnectionSource();

        try
        {
            Dao<LocationDetails, Integer> dao = DaoManager.createDao(connectionSource, LocationDetails.class);
            return dao.queryForId(location_id);
        }
        catch (SQLException e)
        {
            return null;
        }
    }

    public List<Service> getServices()
    {
        ConnectionSource connectionSource = getConnectionSource();

        try
        {
            Dao<Service, Integer> dao = DaoManager.createDao(connectionSource, Service.class);
            List<Service> services = dao.queryBuilder().orderBy("sort_order", true).query();

            return services;
        }
        catch (SQLException e)
        {
            return null;
        }
    }

    public Service getService(int service_id)
    {
        ConnectionSource connectionSource = getConnectionSource();

        try
        {
            Dao<Service, Integer> dao = DaoManager.createDao(connectionSource, Service.class);
            return dao.queryForId(service_id);
        }
        catch(SQLException e)
        {
            return null;
        }
    }

    public List<AreaDetails> getAreas()
    {
        ConnectionSource connectionSource = getConnectionSource();

        try
        {
            Dao<AreaDetails, Integer> dao = DaoManager.createDao(connectionSource, AreaDetails.class);
            List<AreaDetails> areas = dao.queryForAll();

            return areas;
        }
        catch (SQLException e)
        {
            return null;
        }
    }

    public List<SafetyIssueSource> getActiveIssueSources()
    {
        ConnectionSource connectionSource = getConnectionSource();

        try
        {
            Dao<SafetyIssueSource, Integer> dao = DaoManager.createDao(connectionSource, SafetyIssueSource.class);
            QueryBuilder<SafetyIssueSource, Integer> qb = dao.queryBuilder();
            qb.where().eq("active", 1);
            qb.orderBy("sort_order", true);
            List<SafetyIssueSource> sources = qb.query();

            return sources;
        }
        catch (SQLException e)
        {
            return null;
        }
    }

    public List<IssueType> getActiveIssueTypes()
    {
        ConnectionSource connectionSource = getConnectionSource();

        try
        {
            Dao<IssueType, Integer> dao = DaoManager.createDao(connectionSource, IssueType.class);
            QueryBuilder<IssueType, Integer> qb = dao.queryBuilder();
            qb.where().eq("active", 1);
            qb.orderBy("sort_order", true);
            List<IssueType> issueTypes = qb.query();

            return issueTypes;
        }
        catch (SQLException e)
        {
            return null;
        }
    }

    public List<SubIssueType> getIssueSubTypes(int issue_type_id)
    {
        ConnectionSource connectionSource = getConnectionSource();

        try
        {
            Dao<SubIssueType, Integer> dao = DaoManager.createDao(connectionSource, SubIssueType.class);
            QueryBuilder<SubIssueType, Integer> qb = dao.queryBuilder();
            qb.where().eq("active", 1)
                .and()
                .eq("issue_type_id", issue_type_id);
            qb.orderBy("sort_order", true);
            List<SubIssueType> issueTypes = qb.query();

            return issueTypes;
        }
        catch (SQLException e)
        {
            return null;
        }
    }

    public Issue getIssue(int issue_id)
    {
        ConnectionSource connectionSource = getConnectionSource();

        try
        {
            Dao<Issue, Integer> dao = DaoManager.createDao(connectionSource, Issue.class);
            Issue issue = dao.queryForId(issue_id);

            return issue;
        }
        catch (SQLException e)
        {
            return null;
        }
    }

    public void saveIssue(Issue issue) throws SQLException
    {

        ConnectionSource connectionSource = getConnectionSource();

        Dao<Issue, Integer> dao = DaoManager.createDao(connectionSource, Issue.class);
        dao.createOrUpdate(issue);
    }

    public List<IssueEvidence> getActiveEvidences()
    {
        ConnectionSource connectionSource = getConnectionSource();

        try
        {
            Dao<IssueEvidence, Integer> dao = DaoManager.createDao(connectionSource, IssueEvidence.class);
            QueryBuilder<IssueEvidence, Integer> qb = dao.queryBuilder();
            qb.where().eq("active", 1);
            qb.orderBy("sort_order", true);
            List<IssueEvidence> evidenceList = qb.query();

            return evidenceList;
        }
        catch (SQLException e)
        {
            return null;
        }
    }

    public List<Issue> getIssues()
    {
        ConnectionSource connectionSource = getConnectionSource();

        try
        {
            Dao<Issue, Integer> dao = DaoManager.createDao(connectionSource, Issue.class);
            QueryBuilder<Issue, Integer> qb = dao.queryBuilder();
            qb.orderBy("issue_id", true);
            List<Issue> issueList = qb.query();

            return issueList;
        }
        catch (SQLException e)
        {
            return null;
        }
    }

    public IssueType getIssueType(int issue_type_id)
    {
        ConnectionSource connectionSource = getConnectionSource();

        try
        {
            Dao<IssueType, Integer> dao = DaoManager.createDao(connectionSource, IssueType.class);
            IssueType issueType = dao.queryForId(issue_type_id);

            return issueType;
        }
        catch (SQLException e)
        {
            return null;
        }
    }

    public List<CampService> getActiveCampServices()
    {
        ConnectionSource connectionSource = getConnectionSource();

        try
        {
            Dao<CampService, Integer> dao = DaoManager.createDao(connectionSource, CampService.class);
            QueryBuilder<CampService, Integer> qb = dao.queryBuilder();
            qb.orderBy("sort_order", true);
            List<CampService> campServiceList = qb.query();

            return campServiceList;
        }
        catch (SQLException e)
        {
            return null;
        }
    }

    public List<AreaHierarchyNode> getAreaHierarchy()
    {
        LinkedList<AreaHierarchyNode> areaList = new LinkedList<>();
        doGetAreaHierarchy(0, 0, areaList);

        return areaList;
    }

    private void doGetAreaHierarchy(int parent_id, int level, LinkedList<AreaHierarchyNode> areaList)
    {
        List<AreaDetails> areas = getAreasByParent(parent_id);
        if (areas == null) return;

        for(AreaDetails area: areas)
        {
            AreaHierarchyNode node = new AreaHierarchyNode();
            node.setArea_id(area.area_id);
            node.setParent_id(area.parent_id);
            node.setLevel(level);
            node.setName(area.name);
            areaList.add(node);

            doGetAreaHierarchy(area.area_id, level+1, areaList);
        }
    }

    public List<AreaDetails> getAreasByParent(int parent_id)
    {
        ConnectionSource connectionSource = getConnectionSource();

        try
        {
            Dao<AreaDetails, Integer> dao = DaoManager.createDao(connectionSource, AreaDetails.class);
            QueryBuilder<AreaDetails, Integer> qb = dao.queryBuilder();
            qb.where().eq("parent_id", parent_id);
            qb.orderBy("name", true);
            List<AreaDetails> areaDetailsList = qb.query();

            return areaDetailsList;
        }
        catch (SQLException e)
        {
            return null;
        }
    }

    public void deleteIssue(Issue issue) throws SQLException
    {
        ConnectionSource connectionSource = getConnectionSource();

        Dao<Issue, Integer> dao = DaoManager.createDao(connectionSource, Issue.class);
        dao.delete(issue);
    }

    public List<Issue> getPendingIssues()
    {
        ConnectionSource connectionSource = getConnectionSource();

        try
        {
            Dao<Issue, Integer> dao = DaoManager.createDao(connectionSource, Issue.class);
            QueryBuilder<Issue, Integer> qb = dao.queryBuilder();
            qb.where().eq("status", "Pending").or().eq("status", "Error");
            qb.orderBy("issue_id", true);
            List<Issue> issueList = qb.query();

            return issueList;
        }
        catch (SQLException e)
        {
            return null;
        }
    }
}
