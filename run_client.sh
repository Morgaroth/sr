#!/bin/bash

if [ "$1" == "--help" ]
then
    echo "args: adres port hostname nick [start|join oponent]  [auto]"
else
    jar=`ls *.jar`

    policy_file=`ls *.policy`

    policy=`readlink -f $policy_file`

    codebase="student"
    if [ "$codebase" == "student" ]
    then
        codebase="http://student.agh.edu.pl/~mjaje/checkers-server-classes.jar"
    else
        codebase=`readlink -f $3`
    fi

    adres=$1

    port=$2

    hostname=$3

    nick=$4

    mode=$5

    oponent=$6

    policy_opt="-Djava.security.policy=\"file://$policy\""

    codebase_opt="-Djava.rmi.server.codebase=\"$codebase\""

    echo "java $policy_opt $codebase_opt -jar $jar $adres $port $hostname $nick $mode $oponent"

fi