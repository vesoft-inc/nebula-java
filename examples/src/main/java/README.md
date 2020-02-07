### examples
* MetaClientExample
Please create a test space and tag/edge at first, you can do it in console:
```
CREATE space test(partition_num=1, replica_factor=1)
USE test;
CREATE TAG test_tag (name string, credits int);
CREATE TAG test_edge (name string);
```

* ScanEdgeInSpaceExample
Scan edge of select in a partition. You need to insert some data before use it, or you can just run `GraphClientExample`, which will insert some data. The Schema looks as follows:
```
CREATE EDGE select(grade int);

# insert some edge
INSERT EDGE select(grade) VALUES 201 -> 102:(3);
```

* ScanVertexInPartExample
Scan tag of student in a space. You need to insert some data before use it, or you can just run `GraphClientExample`, which will insert some data. The schema looks as follows
```
CREATE TAG student(name string, age int, gender string);

# insert some edge
INSERT VERTEX student(name, age, gender) VALUES 201:("a", 16, "female");
```

* SparkExample
A simple example of using data scanned from nebula in spark. Please make sure you have run `ScanEdgeInSpaceExample` before, which would generate a `edge.csv`.
The parameter of `SparkExample` is spark master url and path of `edge.csv`. 
