package org.apache.flink.connector.nebula.catalog;

/**
 * 用于读取nebula的元数据
 */
public interface AbstractNebulaCatalog {

    boolean graphSpaceExists(String graphSpace);
    boolean tagExists(String graphSpace, String tag);
    boolean edgeExists(String graphSpace, String edge);

    void listSpaces();
    void getEdges(String graphSpace);
    void getTags(String graphSpace);

    void getTagProperties(String graphSpace, String tag);
    void getEdgeProperties(String graphSpace, String edge);

}
