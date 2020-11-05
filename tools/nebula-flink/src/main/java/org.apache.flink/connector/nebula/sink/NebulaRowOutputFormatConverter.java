package org.apache.flink.connector.nebula.sink;

import com.esotericsoftware.minlog.Log;
import org.apache.flink.connector.nebula.statement.ExecutionOptions;
import org.apache.flink.connector.nebula.utils.NebulaConstant;
import org.apache.flink.connector.nebula.utils.PolicyEnum;
import org.apache.flink.types.Row;

import java.util.ArrayList;
import java.util.List;

public class NebulaRowOutputFormatConverter implements NebulaOutputFormatConverter<Row> {

    private static final long serialVersionUID = -7728344698410737677L;

    private final int idIndex;
    private final int srcIdIndex;
    private final int dstIdIndex;
    private final int rankIndex;

    public NebulaRowOutputFormatConverter(ExecutionOptions executionOptions) {
        this.idIndex = executionOptions.getIdIndex();
        this.srcIdIndex = executionOptions.getSrcIndex();
        this.dstIdIndex = executionOptions.getDstIndex();
        this.rankIndex = executionOptions.getRankIndex();
    }

    /**
     * convert row to nebula's insert values
     *
     * @param row       flink row
     * @param isVertex  true if row represents vertex
     * @param policy    see {@link PolicyEnum}
     *
     * @return String
     */
    @Override
    public String createValue(Row row, Boolean isVertex, PolicyEnum policy) {
        if(row == null|| row.equals("")){
            Log.error("empty row");
            return null;
        }
        if (isVertex) {
            Object id = row.getField(idIndex);
            if (id == null) {
                return null;
            }
            List<String> vertexProps = new ArrayList<>();
            for (int i = 0; i < row.getArity(); i++) {
                if (i != idIndex) {
                    vertexProps.add(String.valueOf(row.getField(i)));
                }
            }
            if (policy == null) {
                return String.format(NebulaConstant.VERTEX_VALUE_TEMPLATE, id.toString(), String.join(",", vertexProps));
            } else{
                return String.format(NebulaConstant.VERTEX_VALUE_TEMPLATE_WITH_POLICY, policy, id.toString(), String.join(",", vertexProps));
            }
        }

        Object srcId = row.getField(srcIdIndex);
        Object dstId = row.getField(dstIdIndex);
        if(srcId == null || dstId == null){
            return null;
        }
        List<String> edgeProps = new ArrayList<>();
        for(int i=0; i<row.getArity(); i++){
            if(i != srcIdIndex && i!= dstIdIndex){
                edgeProps.add(row.getField(i).toString());
            }
        }
        String srcFormatId = srcId.toString();
        String dstFormatId = dstId.toString();
        if(policy == null){
            srcFormatId = String.format(NebulaConstant.ENDPOINT_TEMPLATE, policy.policy(), srcId.toString());
            dstFormatId = String.format(NebulaConstant.ENDPOINT_TEMPLATE, policy.policy(), dstId.toString());
        }
        if(rankIndex >= 0){
            assert row.getField(rankIndex) != null;
            String rank = row.getField(rankIndex).toString();
            return String.format(NebulaConstant.EDGE_VALUE_TEMPLATE, srcFormatId, dstFormatId, rank, edgeProps);
        } else{
            return String.format(NebulaConstant.EDGE_VALUE_WITHOUT_RANKING_TEMPLATE, srcFormatId, dstFormatId, edgeProps);
        }
    }
}
