DROP KEYSPACE IF EXISTS iot_data;
CREATE KEYSPACE iot_data WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1};
