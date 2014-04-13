#!/bin/bash

serv="dist/server/"

client="dist/client/"

rm -rf dist

mkdir dist/client -p

mkdir dist/server -p

mvn clean package

cp checkers-client/target/checkers-client-1.0-SNAPSHOT-exec.jar $client

cp checkers-client/target/classes/client.policy $client

cp ./run_client.sh $client

cp checkers-server/target/checkers-server-1.0-SNAPSHOT-exec.jar $serv

cp checkers-server/target/classes/server.policy $serv

cp ./run_server.sh $serv

echo "\nDone!"