#!/usr/bin/env python3
# --coding:utf-8--

# Copyright (c) 2020 vesoft inc. All rights reserved.
#
# This source code is licensed under Apache 2.0 License,
# attached with Common Clause Condition 1.0, found in the LICENSES directory.


from gremlin_python.structure.graph import Graph
from gremlin_python.process.graph_traversal import __, GraphTraversalSource
from gremlin_python.driver.driver_remote_connection import DriverRemoteConnection


def data2janus(g: GraphTraversalSource, data: dict):
    for name, vertex in data['vertex'].items():
        for properties in vertex['data']:
            vv = g.addV(name)
            for k, v in properties.items():
                vv = vv.property(k, v)
            vv.next()
    for name, edge in data['edge'].items():
        for e in edge['data']:
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


if __name__ == '__main__':
    import argparse
    import json

    parser = argparse.ArgumentParser(description='json to janus graph')

    parser.add_argument('-f', '--file', help='json file path',
                        type=str, required=True)
    parser.add_argument('-a', '--address', help='gremlin ws address',
                        type=str, default="ws://localhost:8182/gremlin")
    args = parser.parse_args()

    graph = Graph()
    connection = DriverRemoteConnection('ws://localhost:8182/gremlin', 'g')
    g = graph.traversal().withRemote(connection)
    with open(args.file) as f:
        data = json.load(f)
        data2janus(g, data)
    print("total count: {}".format(g.V().count().next()))
