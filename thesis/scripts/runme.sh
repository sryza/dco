#!/bin/sh

### parameters for the job ###

# use current working directory, else defaults to home
#$ -cwd

# parallel environment: symmetric multiprocessing 
# use all 4 cores on dblade machines
#$ -pe smp 4

# run for up to a day
#$ -l day

# use only machines in group dblade
#$ -q '*@@dblade'

# use 1-x number of machines
#$ -t 1-40

# mail me when job aborts, begins, exits, and suspends
#$ -m abes

host=`hostname -f`
my_machine="torch.cs.brown.edu"

# clear any old data
if [ -e /ltmp/jakelley/ ] ; then
    rm -rf /ltmp/jakelley/
fi

# set up install-dir 
mkdir /ltmp/jakelley
# check exit status
if [ $? != 0 ] ; then
    echo "failed to created /ltmp/jakelley" > /home/jakelley/error."$host"
    exit -1
fi
chmod 0700 /ltmp/jakelley

cp -r /home/jakelley/hadoop/hadoop-secure/hadoop-install/ /ltmp/jakelley
# check exit status
if [ $? != 0 ] ; then
    echo "failed to install hadoop" > /home/jakelley/hadoop/benchmarking/error."$host"
    rm -rf /ltmp/jakelley
    exit -2
fi


# set up environment
export HADOOP_HOME="/ltmp/jakelley/hadoop-install"
export JAVA_HOME="/usr"
export HADOOP_CONF_DIR="$HADOOP_HOME/conf"
export PATH="$HADOOP_HOME/bin":$PATH

# start datanode and tasktracker as daemons
nohup $HADOOP_HOME/bin/hadoop --config $HADOOP_CONF_DIR datanode 2> /home/jakelley/hadoop/benchmarking/logs/"$host"-data.out &
nohup $HADOOP_HOME/bin/hadoop --config $HADOOP_CONF_DIR tasktracker 2> /home/jakelley/hadoop/benchmarking/logs/"$host"-task.out &

sleep 5
# sleep until both die, then exit
java_procs=`ps -u jakelley | grep "java"`
# if length of string is non-zero, there are some java processes
while [ -n "$java_procs" ] ; do
    java_procs=`ps -u jakelley | grep "java"`

    # this is a fail-safe in case things are taking too long
    # kill the processes and then clear the data
    sleep 82800 # 23 hours, limit of 24 hours imposed by grid for the queue
    killall -9 -u jakelley java
    rm -rf /ltmp/jakelley
done
#clean up a bit, only if not on my machine
if [ "$host" != "$my_machine" ] ; then
    rm -rf /ltmp/jakelley
fi
echo "exiting"
