# scripts for spark importer

## install dependence

```shell
sudo pip3 install -r requirements.txt
```

## scripts introduction

### mock_data.py

#### overview of mock_data

Use to generate graph data.

It will generate two types vertex: xxtagA, xxtagB, And two types edge: xxedgeAA, xxedgeAB.

#### args of mock_data

1. -o/--output

    - overview: output address

    - type: string

    - required

    - options:

    If set `-t` different, this arg is different.

    | `-t`  | `-o`                                                    | `-o` example                |
    | ----- | ------------------------------------------------------- | --------------------------- |
    | json  | json output path prefix, not have to add `.json` suffix | /path/data                  |
    | csv   | csv output path prefix, not have to add `.csv` suffix   | /path/data                  |
    | sql   | sql output path prefix, not have to add `.sql` suffix   | /path/data                  |
    | neo4j | neo4j bolt server address                               | bolt://localhost:7687       |
    | janus | janus ws server address                                 | ws://localhost:8182/gremlin |

2. -t/--type

    - overview: type of output address

    - choices: json, csv, sql, neo4j, janus

    - default: json

3. -a/--tagAnum

    - overview: the number of vertex of tagA.

    - type: int

4. -b/--tagBnum

    - overview: the number of vertex of tagB.

    - type: int

5. -edge_count_type/--edge_count_type

    - overview: the edge numbers count type. it has two options:

        1. count: the -aa or -ab args will be seen as number of edge.

        2. degree: the -aa or -ab args will be seen as number of tagA's out-degree of tagA/tagB. when set that, the number of tagA must equal tagB's.

    - type: string

    - default: count

6. -aa/--edgeAAnum

    - overview: the number of edge of edgeAA. this type edge use tagA as source vertex, and tagA as target vertex.

    - type: int

7. -ab/--edgeABnum

    - overview: the number of edge of edgeAB. this type edge use tagA as source vertex, and tagB as target vertex.

    - type: int

8. -p/--prefix

    - overview: this use to the prefix in tag/edge name.

9. -u/--user

    - overview: server auth username, if you need.

10. -p/--password

    - overview: server auth password, if you need.

#### output format

```json
{
    "vertex": {
        "tag_name": {
            "data": [
                {
                    "property_key": "property_value"
                }
            ]
        }
    },

    "edge": {
        "edge_name": {
            "data": [
                {
                    "from": {
                        "tag": "source tag name",
                        "match": {
                            "primary_key": "primary_value"
                        }
                    },
                    "to": {
                        "tag": "target tag name",
                        "match": {
                            "primary_key": "primary_value"
                        }
                    },
                    "data": {
                    "property_key": "property_value"
                    }
                }
            ]
        }
    }
}
```

### pulsar_producer.py

#### overview of pulsar

Use to generate stream of pulsar

#### args of pulsar_producer.py

1. -a/--address

    - overview: the pulsar server address

    - type: string

    - default: pulsar://localhost:6650

2. -b/-batch_size

    - overview: the batch size per interval

    - type: int

    - default: 10

3. -i/--interval

    - overview: pulsar interval seconds

    - type: int

    - default: 10

4. -s/--schema_type

    - overview: the output data schema type

    - type: string

    - choices: avro, json

    - default: avro

5. -t/--tag_topics

    - overview: the tag topics, can add more than one. But -t or -e only one can set

6. -e/--edge_topics

    - overview: the edge topics, can add more than one. But -t or -e only one can set

### verify_nebula.py

#### overview of verify_nebula

Use to verify nebula database data.

#### args of verify_nebula

1. -a/--address

    - overview: the address of nebula database.

    - type: string

    - defalut: 127.0.0.1:3699

2. -u/--user

    - overview: the user of nebula.

    - type: string

    - default: user

3. -p/--password

    - overview: the password of nebula.

    - type: string

    - default: password

4. -j/--jsons

    - overview: the json files use to verify.if have many json file, you can use space separate those.

    - type: string

5. -c/--config

    - overview: spark importer use config path.

    - type: string

    - default: application.conf

6. -l/--log_level

    - overview: set log level. (10:debug, 20:info, 30:warn, 40:error)

    - type: int

    - default: 30
