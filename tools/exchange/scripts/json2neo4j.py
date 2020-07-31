#!/usr/bin/env python3
# --coding:utf-8--

# Copyright (c) 2020 vesoft inc. All rights reserved.
#
# This source code is licensed under Apache 2.0 License,
# attached with Common Clause Condition 1.0, found in the LICENSES directory.


from neo4j import GraphDatabase
import json
import threading

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
    print('start thread:', threading.currentThread().getName())
    vertext_time = time.time()
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
        print("%{}:vertex write {}/{} use {} seconds".format(batch_ids,
                                                             ids, total_count, time.time() - batch_start_time))
    vertex_use_time = {
        "tagname": name,
        "use_time": time.time() - vertext_time,
        "property_keys": [] if len(data) == 0 else list(data[0].keys()),
        "length": len(v['data'])
    }
    print(vertex_use_time)


def runAddEdge(session, name: str, data: list):
    edge_time = time.time()
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
        print("%{}:edge write {}/{} use {} seconds".format(batch_ids,
                                                           ids, total_count, time.time() - batch_start_time))

    edge_use_time = {
        "edgename": name,
        "use_time": time.time() - edge_time,
        "property_kest": [] if len(data) == 0 else list(data[0].keys()),
        "length": len(data)
    }
    print(edge_use_time)


if __name__ == '__main__':

    import argparse
    import time

    parser = argparse.ArgumentParser(description='import json file to neo4j')

    parser.add_argument('-f', '--file', help='json file path',
                        type=str, required=True)
    parser.add_argument('-a', '--address', help='neo4j address',
                        type=str, default="bolt://127.0.0.1:7687")
    parser.add_argument('-p', '--password',
                        help='neo4j password', type=str, default="neo4j")
    parser.add_argument('-u', '--user', help='neo4j user name',
                        type=str, default="neo4j")
    parser.add_argument('-t', '--thread_num', type=int, default=4)
    args = parser.parse_args()

    time_report = {"vertex_use_time": [], "edge_use_time": [],
                   "vertex_batch_size": vertex_batch_size, "edge_batch_size": edge_batch_size}
    vertex_use_time = time_report["vertex_use_time"]
    edge_use_time = time_report["edge_use_time"]

    thread_num = args.thread_num
    driver = GraphDatabase.driver(
        args.address, auth=(args.user, args.password))
    with open(args.file) as f:
        data = json.load(f)
        print("start write")
        sessions = [driver.session() for i in range(thread_num)]
        total_start_time = time.time()
        time_report["total_start_time"] = total_start_time

        thread_vertex = []
        tag_count = len(data['vertex'])
        thread_tag_count = thread_num // tag_count
        if thread_tag_count < 1:
            raise Exception("thread_num must great tag_count")

        vertices_time = time.time()
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

        time_report["total_vertex"] = {
            "vertex_num": len(data["vertex"]),
            "use_time": time.time() - vertices_time
        }

        session_ids = 0
        thread_edge = []
        edge_type_count = len(data['edge'])
        thread_edge_type_count = thread_num // edge_type_count
        if thread_edge_type_count < 1:
            raise Exception("thread_num must great edge_type_count")
        edges_time = time.time()
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

        time_report["total_edge"] = {
            "edge_num": len(data["edge"]),
            "use_time": time.time() - edges_time
        }
        time_report["begin_to_end_insert"] = time.time() - total_start_time
        map(lambda session: session.close(), sessions)
    driver.close()
    print(json.dumps(time_report, indent=4))
