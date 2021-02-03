package com.sonjara.listenup.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "mobile_user_details")
public class MobileUserDetails
{
    @DatabaseField(id = true)
    public int user_id;

    @DatabaseField
    public String name;

    @DatabaseField
    public String organization;

    @DatabaseField
    public int operational_area_id;

    @DatabaseField
    public int active;
}
