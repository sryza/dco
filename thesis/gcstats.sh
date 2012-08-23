#/bin/bash

hadoop fs -text  $( hadoop fs -ls /users/sryza/gcdir | tail -n $1 | head -n 1 | sed -e 's/ /\n/g' | tail -n 1 )/jobstats.stats
