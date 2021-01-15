package com.sonjara.listenup.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;

@DatabaseTable(tableName="safety_issue_evidence")
public class IssueEvidence
{
    @DatabaseField(id = true)
    public int safety_issue_evidence_id;

    @DatabaseField
    public String name;

    @DatabaseField
    public int active;

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
}
