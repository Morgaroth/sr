#!/bin/bash

if [ "$1" == "--help" ]
then
    echo "args: adres port\n"
else

    jar=`ls *.jar`

    policy_file=`ls *.policy`

    policy=`readlink -f $policy_file`

    adres=$1

    port=$2

    policy_opt="-Djava.security.policy=\"file://"$policy"\""

    java $policy_opt -jar $jar $adres $port

fi