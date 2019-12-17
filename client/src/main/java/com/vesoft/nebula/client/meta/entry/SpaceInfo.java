package com.vesoft.nebula.client.meta.entry;

import com.vesoft.nebula.meta.IdName;

public class SpaceInfo {

    private String name;
    private int id;

    public SpaceInfo(IdName entry) {
        name = entry.name;
        id = entry.id.getSpace_id();
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
