#!/bin/bash

count=1
for i in {571..658}
do
	if [ $count -le $2 ]; then
		echo $i
		qsub -ar $i subgridvassal.sh hertz $i 2 $1
		let "count = $count + 1"
	fi
done
