#!/usr/bin/env bash
cqlsh -f /scripts/cassandra-setup.cql cassandra-server
#cqlsh describe keyspaces;
echo "### IOT_DATA Project - SETUP DONE! ###"