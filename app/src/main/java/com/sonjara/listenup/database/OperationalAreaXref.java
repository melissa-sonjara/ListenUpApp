package com.sonjara.listenup.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;

@DatabaseTable(tableName = "operational_area_xref")
public class OperationalAreaXref
{
    @DatabaseField(id = true)
    public int xref_id;

    @DatabaseField
    public int operational_area_id;

    @DatabaseField
    public int area_id;
}
