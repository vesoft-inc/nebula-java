package org.apache.flink.connector.nebula.utils;

public enum DataTypeEnum {
    VERTEX("VERTEX"),

    EDGE("EDGE");

    private String type;

    DataTypeEnum(String type){
        this.type = type;
    }

    public boolean isVertex(){
        if(VERTEX.type.equals(this.type)){
            return true;
        }
        return false;
    }
}
