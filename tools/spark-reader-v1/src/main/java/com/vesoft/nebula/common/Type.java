package com.vesoft.nebula.common;

public enum Type {

    VERTEX("VERTEX"),EDGE("EDGE");

    private String type;

    Type(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }


}
