#!/usr/bin/env python3
# --coding:utf-8--

# Copyright (c) 2020 vesoft inc. All rights reserved.
#
# This source code is licensed under Apache 2.0 License,
# attached with Common Clause Condition 1.0, found in the LICENSES directory.

import collections


def gen_property(id: int):
    return {
        "idInt": id,
        "idString": str(id),
        "tboolean": id % 2 == 0,
        "tdouble": id + 0.01
    }


property_schema = collections.OrderedDict({
    "idInt": "int",
    "idString": "string",
    "tboolean": "boolean",
    "tdouble": "double"
})

edge_schema = {
    "from": ["idFrom", "int"],
    "to": ["idTo", "int"],
    "property": property_schema
}


def gen_vertex(id: int):
    return gen_property(id)


def gen_edge(id: int, from_tag: str, from_id: int, to_tag: str, to_id: int):
    return {
        "from": {
            "tag": from_tag,
            "match": {
                "idInt": from_id
            }
        },
        "to": {
            "tag": to_tag,
            "match": {
                "idInt": to_id
            }
        },
        "data": gen_property(id)
    }


def mock_data(start_id: int, prefix_name: str, tagA_num: int, tagB_num: int, a_a_num: int, a_b_num: int,
              edge_count_type: str):
    name_tagA = prefix_name + "tagA"
    name_tagB = prefix_name + "tagB"
    name_edgeAA = prefix_name + "edgeAA"
    name_edgeAB = prefix_name + "edgeAB"
    data = {"vertex": {
        name_tagA: {"data": [], "schema": property_schema},
        name_tagB: {"data": [], "schema": property_schema}
    },
        "edge": {
            name_edgeAA: {"data": [], "schema": edge_schema},
            name_edgeAB: {"data": [], "schema": edge_schema}
    }}
    tagA_data = data["vertex"][name_tagA]["data"]
    tagB_data = data["vertex"][name_tagB]["data"]
    edgeAA_data = data["edge"][name_edgeAA]["data"]
    edgeAB_data = data["edge"][name_edgeAB]["data"]

    ids_for_all = start_id
    for i in range(tagA_num):
        tagA_data.append(gen_vertex(ids_for_all))
        ids_for_all += 1

    for i in range(tagB_num):
        tagB_data.append(gen_vertex(ids_for_all))
        ids_for_all += 1

    if edge_count_type == 'count':
        for from_id in range(tagA_num):
            for to_id in range(from_id + 1, tagA_num):
                if (len(edgeAA_data) < a_a_num):
                    edgeAA_data.append(
                        gen_edge(ids_for_all, name_tagA, from_id, name_tagA, to_id))
                    ids_for_all += 1
                else:
                    break
            for to_id in range(tagB_num):
                if (len(edgeAB_data) < a_b_num):
                    edgeAB_data.append(
                        gen_edge(ids_for_all, name_tagA, from_id, name_tagB, to_id + tagA_num))
                    ids_for_all += 1
                else:
                    break
            if (len(edgeAA_data) >= a_a_num and len(edgeAB_data) >= a_b_num):
                break
    else:
        assert (tagA_num == tagB_num)
        assert (a_a_num < tagA_num and a_b_num < tagB_num)
        from_ids = list(range(tagA_num))
        to_a_ids = list(range(tagA_num))
        to_b_ids = [i + tagA_num for i in range(tagB_num)]
        times = 0
        while times < a_a_num:
            to_a_ids.insert(0, to_a_ids.pop())
            for i in range(tagA_num):
                edgeAA_data.append(
                    gen_edge(ids_for_all, name_tagA, from_ids[i], name_tagA, to_a_ids[i]))
                ids_for_all += 1
            times += 1
        times = 0
        while times < a_b_num:
            to_b_ids.insert(0, to_b_ids.pop())
            for i in range(tagB_num):
                edgeAB_data.append(
                    gen_edge(ids_for_all, name_tagA, from_ids[i], name_tagB, to_b_ids[i]))
                ids_for_all += 1
            times += 1

    return data


if __name__ == '__main__':
    import argparse
    import json

    parser = argparse.ArgumentParser(description='generate graph json')

    parser.add_argument('-o', '--output',
                        help='output path prefix', type=str, required=True)
    parser.add_argument('-a', '--tagAnum',
                        help='tagA num', type=int, default=100)
    parser.add_argument('-b', '--tagBnum', help='tag b num',
                        type=int, default=100)
    parser.add_argument('-aa', '--edgeAAnum', help='edge aa num',
                        type=int, default=100)
    parser.add_argument('-ab', '--edgeABnum', help='edge ab num',
                        type=int, default=100)
    parser.add_argument('-edge_count_type', '--edge_count_type', help="count: edge num, degree: edge degree",
                        choices=['count', 'degree'], default='count')
    parser.add_argument(
        '-p', '--prefix', help='tag/edge name prefix', type=str, default="")
    parser.add_argument("-s", '--start_id',
                        help='start id', default=0, type=int)
    parser.add_argument("-t", '--type', help="output format csv or json",
                        choices=['json', 'csv'], default='json')

    args = parser.parse_args()

    data = mock_data(args.start_id, args.prefix, args.tagAnum,
                     args.tagBnum, args.edgeAAnum, args.edgeABnum, args.edge_count_type)
    if args.type == 'json':
        with open(args.output+".json", "w") as f:
            json.dump(data, f, indent=4)
    elif args.type == 'csv':
        import csv
        import os
        import_sentences = []
        for name, v in data["vertex"].items():
            path = args.output+"{}.vertex.csv".format(name)
            import_sentences.append({
                'name': name,
                'hive_create': "CREATE TABLE {} ".format(name) + "({})".format(
                    ','.join(map(lambda k, t: k+' '+t.upper(), v['schema'].keys(), v['schema'].values()))) +
                ' row format serde \'org.apache.hadoop.hive.serde2.OpenCSVSerde\' with serdeproperties ("separatorChar"=",") stored as textfile;',
                'hive_import': "LOAD DATA LOCAL INPATH '{}' OVERWRITE INTO TABLE {};".format(os.path.abspath(path), name)
            })
            rows = map(lambda x: x.values(), v['data'])
            with open(path, 'w') as f:
                csv_writer = csv.writer(f)
                csv_writer.writerows(rows)

        for name, e in data['edge'].items():
            path = args.output+"{}.edge.csv".format(name)
            import_sentences.append({
                'name': name,
                'hive_create': "CREATE TABLE {} ".format(name) + "({})".format(
                    ','.join(list(map(lambda x:x[0]+" "+x[1].upper(), [e['schema']['from'], e['schema']['to']]))+list(map(lambda k, t: k+' '+t.upper(), e['schema']['property'].keys(), e['schema']['property'].values())))) +
                ' row format serde \'org.apache.hadoop.hive.serde2.OpenCSVSerde\' with serdeproperties ("separatorChar"=",") stored as textfile;',
                'hive_import': "LOAD DATA LOCAL INPATH '{}' OVERWRITE INTO TABLE {};".format(os.path.abspath(path), name)
            })
            rows = map(lambda x: [list(x['from']['match'].values())[0], list(
                x['to']['match'].values())[0]]+list(x['data'].values()), e['data'])
            with open(path, 'w') as f:
                csv_writer = csv.writer(f)
                csv_writer.writerows(rows)

        with open(args.output+"import.sentence.txt", 'w') as f:
            # json.dump(import_sentences, f, indent=4)
            for item in import_sentences:
                for k,v in item.items():
                    f.write("{}:\n{}\n".format(k, v))
                f.write("="*30+"\n")
            
