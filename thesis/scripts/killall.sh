#!/bin/bash
#arg 1 is vassals file
for line in $(cat $1); do
	echo $line; 
	ssh $line pkill -f bnb.vassal.VassalMain
done

