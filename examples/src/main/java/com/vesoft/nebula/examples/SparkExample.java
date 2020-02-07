package com.vesoft.nebula.examples;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spark_project.guava.collect.Lists;
import scala.Tuple2;

public class SparkExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(SparkExample.class);

    private static void spark(String url, String csvPath) {
        SparkSession spark = SparkSession
                .builder()
                .master(url)
                .appName("NebulaSparkExample")
                .getOrCreate();

        JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());

        // Each row in csv represents a edge in nebula, there would be 3 field in each row:
        // [src, dst, property value]
        JavaRDD<String> rows = jsc.textFile(csvPath);
        JavaRDD<String[]> edges = rows.map((String line) -> line.split(","));
        JavaPairRDD<Long, String[]> edgePairs =
                edges.mapToPair(edge -> new Tuple2(Long.valueOf(edge[0]), edge));

        long distinctSrcCount = edgePairs.keys().distinct().count();
        LOGGER.info("We have " + distinctSrcCount + "distinct src vertex");
        Map<Long, Long> edgesCountBySrc = edgePairs.countByKey();

        Set<Map.Entry<Long, Long>> sorted = new TreeSet<>(new Comparator<Map.Entry<Long, Long>>() {
            @Override
            public int compare(Map.Entry<Long, Long> o1, Map.Entry<Long, Long> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        sorted.addAll(edgesCountBySrc.entrySet());

        long vertexWithMostEdges = 0;
        for (Map.Entry<Long, Long> entry : sorted) {
            vertexWithMostEdges = entry.getKey();
            LOGGER.info("Vertex with most out edges " + entry.getKey()
                        + ", edge count: " + entry.getValue());
            break;
        }

        List<String[]> edgesOfBigVertex = edgePairs.lookup(vertexWithMostEdges);
        for (String[] edge : edgesOfBigVertex) {
            LOGGER.info("edge: " + edge[0] + " -> " + edge[1] + ", value = " + edge[2]);
        }

        JavaPairRDD<Long, Long> values = edgePairs.mapValues(edge -> Long.valueOf(edge[2]));
        Map<Long, Long> valueSumBySrc = values.foldByKey(
                new Long(0), (Long v1, Long v2) -> v1 + v2).collectAsMap();
        LOGGER.info("Value sum of vertex with most edges: "
                    + valueSumBySrc.get(vertexWithMostEdges));

        // group by src, and then sort by dst and value
        JavaPairRDD<Long, Iterable<String[]>> groupedEdges = edgePairs.groupByKey();
        JavaPairRDD<Long, Iterable<String[]>> sortedEdges = groupedEdges.mapValues(
            new Function<Iterable<String[]>, Iterable<String[]>>() {
                @Override
                public Iterable<String[]> call(Iterable<String[]> strings) throws Exception {
                    List<String[]> result = Lists.newArrayList(strings);
                    Collections.sort(result, new Comparator<String[]>() {
                        @Override
                        public int compare(String[] e1, String[] e2) {
                            Long dst1 = Long.valueOf(e1[1]);
                            Long dst2 = Long.valueOf(e2[1]);
                            if (dst1.equals(dst2)) {
                                return Long.valueOf(e1[2]).compareTo(Long.valueOf(e2[2]));
                            }
                            return dst1.compareTo(dst2);
                        }
                    });
                    return result;
                }
            }
        );
        Map<Long, Iterable<String[]>> sortedEdgesMap = sortedEdges.collectAsMap();
        for (String[] edge : sortedEdgesMap.get(vertexWithMostEdges)) {
            LOGGER.info("edge: " + edge[0] + " -> " + edge[1] + ", value = " + edge[2]);
        }

        jsc.stop();
        spark.stop();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: + com.vesoft.nebula.examples.SparkExample"
                    + "<spark master url> <edge csv path>");
            return;
        }
        spark(args[0], args[1]);
    }
}
