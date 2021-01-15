package com.sonjara.listenup.database;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;
import java.util.List;

@DatabaseTable(tableName = "issue_type")
public class IssueType
{
    @DatabaseField(id = true)
    public int issue_type_id;

    @DatabaseField
    public String name;

    @DatabaseField
    public int active;

    @DatabaseField
    public String description;

    @DatabaseField
    public int sort_order;

    @DatabaseField
    public int created_by_id;

    @DatabaseField
    public Timestamp created_date;

    @DatabaseField
    public int last_modified_by_id;

    @DatabaseField
    public Timestamp last_modified;

    public List<SubIssueType> getSubTypes()
    {
        DatabaseHelper db = DatabaseHelper.getInstance();
        return db.getIssueSubTypes(issue_type_id);
    }
}
