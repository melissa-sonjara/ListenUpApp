package com.sonjara.listenup.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Date;
import java.sql.Timestamp;

@DatabaseTable(tableName = "safety_issue")
public class Issue
{
    @DatabaseField(generatedId = true)
    public int issue_id;

    @DatabaseField
    public int safety_issue_id;

    @DatabaseField
    public String status;

    @DatabaseField
    public int safety_issue_source_id;

    @DatabaseField
    public int issue_type_id;

    @DatabaseField
    public int sub_issue_type_id;

    @DatabaseField
    public String safety_issue_source_note;

    @DatabaseField
    public String related_services;

    @DatabaseField
    public String title;

    @DatabaseField
    public String description;

    @DatabaseField
    public String recommendation;

    @DatabaseField
    public String latitude;

    @DatabaseField
    public String longitude;

    @DatabaseField
    public Date date_collected;

    @DatabaseField
    public String contact_name;

    @DatabaseField
    public int reported_by_id;

    @DatabaseField
    public Timestamp reported_date;

    @DatabaseField
    public int last_updated_by_id;

    @DatabaseField
    public Timestamp last_updated;

    @DatabaseField
    public String evidence;

    @DatabaseField
    public String camp_services;

    @DatabaseField
    public String areas;

    public IssueType getIssueType()
    {
        if (issue_type_id == 0) return null;

        DatabaseHelper db = DatabaseHelper.getInstance();
        return db.getIssueType(issue_type_id);
    }
}
