#!/usr/bin/env python3
# --coding:utf-8--

# Copyright (c) 2020 vesoft inc. All rights reserved.
#
# This source code is licensed under Apache 2.0 License,
# attached with Common Clause Condition 1.0, found in the LICENSES directory.

import pulsar
import sys
import time
from pulsar.schema import *

import logging

logging.basicConfig(level=logging.INFO,
                    format='%(asctime)s-%(levelname)s-%(message)s')
logger = logging.getLogger(__name__)


class tagA(Record):
    idInt = Integer()
    idString = String()
    tboolean = Boolean()
    tdouble = Double()


class edgeAA(Record):
    idFrom = Integer()
    idTo = Integer()
    idInt = Integer()
    idString = String()
    tboolean = Boolean()
    tdouble = Double()


class Producer:

    schema_map = {
        "avro": AvroSchema,
        "json": JsonSchema
    }

    record_map = {
        "tagA": tagA,
        "edgeAA": edgeAA
    }

    record_gen_data_func_map = {
        "tagA": lambda i, pid: tagA(idInt=i, idString='{}_{}'.format(pid, i), tboolean=i % 2 == 0, tdouble=i+0.1),
        "edgeAA": lambda i, pid: edgeAA(idFrom=i, idTo=i, idInt=i, idString='{}_{}'.format(pid, i), tboolean=i % 2 == 0, tdouble=i+0.1)
    }

    def __init__(self, topic: str, schema_name: str, record_name: str):
        self.topic = topic
        self.schema_name = schema_name
        self.record_name = record_name
        self.producer = None

    def getProducer(self, client: pulsar.Client):
        if self.producer is None:
            self.producer = client.create_producer(self.topic, schema=Producer.schema_map[self.schema_name](
                Producer.record_map[self.record_name]))
        return self.producer

    def send(self, client: pulsar.Client, ids: int, producer_id: int):
        self.getProducer(client).send(
            Producer.record_gen_data_func_map[self.record_name](ids, producer_id))


def pulsar_streaming(client: pulsar.Client, producers: list, batch_size: int, interval: int):
    batch_count = 0
    batch_num = 0
    while True:
        for i in range(sys.maxsize):
            for pid, producer in enumerate(producers):
                producer.send(client, i, pid)
            batch_count += 1
            if batch_count >= batch_size:
                time.sleep(interval)
                batch_count = 0
                batch_num += 1
                logger.info('send {} batch to pulsar'.format(batch_num))


if __name__ == '__main__':
    import argparse

    parser = argparse.ArgumentParser(description='pulsar streaming')

    parser.add_argument('-a', '--address', help='pulsar address',
                        type=str, default="pulsar://localhost:6650")
    parser.add_argument('-b', '--batch_size',
                        help='pulsar batch size', type=int, default=10)
    parser.add_argument('-i', '--interval', help='pulsar interval per batch (seconds)',
                        type=int, default=10)

    parser.add_argument('-s', '--schema_type',
                        choices=['avro', 'json'], type=str, default='avro')
    parser.add_argument('-t', '--tag_topics', nargs="+", type=str, default=[])
    parser.add_argument('-e', '--edge_topics', nargs="+", type=str, default=[])
    args = parser.parse_args()
    client = pulsar.Client(args.address)
    producers = list(map(lambda topic: Producer(topic, args.schema_type, "tagA"), args.tag_topics)) + \
        list(map(lambda topic: Producer(
            topic, args.schema_type, "edgeAA"), args.edge_topics))
    pulsar_streaming(client, producers, args.batch_size, args.interval)
    client.close()
