package com.sonjara.listenup.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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

    public List<Service> getServices()
    {
        List<Service> serviceList = new ArrayList<>();

        if (services == null || "".equals(services)) return serviceList;

        String[] serviceIds = services.split(",");

        for(String serviceId : serviceIds)
        {
            int id = Integer.parseInt(serviceId);
            Service service = DatabaseHelper.getInstance().getService(id);
            if (service != null)
            {
                serviceList.add(service);
            }
        }

        return serviceList;
    }

    public String getServicesText()
    {
        List<Service> services = getServices();
        String servicesText = "";
        for(Service s : services)
        {
            servicesText += s.name;
            servicesText += "\n";
        }

        return servicesText;
    }

    public boolean hasService(String[] ids)
    {
        // Empty filter always matches
        if (ids.length == 1 && "".equals(ids[0])) return true;

        LinkedList<String> serviceIds = new LinkedList<>(Arrays.asList(services.split(",")));

        for(String id : ids)
        {
            if (serviceIds.contains(id)) return true;
        }

        return false;
    }
}
