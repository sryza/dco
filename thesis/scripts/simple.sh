#!/bin/sh

### parameters for the job ###

# use current working directory, else defaults to home
#$ -cwd

# parallel environment: symmetric multiprocessing 
# use all 4 cores on dblade machines
#$ -pe smp 4

# run for up to an hour
#$ -l hour

# use only machines in group dblade
#$ -q '*@@dblade'

# mail me when job aborts, begins, exits, and suspends
#$ -m abes

ls /home/jakelley
