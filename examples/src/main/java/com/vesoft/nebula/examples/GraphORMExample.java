/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.examples;

import com.google.common.collect.Lists;
import com.vesoft.nebula.annotation.GraphEdge;
import com.vesoft.nebula.annotation.GraphProperty;
import com.vesoft.nebula.annotation.GraphVertex;
import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import com.vesoft.nebula.domain.VertexQuery;
import com.vesoft.nebula.domain.impl.QueryResult;
import com.vesoft.nebula.engine.NebulaVertexQuery;
import com.vesoft.nebula.enums.GraphDataTypeEnum;
import com.vesoft.nebula.enums.GraphKeyPolicy;
import com.vesoft.nebula.enums.GraphPropertyTypeEnum;
import com.vesoft.nebula.mapper.NebulaGraphMapper;
import com.vesoft.nebula.session.NebulaPoolSessionManager;
import java.net.UnknownHostException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class GraphORMExample {

    private static int nebulaPoolMaxConnSize = 1000;

    private static int nebulaPoolMinConnSize = 50;

    private static int nebulaPoolIdleTime = 180000;

    private static int nebulaPoolTimeout = 300000;

    private static String nebulaCluster = "127.0.0.1:5569";

    private static String userName = "root";

    private static String password = "nebula";

    private static String space = "test";

    public static NebulaPoolConfig nebulaPoolConfig() {
        NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
        nebulaPoolConfig.setMaxConnSize(nebulaPoolMaxConnSize);
        nebulaPoolConfig.setMinConnSize(nebulaPoolMinConnSize);
        nebulaPoolConfig.setIdleTime(nebulaPoolIdleTime);
        nebulaPoolConfig.setTimeout(nebulaPoolTimeout);
        return nebulaPoolConfig;
    }

    public static NebulaPool nebulaPool(NebulaPoolConfig nebulaPoolConfig)
            throws UnknownHostException {
        List<HostAddress> addresses = null;
        try {
            String[] hostPorts = StringUtils.split(nebulaCluster, ",");
            addresses = Lists.newArrayListWithExpectedSize(hostPorts.length);
            for (String hostPort : hostPorts) {
                String[] linkElements = StringUtils.split(hostPort, ":");
                HostAddress hostAddress = new HostAddress(linkElements[0],
                        Integer.valueOf(linkElements[1]));
                addresses.add(hostAddress);
            }
        } catch (Exception e) {
            throw new RuntimeException("nebula数据库连接信息配置有误，正确格式：ip1:port1,ip2:port2");
        }
        NebulaPool pool = new NebulaPool();
        pool.init(addresses, nebulaPoolConfig);
        return pool;
    }

    public static NebulaPoolSessionManager nebulaPoolSessionManager(NebulaPool nebulaPool) {
        return new NebulaPoolSessionManager(nebulaPool, userName, password, true);
    }

    public static NebulaGraphMapper nebulaGraphMapper(
            NebulaPoolSessionManager nebulaPoolSessionManager) {
        return new NebulaGraphMapper(nebulaPoolSessionManager, space);
    }

    public static void main(String[] args) throws UnknownHostException {
        NebulaGraphMapper nebulaGraphMapper = nebulaGraphMapper(nebulaPoolSessionManager(
                nebulaPool(nebulaPoolConfig())));
        User user = new User("UR123", "张三");
        //保存顶点
        int i = nebulaGraphMapper.saveVertexEntities(Lists.newArrayList(user));
        //查询顶点
        List<User> users = nebulaGraphMapper.fetchVertexTag(User.class, "UR123");
        //保存边和查询边类似
        Follow follow = new Follow("UR123", "UR234", 1);
        //保存边
        nebulaGraphMapper.saveEdgeEntities(Lists.newArrayList(follow));
        //查询出边
        List<Follow> follows = nebulaGraphMapper.goOutEdge(Follow.class, "UR123");
        //查询反向边
        List<Follow> fans = nebulaGraphMapper.goReverseEdge(Follow.class, "UR123");
        //查询API
        VertexQuery query = NebulaVertexQuery.build().fetchPropOn(User.class, "UR123")
                .yield("userName");
        QueryResult rows = nebulaGraphMapper.executeQuery(query);
    }

    @GraphVertex(value = "user", keyPolicy = GraphKeyPolicy.string_key)
    public static class User {
        @GraphProperty(value = "user_no", required = true,
                propertyTypeEnum = GraphPropertyTypeEnum.GRAPH_VERTEX_ID)
        private String userNo;
        @GraphProperty(value = "user_name", required = true)
        private String userName;

        public User() {
        }

        public User(String userNo, String userName) {
            this.userNo = userNo;
            this.userName = userName;
        }
    }

    @GraphEdge(value = "user", srcVertex = User.class, dstVertex = User.class)
    public static class Follow {
        @GraphProperty(value = "user_no1", required = true,
                propertyTypeEnum = GraphPropertyTypeEnum.GRAPH_EDGE_SRC_ID)
        private String userNo1;
        @GraphProperty(value = "user_no2", required = true,
                propertyTypeEnum = GraphPropertyTypeEnum.GRAPH_EDGE_DST_ID)
        private String userNo2;
        @GraphProperty(value = "follow_type", required = true, dataType = GraphDataTypeEnum.INT)
        private Integer followType;

        public Follow() {
        }

        public Follow(String userNo1, String userNo2, Integer followType) {
            this.userNo1 = userNo1;
            this.userNo2 = userNo2;
            this.followType = followType;
        }
    }

}
