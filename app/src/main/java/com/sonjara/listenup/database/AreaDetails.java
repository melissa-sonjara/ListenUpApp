package com.sonjara.listenup.database;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;

@DatabaseTable(tableName = "area_details")
public class AreaDetails
{
    @DatabaseField(id = true)
    public int area_id;

    @DatabaseField
    public int parent_id;

    @DatabaseField
    public String name;

    @DatabaseField
    public int admin_level_id;

    @DatabaseField
    public double latitude;

    @DatabaseField
    public double longitude;

    @DatabaseField( dataType = DataType.LONG_STRING)
    public String boundary;

    @DatabaseField
    public int external_id;

    @DatabaseField
    public String admin_level;

    @DatabaseField
    public String admin_level_name;

    @DatabaseField
    public int created_by_id;

    @DatabaseField
    public Timestamp created_date;

    @DatabaseField
    public int last_modified_by_id;

    @DatabaseField
    public Timestamp last_modified;
}