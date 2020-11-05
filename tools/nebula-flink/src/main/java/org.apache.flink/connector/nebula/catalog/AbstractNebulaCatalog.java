package org.apache.flink.connector.nebula.catalog;

/**
 * AbstractNebulaCatalog is used to get nebula schema
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
