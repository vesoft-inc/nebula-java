package org.apache.flink.connector.nebula.utils;


public enum PolicyEnum {
    /** HASH policy */
    HASH("HASH"),

    /** UUID policy */
    UUID("UUID");

    private String type;

    PolicyEnum(String type) {
        this.type = type;
    }

    public String policy(){
        return type;
    }
}
