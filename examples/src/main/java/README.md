### examples
* MetaClientExample
please create a test space and tag/edge at first, you can do it in console:
```
CREATE space test(partition_num=1, replica_factor=1)
USE test;
CREATE TAG test_tag (name string, credits int);
CREATE TAG test_edge (name string);
```

* ScanEdgeInSpaceExample
scan edge of select in a partition, you need to insert some data before use it. The Schema looks as follows:
```
CREATE EDGE select(grade int);

# insert some edge
INSERT EDGE select(grade) VALUES 201 -> 102:(3);
```

* ScanVertexInPartExample
scan tag of student in a space, you need to insert some data before use it. The schema looks as follows
```
CREATE TAG student(name string, age int, gender string);

# insert some edge
INSERT VERTEX student(name, age, gender) VALUES 201:("a", 16, "female");
```
