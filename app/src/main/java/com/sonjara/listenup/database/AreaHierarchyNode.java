package com.sonjara.listenup.database;

public class AreaHierarchyNode
{
    private int m_area_id;
    private int m_parent_id;
    private String m_name;
    private int m_level;

    public int getArea_id()
    {
        return m_area_id;
    }

    public void setArea_id(int area_id)
    {
        m_area_id = area_id;
    }

    public int getParent_id()
    {
        return m_parent_id;
    }

    public void setParent_id(int parent_id)
    {
        m_parent_id = parent_id;
    }

    public String getName()
    {
        return m_name;
    }

    public void setName(String name)
    {
        m_name = name;
    }

    public int getLevel()
    {
        return m_level;
    }

    public void setLevel(int level)
    {
        m_level = level;
    }
}
