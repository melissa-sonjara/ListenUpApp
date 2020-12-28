package com.sonjara.listenup.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Date;
import java.sql.Timestamp;

@DatabaseTable(tableName = "service")
public class Service
{
    @DatabaseField(id = true)
    public int service_id;

    @DatabaseField
    public String name;

    @DatabaseField
    public String description;

    @DatabaseField
    public int image_id;

    @DatabaseField
    public Boolean show_in_hot_list;

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

    public Service()
    {
    }
}
