#!/bin/bash

set -e

if [[ $# -eq 0 ]] ; then
    printf 'Error running script....\n Please pass 2 valid arguments like 10 YES or 100 NO or 1000 YES'
	printf 'First argument is reset count. Second argument is flag to by-pass Pega'	
    exit 0
fi
# Kill all running billing miscoservices
printf "Killing all billing microservices...\n"

kill -9 `ps -ef | grep billing | grep -v grep | awk '{print $2}'`

printf "copying s7.jar to s7b.jar\n"

cp billing-s7.jar billing-s7b.jar

sleep 2

printf "*********************"
printf "Starting S3...\n"

java -jar billing-s3.jar > s3.log &

sleep 8
tail -5 s3.log

printf "*********************"
printf "Starting S4...\n"

java -jar billing-s4.jar > s4.log &

sleep 8
tail -5 s4.log

curl localhost:8084/load

printf "*********************"
printf "Starting S5...\n"

java -jar billing-s5.jar > s5.log &

sleep 8
tail -5 s5.log
curl localhost:8085/load

printf "*********************"
printf "Starting S6...\n"

java -jar billing-s6.jar > s6.log &

sleep 8
tail -5 s6.log
curl localhost:8086/load

printf "*********************"
printf "Starting S7...\n"

java -jar billing-s7.jar --pega.bypass=$2 > s7.log &
sleep 8
tail -5 s7.log
curl localhost:8087/load

printf "*********************"
printf "Starting S7b...\n"

java -jar billing-s7b.jar --pega.bypass=$2 --server.port=8097 > s7b.log &
sleep 8
tail -5 s7b.log
curl localhost:8097/load

printf "*********************"
printf "Starting S8...\n"

java -jar billing-s8.jar > s8.log &
sleep 8
tail -5 s8.log
curl localhost:8088/load

sleep 2
printf "********************\n Strating reset\n"
sh ./reset.sh $1
sleep 2
printf "************** ALL DONE !! :-) **************\n"
ps -ef | grep 'billing' 

