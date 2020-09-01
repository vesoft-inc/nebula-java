#!/usr/bin/env python3
# --coding:utf-8--

# Copyright (c) 2020 vesoft inc. All rights reserved.
#
# This source code is licensed under Apache 2.0 License,
# attached with Common Clause Condition 1.0, found in the LICENSES directory.

from nebula.ConnectionPool import ConnectionPool
from nebula.Client import GraphClient
from nebula.Common import *
from graph import ttypes

from pyhocon import ConfigFactory

import logging

logging.basicConfig(
    level=logging.INFO, format='%(asctime)s-%(levelname)s-%(message)s')
logger = logging.getLogger(__name__)


def do_simple_execute(client, cmd):
    print("do execute %s" % cmd)
    resp = client.execute(cmd)
    if resp.error_code != 0:
        print('Execute failed: %s, error msg: %s' % (cmd, resp.error_msg))
        raise ExecutionException(
            'Execute failed: %s, error msg: %s' % (cmd, resp.error_msg))


def fetch_info(client: GraphClient, key: str):
    return client.execute_query("FETCH PROP ON " + key)


def handle_fetch_resp(resp):
    if not resp.rows:
        return []
    ret = []
    for row in resp.rows:
        if len(resp.column_names) != len(row.columns):
            continue
        name_value_list = {}
        for name, col in zip(resp.column_names, row.columns):
            name = str(name, encoding='utf8')
            if '.' in name:
                name = name.split('.')[1]

            if col.getType() == ttypes.ColumnValue.__EMPTY__:
                print('ERROR: type is empty')
            elif col.getType() == ttypes.ColumnValue.BOOL_VAL:
                name_value_list[name] = col.get_bool_val()
            elif col.getType() == ttypes.ColumnValue.INTEGER:
                name_value_list[name] = col.get_integer()
            elif col.getType() == ttypes.ColumnValue.ID:
                name_value_list[name] = col.get_id()
            elif col.getType() == ttypes.ColumnValue.STR:
                name_value_list[name] = col.get_str().decode('utf-8')
            elif col.getType() == ttypes.ColumnValue.DOUBLE_PRECISION:
                name_value_list[name] = col.get_double_precision()
            elif col.getType() == ttypes.ColumnValue.TIMESTAMP:
                name_value_list[name] = col.get_timestamp()
            else:
                print('ERROR: Type unsupported')
        ret.append(name_value_list)
    return ret


class TagConf:
    def __init__(self,
                 tag_name: str,
                 id_field: str,
                 id_policy: str,
                 fields: list,
                 type_name=""):
        self.tag_name = tag_name
        self.id_field = id_field
        self.id_policy = id_policy
        self.fields = fields
        self.type_name = type_name

    def __repr__(self):
        return self.tag_name

    def get_id(self, tag_id, fields: list):
        ret = (tag_id if self.id_policy == '' else
               self.id_policy + '({})'.format(tag_id)) + ":"
        ret += "(" + ','.join([fields[field] for field in self.fields]) + ")"
        return ret


class EdgeConf:
    def __init__(self,
                 edge_name,
                 from_field: str,
                 from_policy: str,
                 to_field: str,
                 to_policy: str,
                 fields: list,
                 ranking='',
                 type_name=""):
        self.edge_name = edge_name
        self.from_field = from_field
        self.from_policy = from_policy
        self.to_field = to_field
        self.to_policy = to_policy
        self.fields = fields
        self.type_name = type_name
        self.ranking = ranking

    def __repr__(self):
        return self.edge_name

    def get_id(self, from_id, to_id, ranking=0):
        return (str(from_id) if self.from_policy == '' else self.from_policy + '({})'.format(from_id)) + "->" + \
               (str(to_id) if self.to_policy == '' else self.to_policy +
                '({})'.format(to_id)) + '@{}'.format(ranking)


class HandleConf:
    def __init__(self, conf):
        self.tag_data = {}
        self.edge_data = {}
        for c in conf["tags"]:
            tag_name = c.get_string("name")
            type_name = c.get_string("type", "")
            if "vertex.field" in c:
                id_field = c.get_string("vertex.field")
                id_policy = c.get_string("vertex.policy", "")
            else:
                id_field = c.get_string("vertex")
                id_policy = ""
            fields = list(map(lambda x: str(x), c.get("fields").values()))
            self.tag_data[tag_name] = TagConf(tag_name, id_field, id_policy,
                                              fields, type_name)

        for c in conf["edges"]:
            edge_name = c.get_string("name")
            type_name = c.get_string("type", "")
            if "source.field" in c:
                from_field = c.get_string("source.field")
                from_policy = c.get_string("source.policy", "")
            else:
                from_field = c.get_string("source")
                from_policy = ""
            if "target.field" in c:
                to_field = c.get_string("target.field")
                to_policy = c.get_string("target.policy", "")
            else:
                to_field = c.get_string("target")
                to_policy = ""
            ranking = c.get_string("ranking", "")
            fields = list(map(lambda x: str(x), c.get("fields").values()))
            self.edge_data[edge_name] = EdgeConf(
                edge_name, from_field, from_policy, to_field, to_policy,
                fields, ranking, type_name)


def verify_nebula(client: GraphClient, datas: list, conf):
    nebula_space = conf.get_string("nebula.space")
    do_simple_execute(client, 'use ' + nebula_space)
    batch_size = 50
    full_conf = HandleConf(conf)
    json_vertex_data = {}
    json_edge_data = {}
    for data in datas:
        for tag_name, vertices in data["vertex"].items():
            json_vertex_data[tag_name] = vertices["data"]
        for edge_name, edge in data["edge"].items():
            json_edge_data[edge_name] = edge["data"]
    for tag_name, tag_conf in full_conf.tag_data.items():
        logger.info("verify tag:" + tag_name)
        data = json_vertex_data[tag_name]
        query = 'fetch prop on {} '.format(tag_name)
        ids = 0
        while ids < len(data):
            _ = 0
            batch_ids = []
            search_map = {}
            while _ < batch_size and ids < len(data):
                batch_ids.append(str(data[ids][tag_conf.id_field]))
                search_map[str(data[ids][tag_conf.id_field])] = data[ids]
                ids += 1
                _ += 1
            exec_query = query + ','.join(batch_ids)
            logger.info("nebula exec: " + exec_query)

            query_resp = client.execute_query(exec_query)
            if query_resp.error_code != 0:
                logger.error("resp msg:" + query_resp.error_msg)
            else:
                nebula_datas = handle_fetch_resp(query_resp)
                if len(batch_ids) != len(nebula_datas):
                    logger.error("search length:{}, real length:{}".format(
                        len(batch_ids), len(nebula_datas)))
                for nebula_data in nebula_datas:
                    json_data = search_map[str(nebula_data[str(
                        tag_conf.id_field)])]
                    for field in tag_conf.fields:
                        if nebula_data[field] != json_data[field]:
                            logger.error(
                                "json data:{} , nebula data:{} , field:{}, json value:{}, nebula value: {}",
                                json_data, nebula_data, field,
                                json_data[field], nebula_data[field])

    def trim_edge_id(x):
        return x if '.' not in x else x.split('.')[1]

    for edge_name, edge_conf in full_conf.edge_data.items():
        logger.info("verify edge:" + edge_name)
        query = 'fetch prop on {} '.format(edge_name)
        data = json_edge_data[edge_name]
        for json_data in data:

            edge_id = edge_conf.get_id(
                json_data['from']['match'][trim_edge_id(edge_conf.from_field)],
                json_data['to']['match'][trim_edge_id(
                    edge_conf.to_field)], 0 if edge_conf.ranking == '' else
                json_data['data'][edge_conf.ranking])

            exec_query = query + edge_id
            logger.info("nebula exec: " + exec_query)

            query_resp = client.execute_query(exec_query)
            if query_resp.error_code != 0:
                logger.error("resp msg: " + query_resp.error_msg)
            else:
                nebula_datas = handle_fetch_resp(query_resp)
                if len(nebula_datas) != 1:
                    logger.error("nebula data:{}, json_data:{}".format(
                        nebula_datas, json_data))
                else:
                    nebula_data = nebula_datas[0]
                    for field in edge_conf.fields:
                        if field not in nebula_data:
                            logger.error("field:{} not in nebula data{}",
                                         field, nebula_data)
                        if nebula_data[field] != json_data['data'][field]:
                            logger.error(
                                "json data:{} , nebula data:{} , field:{}, json value:{}, nebula value: {}",
                                json_data, nebula_data, field,
                                json_data[field], nebula_data[field])
                    if edge_conf.ranking != '':
                        if nebula_data['_rank'] != json_data['data'][
                                edge_conf.ranking]:
                            logger.error(
                                "rank field {}, nebula rank {}, json rank {}".
                                format(edge_conf.ranking, nebula_data['_rank'],
                                       json_data['data'][edge_conf.ranking]))


if __name__ == '__main__':
    import argparse
    import json

    parser = argparse.ArgumentParser(description='verify nebula use json')

    parser.add_argument(
        '-j',
        '--jsons',
        help='json files path',
        nargs='+',
        type=str,
        required=True)
    parser.add_argument(
        '-a',
        '--address',
        help='nebula address',
        type=str,
        default="127.0.0.1:3699")
    parser.add_argument(
        '-p',
        '--password',
        help='nebula password',
        type=str,
        default="password")
    parser.add_argument(
        '-u', '--user', help='nebula user name', type=str, default="user")

    parser.add_argument(
        '-c',
        '--config',
        help='spark importer config path',
        type=str,
        default="application.conf")

    parser.add_argument(
        '-l',
        '--log_level',
        help='log level=> 10:debug, 20:info, 30:warn, 40:error',
        type=int,
        default=30)
    args = parser.parse_args()

    connection_pool = ConnectionPool(
        args.address.split(":")[0],
        args.address.split(":")[1])
    client = GraphClient(connection_pool)
    auth_resp = client.authenticate(args.user, args.password)
    if auth_resp.error_code:
        raise AuthException("Auth failed")

    conf = ConfigFactory.parse_file(args.config)

    data = []
    for j in args.jsons:
        with open(j) as f:
            data.append(json.load(f))

    logger.setLevel(args.log_level)
    verify_nebula(client, data, conf)
