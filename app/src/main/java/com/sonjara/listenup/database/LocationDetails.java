package com.sonjara.listenup.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;

@DatabaseTable(tableName="location_details")
public class LocationDetails
{
    @DatabaseField(id = true)
    public int location_id;

    @DatabaseField
    public int area_id;

    @DatabaseField
    public String organization_name;

    @DatabaseField
    public String name;

    @DatabaseField
    public String address;

    @DatabaseField
    public String contact_name;

    @DatabaseField
    public String contact_phone;

    @DatabaseField
    public String hours_of_service;

    @DatabaseField
    public String notes;

    @DatabaseField
    public String latitude;

    @DatabaseField
    public String longitude;

    @DatabaseField
    public String services;

    @DatabaseField
    public int created_by_id;

    @DatabaseField
    public Timestamp created_date;

    @DatabaseField
    public int last_modified_by_id;

    @DatabaseField
    public Timestamp last_modified;
}
