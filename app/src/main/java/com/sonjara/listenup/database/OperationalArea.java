package com.sonjara.listenup.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;

@DatabaseTable(tableName = "operational_area")
public class OperationalArea
{
    @DatabaseField(id = true)
    public int operational_area_id;

    @DatabaseField
    public String name;

    @DatabaseField
    public int region_image_id;

    @DatabaseField
    public int area_image_id;

    @DatabaseField
    public int toolkit_id;

    @DatabaseField
    public int referral_pathway_id;

    @DatabaseField
    public int active;

    @DatabaseField
    public int is_default;
}
