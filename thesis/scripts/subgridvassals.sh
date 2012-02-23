#!/bin/bash

count=1
for i in {78..117}
do
	if [ $count -le $2 ]; then
		echo $i
		qsub -ar $i subgridvassal.sh tesla $i 2 $1
		let "count = $count + 1"
	fi
done
