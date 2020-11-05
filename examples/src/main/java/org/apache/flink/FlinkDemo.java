package org.apache.flink;

import com.google.common.net.HostAndPort;
import com.vesoft.nebula.client.meta.MetaClientImpl;
import com.vesoft.nebula.client.storage.StorageClientImpl;
import com.vesoft.nebula.client.storage.processor.ScanVertexProcessor;
import com.vesoft.nebula.data.Property;
import com.vesoft.nebula.data.Result;
import com.vesoft.nebula.data.Row;
import com.vesoft.nebula.storage.ScanVertexResponse;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.connector.nebula.connection.NebulaClientOptions;
import org.apache.flink.connector.nebula.connection.NebulaConnectionProvider;
import org.apache.flink.connector.nebula.connection.NebulaGraphConnectionProvider;
import org.apache.flink.connector.nebula.connection.NebulaMetaConnectionProvider;
import org.apache.flink.connector.nebula.source.NebulaInputFormat;
import org.apache.flink.connector.nebula.source.NebulaSourceFunction;
import org.apache.flink.connector.nebula.statement.ExecutionOptions;
import org.apache.flink.connector.nebula.sink.AbstractNebulaOutPutFormat;
import org.apache.flink.connector.nebula.sink.NebulaBatchOutputFormat;
import org.apache.flink.connector.nebula.sink.NebulaSinkFunction;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class FlinkDemo {
    private static final Logger LOG = LoggerFactory.getLogger(FlinkDemo.class);

    private static final String address = "127.0.0.1:45500";
    private static final String username = "root";
    private static final String password = "nebula";
    private static final String namespace = "nb";
    private static final String label = "player";
    private static final ExecutionOptions sourceExecutionOptions;
    private static final ExecutionOptions sinkExecutionOptions;
    private static final NebulaConnectionProvider graphConnectionProvider;
    private static final NebulaConnectionProvider metaConnectionProvider;

    static{
        NebulaClientOptions nebulaClientOptions = new NebulaClientOptions
                .NebulaClientOptionsBuilder()
                .setAddress("127.0.0.1:3699")
                .build();
        graphConnectionProvider = new NebulaGraphConnectionProvider(nebulaClientOptions);

        NebulaClientOptions nebulaClientOptions1 = new NebulaClientOptions
                .NebulaClientOptionsBuilder()
                .setAddress("127.0.0.1:45500")
                .build();
        metaConnectionProvider = new NebulaMetaConnectionProvider(nebulaClientOptions1);

        List<String> cols = new ArrayList<>();
        cols.add("name");
        cols.add("age");
        sourceExecutionOptions = new ExecutionOptions.ExecutionOptionBuilder().setDataType("VERTEX")
                .setGraphSpace("flinkSource")
                .setLabel(label)
                .setFields(cols)
                .setLimit(100)
                .builder();
        sinkExecutionOptions = new ExecutionOptions.ExecutionOptionBuilder().setDataType("VERTEX")
                .setGraphSpace("flinkSink")
                .setLabel(label)
                .setFields(cols)
                .setIdIndex(0)
                .setBatch(2)
                .builder();

    }

    public static void main(String[] args) throws Exception {
//        testNebulaSinkFunction();
//        testSourceSink();
//        testSourceSink();
		testNebulaSinkFunction();
    }

    public static void addNebulaSource() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        // customize datasource
        SourceFunction source = new SourceFunction<List<String>>() {
            private static final long serialVersionUID = -7958462911936661287L;
            private volatile boolean isRunning = true;

            @Override
            public void run(SourceContext<List<String>> ctx) throws Exception {
                List<HostAndPort> hostAddress = new ArrayList<>();
                hostAddress.add(HostAndPort.fromString(address));
                MetaClientImpl metaClient = new MetaClientImpl(hostAddress);
                metaClient.connect();
                StorageClientImpl storageClient = new StorageClientImpl(metaClient);

                Map<String, List<String>> returnCols = new HashMap<>();
                List<String> cols = new ArrayList<>();
                cols.add("name");
                returnCols.put("player", cols);
                Iterator<ScanVertexResponse> scanVertexResponseIterator = storageClient.scanVertex(namespace, returnCols);
                if (!scanVertexResponseIterator.hasNext()) {
                    LOG.error("**** empty vertexScan result");
                }
                ScanVertexProcessor processor = new ScanVertexProcessor(metaClient);

                while (scanVertexResponseIterator.hasNext()) {
                    LOG.info("**** start to process nebula vertex");
                    Result result = processor.process(namespace, scanVertexResponseIterator.next());
                    List<Row> rows = result.getRows("player");

                    for (Row row : rows) {
                        Property[] properties = row.getProperties();
                        LOG.info("**** flink read nebula player:" + properties);
                        List<String> values = new ArrayList<String>();
                        for (Property prop : properties) {
                            values.add(prop.getValue().toString());
                        }
                        ctx.collect(values);
                    }
                }
            }

            @Override
            public void cancel() {
                isRunning = false;
            }
        };
        DataStream<List<String>> dataStream = env.addSource(source);
        dataStream.map(Object::toString).print();
        env.execute("scan nebula nb.player");
    }


    public static void testNebulaSource() throws Exception {

        NebulaInputFormat inputFormat = new NebulaInputFormat(metaConnectionProvider)
                .setExecutionOptions(sourceExecutionOptions);

        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSource<org.apache.flink.types.Row> dataSource = env.createInput(inputFormat);
        LOG.info("data source size={}", dataSource.count());
        dataSource.print();
    }


    public static void testNebulaSinkFunction() throws Exception {
        // source
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
		env.enableCheckpointing(10000)
				.getCheckpointConfig()
				.setCheckpointingMode(CheckpointingMode.AT_LEAST_ONCE);

		env.getCheckpointConfig()
				.setCheckpointTimeout(1*60000);
		env.getCheckpointConfig().setMaxConcurrentCheckpoints(3);
		env.setStateBackend(new FsStateBackend("file:///Users/nicole/tmp"));

        // construct source
        List<List<String>> player = new ArrayList<>();
        List<String> fields1 = new ArrayList<>();
        fields1.add("15");
        fields1.add("nicole");
        fields1.add("18");
        player.add(fields1);

        List<String> fields2 = new ArrayList<>();
        fields2.add("16");
        fields2.add("nicole");
        fields2.add("19");
        player.add(fields2);

        List<String> fields3 = new ArrayList<>();
        fields3.add("17");
        fields3.add("nicole");
        fields3.add("20");
        player.add(fields3);


        DataStream<List<String>> playerSource = env.fromCollection(player);
        playerSource.print();
        playerSource.countWindowAll(1);

        // sink
        AbstractNebulaOutPutFormat outPutFormat = new NebulaBatchOutputFormat(graphConnectionProvider)
                .setExecutionOptions(sinkExecutionOptions);
        NebulaSinkFunction nebulaSinkFunction = new NebulaSinkFunction(outPutFormat);

        playerSource.map(row -> {
            org.apache.flink.types.Row record = new org.apache.flink.types.Row(row.size());
            for (int i = 0; i < row.size(); i++) {
                record.setField(i, row.get(i));
            }
            LOG.info("record={}", record);
            return record;
        }).addSink(nebulaSinkFunction);
        env.execute("nebula read and write");
    }

    /** read from nebula and then write into nebula */
    public static void testSourceSink() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(10000)
				.getCheckpointConfig()
				.setCheckpointingMode(CheckpointingMode.AT_LEAST_ONCE);

        env.getCheckpointConfig()
				.setCheckpointTimeout(20*3600);
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
//        env.setStateBackend(new FsStateBackend("hdfs://127.0.0.1:9000/flink/checkpoints"));

        // source
        NebulaSourceFunction sourceFunction = new NebulaSourceFunction(metaConnectionProvider).setExecutionOptions(sourceExecutionOptions);
        DataStreamSource<Row> dataSource = env.addSource(sourceFunction);
        dataSource.map(row ->{
            LOG.info("record={}",row);
            return row;
        });

        // sink
        AbstractNebulaOutPutFormat outPutFormat = new NebulaBatchOutputFormat(graphConnectionProvider)
                .setExecutionOptions(sinkExecutionOptions);
        NebulaSinkFunction nebulaSinkFunction = new NebulaSinkFunction(outPutFormat);

        dataSource.print();
        dataSource.map(row->{
            org.apache.flink.types.Row record = new org.apache.flink.types.Row(3);
            record.setField(0, row.getDefaultProperties()[0].getValue().toString());
            record.setField(1, row.getProperties()[0].getValue().toString());
            record.setField(2, row.getProperties()[1].getValue().toString());
            return record;
        }).addSink(nebulaSinkFunction);

        env.execute("NebulaSourceSink");
    }
}
