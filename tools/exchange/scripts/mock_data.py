#!/usr/bin/env python3
# --coding:utf-8--

# Copyright (c) 2020 vesoft inc. All rights reserved.
#
# This source code is licensed under Apache 2.0 License,
# attached with Common Clause Condition 1.0, found in the LICENSES directory.

import collections
import logging

logging.basicConfig(level=logging.INFO,
                    format='%(asctime)s-%(levelname)s-%(message)s')
logger = logging.getLogger(__name__)


def gen_property(id: int):
    return {
        "idInt": id,
        "idString": str(id),
        "tboolean": id % 2 == 0,
        "tdouble": id + 0.01
    }


property_schema = collections.OrderedDict({
    "idInt": {"hive": "int", "sql": "int"},
    "idString": {"hive": "string", "sql": "text"},
    "tboolean": {"hive": "boolean", "sql": "bool"},
    "tdouble": {"hive": "double", "sql": "double"}
})

edge_schema = {
    "idFrom": {"hive": "int", "sql": "int"},
    "idTo": {"hive": "int", "sql": "int"},
    "property": property_schema
}


def gen_vertex(id: int):
    return gen_property(id)


def value2str(value):
    if isinstance(value, str):
        return '"{}"'.format(value)
    return str(value)


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


def mock_data(start_id: int, prefix_name: str, tagA_num: int, tagB_num: int,
              a_a_num: int, a_b_num: int, edge_count_type: str):
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
                        gen_edge(ids_for_all, name_tagA, from_id, name_tagA,
                                 to_id))
                    ids_for_all += 1
                else:
                    break
            for to_id in range(tagB_num):
                if (len(edgeAB_data) < a_b_num):
                    edgeAB_data.append(
                        gen_edge(ids_for_all, name_tagA, from_id, name_tagB,
                                 to_id + tagA_num))
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
                    gen_edge(ids_for_all, name_tagA, from_ids[i], name_tagA,
                             to_a_ids[i]))
                ids_for_all += 1
            times += 1
        times = 0
        while times < a_b_num:
            to_b_ids.insert(0, to_b_ids.pop())
            for i in range(tagB_num):
                edgeAB_data.append(
                    gen_edge(ids_for_all, name_tagA, from_ids[i], name_tagB,
                             to_b_ids[i]))
                ids_for_all += 1
            times += 1

    return data


def data2neo4j(data: dict, args):
    from neo4j import GraphDatabase
    import threading
    import time

    address = args.output
    user = args.user
    password = args.password
    thread_num = 4

    edge_batch_size = 1000
    vertex_batch_size = 1000

    def dict2json(data: dict):
        parse_str = ''
        for key, value in data.items():
            parse_str += ',' + key + ":" + \
                         ('"' + str(value) + '"' if isinstance(value, str) else str(value)) + " "
        return "{ " + parse_str[1:] + "}"

    def addVertex(tx, element: dict, name: str):
        for data in element:
            statement = "create (:{} {})".format(
                name, dict2json(data))
            tx.run(statement)

    def addEdge(tx, element: dict, name: str):
        for data in element:
            from_statement = "(a:{} {})".format(
                data["from"]["tag"], dict2json(data["from"]["match"]))
            to_statement = "(b:{} {})".format(
                data["to"]["tag"], dict2json(data["to"]["match"]))
            edge_statement = '[:{} {}]'.format(
                name, dict2json(data["data"]))
            statement = "match {} match {} create (a)-{}->(b)".format(
                from_statement, to_statement, edge_statement)
            tx.run(statement)

    def runAddVertex(session, name: str, data: list):
        logger.info('start thread:', threading.currentThread().getName())
        ids = 0
        batch_ids = 0
        total_count = len(data)
        while ids < total_count:
            batch_data = data[ids:ids + vertex_batch_size]
            batch_start_time = time.time()
            session.write_transaction(
                addVertex, batch_data, name)
            ids += vertex_batch_size
            batch_ids += 1
            logger.info("%{}:vertex write {}/{} use {} seconds".format(batch_ids, ids, total_count,
                                                                       time.time() - batch_start_time))

    def runAddEdge(session, name: str, data: list):
        ids = 0
        batch_ids = 0
        total_count = len(data)
        while ids < total_count:
            batch_data = data[ids:ids + edge_batch_size]
            batch_start_time = time.time()
            session.write_transaction(
                addEdge, batch_data, name)
            ids += edge_batch_size
            batch_ids += 1
            logger.info("%{}:edge write {}/{} use {} seconds".format(batch_ids, ids, total_count,
                                                                     time.time() - batch_start_time))

    driver = GraphDatabase.driver(address, auth=(user, password))
    sessions = [driver.session() for _ in range(thread_num)]

    thread_vertex = []
    tag_count = len(data['vertex'])
    thread_tag_count = thread_num // tag_count
    if thread_tag_count < 1:
        raise Exception("thread_num must great tag_count")

    session_ids = 0
    for name, v in data["vertex"].items():
        vertex_count = len(v['data'])
        per_thread_tag_count = vertex_count // thread_tag_count
        for i in range(thread_tag_count):
            end = (i + 1) * per_thread_tag_count if i + \
                                                    1 < thread_tag_count else vertex_count
            t = threading.Thread(target=runAddVertex, name='thread-' + name, args=(
                sessions[session_ids], name, v['data'][i * per_thread_tag_count:end]))
            t.start()
            session_ids += 1
            thread_vertex.append(t)
    for t in thread_vertex:
        t.join()

    session_ids = 0
    thread_edge = []
    edge_type_count = len(data['edge'])
    thread_edge_type_count = thread_num // edge_type_count
    if thread_edge_type_count < 1:
        raise Exception("thread_num must great edge_type_count")
    for name, e in data["edge"].items():
        edge_count = len(e['data'])
        per_thread_edge_count = edge_count // thread_edge_type_count
        for i in range(thread_edge_type_count):
            end = (i + 1) * per_thread_edge_count if i + 1 < thread_edge_type_count else edge_count
            t = threading.Thread(target=runAddEdge, name='thread-' + name, args=(
                sessions[session_ids], name, e['data'][i * per_thread_edge_count:end]
            ))
            t.start()
            session_ids += 1
            thread_vertex.append(t)
    for t in thread_edge:
        t.join()
    map(lambda session: session.close(), sessions)
    driver.close()


def data2janus(data: dict, args):
    from gremlin_python.structure.graph import Graph
    from gremlin_python.driver.driver_remote_connection import DriverRemoteConnection

    address = args.output
    graph = Graph()
    connection = DriverRemoteConnection(address, 'g')
    g = graph.traversal().withRemote(connection)

    for name, vertex in data['vertex'].items():
        vertex_count = len(vertex['data'])
        for i, properties in enumerate(vertex['data']):
            vv = g.addV(name)
            for k, v in properties.items():
                vv = vv.property(k, v)
            vv.next()
            logger.info("{}: {}/{}".format(name, i, vertex_count))

    for name, edge in data['edge'].items():
        edge_count = len(edge['data'])
        for i, e in enumerate(edge['data']):
            from_ = g.V().hasLabel(e['from']['tag'])
            for k, v in e['from']['match'].items():
                from_ = from_.has(k, v)
            to_ = g.V().hasLabel(e['to']['tag'])
            for k, v in e['to']['match'].items():
                to_ = to_.has(k, v)
            ee = g.addE(name).from_(from_).to(to_)
            for k, v in e['data'].items():
                ee = ee.property(k, v)
            ee.next()
            logger.info("{}: {}/{}".format(name, i, edge_count))
    logger.info("total vertex count: {}, total edge count: {}".format(g.V().count().next(), g.E().count().next()))


def data2json(data: dict, args):
    address = args.output
    import json
    with open(address + ".json", "w") as f:
        json.dump(data, f, indent=4)


def data2csv(data: dict, args):
    import csv

    address = args.output

    for name, v in data["vertex"].items():
        path = address + "{}.vertex.csv".format(name)
        rows = map(lambda x: x.values(), v['data'])
        with open(path, 'w') as f:
            csv_writer = csv.writer(f)
            csv_writer.writerows(rows)

    for name, e in data['edge'].items():
        path = address + "{}.edge.csv".format(name)
        rows = map(lambda x: [list(x['from']['match'].values())[0], list(
            x['to']['match'].values())[0]] + list(x['data'].values()), e['data'])
        with open(path, 'w') as f:
            csv_writer = csv.writer(f)
            csv_writer.writerows(rows)


def data2sql(data: dict, args):
    address = args.output
    database_name = 'test'
    batch_size = 10
    path = address + ".sql"
    statements = ['create database if not exists {}'.format(database_name), 'use {}'.format(database_name)]
    for name, v in data["vertex"].items():
        schema = v['schema']
        drop_statement = 'drop table IF EXISTS {}'.format(name)
        statements.append(drop_statement)
        create_statement = "CREATE TABLE {}".format(name) + "({})".format(','.join(
            map(lambda k, t: k + " " + t['sql'].upper(), schema.keys(), schema.values())))
        statements.append(create_statement)
        for i in range(0, len(v['data']) // batch_size + (0 if len(v['data']) % batch_size == 0 else 1)):
            batch_data = v['data'][i * batch_size:(i + 1) * batch_size]
            insert_statement = "insert into {} values {}".format(name, ",".join(
                map(lambda x: '(' + ",".join(map(value2str, x.values())) + ')', batch_data)))
            statements.append(insert_statement)

    for name, e in data['edge'].items():
        schema = e['schema']
        drop_statement = 'drop table IF EXISTS {}'.format(name)
        statements.append(drop_statement)
        create_statement = "CREATE TABLE {}".format(name) + "({})".format(
            ",".join(list(['idFrom ' + schema['idFrom']['sql'].upper(
            ), 'idTo ' + schema['idTo']['sql'].upper()]) + list(
                map(lambda k, t: k + " " + t['sql'].upper(), schema['property'].keys(),
                    schema['property'].values()))))
        statements.append(create_statement)

        for i in range(0, len(e['data']) // batch_size + (0 if len(e['data']) % batch_size == 0 else 1)):
            batch_data = e['data'][i * batch_size:(i + 1) * batch_size]
            insert_statement = "insert into {} values {}".format(name, ",".join(map(lambda x: '(' + ",".join(
                [str(list(x['from']['match'].values())[0]), str(list(x['to']['match'].values())[0])] + list(
                    map(value2str, x['data'].values()))) + ')', batch_data)))
            statements.append(insert_statement)
    with open(path, 'w') as f:
        f.writelines(";\n".join(statements))


if __name__ == '__main__':
    import argparse
    import json

    parser = argparse.ArgumentParser(description='generate graph json')

    parser.add_argument('-o', '--output',
                        help='output server address or file address prefix', type=str, required=True)
    parser.add_argument('-a', '--tagAnum', help='tagA num', type=int, required=True)
    parser.add_argument('-b', '--tagBnum', help='tag b num', type=int, required=True)
    parser.add_argument('-aa', '--edgeAAnum', help='edge aa num', type=int, required=True)
    parser.add_argument('-ab', '--edgeABnum', help='edge ab num', type=int, required=True)
    parser.add_argument('-edge_count_type', '--edge_count_type', help="count: edge num, degree: edge degree",
                        choices=['count', 'degree'], default='count')
    parser.add_argument('--prefix', help='tag/edge name prefix', type=str, default="")
    parser.add_argument("-s", '--start_id',
                        help='start id', default=0, type=int)
    parser.add_argument("-t", '--type', help="output type",
                        choices=['json', 'csv', 'sql', 'neo4j', 'janus'], default='json')
    parser.add_argument("-u", '--user', help='user', required=False, default=None)
    parser.add_argument('-p', '--password', help='password', required=False, default=None)

    args = parser.parse_args()

    data = mock_data(args.start_id, args.prefix, args.tagAnum,
                     args.tagBnum, args.edgeAAnum, args.edgeABnum, args.edge_count_type)
    type_function_map = {
        'json': data2json,
        'csv': data2csv,
        'sql': data2sql,
        'neo4j': data2neo4j,
        'janus': data2janus,
    }
    type_function_map[args.type](data, args)
