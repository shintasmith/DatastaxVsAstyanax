# DatastaxVsAstyanax
Compare and Contrast Datastax java drivers for CQL with Astyanax thrift support for java. 

#Moving on from Astyanax to Datastax:

This is the description of the tests performed to monitor the performance of both Datastax Java-Driver and Astyanax Driver for Cassandra so as to make a decision regarding replacing the current Astyanax Driver used by Blueflood with Datastax Driver.

#Environment Setup:
•	Compute Machines:<br />

Four Rackspace’s On-metal Compute machines with the following configuration:<br />
CPU<br />
2.8 Ghz, 10 core Intel® Xeon® E5-2680 v2<br />
RAM<br />
32 GB<br />
System Disk<br />
32 GB<br />
Network<br />
Redundant 10 Gb / s connections in a high availability bond<br />
Each for Read using Astyanax, Read using Datastax, Write using Astyanax, Write using Datastax

•	Cassandra Cluster:

Cassandra (Version 2.0) cluster with four nodes was spun using Rackspace’s Cassandra Orchestration stack.
Each node with following configuration:<br />
CPU<br />
8 vCPUs<br />
RAM<br />
30 GB<br />
System Disk<br />
1.2 TB<br />
Network<br />
1.2 Gb / s<br />

•	Keyspace and Table:
use "DATA";

CREATE TABLE metrics_full (
  key text,
  column1 bigint,
  value blob,
  PRIMARY KEY ((key), column1)
) WITH COMPACT STORAGE AND
  bloom_filter_fp_chance=0.010000 AND
  caching='KEYS_ONLY' AND
  comment='' AND
  dclocal_read_repair_chance=0.100000 AND
  gc_grace_seconds=864000 AND
  index_interval=128 AND
  read_repair_chance=0.000000 AND
  replicate_on_write='true' AND
  populate_io_cache_on_flush='false' AND
  default_time_to_live=0 AND
  speculative_retry='NONE' AND
  memtable_flush_period_in_ms=0 AND
  compaction={'class': 'SizeTieredCompactionStrategy'} AND
  compression={'sstable_compression': 'LZ4Compressor'};

•	Graphite:

Graphite was used as a time-series database to store the metrics generated during the test.

•	Grafana: To monitor the metrics using nice Graphs.

#Running the program using maven:
mvn clean install<br />
mvn exec:java -Dexec.args="'astyanax' 'read'" (For Read Operations using Astyanax Drivers).<br />
mvn exec:java -Dexec.args="'datastax' 'read'" (For Read Operations using Datastax Drivers).<br />
mvn exec:java -Dexec.args="'astyanax' 'write'" (For Write Operations using Astyanax Drivers).<br />
mvn exec:java -Dexec.args="'datastax' 'write'" (For Write Operations using Datastax Drivers).<br />
