# scripts for spark importer

## install dependence

```shell
sudo pip3 install -r requirements.txt
```

## scripts introduction

### mock_data.py

#### overview of mock_data

use to generate graph data(json format).

it will generate two types vertex: xxtagA, xxtagB, And two types edge: xxedgeAA, xxedgeAB

#### args of mock_data

1. -o/--output

    - overview: output json path

    - type: string

    - required

2. -a/--tagAnum

    - overview: the number of vertex of tagA

    - type: int

    - default: 100

3. -b/--tagBnum

    - overview: the number of vertex of tagB

    - type: int

    - default: 100

4. -edge_count_type/--edge_count_type

    - overview: the edge numbers count type. it has two options:

        1. count: the -aa or -ab args will be seen as number of edge.

        2. degree: the -aa or -ab args will be seen as number of tagA's out-degree of tagA/tagB. when set that, the number of tagA must equal tagB's.

    - type: string

    - default: count

5. -aa/--edgeAAnum

    - overview: the number of edge of edgeAA. this type edge use tagA as source vertex, and tagA as target vertex.

    - typoe: int

    - default: 100

6. -ab/--edgeABnum

    - overview: the number of edge of edgeAB. this type edge use tagA as source vertex, and tagB as target vertex.

    - typoe: int

    - default: 100

7. -p/--prefix

    - overview: this use to the prefix in tag/edge name

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

### json2neo4j.py

#### overview of json2neo4j

use to import json data to neo4j database.

it will print the time of use.

#### args of json2neo4j

1. -f/--file

    - overview: the json file path that your want to import to neo4j.

    - type: string

    - required

2. -a/-address

    - overview: the address of neo4j.

    - type: string

    - default: bolt://127.0.0.1:7687

3. -u/--user

    - overview: the user of neo4j.

    - type: string

    - default: neo4j

4. -p/--password

    - overview: the password of neo4j.

    - type: string

    - default: neo4j

5. -t/--thread_num

    - overview: the thread number of import to neo4j use .

### verify_nebula.py

#### overview of verify_nebula

use to verify nebula database data.

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
